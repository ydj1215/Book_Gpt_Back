package com.book.gpt.dao;

import com.book.gpt.dto.MemberDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class AdminMemberDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 조회
    public List<MemberDTO> findAllMembers() {
        String sql = "SELECT * FROM MEMBER ORDER BY ID ASC";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) ->
                    new MemberDTO(
                            rs.getString("id"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("tel"),
                            rs.getInt("cash"),
                            rs.getInt("auth"),
                            rs.getString("profile_url"),
                            null,
                            null,
                            null
                    ));
        } catch (DataAccessException e) {
            throw new RuntimeException("회원 정보를 조회하는 데 실패했습니다.", e);
        }
    }

    // 추가
    public MemberDTO save(MemberDTO memberDTO) {
        String sql = "INSERT INTO MEMBER (ID, PASSWORD, NAME, EMAIL, TEL, CASH, AUTH, PROFILE_URL) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, memberDTO.getId(), memberDTO.getPassword(), memberDTO.getName(), memberDTO.getEmail(), memberDTO.getTel(), memberDTO.getCash(), memberDTO.getAuth(), memberDTO.getProfileUrl());
            return memberDTO;
        } catch (DataAccessException e) {
            throw new RuntimeException("책을 저장하는데 실패했습니다.", e);
        }
        // 저장 후의 회원 정보를 데이터베이스에서 조회하여 반환하는 것으로 추후 수정 예정
    }

    // 삭제
    public void deleteById(String id) {
        String sql = "DELETE FROM MEMBER WHERE id = ?";
        try {
            jdbcTemplate.update(sql, id);
        } catch (DataAccessException e) {
            throw new RuntimeException("회원을 삭제하는 데 실패했습니다.", e);
        }
    }

    // 수정 (1)
    public Optional<MemberDTO> findMember(String id) {
        String sql = "SELECT * FROM MEMBER WHERE id = ?";
        try {
            MemberDTO memberDTO = jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<>(MemberDTO.class));
            return Optional.ofNullable(memberDTO);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // 수정 (2)
    public Optional<MemberDTO> updateMember(String id, MemberDTO memberDTO) {
        String sql = "UPDATE MEMBER SET ID = ?, NAME = ?, EMAIL = ?, PASSWORD = ?, TEL = ?, CASH = ?, AUTH = ?, PROFILE_URL = ? WHERE id = ?";
        jdbcTemplate.update(sql, memberDTO.getId(), memberDTO.getName(), memberDTO.getEmail(), memberDTO.getPassword(), memberDTO.getTel(), memberDTO.getCash(), memberDTO.getAuth(),
                memberDTO.getProfileUrl(), id);
        return findMember(id);
    }
}
