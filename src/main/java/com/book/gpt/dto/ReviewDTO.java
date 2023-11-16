package com.book.gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private int reviewId;
    private String memberId;
    private int bookId;
    private double rating;
    private String content;
    private Date creationDate;
    private String memberName;

    private double averageRating; // 평균 평점
    private int totalReviews; // 리뷰의 총 개수
}