package com.sascom.chickenstock.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@NoArgsConstructor
@Embeddable
public class Image {
    private String img_link;
    private String img_name;
    private String img_path;

    public Image(String img_link, String img_name, String img_path){
        this.img_link = img_link;
        this.img_name = img_name;
        this.img_path = img_path;
    }

}
