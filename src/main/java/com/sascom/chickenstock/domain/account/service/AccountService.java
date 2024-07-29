package com.sascom.chickenstock.domain.account.service;

import com.sascom.chickenstock.domain.account.dto.response.AccountInfoResponse;
import com.sascom.chickenstock.domain.account.dto.response.ExecutionContentResponse;
import com.sascom.chickenstock.domain.account.dto.response.HistoryInfo;
import com.sascom.chickenstock.domain.account.dto.response.StockInfo;
import com.sascom.chickenstock.domain.account.entity.Account;
import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.competition.entity.Competition;
import com.sascom.chickenstock.domain.competition.repository.CompetitionRepository;
import com.sascom.chickenstock.domain.history.entity.History;
import com.sascom.chickenstock.domain.history.repository.HistoryRepository;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.lang.model.SourceVersion;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final HistoryRepository historyRepository;
    private final MemberRepository memberRepository;
    private final CompetitionRepository competitionRepository;
    private final RedisService redisService;

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

    public List<HistoryInfo> getExecutionContent(Long accountId){
        List<History> histories = historyRepository.findExecutionContent(accountId);
        List<HistoryInfo> result = histories.stream()
                .map(h -> new HistoryInfo(h.getCompany().getName(),
                        h.getPrice(),
                        h.getVolume(),
                        h.getStatus(),
                        h.getCreatedAt()
                        ))
                .collect(Collectors.toList());
        return result;
    }
}
