package com.book.gpt.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.book.gpt.dto.BookDTO;


public class BookRowMapper implements RowMapper<BookDTO> {
    // RowMapper 인터페이스는 JDBC 쿼리의 결과로 반환된 ResultSet의 각 행을 원하는 타입의 객체로 매핑하는 역할을 수행
    // 아래 코드에서는 BookDTO 타입의 객체로 변환
    @Override
    public BookDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(rs.getInt("id"));
        bookDTO.setTitle(rs.getString("title"));
        bookDTO.setAuthor(rs.getString("author"));
        bookDTO.setContentUrl(rs.getString("content_url"));
        return bookDTO;
    }
}
