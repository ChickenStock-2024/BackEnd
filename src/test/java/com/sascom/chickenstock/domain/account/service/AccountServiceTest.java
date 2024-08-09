package com.sascom.chickenstock.domain.account.service;

import com.nimbusds.jose.proc.SecurityContext;
import com.sascom.chickenstock.domain.account.dto.request.StockOrderRequest;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.global.oauth.dto.MemberPrincipalDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootTest
@Disabled
@Transactional
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @BeforeEach
    void init() {
        Member member = Member.of(1L, "test-user");
        MemberPrincipalDetails details = new MemberPrincipalDetails(member, null, null);
        Authentication testAuthentication = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(testAuthentication);
    }

    @Test
    void 시장가_동시_요청_테스트() throws InterruptedException {
//        final int EXECUTE_VOLUME = 50;
//        List<Integer> sellList = new ArrayList<>(), buyList = new ArrayList<>();
//        int sellCount = 0, buyCount = 0;
//        while(sellCount < EXECUTE_VOLUME) {
//            sellList.add(Math.min(EXECUTE_VOLUME - sellCount, (int)(Math.random() * 5) + 1));
//            sellCount += sellList.get(sellList.size() - 1);
//        }
//        while(buyCount < EXECUTE_VOLUME) {
//            buyList.add(Math.min(EXECUTE_VOLUME - buyCount, (int)(Math.random() * 5) + 1));
//            buyCount += buyList.get(buyList.size() - 1);
//        }
//        System.out.println(sellCount);
//        for(int a : sellList)
//            System.out.print(a + " ");
//        System.out.println();
//        System.out.println(buyCount);
//        for(int a : buyList)
//            System.out.print(a + " ");
//        System.out.println();
//        int reqCount = sellList.size() + buyList.size();

        ExecutorService executorService = Executors.newFixedThreadPool(325);
        CountDownLatch latch = new CountDownLatch(325);
//        for(int i = 0; i < 125; i++){
//            executorService.execute(() -> {
//                Member member = Member.of(1L, "test-user");
//                MemberPrincipalDetails details = new MemberPrincipalDetails(member, null, null);
//                Authentication testAuthentication = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
//                SecurityContextHolder.getContext().setAuthentication(testAuthentication);
//                accountService.sellMarketStocks(
//                        new StockOrderRequest(
//                                1L,
//                                1L,
//                                11L,
//                                2L,
//                                "삼성전자",
//                                0,
//                                8
//                        )
//                );
//                latch.countDown();
//            });
//        }
//        for(int i = 0; i < 200; i++){
//            executorService.execute(() -> {
//                Member member = Member.of(1L, "test-user");
//                MemberPrincipalDetails details = new MemberPrincipalDetails(member, null, null);
//                Authentication testAuthentication = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
//                SecurityContextHolder.getContext().setAuthentication(testAuthentication);
//                accountService.buyMarketStocks(
//                        new StockOrderRequest(
//                                1L,
//                                1L,
//                                11L,
//                                2L,
//                                "삼성전자",
//                                0,
//                                5
//                        )
//                );
//                latch.countDown();
//            });
//        }
        for(int i = 0; i < 125; i++){
            executorService.execute(() -> {
                Member member = Member.of(1L, "test-user");
                MemberPrincipalDetails details = new MemberPrincipalDetails(member, null, null);
                Authentication testAuthentication = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(testAuthentication);
                accountService.sellMarketStocks(
                        new StockOrderRequest(
                                1L,
                                1L,
                                11L,
                                2L,
                                "삼성전자",
                                0,
                                8
                        )
                );
                latch.countDown();
            });
            executorService.execute(() -> {
                Member member = Member.of(1L, "test-user");
                MemberPrincipalDetails details = new MemberPrincipalDetails(member, null, null);
                Authentication testAuthentication = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(testAuthentication);
                accountService.buyMarketStocks(
                        new StockOrderRequest(
                                1L,
                                1L,
                                11L,
                                2L,
                                "삼성전자",
                                0,
                                5
                        )
                );
                latch.countDown();
            });
        }
        for(int i = 0; i < 75; i++){
            executorService.execute(() -> {
                Member member = Member.of(1L, "test-user");
                MemberPrincipalDetails details = new MemberPrincipalDetails(member, null, null);
                Authentication testAuthentication = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(testAuthentication);
                accountService.buyMarketStocks(
                        new StockOrderRequest(
                                1L,
                                1L,
                                11L,
                                2L,
                                "삼성전자",
                                0,
                                5
                        )
                );
                latch.countDown();
            });
        }
        latch.await();
    }

}