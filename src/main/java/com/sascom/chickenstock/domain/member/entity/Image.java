package com.sascom.chickenstock.domain.member.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class Image {
    public String img_link;
    public String img_name;
    public String img_path;

    public Image(String img_link, String img_name, String img_path){
        this.img_link = img_link;
        this.img_name = img_name;
        this.img_path = img_path;
    }
}
