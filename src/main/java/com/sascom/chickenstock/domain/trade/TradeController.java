package com.sascom.chickenstock.domain.trade;

import com.sascom.chickenstock.domain.trade.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

//@RestController
//@RequestMapping("/trade")
//public class TradeController {
//
//    private final TradeService tradeService;
//
//    @Autowired
//    public TradeController(TradeService tradeService) {
//        this.tradeService = tradeService;
//    }
//
//    @PostMapping("/buy/{company}")
//    public String buyStock(@PathVariable String company, @RequestBody Map<String, Double> price) {
//        tradeService.addBuyRequest(company, new TradeRequest("buy", price.get("price"), 1));
//        return "Buy request added to " + company + " queue";
//    }
//
//    @PostMapping("/sell/{company}")
//    public String sellStock(@PathVariable String company, @RequestBody Map<String, Double> price) {
//        tradeService.addSellRequest(company, new TradeRequest("sell", price.get("price"), 1));
//        return "Sell request added to " + company + " queue";
//    }
//
//    @GetMapping("/process/buy/{company}")
//    public String processBuyRequest(@PathVariable String company) {
//        TradeRequest request = tradeService.processBuyRequest(company);
//        if (request != null) {
//            return "Processed buy request: " + request;
//        } else {
//            return "No buy requests to process for " + company;
//        }
//    }
//
//    @GetMapping("/process/sell/{company}")
//    public String processSellRequest(@PathVariable String company) {
//        TradeRequest request = tradeService.processSellRequest(company);
//        if (request != null) {
//            return "Processed sell request: " + request;
//        } else {
//            return "No sell requests to process for " + company;
//        }
//    }
//
//    @GetMapping("/{company}/check")
//    public String check(@PathVariable String company) {
//        return tradeService.matchTrades(company);
//    }
//}
