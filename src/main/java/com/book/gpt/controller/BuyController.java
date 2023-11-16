package com.book.gpt.controller;

import com.book.gpt.dto.BuyDTO;
import com.book.gpt.service.BuyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/buy")
public class BuyController {
    @Autowired
    private BuyService buyService;

    @GetMapping("/boughtbooks")
    public ResponseEntity<List<BuyDTO>> getBoughtBooks(@RequestParam String memberId) {
        List<BuyDTO> boughtBooks = buyService.getBoughtBooks(memberId);
        return new ResponseEntity<>(boughtBooks, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable int id) {
        buyService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
