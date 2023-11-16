package com.book.gpt.dao;

import com.book.gpt.dto.CartDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CartDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CartDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 장바구니 아이템 추가
    public void addCart(CartDTO cart) {
        String sql = "INSERT INTO Cart (MEMBER_ID, BOOK_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, cart.getMemberId(), cart.getBookId());
    }

    // 장바구니 아이템 가져오기
    public List<CartDTO> getCartItems(String memberId) {
        String sql = "SELECT * FROM Cart WHERE MEMBER_ID = ?";
        return jdbcTemplate.query(sql, new Object[]{memberId}, (rs, rowNum) -> {
            CartDTO cart = new CartDTO();
            cart.setCartId(rs.getInt("CART_ID"));
            cart.setMemberId(rs.getString("MEMBER_ID"));
            cart.setBookId(rs.getInt("BOOK_ID"));
            return cart;
        });
    }

    // 장바구니 아이템 제거
    public void removeFromCart(String memberId, int bookId) {
        String sql = "DELETE FROM Cart WHERE MEMBER_ID = ? AND BOOK_ID = ?";
        jdbcTemplate.update(sql, memberId, bookId);
    }
}
