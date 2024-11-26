package edeu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class LectureMainWindowInstructor extends JFrame {
    private JPanel mainPanel;
    private JPanel lectureListPanel;
    private HashMap<Integer, String> lectureMap;
    private static String userId;
    private DBMgr dbMgr;

    public LectureMainWindowInstructor(String userId) {
        this.userId = userId;
        dbMgr = new DBMgr(); // DBMgr 인스턴스 초기화

        // JFrame 설정
        setTitle("강의실 메인화면 (강사용)");
        setSize(1440, 1024);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        // 메인 패널 생성
        mainPanel = new JPanel();
        mainPanel.setBounds(100, 150, 1000, 570);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(null);
        getContentPane().add(mainPanel);

        // 강의 목록 패널 생성
        createLectureListPanel();

        mainPanel.add(lectureListPanel);

        setVisible(true);
    }

    private void createLectureListPanel() {
        lectureListPanel = new JPanel();
        lectureListPanel.setLayout(null);
        lectureListPanel.setBackground(Color.WHITE);
        lectureListPanel.setBounds(0, 0, 1340, 924);
        try {
 			// 폰트 파일 로드
 			Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));
			
	        // 상단 라벨
	        JLabel titleLabel = new JLabel("E - DEU", SwingConstants.CENTER);
	        titleLabel.setFont(HFont.deriveFont(Font.BOLD, 35f));
	        titleLabel.setBounds(400, 20, 200, 40);
	        lectureListPanel.add(titleLabel);
	
	        // 로그아웃 버튼
	        JButton logoutButton = new JButton("로그아웃");
	        logoutButton.setBounds(875, 20, 100, 40);
	        logoutButton.setFont(HFont.deriveFont(Font.BOLD, 15f));
	        logoutButton.setBackground(new Color(235, 212, 212));
	        logoutButton.setForeground(Color.BLACK);
	        logoutButton.setOpaque(true);
	        logoutButton.setBorderPainted(false);
	        lectureListPanel.add(logoutButton);
	
	        logoutButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                dispose();
	                new LoginWindow();
	            }
	        });
	
	        // "나의 강의" 패널 생성
	        JPanel lecturePanel = new JPanel();
	        lecturePanel.setLayout(new BoxLayout(lecturePanel, BoxLayout.Y_AXIS));
	        JScrollPane scrollPane = new JScrollPane(lecturePanel);
	        scrollPane.setBounds(50, 80, 900, 400);
	        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
	        lectureListPanel.add(scrollPane);
	
	        // 데이터베이스에서 강의 목록 불러오기
	        loadLecturesFromDB(lecturePanel);
	        
	     // 강의 추가 버튼
	        JButton addButton = new JButton("+");
	        addButton.setBounds(910, 505, 45, 30); // 위치와 크기 설정
	        addButton.setFont(HFont.deriveFont(Font.BOLD, 15f));
	        addButton.setBackground(Color.DARK_GRAY);
	        addButton.setForeground(Color.WHITE);
	        addButton.setOpaque(true);
	        addButton.setBorderPainted(false);
	        lectureListPanel.add(addButton);
	
	        addButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                // 강의 추가를 위한 새 창 열기
	                openAddLectureWindow(lecturePanel);
	            }
	        });
        } catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
    }

    private void loadLecturesFromDB(JPanel lecturePanel) {
        lectureMap = dbMgr.loadLecturesForUser(userId);

        // 강의 목록 추가
        for (int lectureCode : lectureMap.keySet()) {
            addLecture(lectureCode, lecturePanel);
        }
    }

    private void openAddLectureWindow(JPanel lecturePanel) {
        JFrame addLectureFrame = new JFrame("강의 추가");
        addLectureFrame.setSize(400, 400);
        addLectureFrame.setLayout(new BorderLayout());
        addLectureFrame.getContentPane().setBackground(Color.WHITE);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.setBackground(Color.WHITE);

        int fieldWidth = 350;
        try {
 			// 폰트 파일 로드
 			Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));
			
	        JLabel nameLabel = new JLabel("강의명:");
	        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
	        nameLabel.setFont(HFont.deriveFont(Font.BOLD, 20f));
	        nameLabel.setMaximumSize(new Dimension(fieldWidth, 30));
	
	        JTextField nameField = new JTextField();
	        nameField.setMaximumSize(new Dimension(fieldWidth, 30));
	        nameField.setFont(HFont.deriveFont(Font.BOLD, 15f));
	        nameField.setPreferredSize(new Dimension(fieldWidth, 30));
	        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
	
	        JLabel contentLabel = new JLabel("강의 내용:");
	        contentLabel.setFont(HFont.deriveFont(Font.BOLD, 20f));
	        contentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
	        contentLabel.setMaximumSize(new Dimension(fieldWidth, 30));
	
	        JTextArea contentArea = new JTextArea();
	        contentArea.setFont(HFont.deriveFont(Font.BOLD, 15f));
	        contentArea.setLineWrap(true);
	        contentArea.setWrapStyleWord(true);
	
	        JScrollPane contentScrollPane = new JScrollPane(contentArea);
	        contentScrollPane.setPreferredSize(new Dimension(fieldWidth, 150));
	        contentScrollPane.setMaximumSize(new Dimension(fieldWidth, 150));
	        contentScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
	
	        JButton addLectureButton = new JButton("추가");
	        addLectureButton.setFont(HFont.deriveFont(Font.BOLD, 15f));
	        addLectureButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                String lectureName = nameField.getText().trim();
	                String lectureContent = contentArea.getText().trim();
	                
	                if (!lectureName.isEmpty() && !lectureContent.isEmpty()) {
	                    // 강의명 중복 체크
	                    if (dbMgr.isLectureNameExists(lectureName)) {
	                        JOptionPane.showMessageDialog(addLectureFrame, "이미 존재하는 강의명입니다.", "오류", JOptionPane.ERROR_MESSAGE);
	                        return;
	                    }
	
	                    // 새로운 강의 ID 생성 (마지막 ID + 1)
	                    int newLectureId = dbMgr.getLastLectureId() + 1;
	
	                    // 강의 정보를 DB에 삽입
	                    boolean success = dbMgr.insertLecture(newLectureId, lectureName, userId, lectureContent);
	                    
	                    if (success) {
	                        // 강의 목록 업데이트
	                        lectureMap.put(newLectureId, lectureName);
	                        addLecture(newLectureId, lecturePanel);
	                        addLectureFrame.dispose();
	                        dbMgr.enrollInCourse(userId, newLectureId);
	                        JOptionPane.showMessageDialog(null, "성공적으로 추가되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
	                    } else {
	                        JOptionPane.showMessageDialog(addLectureFrame, "강의 추가 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
	                    }
	                } else {
	                    JOptionPane.showMessageDialog(addLectureFrame, "모든 필드를 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
	                }
	            }
	        });
	
	        inputPanel.add(nameLabel);
	        inputPanel.add(nameField);
	        inputPanel.add(Box.createVerticalStrut(10));
	        inputPanel.add(contentLabel);
	        inputPanel.add(contentScrollPane);
	
	        addLectureFrame.add(inputPanel, BorderLayout.CENTER);
	        addLectureFrame.add(addLectureButton, BorderLayout.SOUTH);
	
	        addLectureFrame.setVisible(true);
        } catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
    }
    
    private void addLecture(int lectureCode, JPanel lecturePanel) {
        String lectureName = lectureMap.get(lectureCode);
        try {
 			// 폰트 파일 로드
 			Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));
			
	        if (lectureName != null) {
	            JLabel lectureLabel = new JLabel(lectureName + "(" + lectureCode + ")");
	            lectureLabel.setFont(HFont.deriveFont(Font.PLAIN, 20f));
	            lectureLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	
	            lectureLabel.addMouseListener(new java.awt.event.MouseAdapter() {
	                public void mouseClicked(java.awt.event.MouseEvent evt) {
	                    new LectureDetailWindowInstructor(userId, lectureCode);
	                    dispose();
	                }
	            });
	
	            lecturePanel.add(lectureLabel);
	            lecturePanel.revalidate();
	            lecturePanel.repaint();
	        } else {
	            JOptionPane.showMessageDialog(this, "해당 강의 코드를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
	        }
        } catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LectureMainWindowInstructor(userId));
    }
}
