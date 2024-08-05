package com.sascom.chickenstock.domain.account.service;

import com.sascom.chickenstock.domain.account.dto.request.StockOrderRequest;
import com.sascom.chickenstock.domain.account.dto.response.AccountInfoResponse;
import com.sascom.chickenstock.domain.account.dto.response.ExecutionContentResponse;
import com.sascom.chickenstock.domain.account.dto.response.HistoryInfo;
import com.sascom.chickenstock.domain.account.dto.response.StockInfo;
import com.sascom.chickenstock.domain.account.entity.Account;
import com.sascom.chickenstock.domain.account.error.code.AccountErrorCode;
import com.sascom.chickenstock.domain.account.error.exception.AccountNotEnoughException;
import com.sascom.chickenstock.domain.account.error.exception.AccountNotFoundException;
import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.company.entity.Company;
import com.sascom.chickenstock.domain.company.error.code.CompanyErrorCode;
import com.sascom.chickenstock.domain.company.error.exception.CompanyNotFoundException;
import com.sascom.chickenstock.domain.company.repository.CompanyRepository;
import com.sascom.chickenstock.domain.competition.entity.Competition;
import com.sascom.chickenstock.domain.competition.error.code.CompetitionErrorCode;
import com.sascom.chickenstock.domain.competition.error.exception.CompetitionNotFoundException;
import com.sascom.chickenstock.domain.competition.repository.CompetitionRepository;
import com.sascom.chickenstock.domain.history.entity.History;
import com.sascom.chickenstock.domain.history.entity.HistoryStatus;
import com.sascom.chickenstock.domain.history.repository.HistoryRepository;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.error.code.MemberErrorCode;
import com.sascom.chickenstock.domain.member.error.exception.MemberNotFoundException;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import com.sascom.chickenstock.domain.trade.dto.OrderType;
import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.response.TradeResponse;
import com.sascom.chickenstock.domain.trade.service.TradeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final HistoryRepository historyRepository;
    private final MemberRepository memberRepository;
    private final CompetitionRepository competitionRepository;
    private final CompanyRepository companyRepository;
    private final RedisService redisService;
    private final TradeService tradeService;

    @Transactional
    public Long createAccount(Long memberId, Long competitionId) {

        // TODO: 커스텀 에러로 수정 필요
        Member member = memberRepository.findById(memberId)
                .orElseThrow(EntityNotFoundException::new);
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(EntityNotFoundException::new);
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

            stocks.add(new StockInfo(st.nextToken(),
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
        Company company = companyRepository.findById(stockOrderRequest.companyId())
                .orElseThrow(() -> CompanyNotFoundException.of(CompanyErrorCode.NOT_FOUND));

        // 주식 매수하려고 하는데 계좌에 구매가능 잔고 있는지 확인
        if (account.getBalance() < (long) stockOrderRequest.amount() * stockOrderRequest.unitCost()) {
            throw AccountNotEnoughException.of(AccountErrorCode.NOT_ENOUGH_BALANCE);
        }

        // History Table에 기록 Write
        History history = historyRepository.save(History.builder()
                .account(account)
                .price(stockOrderRequest.unitCost())
                .company(company)
                .volume(stockOrderRequest.amount())
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

        // 주식 매도하려고 하는데 사용자가 해당 주식을 매도하려는만큼 가지고있는지 확인

        // History Table에 기록 Write
        History history = historyRepository.save(History.builder()
                .account(account)
                .price(stockOrderRequest.unitCost())
                .company(company)
                .volume(stockOrderRequest.amount())
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

        // 주식 매수하려고 하는데 계좌에 구매가능 잔고 있는지 확인
        if (account.getBalance() < (long) stockOrderRequest.amount() * stockOrderRequest.unitCost()) {
            throw AccountNotEnoughException.of(AccountErrorCode.NOT_ENOUGH_BALANCE);
        }

        // History Table에 기록 Write
        History history = historyRepository.save(History.builder()
                .account(account)
                .price(stockOrderRequest.unitCost())
                .company(company)
                .volume(stockOrderRequest.amount())
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

        // 주식 매도하려고 하는데 사용자가 해당 주식을 매도하려는만큼 가지고있는지 확인

        // History Table에 기록 Write
        History history = historyRepository.save(History.builder()
                .account(account)
                .price(stockOrderRequest.unitCost())
                .company(company)
                .volume(stockOrderRequest.amount())
                .status(HistoryStatus.시장가매도요청)
                .build()
        );

        Long historyId = history.getId();

        // 구매요청
        return tradeService.addMarketSellRequest(
                stockOrderRequest.toSellTradeRequestEntity(historyId, history.getCreatedAt(), OrderType.MARKET)
        );
    }

    public void validateStockOrderRequest(StockOrderRequest stockOrderRequest) {
        // Member 유효성 체크
        Member member = memberRepository.findById(stockOrderRequest.memberId())
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));

        // Account 유효성 체크
        Account account = accountRepository.findById(stockOrderRequest.accountId())
                .orElseThrow(() -> AccountNotFoundException.of(AccountErrorCode.NOT_FOUND));

        // Company 유효성 체크
        Company company = companyRepository.findById(stockOrderRequest.companyId())
                .orElseThrow(() -> CompanyNotFoundException.of(CompanyErrorCode.NOT_FOUND));

        // Competition 유효성 체크
        Competition competition = competitionRepository.findById(stockOrderRequest.competitionId())
                .orElseThrow(() -> CompetitionNotFoundException.of(CompetitionErrorCode.NOT_FOUND));
    }
}

