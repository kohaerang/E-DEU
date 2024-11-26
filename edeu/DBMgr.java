package edeu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DBMgr {
    private DBConnectionMgr pool;
    
    public DBMgr() {
    	pool = DBConnectionMgr.getInstance();
    }
    
	public static void main(String[] args) {

	}
	
	// 로그인
	public String loginCheck(String userId, String password) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		String role = null;
		try {
			con = pool.getConnection();
			sql = "SELECT ROLE FROM Users WHERE userId = ? AND password = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			pstmt.setString(2, password);
			
			rs = pstmt.executeQuery();
			if (rs.next()) {
                role = rs.getString("ROLE"); // ROLE 값 저장
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return role; // ROLE 값 반환, 로그인 실패 시 null 반환
	}
	
	// 회원가입
	public void userjoin(String emailField, String passwordField, String nameField, String idField, String role) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = null;
		try {
			con = pool.getConnection();
			sql = "INSERT INTO Users(userId, password, userName, userInfo, "
					+ "ROLE) VALUES(?, ?, ?, ?, ?)";
			pstmt = con.prepareStatement(sql);
	        pstmt.setString(1, emailField); // userId
	        pstmt.setString(2, passwordField); // password
	        pstmt.setString(3, nameField); // userName
	        pstmt.setString(4, idField); // userInfo 
	        pstmt.setString(5, role); // ROLE
	        int result = pstmt.executeUpdate(); // 쿼리 실행
	        if(result > 0) {
	            System.out.println("회원가입 성공!");
	        } else {
	            System.out.println("회원가입 실패!");
	        }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
	}

	// 강의목록 출력
    public HashMap<Integer, String> loadLecturesForUser(String userId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<Integer, String> lectureMap = new HashMap<>();
        String sql = "SELECT l.lectureId, l.lectureName " +
                     "FROM Lectures l " +
                     "JOIN Courses c ON l.lectureId = c.lectureId " +
                     "WHERE c.userId = ?";
        try {
            con = pool.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int lectureId = rs.getInt("lectureId");
                String lectureName = rs.getString("lectureName");
                lectureMap.put(lectureId, lectureName); // 해시맵에 강의 정보 저장
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(con, pstmt, rs);
        }
        return lectureMap; // 강의 데이터를 반환
    }
    
    // 강의 정보 확인
    public String getLectureName(int lectureId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String lectureName = null;

        try {
            con = pool.getConnection();
            String sql = "SELECT lectureName FROM Lectures WHERE lectureId = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, lectureId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                lectureName = rs.getString("lectureName");
            }
        } catch (Exception e) {
			e.printStackTrace();
		} finally {
            pool.freeConnection(con, pstmt, rs);
        }

        return lectureName;
    }
    
    // 수강 정보 추가
    public void enrollInCourse(String userId, int lectureId) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = pool.getConnection();
            // Courses 테이블에 수강 정보를 추가
            String sql = "INSERT INTO Courses(userId, lectureId) VALUES(?, ?)";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setInt(2, lectureId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(con, pstmt);
        }
    }
    
    // 강의 정보 추가
    public boolean insertLecture(int lectureId, String lectureName, String lectureInstructor, String lectureInfo) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = pool.getConnection();
            String sql = "INSERT INTO Lectures(lectureId, lectureName, lectureInstructor, lectureInfo) VALUES(?, ?, ?, ?)";
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, lectureId);
            pstmt.setString(2, lectureName);
            pstmt.setString(3, lectureInstructor);
            pstmt.setString(4, lectureInfo);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            pool.freeConnection(con, pstmt);
        }
    }
    
    // 중복 강의명 체크 메서드
    public boolean isLectureNameExists(String lectureName) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = pool.getConnection();
            String sql = "SELECT COUNT(*) FROM Lectures WHERE lectureName = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, lectureName);
            rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(con, pstmt, rs);
        }
        return false;
    }
    // 마지막 강의 ID
    public int getLastLectureId() {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int lastId = 0;

        try {
            con = pool.getConnection();
            String sql = "SELECT MAX(lectureId) FROM Lectures";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                lastId = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(con, pstmt, rs);
        }
        return lastId;
    }
    
    // 수업계획서 정보 
    public String[] loadLecturePlan(int lectureId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] lecturePlan = new String[3]; // [0] = 교과목명, [1] = 담당강사, [2] = 설명
        String sql = "SELECT L.lectureName, U.userName, L.lectureInfo " +
                "FROM Lectures L " +
                "JOIN Users U ON L.lectureInstructor = U.userId " +
                "WHERE L.lectureId = ?";
        try {
            con = pool.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, lectureId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                lecturePlan[0] = rs.getString("lectureName");
                lecturePlan[1] = rs.getString("userName");
                lecturePlan[2] = rs.getString("lectureInfo");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(con, pstmt, rs);
        }
        return lecturePlan;
    }
    
    // 게시글 정보
    public ArrayList<String[]> loadNoticeData(int lectureId) {
    	Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<String[]> noticeData = new ArrayList<>(); // 데이터를 저장할 리스트
        String sql = "SELECT U.postTitle, U.postDate, US.userName, "
        		+ "U.postType, U.postContent, U.filePath, U.postDate, U.dueDate, U.uploadId "
        		+ "FROM Upload U " 
        		+ "JOIN Users US ON US.userId = U.userId "
        		+ "WHERE U.lectureId = ? "
        		+ "ORDER BY postDate DESC";
        try {
            con = pool.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, lectureId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
            	// [0] = 제목, [1] = 쓴사람, [2] = 유형, [3] = 내용, [4] = 파일경로, [5] = 게시일, [6] = 마감일, [7] = 업로드 번호
                String[] data = new String[9]; 
                data[0] = rs.getString("postTitle");
                data[1] = rs.getString("userName");
                data[2] = rs.getString("postType");
                data[3] = rs.getString("postContent");
                data[4] = rs.getString("filePath");
                data[5] = rs.getString("postDate");
                data[6] = rs.getString("dueDate");
                data[7] = rs.getString("uploadId");
                noticeData.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(con, pstmt, rs);
        }
        return noticeData;
    }
    
    // 수업계획서 수정 후 저장 
    public void updatelectureInfo(int lectureId, String lectureInfoText) {
    	Connection con = null;
		PreparedStatement pstmt = null;
		String sql = null;
		try {
			con = pool.getConnection();
			sql = "UPDATE Lectures SET lectureInfo = ? WHERE lectureId = ?";
	        pstmt = con.prepareStatement(sql);
	        pstmt.setString(1, lectureInfoText); // 새로운 강의 설명 설정
	        pstmt.setInt(2, lectureId);
	        int result = pstmt.executeUpdate(); // 쿼리 실행
	        if(result > 0) {
	            System.out.println("업데이트 성공");
	        } else {
	            System.out.println("업데이트 실패");
	        }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
    }
    
    // 학생 제출 데이터를 로드하는 메서드 추가
    public ArrayList<String[]> loadStudentSubmissions(int lectureId, String projectId) {
        ArrayList<String[]> submissions = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = null;

        try {
            con = pool.getConnection();
            sql = "SELECT u.userName, u.userId, s.submitDate, s.isSubmit " +
                  "FROM Submit s " +
                  "JOIN Users u ON s.userId = u.userId " +
                  "WHERE s.lectureId = ? AND s.projectId = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, lectureId);
            pstmt.setString(2, projectId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                String userName = rs.getString("u.userName");
                String userEmail = rs.getString("u.userId");
                String submitDate = rs.getTimestamp("s.submitDate") != null ? rs.getTimestamp("s.submitDate").toString() : "";
                String status = rs.getBoolean("s.isSubmit") ? "제출" : "미제출";

                // 데이터를 배열에 추가
                submissions.add(new String[]{userName, userEmail, submitDate, status});
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(con, pstmt, rs);
        }
        return submissions;
    }
    
    // 게시글 등록 메서드
    public int insertPost(String userId, int lectureId, String postType, String title, String content, String filePath, String postDate, String dueDate) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int uploadId = -1;
        
        try {
            con = pool.getConnection();
            String sql = "INSERT INTO Upload (userId, lectureId, postType, postTitle, postContent, filePath, postDate, dueDate) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, userId);
            pstmt.setInt(2, lectureId);
            pstmt.setString(3, postType);
            pstmt.setString(4, title);
            pstmt.setString(5, content);
            pstmt.setString(6, filePath);
            pstmt.setString(7, postDate);
            pstmt.setString(8, dueDate);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    uploadId = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(con, pstmt, rs);
        }
        
        return uploadId;
    }

    // 과제 등록 시 학생들의 미제출 상태 추가 메서드
    public void insertSubmitRecords(int lectureId, int uploadId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = pool.getConnection();
            // 1. 강의에 등록된 학생들을 조회
            String getStudentsSql = "SELECT c.userId " +
                    "FROM Courses c " +
                    "JOIN Users u ON c.userId = u.userId " +
                    "WHERE c.lectureId = ? AND u.ROLE = '학생'";
            pstmt = con.prepareStatement(getStudentsSql);
            pstmt.setInt(1, lectureId);
            rs = pstmt.executeQuery();
            
            // 2. 학생들을 `Submit` 테이블에 삽입
            String insertSubmitSql = "INSERT INTO Submit (lectureId, projectId, userId, isSubmit) VALUES (?, ?, ?, ?)";
            pstmt = con.prepareStatement(insertSubmitSql);
            
            while (rs.next()) {
                String userId = rs.getString("userId");
                
                pstmt.setInt(1, lectureId);
                pstmt.setInt(2, uploadId);
                pstmt.setString(3, userId);
                pstmt.setBoolean(4, false); // 미제출 상태로 설정
                
                pstmt.addBatch(); // 배치 작업에 추가
            }
            pstmt.executeBatch(); // 배치 작업 실행
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(con, pstmt);
        }
    }

    // 과제 제출 시 상태를 '제출완료'로 업데이트
    public void updateSubmitStatus(int lectureId, String uploadId, String userId, String filePath, String formattedDate) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            // DB 연결
            con = pool.getConnection();

            // 제출 상태 업데이트 쿼리
            String updateSubmitSql = "UPDATE Submit SET isSubmit = ?, filePath = ?, submitDate = ? " +
                                     "WHERE lectureId = ? AND projectId = ? AND userId = ?";
            pstmt = con.prepareStatement(updateSubmitSql);
            pstmt.setBoolean(1, true); // 제출 상태를 '제출완료'로 설정
            pstmt.setString(2, filePath); // 제출한 파일 경로 설정
            pstmt.setString(3, formattedDate);
            pstmt.setInt(4, lectureId);
            pstmt.setString(5, uploadId);
            pstmt.setString(6, userId);

            // 업데이트 실행
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("과제가 성공적으로 제출되었습니다.");
            } else {
                System.out.println("과제 제출에 실패했습니다. 해당 레코드를 찾을 수 없습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (con != null) pool.freeConnection(con);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // 특정 과제가 이미 제출되었는지 확인하는 메서드
    public boolean checkSubmitStatus(int lectureId, String uploadId, String userId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isSubmitted = false;

        try {
            con = pool.getConnection();
            String checkSubmitSql = "SELECT isSubmit FROM Submit WHERE lectureId = ? AND projectId = ? AND userId = ?";
            pstmt = con.prepareStatement(checkSubmitSql);
            pstmt.setInt(1, lectureId);
            pstmt.setString(2, uploadId);
            pstmt.setString(3, userId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                isSubmitted = rs.getBoolean("isSubmit");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (con != null) pool.freeConnection(con);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isSubmitted;
    }
}
