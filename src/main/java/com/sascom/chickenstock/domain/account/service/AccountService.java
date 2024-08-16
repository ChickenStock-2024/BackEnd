package com.sascom.chickenstock.domain.account.service;

import com.sascom.chickenstock.domain.account.dto.request.CancelOrderRequest;
import com.sascom.chickenstock.domain.account.dto.request.StockOrderRequest;
import com.sascom.chickenstock.domain.account.dto.response.*;
import com.sascom.chickenstock.domain.account.entity.Account;
import com.sascom.chickenstock.domain.account.error.code.AccountErrorCode;
import com.sascom.chickenstock.domain.account.error.exception.AccountNotEnoughException;
import com.sascom.chickenstock.domain.account.error.exception.AccountNotFoundException;
import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.auth.dto.response.AccountInfoForLogin;
import com.sascom.chickenstock.domain.company.entity.Company;
import com.sascom.chickenstock.domain.company.error.code.CompanyErrorCode;
import com.sascom.chickenstock.domain.company.error.exception.CompanyNotFoundException;
import com.sascom.chickenstock.domain.company.repository.CompanyRepository;
import com.sascom.chickenstock.domain.company.service.CompanyService;
import com.sascom.chickenstock.domain.competition.entity.Competition;
import com.sascom.chickenstock.domain.competition.error.code.CompetitionErrorCode;
import com.sascom.chickenstock.domain.competition.error.exception.CompetitionNotFoundException;
import com.sascom.chickenstock.domain.competition.repository.CompetitionRepository;
import com.sascom.chickenstock.domain.competition.service.CompetitionService;
import com.sascom.chickenstock.domain.history.entity.History;
import com.sascom.chickenstock.domain.history.entity.HistoryStatus;
import com.sascom.chickenstock.domain.history.error.code.HistoryErrorCode;
import com.sascom.chickenstock.domain.history.error.exception.HistoryNotFoundException;
import com.sascom.chickenstock.domain.history.repository.HistoryRepository;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.error.code.MemberErrorCode;
import com.sascom.chickenstock.domain.member.error.exception.MemberNotFoundException;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import com.sascom.chickenstock.domain.trade.dto.OrderType;
import com.sascom.chickenstock.domain.trade.dto.TradeType;
import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.response.CancelOrderResponse;
import com.sascom.chickenstock.domain.trade.dto.response.TradeResponse;
import com.sascom.chickenstock.domain.trade.service.TradeService;
import com.sascom.chickenstock.global.util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final HistoryRepository historyRepository;
    private final MemberRepository memberRepository;
    private final CompetitionRepository competitionRepository;
    private final RedisService redisService;
    private final TradeService tradeService;
    private final CompetitionService competitionService;
    private final CompanyService companyService;
    private final CompanyRepository companyRepository;

    @Transactional
    public Long createAccount(Long memberId, Long competitionId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> CompetitionNotFoundException.of(CompetitionErrorCode.NOT_FOUND));
        Account account = new Account(
                member,
                competition
        );
        return accountRepository.save(account).getId();
    }


    public AccountInfoResponse getAccountInfo(Long accountId) {
        Account account = accountRepository.findById(accountId).
                orElseThrow(EntityNotFoundException::new);

        List<StockInfo> stocks = new ArrayList<>();
        // 사용자 주식 정보 조회
        Map<String, Map<String, String>> allStockInfo = redisService.getStockInfo(accountId);
        for (Map.Entry<String, Map<String, String>> entry : allStockInfo.entrySet()) {
            String key = entry.getKey();
            Map<String, String> stockData = entry.getValue();
            StringTokenizer st = new StringTokenizer(key, ":");
            st.nextToken();
            st.nextToken();
            st.nextToken();
            Long companyId = Long.valueOf(st.nextToken());
            Company company = companyService.findById(companyId);

            stocks.add(new StockInfo(company.getName(),
                    Integer.valueOf(stockData.get("price")),
                    Integer.valueOf(stockData.get("volume")))
            );
        }

        // 사용자 계좌 잔고 조회
        AccountInfoResponse accountInfoResponse = new AccountInfoResponse(
                account.getBalance(),
                stocks
        );

        return accountInfoResponse;
    }

    public ExecutionContentResponse getExecutionContent(Long accountId){
        List<History> histories = historyRepository.findExecutionContent(accountId);
        List<HistoryInfo> result = histories.stream()
                .map(h -> new HistoryInfo(h.getCompany().getName(),
                        h.getPrice(),
                        h.getVolume(),
                        h.getStatus(),
                        h.getCreatedAt()
                ))
                .collect(Collectors.toList());
        return new ExecutionContentResponse(result);
    }

    @Transactional
    public TradeResponse buyLimitStocks(StockOrderRequest stockOrderRequest) {
        validateStockOrderRequest(stockOrderRequest);

        Account account = accountRepository.findById(stockOrderRequest.accountId())
                .orElseThrow(() -> AccountNotFoundException.of(AccountErrorCode.NOT_FOUND));
        if(!account.getCompetition().getId().equals(stockOrderRequest.competitionId())) {

        }
        Company company = companyRepository.findById(stockOrderRequest.companyId())
                .orElseThrow(() -> CompanyNotFoundException.of(CompanyErrorCode.NOT_FOUND));

        // 주식 매수하려고 하는데 계좌에 구매가능 잔고 있는지 확인
        if (account.getBalance() < (long) stockOrderRequest.volume() * stockOrderRequest.unitCost()) {
            throw AccountNotEnoughException.of(AccountErrorCode.NOT_ENOUGH_BALANCE);
        }

        // History Table에 기록 Write
        History history = historyRepository.save(History.builder()
                .account(account)
                .price(stockOrderRequest.unitCost())
                .company(company)
                .volume(stockOrderRequest.volume())
                .status(HistoryStatus.지정가매수요청)
                .build()
        );
        Long historyId = history.getId(); // historyId를 요청 객체(TradeRequest)에 포함시킬 것임

        // 구매요청
        return tradeService.addLimitBuyRequest(
                stockOrderRequest.toBuyTradeRequestEntity(historyId, history.getCreatedAt(), OrderType.LIMIT)
        );

    }


    @Transactional
    public TradeResponse sellLimitStocks(StockOrderRequest stockOrderRequest) {
        validateStockOrderRequest(stockOrderRequest);

        Account account = accountRepository.findById(stockOrderRequest.accountId())
                .orElseThrow(() -> AccountNotFoundException.of(AccountErrorCode.NOT_FOUND));
        Company company = companyRepository.findById(stockOrderRequest.companyId())
                .orElseThrow(() -> CompanyNotFoundException.of(CompanyErrorCode.NOT_FOUND));

        // TODO: 주식 매도하려고 하는데 사용자가 해당 주식을 매도하려는만큼 가지고있는지 확인

        // History Table에 기록 Write
        History history = historyRepository.save(History.builder()
                .account(account)
                .price(stockOrderRequest.unitCost())
                .company(company)
                .volume(stockOrderRequest.volume())
                .status(HistoryStatus.지정가매도요청)
                .build()
        );

        Long historyId = history.getId();

        // 구매요청
        return tradeService.addLimitSellRequest(
                stockOrderRequest.toSellTradeRequestEntity(historyId, history.getCreatedAt(), OrderType.LIMIT)
        );
    }


    @Transactional
    public TradeResponse buyMarketStocks(StockOrderRequest stockOrderRequest) {
        validateStockOrderRequest(stockOrderRequest);

        Account account = accountRepository.findById(stockOrderRequest.accountId())
                .orElseThrow(() -> AccountNotFoundException.of(AccountErrorCode.NOT_FOUND));
        Company company = companyRepository.findById(stockOrderRequest.companyId())
                .orElseThrow(() -> CompanyNotFoundException.of(CompanyErrorCode.NOT_FOUND));

        // History Table에 기록 Write
        History history = historyRepository.save(History.builder()
                .account(account)
                .price(stockOrderRequest.unitCost())
                .company(company)
                .volume(stockOrderRequest.volume())
                .status(HistoryStatus.시장가매수요청)
                .build()
        );
        Long historyId = history.getId();

        // 구매요청
        return tradeService.addMarketBuyRequest(
                stockOrderRequest.toBuyTradeRequestEntity(historyId, history.getCreatedAt(), OrderType.MARKET)
        );

    }

    @Transactional
    public TradeResponse sellMarketStocks(StockOrderRequest stockOrderRequest) {
        validateStockOrderRequest(stockOrderRequest);

        Account account = accountRepository.findById(stockOrderRequest.accountId())
                .orElseThrow(() -> AccountNotFoundException.of(AccountErrorCode.NOT_FOUND));
        Company company = companyRepository.findById(stockOrderRequest.companyId())
                .orElseThrow(() -> CompanyNotFoundException.of(CompanyErrorCode.NOT_FOUND));

        // TODO: 주식 매도하려고 하는데 사용자가 해당 주식을 매도하려는만큼 가지고있는지 확인

        // History Table에 기록 Write
        History history = historyRepository.save(History.builder()
                .account(account)
                .price(stockOrderRequest.unitCost())
                .company(company)
                .volume(stockOrderRequest.volume())
                .status(HistoryStatus.시장가매도요청)
                .build()
        );

        Long historyId = history.getId();

        // 구매요청
        return tradeService.addMarketSellRequest(
                stockOrderRequest.toSellTradeRequestEntity(historyId, history.getCreatedAt(), OrderType.MARKET)
        );
    }

    @Transactional
    public CancelOrderResponse cancelStockOrder(CancelOrderRequest cancelOrderRequest) {
        // validate member
        Member member = validateMember(cancelOrderRequest.memberId());

        // validate account
        Account account = validateAccount(member, cancelOrderRequest.accountId(), cancelOrderRequest.competitionId());

        // validate history
        History history = validateHistory(account, cancelOrderRequest.historyId());

        CancelOrderResponse response = null;
        if(HistoryStatus.지정가매도요청.equals(history.getStatus()) ||
                HistoryStatus.시장가매도요청.equals(history.getStatus())) {
            SellTradeRequest sellTradeRequest = SellTradeRequest.builder()
                    .orderType(HistoryStatus.지정가매도요청.equals(history.getStatus())?
                            OrderType.LIMIT :
                            OrderType.MARKET)
                    .accountId(cancelOrderRequest.accountId())
                    .memberId(cancelOrderRequest.memberId())
                    .companyId(history.getCompany().getId())
                    .competitionId(account.getCompetition().getId())
                    .historyId(history.getId())
                    .companyName(history.getCompany().getName())
                    .unitCost(history.getPrice())
                    .totalOrderVolume(history.getVolume())
                    .orderTime(history.getCreatedAt())
                    .build();
            response = tradeService.cancelOrderRequest(sellTradeRequest);
        }
        if(HistoryStatus.지정가매수요청.equals(history.getStatus()) ||
                HistoryStatus.시장가매수요청.equals(history.getStatus())) {
            BuyTradeRequest buyTradeRequest = BuyTradeRequest.builder()
                    .orderType(HistoryStatus.지정가매수요청.equals(history.getStatus())?
                            OrderType.LIMIT :
                            OrderType.MARKET)
                    .accountId(cancelOrderRequest.accountId())
                    .memberId(cancelOrderRequest.memberId())
                    .companyId(history.getCompany().getId())
                    .competitionId(account.getCompetition().getId())
                    .historyId(history.getId())
                    .companyName(history.getCompany().getName())
                    .unitCost(history.getPrice())
                    .totalOrderVolume(history.getVolume())
                    .orderTime(history.getCreatedAt())
                    .build();
            response = tradeService.cancelOrderRequest(buyTradeRequest);
        }
        if(response == null) {
            throw new IllegalStateException("server error");
        }
        return response;
    }

    public void validateStockOrderRequest(StockOrderRequest stockOrderRequest) {
        // Member 유효성 체크
        Member member = validateMember(stockOrderRequest.memberId());

        // Account 유효성 체크
        Account account = validateAccount(member, stockOrderRequest.accountId(), stockOrderRequest.competitionId());

        // Company 유효성 체크
        Company company = companyRepository.findById(stockOrderRequest.companyId())
                .orElseThrow(() -> CompanyNotFoundException.of(CompanyErrorCode.NOT_FOUND));

        // Competition 유효성 체크
        Competition competition = competitionRepository.findById(stockOrderRequest.competitionId())
                .orElseThrow(() -> CompetitionNotFoundException.of(CompetitionErrorCode.NOT_FOUND));

        if(!account.getCompetition().getId().equals(stockOrderRequest.competitionId())) {
            throw AccountNotFoundException.of(AccountErrorCode.INVALID_VALUE);
        }
    }


    public AccountInfoForLogin getInfoForLogin(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));
        Account account = accountRepository.findTopByMemberOrderByIdDesc(member);

        if (account != null) {
            // 최신 계좌 조회해서 거기에 있는 CompetitonId가 현재의 대회 pk랑 같은지 체크
            Competition accountCompetition = account.getCompetition();
            Optional<Competition> competition = competitionRepository.findById(accountCompetition.getId());

            if (competition.isPresent() && competitionService.isActiveCompetition(competition.get())) { // 지금 열리고 있는 대회
                return AccountInfoForLogin.create(true, account.getId(), account.getBalance(), account.getRanking());
            }
        }

        return AccountInfoForLogin.create(false, null,0L, 0);
    }

    private Member validateMember(Long memberId) {
        Long loginMemberId = SecurityUtil.getCurrentMemberId();
        if(!loginMemberId.equals(memberId)) {
            throw MemberNotFoundException.of(MemberErrorCode.INVALID_VALUE);
        }
        return memberRepository.findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));
    }

    private Account validateAccount(Member member, Long accountId, Long competitionId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> AccountNotFoundException.of(AccountErrorCode.NOT_FOUND));
        if(account.getMember() != member) {
            throw AccountNotFoundException.of(AccountErrorCode.INVALID_VALUE);
        }
        if(!account.getCompetition().getId().equals(competitionId)) {
            throw AccountNotFoundException.of(AccountErrorCode.INVALID_VALUE);
        }
        return account;
    }

    private History validateHistory(Account account, Long historyId) {
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> HistoryNotFoundException.of(HistoryErrorCode.NOT_FOUND));
        if(!history.getAccount().equals(account)) {
            throw AccountNotFoundException.of(AccountErrorCode.INVALID_VALUE);
        }

        final HistoryStatus[] validStatus = new HistoryStatus[]{
                HistoryStatus.지정가매수요청,
                HistoryStatus.지정가매도요청,
                HistoryStatus.시장가매수요청,
                HistoryStatus.시장가매도요청
        };
        if(!Arrays.asList(validStatus).contains(history.getStatus())) {
            throw HistoryNotFoundException.of(HistoryErrorCode.INVALID_VALUE);
        }
        return history;
    }

    public UnexecutionContentResponse getUnexecutionContent(Long accountId) {
        Map<String,Map<String,String>> Unexcuted = redisService.getUnexcutionContent(accountId);
        List<UnexcutedStockInfo> unexcutedStockInfos = new ArrayList<>();

        for (Map.Entry<String, Map<String, String>> entry : Unexcuted.entrySet()) {
            Map<String, String> stockData = entry.getValue();

            unexcutedStockInfos.add(new UnexcutedStockInfo(Long.valueOf("companyId"),
                    Integer.valueOf(stockData.get("price")),
                    Integer.valueOf(stockData.get("volume")),
                    TradeType.valueOf(stockData.get("tradeType")))
            );
        }
        return new UnexecutionContentResponse(unexcutedStockInfos);
    }
}

