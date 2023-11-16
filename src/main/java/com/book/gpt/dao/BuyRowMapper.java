package com.book.gpt.dao;

import com.book.gpt.dto.BuyDTO;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BuyRowMapper implements RowMapper<BuyDTO> {
    @Override
    public BuyDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        BuyDTO buy = new BuyDTO();
        buy.setBuyId(rs.getInt("buy_id"));
        buy.setMemberId(rs.getString("member_id"));
        buy.setBookId(rs.getInt("book_id"));
        return buy;
    }
}
