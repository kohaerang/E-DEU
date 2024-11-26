package edeu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class LectureMainWindow extends JFrame implements ActionListener {
    private JPanel lecturePanel;
    private JTextField lectureCodeField;
    private JButton addButton;
    private JButton logoutButton;
    private HashMap<Integer, String> lectureMap; // 강의 목록 저장
    private DBMgr dbMgr;
    private static String userId; // 사용자 ID를 저장할 변수

    public LectureMainWindow(String userId) { // 생성자에서 사용자 ID를 받음
        this.userId = userId;
        dbMgr = new DBMgr();
        try {
 			// 폰트 파일 로드
 			Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));
			
	        // 프레임 설정
	        setTitle("강의실 메인화면");
	        setSize(1440, 1024);
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        getContentPane().setLayout(null);
	
	        // 메인 패널 생성
	        JPanel mainPanel = new JPanel();
	        mainPanel.setBounds(100, 150, 1000, 570); // 프레임 내에서 패널의 위치와 크기 설정
	        mainPanel.setBackground(Color.WHITE);
	        mainPanel.setLayout(null);
	        getContentPane().add(mainPanel);
	
	        // 상단 라벨
	        JLabel titleLabel = new JLabel("E - DEU", SwingConstants.CENTER);
	        titleLabel.setFont(HFont.deriveFont(Font.BOLD, 35f)); // 폰트 크기 및 스타일 설정
	        titleLabel.setBounds(400, 20, 200, 40);
	        mainPanel.add(titleLabel);
	
	        // 로그아웃 버튼
	        logoutButton = new JButton("로그아웃");
	        logoutButton.setBounds(875, 20, 100, 40);
	        logoutButton.setFont(HFont.deriveFont(Font.BOLD, 20f));
	        logoutButton.setBackground(new Color(235, 212, 212)); // 배경색 설정
	        logoutButton.setForeground(Color.BLACK); // 글자 색상 설정
	        logoutButton.setOpaque(true); // 버튼 배경색 표시 설정
	        logoutButton.setBorderPainted(false); // 버튼 테두리 비활성화
	        logoutButton.addActionListener(this); // 로그아웃 버튼 이벤트 처리 추가
	        mainPanel.add(logoutButton);
	
	        // "나의 강의" 패널 생성
	        lecturePanel = new JPanel();
	        lecturePanel.setLayout(new BoxLayout(lecturePanel, BoxLayout.Y_AXIS));
	        JScrollPane scrollPane = new JScrollPane(lecturePanel);
	        scrollPane.setBounds(50, 80, 900, 400);
	        mainPanel.add(scrollPane);
	
	        // 강의 목록 불러오기
	        loadLectures(); // 강의 목록 불러오기 메서드 호출
	
	        // 강의 코드 입력 필드
	        lectureCodeField = new JTextField("강의 코드를 입력하세요.");
	        lectureCodeField.setForeground(Color.GRAY);
	        lectureCodeField.setBounds(700, 505, 200, 30);
	        lectureCodeField.setFont(HFont.deriveFont(Font.BOLD, 12f)); // 폰트 크기 및 스타일 설정
	        mainPanel.add(lectureCodeField);
	
	        // 텍스트 필드 포커스 이벤트 처리
	        lectureCodeField.addFocusListener(new FocusAdapter() {
	            public void focusGained(FocusEvent evt) {
	                if (lectureCodeField.getText().equals("강의 코드를 입력하세요.")) {
	                    lectureCodeField.setText("");
	                    lectureCodeField.setForeground(Color.BLACK);
	                }
	            }
	
	            public void focusLost(FocusEvent evt) {
	                if (lectureCodeField.getText().isEmpty()) {
	                    lectureCodeField.setForeground(Color.GRAY);
	                    lectureCodeField.setText("강의 코드를 입력하세요.");
	                }
	            }
	        });
	
	        // 강의 추가 버튼
	        addButton = new JButton("+");
	        addButton.setBounds(910, 505, 45, 30);
	        addButton.setFont(HFont.deriveFont(Font.BOLD, 15f));
	        addButton.setBackground(Color.DARK_GRAY); // 배경색 설정
	        addButton.setForeground(Color.WHITE); // 글자 색상 설정
	        addButton.setOpaque(true); // 버튼 배경색 표시 설정
	        addButton.setBorderPainted(false); // 버튼 테두리 비활성화
	        addButton.addActionListener(this); // 강의 추가 버튼 이벤트 처리 추가
	        mainPanel.add(addButton);
        } catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
        // 프레임을 화면에 표시
        setVisible(true);
    }

    // 강의 목록을 불러와서 패널에 표시하는 메서드
    private void loadLectures() {
        lectureMap = dbMgr.loadLecturesForUser(userId); // 사용자 ID를 이용해 강의 목록을 불러옴
        try {
 			// 폰트 파일 로드
 			Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));
			
	        for (Integer lectureId : lectureMap.keySet()) {
	            String lectureName = lectureMap.get(lectureId);
	            JLabel lectureLabel = new JLabel(lectureName + "(" + lectureId + ")");
	            lectureLabel.setFont(HFont.deriveFont(Font.BOLD, 20f));
	            lectureLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	            lectureLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	            lectureLabel.addMouseListener(new MouseAdapter() {
	                public void mouseClicked(MouseEvent evt) {
	                    // 강의 클릭 시 LectureDetailWindow로 이동
	                    dispose();
	                    new LectureDetailWindow(userId, lectureId);
	                }
	            });
	            lecturePanel.add(lectureLabel);
	        }
	        lecturePanel.revalidate();
	        lecturePanel.repaint();
        } catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
    }

    // ActionListener 구현
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();

        if (obj == addButton) {
            // 강의 추가 버튼 클릭 처리
            if (!lectureCodeField.equals("강의 코드를 입력하세요.")) {
            	try {
            		int lectureCode = Integer.parseInt(lectureCodeField.getText().trim());
                    // 강의가 이미 추가된 경우 확인
                    if (lectureMap.containsKey(lectureCode)) {
                        JOptionPane.showMessageDialog(this, "이미 추가된 강의입니다.", "중복 강의", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // DB에서 강의 정보 확인
                        String lectureName = dbMgr.getLectureName(lectureCode);
                        if (lectureName == null) {
                            // 강의가 DB에 존재하지 않는 경우
                            JOptionPane.showMessageDialog(this, "존재하지 않는 강의입니다.", "강의 없음", JOptionPane.ERROR_MESSAGE);
                        } else {
                            // 강의 추가 및 패널 업데이트
                        	dbMgr.enrollInCourse(userId, lectureCode); // Courses 테이블에 수강 정보 추가
                            addLecture(lectureCode, lectureName);
                            JOptionPane.showMessageDialog(this, "강의가 추가되었습니다.", "강의 추가", JOptionPane.INFORMATION_MESSAGE);
                            lectureCodeField.setText("강의 코드를 입력하세요.");
                            lectureCodeField.setForeground(Color.GRAY);
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "강의 코드를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (obj == logoutButton) {
            dispose();
            new LoginWindow();
        } 
    }

    // 강의 목록에 강의 추가 및 패널에 표시
    private void addLecture(int lectureId, String lectureName) {
        lectureMap.put(lectureId, lectureName);
        try {
 			// 폰트 파일 로드
 			Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));
			
	        JLabel lectureLabel = new JLabel(lectureName + "(" + lectureId + ")");
	        lectureLabel.setFont(HFont.deriveFont(Font.BOLD, 20f));
	        lectureLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	        lectureLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	        lectureLabel.addMouseListener(new MouseAdapter() {
	            public void mouseClicked(MouseEvent evt) {
	                // 강의 클릭 시 LectureDetailWindow로 이동
	                dispose();
	                new LectureDetailWindow(userId, lectureId);
	            }
	        });
	
	        lecturePanel.add(lectureLabel);
	        lecturePanel.revalidate();
	        lecturePanel.repaint();
        } catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LectureMainWindow(userId));
    }
}
