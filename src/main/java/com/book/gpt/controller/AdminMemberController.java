package com.book.gpt.controller;

import com.book.gpt.dto.MemberDTO;
import com.book.gpt.service.AdminMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/member")

public class AdminMemberController {
    @Autowired
    private AdminMemberService adminMemberService;

    @GetMapping("/admin")
    public List<MemberDTO> getAllMembers() {
        return adminMemberService.getAllMembers();
    }

    @PostMapping("/admin")
    public ResponseEntity<MemberDTO> addMember(@RequestBody MemberDTO newMember) {
        MemberDTO memberDTO = adminMemberService.addMember(newMember);
        return new ResponseEntity<>(memberDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/{id}")
    // ResponseEntity : 스프링에서 http의 응답을 표현하는 클래스
    public ResponseEntity<Void> deleteMember(@PathVariable String id) {
        adminMemberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<MemberDTO> updateMember(@PathVariable String id, @RequestBody MemberDTO memberDTO) {
        return adminMemberService.updateMember(id, memberDTO)
                .map(member -> new ResponseEntity<>(member, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<MemberDTO> findMember(@PathVariable String id) {
        return adminMemberService.findMember(id)
                .map(member -> new ResponseEntity<>(member, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}


