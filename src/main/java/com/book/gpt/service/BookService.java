package com.book.gpt.service;

import com.book.gpt.dao.BookDAO;
import com.book.gpt.dto.BookDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookDAO bookDAO;
    // id를 기준으로 책을 read 하는 메서드 2023/11/07
    public BookDTO getBookByID(int id) {return bookDAO.findBookById(id); }

    public List<BookDTO> getAllBooks() {
        return bookDAO.findAllBooks();
    }

    // 새로운 책을 데이터베이스에 추가하는 메소드
    public BookDTO addBook(BookDTO newBook) {
        return bookDAO.save(newBook);
    }

    // 책을 데이터베이스에서 삭제하는 메소드
    public void deleteBook(int id) {
        bookDAO.deleteById(id);
    }

    public Optional<BookDTO> updateBook(int id, BookDTO bookDTO) {
        return bookDAO.updateBook(id, bookDTO);
    }

    public Optional<BookDTO> findBook(int id) {
        return bookDAO.findBook(id);
    }

    @Transactional(readOnly = true)
    public BookDTO getBookByTitleAndAuthor(String title, String author) {
        return bookDAO.findByTitleAndAuthor(title, author);
    }

    public boolean isBookBoughtByUser(String memberId, int bookId) {
        return bookDAO.isBookBoughtByUser(memberId, bookId);
    }
}

