package com.book.gpt.service;

import com.book.gpt.dao.BuyDAO;
import com.book.gpt.dto.BuyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuyService {
    @Autowired
    private BuyDAO buyDAO;

    public List<BuyDTO> getBoughtBooks(String memberId) {
        return buyDAO.getBoughtBooks(memberId);
    }

    public void deleteBook(int id) {
        buyDAO.deleteBook(id);
    }
}
