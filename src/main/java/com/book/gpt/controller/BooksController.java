package com.book.gpt.controller;

import com.book.gpt.dao.BookDAO;
import com.book.gpt.dto.BookDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin("*")
@RestController
@RequestMapping("/books")
public class BooksController {
    @Autowired
    private BookDAO bookDAO;

    // 책의 상세 정보 가져오기
    @GetMapping("/{bookId}")
    public ResponseEntity<BookDTO> getBookInfo(@PathVariable int bookId) {
        BookDTO bookInfo = bookDAO.getBookInfo(bookId);
        return ResponseEntity.ok(bookInfo);
    }
    // 구매 여부 확인
    @GetMapping("/{memberId}/{bookId}")
    public ResponseEntity<Boolean> checkPurchase(@PathVariable String memberId, @PathVariable int bookId) {
        Boolean isPurchased = bookDAO.checkPurchase(memberId, bookId);
        return ResponseEntity.ok(isPurchased);
    }
    // 책 구매
    @PostMapping("/purchase/{memberId}/{bookId}")
    public ResponseEntity<Boolean> purchaseBook(@PathVariable String memberId, @PathVariable int bookId) {
        try {
            if (bookDAO != null) {
                BookDTO book = bookDAO.getBookInfo(bookId);
                Boolean isPurchased = bookDAO.purchaseBook(memberId, book.getId(), book.getPrice());
                System.out.println(isPurchased);
                return ResponseEntity.ok(isPurchased);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    // 여러 책 구매
    @PostMapping("/purchase/multiple/{memberId}")
    public ResponseEntity<Boolean> purchaseBooks(@PathVariable String memberId, @RequestBody List<Integer> bookIds) {
        try {
            if (bookDAO != null) {
                Boolean isPurchased = bookDAO.purchaseBooks(memberId, bookIds);
                System.out.println(isPurchased);
                return ResponseEntity.ok(isPurchased);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}