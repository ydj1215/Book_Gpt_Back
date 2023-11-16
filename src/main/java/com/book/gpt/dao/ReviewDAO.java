package com.book.gpt.dao;

import com.book.gpt.dto.ReviewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ReviewDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public ReviewDTO addReview(ReviewDTO review) {
        String sql = "INSERT INTO REVIEW (MEMBER_ID, BOOK_ID, RATING, CONTENT, CREATION_DATE) VALUES (?, ?, ?, ?, ?)";
        int rowsAffected = jdbcTemplate.update(sql, review.getMemberId(), review.getBookId(), review.getRating(), review.getContent(), new java.sql.Timestamp(System.currentTimeMillis()));
        if (rowsAffected > 0) {
            return review;
        } else {
            return null;
        }
    }

    public List<ReviewDTO> getReviews(int bookId) {
        String sql = "SELECT R.*, M.ID as MEMBER_ID, M.NAME as MEMBER_NAME FROM REVIEW R INNER JOIN MEMBER M ON R.MEMBER_ID = M.ID WHERE R.BOOK_ID = ? ORDER BY CREATION_DATE DESC";
        return jdbcTemplate.query(sql, new Object[]{bookId}, (rs, rowNum) -> {
            ReviewDTO review = new ReviewDTO();
            review.setMemberId(rs.getString("MEMBER_ID"));
            review.setMemberName(rs.getString("MEMBER_NAME"));
            review.setBookId(rs.getInt("BOOK_ID"));
            review.setRating(rs.getDouble("RATING"));
            review.setContent(rs.getString("CONTENT"));
            review.setCreationDate(rs.getDate("CREATION_DATE"));
            return review;
        });
    }

    public ReviewDTO getReviewStats(int bookId) {
        String sql = "SELECT AVG(rating) AS average_rating, COUNT(*) AS total_reviews FROM REVIEW WHERE book_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{bookId}, (rs, rowNum) -> {
                ReviewDTO reviewStats = new ReviewDTO();
                reviewStats.setAverageRating(rs.getDouble("average_rating"));
                reviewStats.setTotalReviews(rs.getInt("total_reviews"));
                return reviewStats;
            });
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }
}
