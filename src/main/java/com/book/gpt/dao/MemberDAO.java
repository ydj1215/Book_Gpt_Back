package com.book.gpt.dao;

import com.book.gpt.common.Common;
import com.book.gpt.dto.MemberDTO;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Repository

public class MemberDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private PreparedStatement pStmt = null;

    // 정보 조회
    public List<MemberDTO> memberInfo(String getId) {
        List<MemberDTO> list = new ArrayList<>();
        if (getId == null) {
            return list;  // getId가 null일 경우 빈 리스트 반환
        }
        String sql = "SELECT * FROM MEMBER WHERE ID = ?";
        try (Connection conn = Common.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, getId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("ID");
                    String pw = rs.getString("PASSWORD");
                    String name = rs.getString("NAME");
                    String email = rs.getString("EMAIL");
                    String tel = rs.getString("TEL");
                    int cash = rs.getInt("CASH");
                    String profileUrl = rs.getString("PROFILE_URL");
                    System.out.println("DAO" + profileUrl);

                    MemberDTO dto = new MemberDTO();
                    dto.setId(id != null ? id : "");  // 필드값이 null이면 빈 문자열 할당
                    dto.setPassword(pw != null ? pw : "");
                    dto.setName(maskName(name)); // 마스킹 되는 경우 해당 예외 처리가 이미 발생된 상황
                    dto.setEmail(email != null ? email : "");
                    dto.setTel(maskTel(tel)); // 마스킹 되는 경우 해당 예외 처리가 이미 발생된 상황
                    dto.setCash(cash);
                    dto.setRole("ROLE_USER");
                    dto.setProfileUrl(profileUrl != null ? profileUrl : "");
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    // 이름, 전화번호, 이메일 마스킹
    // 이름
    private String maskName(String name) {
        if (name != null && name.length() > 1) {
            return name.substring(0, 1) + "*".repeat(name.length() - 1);
        }
        return name;
    }
    // 전화번호
    private String maskTel(String tel) {
        if (tel != null && tel.length() >= 12) {
            return tel.substring(0, 4) + tel.substring(4, 5) + "***" + tel.substring(8,10) + "***";
        }
        return tel;
    }


    // 이름~이메일 입력시 존재하는 지 체크
    public boolean memberCheck(String name, String id, String pw, String email) {
        boolean isChecked = false;
        System.out.println("이름, 이메일, 아이디, 비번 : 1 : " + isChecked);
        try {
            conn = Common.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT NAME, ID, PASSWORD, EMAIL FROM MEMBER WHERE NAME = '" + name + "'"
                    + " AND ID = '" + id + "'"
                    + " AND PASSWORD = '" + pw + "'"
                    + " AND EMAIL = '" + email + "'";
            rs = stmt.executeQuery(sql);
            System.out.println("회원 가입 여부 확인 Name : " + name);
            System.out.println("회원 가입 여부 확인 ID : " + id);
            System.out.println("회원 가입 여부 확인 pw : " + pw);
            System.out.println("회원 가입 여부 확인 email : " + email);
            if (rs.next()) {
                isChecked = true;
            } else {
                isChecked = false;
            }

            System.out.println("이름, 이메일, 아이디, 비번 :  2 : " + isChecked);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(rs);
        Common.close(stmt);
        Common.close(conn);
        System.out.println("이름, 이메일, 아이디, 비번 :  3 : " + isChecked);
        return isChecked; // 가입 되어 있으면 false, 가입이 안되어 있으면 true
    }


    // 아이디 변경을 위해 중복 체크
    public boolean isIdcheck(String newId) {
        boolean isDuplicate = false;
        System.out.println("아이디 체크: 1 " + isDuplicate);
        System.out.println("아이디 체크: 새로운 아이디 : " + newId);

        try {
            conn = Common.getConnection();
            String sql = "SELECT COUNT(*) AS count FROM MEMBER WHERE ID = ?";
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, newId);
            rs = pStmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                isDuplicate = (count != 1);
                System.out.println("아이디 체크: 2 " + isDuplicate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isDuplicate;
    }


    public boolean modifyId(String currentId, String newId) {
        boolean isData = false;
        System.out.println("아이디 수정: 1 " + isData);
        System.out.println("아이디 수정: 현제 아이디 " + currentId);
        System.out.println("아이디 수정: 새로운 아이디 " + newId);
        try {
            conn = Common.getConnection();
            // 아이디 변경을 위한 SQL 쿼리를 작성합니다.
            String updateSql = "UPDATE MEMBER SET ID = ? WHERE ID = ?";
            if (isIdcheck(newId) == true) {
                // PreparedStatement를 생성하고 파라미터 값을 설정합니다.
                PreparedStatement pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, newId);
                pstmt.setString(2, currentId);

                // 업데이트 쿼리를 실행합니다.
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated >= 0) {
                    isData = true;
                    System.out.println("아이디 변경 완료");
                } else {
                    isData = false;
                    System.out.println("아이디 변경 실패");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isData;
    }

    // 비밀번호 중복 체크
    public boolean isPwcheck(String newPw) {
        // 중복 체크
        boolean isDup = false;
        try {
            conn = Common.getConnection();
            String checkSql = "SELECT COUNT(*) AS count FROM MEMBER WHERE PASSWORD = ?";
            PreparedStatement pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, newPw);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                if (count == 0) {
                    System.out.println(isDup);
                    isDup = true;
                } else {
                    System.out.println(isDup);
                    isDup = false;
                }
            }
            System.out.println(isDup);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isDup;
    }

    public boolean modifyPw(String currentPw, String newPw) {
        boolean isData = false;
        try {
            conn = Common.getConnection();
            // 아이디 변경을 위한 SQL 쿼리를 작성합니다.
            String updateSql = "UPDATE MEMBER SET PASSWORD = ? WHERE PASSWORD = ?";
            if (isPwcheck(newPw) == true) {
                // PreparedStatement를 생성하고 파라미터 값을 설정합니다.
                PreparedStatement pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, newPw);
                pstmt.setString(2, currentPw);

                // 업데이트 쿼리를 실행합니다.
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    isData = true;
                    System.out.println("비밀번호 변경 완료");
                } else {
                    isData = false;
                    System.out.println("비밀번호 변경 실패");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isData;
    }

    public boolean modifyName(String currentName, String newName) {
        boolean isData = false;
        try {
            conn = Common.getConnection();
            // 이름 변경을 위한 SQL 쿼리를 작성합니다.
            String updateSql = "UPDATE MEMBER SET NAME = ? WHERE NAME = ?";
            PreparedStatement pstmt = conn.prepareStatement(updateSql);
            pstmt.setString(1, newName);
            pstmt.setString(2, currentName);
            System.out.println(newName);
            System.out.println(currentName);
            // 업데이트 쿼리를 실행합니다.
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                isData = true;
                System.out.println("이름 변경 완료");
            } else {
                isData = false;
                System.out.println("이름 변경 실패");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isData;
    }


    // 삭제
    // 문제 점: try catch 구문의 바깥쪽에 선언된 boolean isData = false;
    // try catch 구문안에서 값을 변화 시키면 변화하지 않음?
    public boolean deleteMember(String memberId) {
        boolean isData = false;
        int rowsUpdated = 0;
        try {
            // Member 삭제 후에 관련된 Cart와 Buy 데이터도 삭제
            deleteCart(memberId);
            deleteBuy(memberId);
            conn = Common.getConnection();
            String sql = "DELETE FROM MEMBER WHERE ID = ?";
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, memberId);
            rowsUpdated = pStmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Common.close(pStmt);
            Common.close(conn);
        }
        if (rowsUpdated == 1) {
            isData = true;
            System.out.println("삭제 완료: " + rowsUpdated);
        } else {
            System.out.println("삭제 실패: " + rowsUpdated);
        }
        return isData;
    }

    // 카트 삭제
    public void deleteCart(String memberId) {
        try {
            conn = Common.getConnection();
            String sql = "DELETE FROM CART WHERE MEMBER_ID = ?";
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, memberId);
            pStmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Common.close(pStmt);
            Common.close(conn);
        }
    }

    // 구매 삭제
    public void deleteBuy(String memberId) {
        try {
            conn = Common.getConnection();
            String sql = "DELETE FROM BUY WHERE MEMBER_ID = ?";
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, memberId);
            pStmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Common.close(pStmt);
            Common.close(conn);
        }
    }


    public boolean chargingCash(String getId, int getCash) {
        boolean isData = false;
        int rowsUpdated = 0;
        try {
            conn = Common.getConnection();
            String sql = "UPDATE MEMBER SET CASH = CASH + ? WHERE id = ?";
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, getCash);
            pStmt.setString(2, getId);
            rowsUpdated = pStmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(pStmt);
        Common.close(conn);
        if (rowsUpdated == 1) {
            isData = true;
            System.out.println("충전 완료: " + rowsUpdated);
        } else {
            isData = false;
            System.out.println("충전 실패: " + rowsUpdated);
        }
        return isData;
    }


    // 이미지 url 저장
    public boolean setImageUrl(String getId, String getUrl) {
        boolean isData = false;
        int rowsUpdated = 0;
        try {
            conn = Common.getConnection();
            String sql = "UPDATE MEMBER SET PROFILE_URL = ? WHERE id = ?";
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, getUrl);
            pStmt.setString(2, getId);
            rowsUpdated = pStmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(pStmt);
        Common.close(conn);
        if (rowsUpdated == 1) {
            isData = true;
            System.out.println("이미지 업로드 완료: " + rowsUpdated);
        } else {
            isData = false;
            System.out.println("이미지 업로드  실패: " + rowsUpdated);
        }
        return isData;
    }

}