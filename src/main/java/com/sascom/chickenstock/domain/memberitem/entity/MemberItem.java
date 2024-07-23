package com.sascom.chickenstock.domain.memberitem.entity;


import com.sascom.chickenstock.domain.item.entity.Item;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "memberitem")
public class MemberItem extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberitem_id")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @NotNull
    @Column(name = "quantity")
    private Integer quantity;

    public MemberItem(Member member, Item item, Integer quantity) {
        this.member = member;
        this.item = item;
        updateQuantity(quantity);
    }

    public int updateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("잘못된 수량~");
        }
        return this.quantity = quantity;
    }
}

