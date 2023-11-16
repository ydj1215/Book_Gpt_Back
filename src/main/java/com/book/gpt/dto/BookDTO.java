package com.book.gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor // 기본 생성자는 제외
@NoArgsConstructor // 기본 생성자
public class BookDTO {
    private int id;
    private String title;
    private String author;
    private String publisher;
    private String genre;
    private String imageUrl;
    private String contentUrl;
    private String summary;
    private int price;
    private Date publishYear;
    private Date entryTime;
    private int purchaseCount;
}
