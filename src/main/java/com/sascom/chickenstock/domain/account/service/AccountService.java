package com.sascom.chickenstock.domain.account.service;

import com.sascom.chickenstock.domain.account.dto.request.BuyStockRequest;
import com.sascom.chickenstock.domain.account.dto.response.AccountInfoResponse;
import com.sascom.chickenstock.domain.account.dto.response.StockInfo;
import com.sascom.chickenstock.domain.account.entity.Account;
import com.sascom.chickenstock.domain.account.error.code.AccountErrorCode;
import com.sascom.chickenstock.domain.account.error.exception.AccountNotFoundException;
import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.company.entity.Company;
import com.sascom.chickenstock.domain.company.error.code.CompanyErrorCode;
import com.sascom.chickenstock.domain.company.error.exception.CompanyNotFoundException;
import com.sascom.chickenstock.domain.company.repository.CompanyRepository;
import com.sascom.chickenstock.domain.competition.entity.Competition;
import com.sascom.chickenstock.domain.competition.repository.CompetitionRepository;
import com.sascom.chickenstock.domain.history.entity.History;
import com.sascom.chickenstock.domain.history.entity.HistoryStatus;
import com.sascom.chickenstock.domain.history.repository.HistoryRepository;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.error.code.MemberErrorCode;
import com.sascom.chickenstock.domain.member.error.exception.MemberNotFoundException;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.service.TradeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.lang.model.SourceVersion;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final CompetitionRepository competitionRepository;
    private final CompanyRepository companyRepository;
    private final HistoryRepository historyRepository;
    private final RedisService redisService;
    private final TradeService tradeService;

    public Long createAccount(Long memberId, Long competitionId){

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


    public AccountInfoResponse getAccountInfo(Long accountId){
        Account account = accountRepository.findById(accountId).
                orElseThrow(EntityNotFoundException::new);

        List<StockInfo> stocks = new ArrayList<>();
        // 사용자 주식 정보 조회
        Map<String, Map<String, String>> allStockInfo = redisService.getStockInfo(accountId);
        for (Map.Entry<String, Map<String, String>> entry : allStockInfo.entrySet()) {
            String key = entry.getKey();
            Map<String, String> stockData = entry.getValue();
            StringTokenizer st = new StringTokenizer(key,":");
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

    public void buyStocks(BuyStockRequest buyStockRequest) {

        // Member 유효성 체크
        Member member = memberRepository.findById(buyStockRequest.memberId())
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));

        // Account 유효성 체크
        Account account = accountRepository.findById(buyStockRequest.accountId())
                .orElseThrow(() -> AccountNotFoundException.of(AccountErrorCode.NOT_FOUND));

        // 계좌에 구매가능 잔고 있는지 확인
        if(account.getBalance() < (long) buyStockRequest.amount() * buyStockRequest.unitCost()) {
            throw new EntityNotFoundException();
        }

        // Company 유효성 체크
        Company company = companyRepository.findById(buyStockRequest.companyId())
                .orElseThrow(() -> CompanyNotFoundException.of(CompanyErrorCode.NOT_FOUND));

        // Competition 유효성 체크
        Competition competition = competitionRepository.findById(buyStockRequest.companyId())
                .orElseThrow();

        // 계좌에서 해당 금액만큼 임시구매처리 (미완)

        // History Table에 기록 Write
        historyRepository.save(History.builder()
                .account(account)
                .price(buyStockRequest.unitCost())
                .company(company)
                .volume(buyStockRequest.amount())
                .status(HistoryStatus.매수요청)
                .build());

        // 구매요청
        tradeService.addBuyRequest(
                BuyTradeRequest.builder()
                        .accountId(buyStockRequest.accountId())
                        .memberId(buyStockRequest.memberId())
                        .companyId(buyStockRequest.companyId())
                        .competitionId(buyStockRequest.competitionId())
                        .unitCost(buyStockRequest.unitCost())
                        .amount(buyStockRequest.amount())
                        .orderTime(buyStockRequest.orderTime())
                        .build());

    }

    public void sellStocks(BuyStockRequest sellStockRequest) {
    }
}
