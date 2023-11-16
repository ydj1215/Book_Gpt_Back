package com.book.gpt.dao;

import com.book.gpt.dto.BookDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class BookDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<BookDTO> findAllBooks() {
        String sql = "SELECT * FROM BOOK ORDER BY ID ASC";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) ->
                    new BookDTO(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("publisher"),
                            rs.getString("genre"),
                            rs.getString("image_url"),
                            rs.getString("content_url"),
                            rs.getString("summary"),
                            rs.getInt("price"),
                            rs.getDate("publish_year"),
                            rs.getDate("entry_time"),
                            rs.getInt("purchase_count")
                    ));
        } catch (DataAccessException e) {
            throw new RuntimeException("책 정보를 조회하는 데 실패했습니다.", e);
        }
    }

    // ID를 기준으로 특정 책에 대한 데이터를 쿼리 2023/11/07 정벼리
    public BookDTO findBookById(int id) {
        String sql = "SELECT * FROM BOOK WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->
                    new BookDTO(
                            rs.getInt("id"), // ID 필드 추가
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("publisher"),
                            rs.getString("genre"),
                            rs.getString("image_url"),
                            rs.getString("content_url"),
                            rs.getString("summary"),
                            rs.getInt("price"),
                            rs.getDate("publish_year"),
                            rs.getDate("entry_time"),
                            rs.getInt("purchase_count")
                    ));
        } catch (DataAccessException e) {
            throw new RuntimeException("ID에 해당하는 책 정보를 조회하는 데 실패했습니다.", e);
        }
    }

    // 새로운 책 정보를 데이터베이스에 저장하는 메소드
    public BookDTO save(BookDTO book) {
        String sql = "INSERT INTO BOOK (title, author, publisher, genre, image_url, content_url, summary, price, publish_year, entry_time, purchase_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try{
            jdbcTemplate.update(sql, book.getTitle(), book.getAuthor(), book.getPublisher(), book.getGenre(), book.getImageUrl(), book.getContentUrl(), book.getSummary(), book.getPrice(), book.getPublishYear(), book.getEntryTime(), book.getPurchaseCount());
            return book;
        } catch (DataAccessException e) {
            throw new RuntimeException("책을 저장하는데 실패했습니다.", e);
        }
        // 이 예제에서는 간단하게 처리하기 위해 저장 후의 책 정보를 반환하지 않았습니다.
        // 실제로는 저장 후의 책 정보를 데이터베이스에서 조회하여 반환하는 것이 좋습니다.
    }

    // 책 정보를 데이터베이스에서 삭제하는 메소드
    public void deleteById(int id) {
        String sql = "DELETE FROM BOOK WHERE id = ?";
        try {
            jdbcTemplate.update(sql, id);
        } catch (DataAccessException e) {
            throw new RuntimeException("책을 삭제하는 데 실패했습니다.", e);
        }
    }

    public Optional<BookDTO> findBook(int id) {
        String sql = "SELECT * FROM book WHERE id = ?";
        try {
            BookDTO book = jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<>(BookDTO.class));
            return Optional.ofNullable(book);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<BookDTO> updateBook(int id, BookDTO bookDTO) {
        String sql = "UPDATE book SET title = ?, author = ?, publisher = ?, genre = ?, image_url = ?, content_url = ?, summary = ?, price = ?, publish_year = ? WHERE id = ?";
        jdbcTemplate.update(sql, bookDTO.getTitle(), bookDTO.getAuthor(), bookDTO.getPublisher(), bookDTO.getGenre(), bookDTO.getImageUrl(), bookDTO.getContentUrl(), bookDTO.getSummary(),
                bookDTO.getPrice(), bookDTO.getPublishYear(), id);
        return findBook(id);
    }
    // 길종환
    @Autowired
    public BookDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public BookDTO getBookInfo(int bookId) {
        String sql = "SELECT * FROM Book WHERE ID = ?";

        return jdbcTemplate.queryForObject(sql, new RowMapper<BookDTO>() {
            @Override
            public BookDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new BookDTO(
                        rs.getInt("ID"),
                        rs.getString("TITLE"),
                        rs.getString("AUTHOR"),
                        rs.getString("PUBLISHER"),
                        rs.getString("GENRE"),
                        rs.getString("IMAGE_URL"),
                        rs.getString("CONTENT_URL"),
                        rs.getString("SUMMARY"),
                        rs.getInt("PRICE"),
                        rs.getDate("PUBLISH_YEAR"),
                        rs.getDate("ENTRY_TIME"),
                        rs.getInt("PURCHASE_COUNT")
                );
            }
        }, bookId);
    }

    public Boolean checkPurchase(String memberId, int bookId) {
        String sql = "SELECT COUNT(*) FROM BUY WHERE MEMBER_ID = ? AND BOOK_ID = ?";

        int count = jdbcTemplate.queryForObject(sql, new Object[]{memberId, bookId}, Integer.class);

        return count > 0;
    }

    public Boolean purchaseBook(String memberId, int bookId, int price) {
        try {
            String checkCashSql = "SELECT cash FROM MEMBER WHERE ID = ?";
            int cash = jdbcTemplate.queryForObject(checkCashSql, new Object[]{memberId}, Integer.class);

            if (cash >= price) {
                String updateCashSql = "UPDATE MEMBER SET cash = cash - ? WHERE ID = ?";
                jdbcTemplate.update(updateCashSql, price, memberId);

                String insertPurchaseSql = "INSERT INTO BUY (MEMBER_ID, BOOK_ID) VALUES (?, ?)";
                jdbcTemplate.update(insertPurchaseSql, memberId, bookId);

                // 책의 PURCHASE_COUNT를 증가
                String updatePurchaseCountSql = "UPDATE BOOK SET PURCHASE_COUNT = PURCHASE_COUNT + 1 WHERE ID = ?";
                jdbcTemplate.update(updatePurchaseCountSql, bookId);

                System.out.println(cash);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @Transactional
    public Boolean purchaseBooks(String memberId, List<Integer> bookIds) {
        try {
            String checkCashSql = "SELECT cash FROM MEMBER WHERE ID = ?";
            int cash = jdbcTemplate.queryForObject(checkCashSql, new Object[]{memberId}, Integer.class);

            int totalCost = 0;
            for (int bookId : bookIds) {
                String bookPriceSql = "SELECT price FROM Book WHERE ID = ?";
                int bookPrice = jdbcTemplate.queryForObject(bookPriceSql, new Object[]{bookId}, Integer.class);
                totalCost += bookPrice;
                String removeCartSql = "DELETE FROM CART WHERE MEMBER_ID = ? AND BOOK_ID = ?";
                jdbcTemplate.update(removeCartSql, memberId, bookId);
            }

            if (cash >= totalCost) {
                for (int bookId : bookIds) {
                    String bookPriceSql = "SELECT price FROM Book WHERE ID = ?";
                    int bookPrice = jdbcTemplate.queryForObject(bookPriceSql, new Object[]{bookId}, Integer.class);

                    String updateCashSql = "UPDATE MEMBER SET cash = cash - ? WHERE ID = ?";
                    jdbcTemplate.update(updateCashSql, bookPrice, memberId);

                    String insertPurchaseSql = "INSERT INTO BUY (MEMBER_ID, BOOK_ID) VALUES (?, ?)";
                    jdbcTemplate.update(insertPurchaseSql, memberId, bookId);

                    // 책의 PURCHASE_COUNT를 증가
                    String updatePurchaseCountSql = "UPDATE BOOK SET PURCHASE_COUNT = PURCHASE_COUNT + 1 WHERE ID = ?";
                    jdbcTemplate.update(updatePurchaseCountSql, bookId);
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    public BookDTO findByTitleAndAuthor(String title, String author) {
        String query = "SELECT * FROM book WHERE title = ? AND author = ?";
        return jdbcTemplate.queryForObject(query, new Object[]{title, author}, new BookRowMapper());
    }

    public boolean isBookBoughtByUser(String memberId, int bookId) {
        String sql = "SELECT COUNT(*) FROM buy WHERE member_id = ? AND book_id = ?";
        // int는 null 값 처리 불가, integer은 null값을 가질 수 있다.
        // sql, ? 값들을 배열로 반환, 반환할 객체의 타입을 integer로 지정
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{memberId, bookId}, Integer.class);
        return (count != null && count > 0);
    }

}
