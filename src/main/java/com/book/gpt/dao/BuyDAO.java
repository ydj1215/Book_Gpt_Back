package com.book.gpt.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.book.gpt.dto.BuyDTO;

import java.util.List;


@Repository
public class BuyDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<BuyDTO> getBoughtBooks(String memberId) {
        String sql = "SELECT * FROM buy WHERE member_id = ? ORDER BY buy_id ASC";
        return jdbcTemplate.query(sql, new Object[]{memberId}, new BuyRowMapper());
    }

    public void deleteBook(int id) {
        String sql = "DELETE FROM buy WHERE buy_id = ?";
        jdbcTemplate.update(sql, id);
    }
}
