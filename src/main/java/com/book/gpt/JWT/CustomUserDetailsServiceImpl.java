package com.book.gpt.JWT;

import com.book.gpt.dao.MemberDAO;
import com.book.gpt.dao.MemberDAO2;
import com.book.gpt.dto.MemberDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
    @Autowired
    private MemberDAO2 memberDAO;
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        MemberDTO user = memberDAO.findId(id);
        if (user != null) {
            // 사용자 정보가 존재하는 경우
            String username = user.getId();
            String password = user.getPassword();
            String role = memberDAO.findRoleById(id);

            System.out.println(role);
            return new CustomUserDetails(username, password, role);
        } else {
            // 사용자 정보가 존재하지 않는 경우
            System.out.println("사용자 정보를 찾을 수 없습니다.");
            throw new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }
    }
}
