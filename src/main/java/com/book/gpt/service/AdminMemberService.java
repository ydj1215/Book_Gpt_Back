package com.book.gpt.service;

import com.book.gpt.dao.AdminMemberDAO;
import com.book.gpt.dto.MemberDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminMemberService {

    @Autowired
    private AdminMemberDAO adminMemberDAO;

    public List<MemberDTO> getAllMembers() {
        return adminMemberDAO.findAllMembers();
    }

    // 새로운 책을 데이터베이스에 추가하는 메소드
    public MemberDTO addMember(MemberDTO newMember) {
        return adminMemberDAO.save(newMember);
    }

    // 책을 데이터베이스에서 삭제하는 메소드
    public void deleteMember(String id) {
        adminMemberDAO.deleteById(id);
    }

    public Optional<MemberDTO> updateMember(String id, MemberDTO memberDTO) {
        return adminMemberDAO.updateMember(id, memberDTO);
    }

    public Optional<MemberDTO> findMember(String id) {
        return adminMemberDAO.findMember(id);
    }
}

