package com.sascom.chickenstock.domain.item.entity;


import com.sascom.chickenstock.domain.memberitem.entity.MemberItem;
import com.sascom.chickenstock.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "item")
public class Item extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "price")
    private Integer price;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "item")
    private List<MemberItem> inventory;

    public Item(String title, Integer price, String description, List<MemberItem> inventory) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.inventory = inventory;
    }
}

