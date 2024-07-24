package com.sascom.chickenstock.domain.member.entity;


import com.sascom.chickenstock.domain.account.entity.Account;
import com.sascom.chickenstock.domain.companylike.entity.CompanyLike;
import com.sascom.chickenstock.domain.memberitem.entity.MemberItem;
import com.sascom.chickenstock.domain.rival.entity.Rival;
import com.sascom.chickenstock.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @NotNull
    @Column(name = "nickname", unique = true)
    private String nickname;

    @NotNull
    @Column(name = "email", unique = true)
    private String email;

    @NotNull
    @Column(name = "password")
    private String password;

    @ColumnDefault("0")
    @Column(name = "point")
    private Integer point;

    @OneToMany(mappedBy = "member")
    private List<Account> accounts;

    @OneToMany(mappedBy = "member")
    private List<Rival> rivals;

    @OneToMany(mappedBy = "enemy")
    private List<Rival> enemies;

    @OneToMany(mappedBy = "member")
    private List<MemberItem> inventory;

    @OneToMany(mappedBy = "member")
    private List<CompanyLike> companyLikes;

    public Member(String nickName, String email, String password, List<Account> accounts, List<Rival> rivals, List<Rival> enemies, List<MemberItem> inventory, List<CompanyLike> companyLikes) {
        this.nickname = nickName;
        this.email = email;
        this.password = password;
        this.accounts = accounts;
        this.rivals = rivals;
        this.enemies = enemies;
        this.inventory = inventory;
        this.companyLikes = companyLikes;
    }

    public int updatePoint(int newPoint) {
        if (newPoint < 0) {
            throw new IllegalArgumentException("잘못된 포인트 업데이트~");
        }
        return this.point = newPoint;
    }

    public String updatePassword(String password) {
        return this.password = password;
    }
}
