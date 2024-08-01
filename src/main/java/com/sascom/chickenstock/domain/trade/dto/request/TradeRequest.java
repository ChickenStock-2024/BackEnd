package com.sascom.chickenstock.domain.trade.dto.request;

import com.sascom.chickenstock.domain.trade.dto.OrderType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public abstract class TradeRequest {
    private OrderType orderType;
    private Long accountId;
    private Long memberId;
    private Long companyId;
    private Long competitionId;
    private String companyName;
    private Integer unitCost;
    @Setter
    private Integer amount;
    private LocalDateTime orderTime;

    /**
     * compareByOrderTime - 시간우선의 원칙
     * @param other - TradeRequest that you want to compare orderTime with this.
     * @return 0 if orderTime of this is  equal   to  that of other.
     *         - if orderTime of this is earlier than that of other.
     *         + if orderTime of this is  later  than that of other.
     */
    public final int compareByOrderTime(TradeRequest other) {
        // ?? other.orderTime해도 compile error가 뜨지 않아요... 왜그럴까유?
        return this.orderTime.compareTo(other.getOrderTime());
    }
    /**
     * compareByOrderTime - 수량우선의 원칙
     * @param other - TradeRequest that you want to compare Amount with this.
     * @return 0 if amount of this is  equal   to  that of other.
     *         - if amount of this is greater than that of other.
     *         + if amount of this is   less  than that of other.
     */
    public final int compareByAmount(TradeRequest other) {
        return -this.amount.compareTo(other.getAmount());
    }
    public final int compareByOrderTimeAndAmount(TradeRequest other) {
        if (compareByOrderTime(other) != 0) {
            return compareByOrderTime(other);
        }
        return compareByAmount(other);
    }
}