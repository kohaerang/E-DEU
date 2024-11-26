package edeu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.net.URI;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;

public class LectureDetailWindow extends JFrame implements ActionListener {
    private JPanel contentPanel;
    private JButton[] menuButtons; // 메뉴 버튼 배열
    private static int lectureId; // 강의 코드
    private String lectureName; // 강의 이름
    private String lectureInstructor; // 강사 이름
    private String lectureInfo; // 강의 설명
    private JLabel titleLabel; // 상단 라벨
    private String command; // 현재 선택된 메뉴 명령어 저장
    private static String userId; // 사용자 ID를 저장할 변수
    private DBMgr mgr;

    private ArrayList<String[]> noticeData;
    private ArrayList<String[]> materialData;
    private ArrayList<String[]> onlineLectureData;
    private ArrayList<String[]> assignmentData;
    private ArrayList<String[]> allData;
    
    // JxBrowser 엔진 및 브라우저 객체
    private Engine engine;
    private Browser browser;;

    public LectureDetailWindow(String userId, int lectureId) {
        mgr = new DBMgr();        // DBMgr 객체를 초기화
        this.userId = userId;
        this.lectureId = lectureId;
        
        // JxBrowser 라이선스 키 설정
        EngineOptions options = EngineOptions.newBuilder(HARDWARE_ACCELERATED)
                .licenseKey("OK6AEKNYF23G6Y3HUAYH7MUFDV3AAF00IFLXOC8RCEM6KDCA2GZ4FFAJZYHYXQ1E62U3MGITOXM57OZRVLAS3H81ZKBXSBNLFSFO3MRKU42OSMZMHAMU4T18O0MFQTF109EQO3TW0MIMU9NP7")
                .build();
        engine = Engine.newInstance(options);
        browser = engine.newBrowser();  // Initialize the browser instance here
        
        loadLectureDetails(); // 강의 상세 정보 초기화
        loadLectureData(); // 공지사항, 자료, 온라인 강의, 과제 겉 정보
        
        // 프레임 설정
        setTitle("강의실 상세 화면");
        setSize(1440, 1024);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        // 메인 패널 생성
        JPanel mainPanel = new JPanel();
        mainPanel.setBounds(100, 150, 1000, 570); // 프레임 내에서 패널의 위치와 크기 설정
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(null);
        getContentPane().add(mainPanel);

        // 좌측 메뉴 패널 생성
        JPanel menuPanel = new JPanel(new GridLayout(7, 1, 10, 10)); // 7x1 그리드 레이아웃
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        menuPanel.setBounds(12, 60, 160, 450);
        mainPanel.add(menuPanel);

        String[] buttonLabels = { "수업계획서", "공지사항", "강의자료", "온라인 강의", "실시간 강의", "과제", "돌아가기" };
        
 		try {
 			// 폰트 파일 로드
 			Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));
		
		    menuButtons = new JButton[buttonLabels.length];
		
		    for (int i = 0; i < buttonLabels.length; i++) {
		        JButton button = new JButton(buttonLabels[i]);
		        button.setFont(HFont.deriveFont(Font.PLAIN, 15f)); // 폰트 크기 및 스타일 설정
		        button.setBackground(Color.WHITE); // 기본 배경색 설정
		        button.setForeground(Color.BLACK); // 글자 색상 설정
		        button.setOpaque(true); // 버튼 배경색 표시 설정
		        button.setBorderPainted(false); // 버튼 테두리 비활성화
		        button.addActionListener(this); // 이벤트 리스너 추가
		        menuPanel.add(button);
		        menuButtons[i] = button; // 배열에 버튼 저장
		    }
		
		    // 상단 라벨
		    titleLabel = new JLabel("", SwingConstants.LEFT);
		    titleLabel.setFont(HFont.deriveFont(Font.PLAIN, 20f));
		    titleLabel.setBounds(20, 20, 400, 30); // mainPanel의 좌상단에 위치하도록 설정
		    mainPanel.add(titleLabel);
		
		    // 내용 패널 생성
		    contentPanel = new JPanel(new CardLayout()); // CardLayout으로 변경
		    contentPanel.setBounds(200, 60, 780, 450);
		    contentPanel.setBackground(Color.WHITE);
		    mainPanel.add(contentPanel);
		
		    // 초기 상태로 첫 번째 버튼을 클릭한 것처럼 보이게 설정
		    menuButtons[0].doClick();
 		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
        // 프레임을 화면에 표시
        setVisible(true);
    }
    
    private void loadLectureDetails() {
        // 수업계획서에 필요한 정보 로드
        String[] lectureDetails = mgr.loadLecturePlan(lectureId);	// [0] = 교과목명, [1] = 담당강사, [2] = 설명
        this.lectureName = lectureDetails[0];
        this.lectureInstructor = lectureDetails[1];
        this.lectureInfo = lectureDetails[2];
    }

    private void loadLectureData() {
    	allData = mgr.loadNoticeData(lectureId); // 강의 게시글 데이터를 모두 불러옴

        // 각 유형별로 데이터를 분류
        noticeData = new ArrayList<>();
        onlineLectureData = new ArrayList<>();
        materialData = new ArrayList<>();
        assignmentData = new ArrayList<>();

        for (String[] entry : allData) {
            String postType = entry[2]; // 게시글 유형
            switch (postType) {
                case "공지사항":
                    noticeData.add(entry);
                    break;
                case "온라인강의":
                    onlineLectureData.add(entry);
                    break;
                case "강의자료":
                    materialData.add(entry);
                    break;
                case "과제":
                    assignmentData.add(entry);
                    break;
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        command = e.getActionCommand();
        
        contentPanel.removeAll(); // 기존 내용 제거

        // 모든 버튼의 배경색을 초기화
        for (JButton button : menuButtons) {
            button.setBackground(Color.WHITE);
        }

        // 클릭된 버튼의 배경색 변경
        JButton clickedButton = (JButton) e.getSource();
        clickedButton.setBackground(new Color(235, 212, 212));

        // titleLabel의 텍스트 변경
        titleLabel.setText(command + " | " + lectureName);

        CardLayout cl = (CardLayout) (contentPanel.getLayout());

        if (command.equals("돌아가기")) {
            dispose();
            new LectureMainWindow(userId); // 돌아가기 버튼 클릭 시 메인 윈도우로 이동
        } else if (command.equals("수업계획서")) {
            JPanel lecturePlanPanel = createLecturePlanPanel();
            contentPanel.add(lecturePlanPanel, "LecturePlan");
            cl.show(contentPanel, "LecturePlan");
        } else if (command.equals("공지사항")) {
            contentPanel.add(new CreateListPanel(noticeData).getScrollPane(), "Notice");
            cl.show(contentPanel, "Notice");
        } else if (command.equals("강의자료")) {
            contentPanel.add(new CreateListPanel(materialData).getScrollPane(), "Material");
            cl.show(contentPanel, "Material");
        } else if (command.equals("온라인 강의")) {
            contentPanel.add(new CreateListPanel(onlineLectureData).getScrollPane(), "OnlineLecture");
            cl.show(contentPanel, "OnlineLecture");
        } else if (command.equals("과제")) {
            contentPanel.add(new CreateListPanel(assignmentData).getScrollPane(), "Assignment");
            cl.show(contentPanel, "Assignment");
        } else if (command.equals("실시간 강의")) {
        	try {
     			// 폰트 파일 로드
     			Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));
     			JPanel livePanel = new JPanel(new GridBagLayout());
     			JButton liveButton = new JButton("실시간 강의 입장");
     			livePanel.setBackground(Color.WHITE);
     			liveButton.setPreferredSize(new Dimension(400, 100));
	            liveButton.setBackground(new Color(235, 212, 212));  // 배경색 설정
	            liveButton.setForeground(Color.BLACK);  // 글자색 설정
	            liveButton.setFont(HFont.deriveFont(Font.BOLD, 30f));  // 글꼴 설정
	            liveButton.setOpaque(true);
	            liveButton.setBorderPainted(false);
	            
	            // 버튼 클릭 시 웹 브라우저에서 특정 링크를 여는 이벤트 추가
	            liveButton.addActionListener(new ActionListener() {
	                @Override
	                public void actionPerformed(ActionEvent e) {
	                    String url = "https://113.198.238.111:8443/ZoomIntegration/Join.jsp";  // 여기에 원하는 URL 입력
	                    try {
	                        java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
	                    } catch (IOException ioException) {
	                        JOptionPane.showMessageDialog(null, "링크를 여는 중 오류가 발생했습니다: " + ioException.getMessage());
	                    }
	                }
	            });
	            GridBagConstraints gbc = new GridBagConstraints();
	            gbc.gridx = 0;
	            gbc.gridy = 0;
	            gbc.anchor = GridBagConstraints.CENTER;
	            livePanel.add(liveButton, gbc);

	            contentPanel.add(livePanel, "LiveLecture");
	            cl.show(contentPanel, "LiveLecture");
        	} catch (FontFormatException | IOException e1) {
    			e1.printStackTrace();
    		}
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }
	// 수업계획서
    private JPanel createLecturePlanPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // 테두리 추가
        
        try {
 			// 폰트 파일 로드
 			Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));
			
	        JLabel nameLabel = new JLabel("교과목명  |  " + lectureName);
	        nameLabel.setFont(HFont.deriveFont(Font.PLAIN, 20f));
	        nameLabel.setBounds(50, 30, 400, 30);
	        panel.add(nameLabel);
	
	        JLabel codeLabel = new JLabel("강의코드  |  " + lectureId);
	        codeLabel.setFont(HFont.deriveFont(Font.PLAIN, 20f));
	        codeLabel.setBounds(50, 80, 400, 30);
	        panel.add(codeLabel);
	
	        JLabel professorLabel = new JLabel("담당강사  |  " + lectureInstructor);
	        professorLabel.setFont(HFont.deriveFont(Font.PLAIN, 20f));
	        professorLabel.setBounds(50, 130, 400, 30);
	        panel.add(professorLabel);
	        
	        JLabel lectureInfoLabel = new JLabel("강의정보");
	        lectureInfoLabel.setFont(HFont.deriveFont(Font.PLAIN, 20f));
	        lectureInfoLabel.setBounds(50, 180, 400, 30);
	        panel.add(lectureInfoLabel);
	        
	        JTextArea lectureInfoText = new JTextArea(lectureInfo);
	        lectureInfoText.setFont(HFont.deriveFont(Font.PLAIN, 20f));
	        lectureInfoText.setLineWrap(true);
	        lectureInfoText.setWrapStyleWord(true);
	        lectureInfoText.setBounds(50, 210, 680, 150);
	        lectureInfoText.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	        lectureInfoText.setEditable(false);
	        panel.add(lectureInfoText);
        } catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
        return panel;
    }

    // 온라인 강의 내용 표시
    private JPanel createOnlineLectureContentPanel(String entry[]) {
    	JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        try {
 			// 폰트 파일 로드
 			Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));
				
 	       // Title Label
 	        JLabel titleLabel = new JLabel(entry[0]);
 	        titleLabel.setFont(HFont.deriveFont(Font.BOLD, 20f));
 	        panel.add(titleLabel, BorderLayout.NORTH);

 	        // BrowserView for JxBrowser
 	        BrowserView view = BrowserView.newInstance(browser);
 	        view.setPreferredSize(new Dimension(780, 300)); // 영상 크기를 조절
 	        panel.add(view, BorderLayout.CENTER);

 	        // Load YouTube video URL
 	        String embedUrl = entry[4].replace("youtu.be", "www.youtube.com/embed");
 	        browser.navigation().loadUrl(embedUrl);
        } catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
        return panel;
    }
    
    // 공지사항 내용 표시
    private JPanel createNoticeContentPanel(String entry[]) {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        try {
 			// 폰트 파일 로드
 			Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));
			
	        // Title Label
	        JLabel titleLabel = new JLabel(entry[0]);
	        titleLabel.setFont(HFont.deriveFont(Font.BOLD, 20f));
	        titleLabel.setBounds(20, 10, 400, 30);
	        panel.add(titleLabel);
	
	        // File Label (첨부파일)
	        JLabel fileLabel = new JLabel("첨부파일");
	        fileLabel.setFont(HFont.deriveFont(Font.BOLD, 15f));
	        fileLabel.setBounds(20, 50, 400, 20);
	        fileLabel.setForeground(Color.BLUE);
	        fileLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
	        panel.add(fileLabel);
	        
	        // 파일 경로가 비어있다면 fileLabel을 숨김
	        if (entry[4] == null || entry[4].isEmpty()) {
	            fileLabel.setVisible(false);
	        } else {
	            // 첨부파일 클릭 이벤트 처리
	            fileLabel.addMouseListener(new MouseAdapter() {
	                public void mouseClicked(MouseEvent e) {
	                    String url = entry[4];  // URL을 그대로 사용하여 브라우저에서 여는 이벤트로 변경
	                    try {
	                        java.awt.Desktop.getDesktop().browse(URI.create(url));
	                    } catch (IOException ioException) {
	                        JOptionPane.showMessageDialog(null, "링크를 여는 중 오류가 발생했습니다: " + ioException.getMessage());
	                    }
	                }
	            });
	        }

	        // Content Area
	        JTextArea contentArea = new JTextArea();
	        contentArea.setFont(HFont.deriveFont(Font.BOLD, 15f));
	        contentArea.setBounds(20, 80, 700, 300);
	        contentArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	        contentArea.setEditable(false);
	        contentArea.setText(entry[3]);
	        panel.add(contentArea);
	
	        // 첨부파일 클릭 이벤트 처리
	        fileLabel.addMouseListener(new MouseAdapter() {
	            public void mouseClicked(MouseEvent e) {
	                String url = entry[4];  // URL을 그대로 사용하여 브라우저에서 여는 이벤트로 변경
	                try {
	                    java.awt.Desktop.getDesktop().browse(URI.create(url));
	                } catch (IOException ioException) {
	                    JOptionPane.showMessageDialog(null, "링크를 여는 중 오류가 발생했습니다: " + ioException.getMessage());
	                }
	            }
	        });
        } catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
        return panel;
    }

    // 강의자료 내용 표시
    private JPanel createMaterialContentPanel(String entry[]) {
    	// [0] = 제목, [1] = 쓴사람, [2] = 유형, [3] = 내용, [4] = 파일경로, [5] = 게시일, [6] = 마감일, [7] = 업로드 번호
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        try {
 			// 폰트 파일 로드
 			Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));
			
	        // Title Label
	        JLabel titleLabel = new JLabel(entry[0]);
	        titleLabel.setFont(HFont.deriveFont(Font.BOLD, 20f));
	        titleLabel.setBounds(20, 10, 400, 30);
	        panel.add(titleLabel);
	
	        // File Label (첨부파일)
	        JLabel fileLabel = new JLabel("첨부파일");
	        fileLabel.setFont(HFont.deriveFont(Font.BOLD, 15f));
	        fileLabel.setBounds(20, 50, 400, 20);
	        fileLabel.setForeground(Color.BLUE);
	        fileLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
	        panel.add(fileLabel);
	        
	        // 파일 경로가 비어있다면 fileLabel을 숨김
	        if (entry[4] == null || entry[4].isEmpty()) {
	            fileLabel.setVisible(false);
	        } else {
	            // 첨부파일 클릭 이벤트 처리
	            fileLabel.addMouseListener(new MouseAdapter() {
	                public void mouseClicked(MouseEvent e) {
	                    String url = entry[4];  // URL을 그대로 사용하여 브라우저에서 여는 이벤트로 변경
	                    try {
	                        java.awt.Desktop.getDesktop().browse(URI.create(url));
	                    } catch (IOException ioException) {
	                        JOptionPane.showMessageDialog(null, "링크를 여는 중 오류가 발생했습니다: " + ioException.getMessage());
	                    }
	                }
	            });
	        }

	        // Content Area
	        JTextArea contentArea = new JTextArea();
	        contentArea.setFont(HFont.deriveFont(Font.BOLD, 15f));
	        contentArea.setBounds(20, 80, 700, 300);
	        contentArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	        contentArea.setEditable(false);
	        contentArea.setText(entry[3]);
	        panel.add(contentArea);
	
	        // 첨부파일 클릭 이벤트 처리
	        fileLabel.addMouseListener(new MouseAdapter() {
	            public void mouseClicked(MouseEvent e) {
	                String url = entry[4];  // URL을 그대로 사용하여 브라우저에서 여는 이벤트로 변경
	                try {
	                    java.awt.Desktop.getDesktop().browse(URI.create(url));
	                } catch (IOException ioException) {
	                    JOptionPane.showMessageDialog(null, "링크를 여는 중 오류가 발생했습니다: " + ioException.getMessage());
	                }
	            }
	        });
    	} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
        return panel;
    }

    // 과제 내용 표시
    private JPanel createAssignmentContentPanel(String entry[]) {
    	// [0] = 제목, [1] = 쓴사람, [2] = 유형, [3] = 내용, [4] = 파일경로, [5] = 게시일, [6] = 마감일, [7] = 업로드 번호
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        try {
 			// 폰트 파일 로드
 			Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));
			
	        // Title Label
	        JLabel titleLabel = new JLabel(entry[0]);	// 제목
	        titleLabel.setFont(HFont.deriveFont(Font.BOLD, 20f));
	        titleLabel.setBounds(20, 10, 400, 30);
	        panel.add(titleLabel);
	        
	        // 제출 상태 Label (초기 상태: 미제출)
	        JLabel submitStatusLabel = new JLabel("미제출");
	        submitStatusLabel.setFont(HFont.deriveFont(Font.BOLD, 15f));
	        submitStatusLabel.setForeground(Color.RED);
	        submitStatusLabel.setBounds(650, 10, 100, 30);  // 제목 우측 끝에 배치
	        panel.add(submitStatusLabel);
	        
	        // 파일 제출 상태를 DB에서 조회
	        boolean isAlreadySubmitted = mgr.checkSubmitStatus(lectureId, entry[7], userId);
	        if (isAlreadySubmitted) {
	            submitStatusLabel.setText("제출완료");
	            submitStatusLabel.setForeground(new Color(0, 128, 0)); // 초록색으로 변경
	        }
	
	        // File Label (첨부파일)
	        JLabel fileLabel = new JLabel("첨부파일");
	        fileLabel.setFont(HFont.deriveFont(Font.BOLD, 15f));
	        fileLabel.setBounds(20, 50, 400, 20);
	        fileLabel.setForeground(Color.BLUE);
	        fileLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
	        panel.add(fileLabel);
	     
	        // 파일 경로가 비어있다면 fileLabel을 숨김
	        if (entry[4] == null || entry[4].isEmpty()) {
	            fileLabel.setVisible(false);
	        } else {
	            // 첨부파일 클릭 이벤트 처리
	            fileLabel.addMouseListener(new MouseAdapter() {
	                public void mouseClicked(MouseEvent e) {
	                    String url = entry[4];  // URL을 그대로 사용하여 브라우저에서 여는 이벤트로 변경
	                    try {
	                        java.awt.Desktop.getDesktop().browse(URI.create(url));
	                    } catch (IOException ioException) {
	                        JOptionPane.showMessageDialog(null, "링크를 여는 중 오류가 발생했습니다: " + ioException.getMessage());
	                    }
	                }
	            });
	        }
	
	        // File Field (사용자가 선택한 파일 경로를 표시)
	        JTextField fileField = new JTextField();
	        fileField.setBounds(20, 410, 300, 30);
	        panel.add(fileField);
	
	        // Add File Button ("+" 버튼)
	        JButton fileAddButton = new JButton("+");
	        fileAddButton.setFont(HFont.deriveFont(Font.BOLD, 20f));
	        fileAddButton.setBounds(330, 410, 50, 30);
	        fileAddButton.setBackground(new Color(235, 212, 212));
	        fileAddButton.setForeground(Color.BLACK);
	        fileAddButton.setOpaque(true);
	        fileAddButton.setBorderPainted(false);
	        panel.add(fileAddButton);
	
	        // 파일 선택 기능 추가
	        fileAddButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                JFileChooser fileChooser = new JFileChooser();
	                int returnValue = fileChooser.showOpenDialog(null);
	                if (returnValue == JFileChooser.APPROVE_OPTION) {
	                    File selectedFile = fileChooser.getSelectedFile();
	                    fileField.setText(selectedFile.getAbsolutePath());
	                }
	            }
	        });
	        panel.add(fileAddButton);
	        
	        // 현재 날짜를 가져와 형식 지정
 			LocalDate currentDate = LocalDate.now();
 			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
 			String formattedDate = currentDate.format(formatter);
    
	        // Submit File Button ("제출" 버튼)
	        JButton submitFileButton = new JButton("제출");
	        submitFileButton.setFont(HFont.deriveFont(Font.BOLD, 15f));
	        submitFileButton.setBounds(390, 410, 80, 30);
	        submitFileButton.setBackground(new Color(235, 212, 212));
	        submitFileButton.setForeground(Color.BLACK);
	        submitFileButton.setOpaque(true);
	        submitFileButton.setBorderPainted(false);
	        panel.add(submitFileButton);
	
	        // 파일 제출 기능 추가
	        submitFileButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                String filePath = fileField.getText();
	                if (!filePath.isEmpty()) {
	                	mgr.updateSubmitStatus(lectureId, entry[7], userId, fileField.getText(), formattedDate);// 제출 상태 변경
	                    JOptionPane.showMessageDialog(panel, "과제가 성공적으로 제출되었습니다.");
	                    submitStatusLabel.setText("제출완료"); // 제출 상태를 '제출완료'로 업데이트
	                    submitStatusLabel.setForeground(new Color(0, 128, 0));  // 초록색으로 변경
	                } else {
	                    JOptionPane.showMessageDialog(panel, "파일을 선택해주세요.");
	                }
	            }
	        });
	        panel.add(submitFileButton);
	
	        // Content Area
	        JTextArea contentArea = new JTextArea();
	        contentArea.setFont(HFont.deriveFont(Font.BOLD, 15f));
	        contentArea.setBounds(20, 80, 700, 300);
	        contentArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	        contentArea.setEditable(false);
	        contentArea.setText(entry[3]); 		// 내용
	        panel.add(contentArea);
	
	        // 첨부파일 클릭 이벤트 처리
	        fileLabel.addMouseListener(new MouseAdapter() {
	            public void mouseClicked(MouseEvent e) {
	            	String url = entry[4];
	            	try {
	                    java.awt.Desktop.getDesktop().browse(URI.create(url));
	                } catch (IOException ioException) {
	                    JOptionPane.showMessageDialog(null, "링크를 여는 중 오류가 발생했습니다: " + ioException.getMessage());
	                }
	            }
	        });
        } catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
        return panel;
    }

    // CreateListPanel 내부 클래스
    private class CreateListPanel {
        private JScrollPane scrollPane;

        public CreateListPanel(ArrayList<String[]> data) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(Color.WHITE);
            try {
     			// 폰트 파일 로드
     			Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));
    		
            for (String[] entry : data) {
                JPanel itemPanel = new JPanel();
                itemPanel.setLayout(new BorderLayout());
                itemPanel.setMaximumSize(new Dimension(780, 50));
                itemPanel.setBackground(Color.WHITE);
                itemPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

                JLabel titleLabel = new JLabel(entry[0]);
                titleLabel.setFont(HFont.deriveFont(Font.BOLD, 15f));
                titleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                titleLabel.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent evt) {
                        // 목록 중 하나를 클릭했을 때 세부 내용 표시
                        if (command.equals("온라인 강의")) {
                            contentPanel.removeAll();
                            contentPanel.add(createOnlineLectureContentPanel(entry));
                            contentPanel.revalidate();
                            contentPanel.repaint();
                        } else if (command.equals("공지사항")) {
                            contentPanel.removeAll();
                            contentPanel.add(createNoticeContentPanel(entry));
                            contentPanel.revalidate();
                            contentPanel.repaint();
                        } else if (command.equals("강의자료")) {
                            contentPanel.removeAll();
                            contentPanel.add(createMaterialContentPanel(entry));
                            contentPanel.revalidate();
                            contentPanel.repaint();
                        } else if (command.equals("과제")) {
                            contentPanel.removeAll();
                            contentPanel.add(createAssignmentContentPanel(entry));
                            contentPanel.revalidate();
                            contentPanel.repaint();
                        }
                    }
                });

                JLabel dateLabel = new JLabel(entry[5]);
                dateLabel.setFont(HFont.deriveFont(Font.BOLD, 15f));
                dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);

                JLabel professorLabel = new JLabel(entry[1]);
                professorLabel.setFont(HFont.deriveFont(Font.BOLD, 15f));
                professorLabel.setHorizontalAlignment(SwingConstants.RIGHT);

                JPanel textPanel = new JPanel(new GridLayout(1, 2));
                textPanel.setBackground(Color.WHITE);
                textPanel.add(dateLabel);
                textPanel.add(professorLabel);

                itemPanel.add(titleLabel, BorderLayout.CENTER);
                itemPanel.add(textPanel, BorderLayout.EAST);

                panel.add(itemPanel);
            }

            scrollPane = new JScrollPane(panel);
            scrollPane.setBounds(0, 0, 780, 400);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
        }	catch (FontFormatException | IOException e) {
				e.printStackTrace();
        	}
		}
        public JScrollPane getScrollPane() {
            return scrollPane;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LectureDetailWindow(userId, lectureId));
    }
}
