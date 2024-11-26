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

public class LectureDetailWindowInstructor extends JFrame implements ActionListener {
	private JPanel contentPanel;
	private JButton[] menuButtons;
	private static int lectureId; // 강의 코드
	private String lectureName; // 강의 이름
	private String lectureInstructor; // 강사 이름
	private String lectureInfo; // 강의 설명
	private JLabel titleLabel;
	private String command;
	private static String userId; // 사용자 ID를 저장할 변수
	private DBMgr mgr;

	// 수정 가능한 필드와 버튼 추가
	private JButton editButton;
	private JButton saveButton;
	private JButton registerButton; // 등록 버튼을 클래스 멤버로 선언

	// 데이터를 저장할 구조체 추가
	private ArrayList<String[]> noticeList;
	private ArrayList<String[]> materialList;
	private ArrayList<String[]> onlineLectureList;
	private ArrayList<String[]> assignmentList;
	private ArrayList<String[]> liveLectureList;
	private ArrayList<String[]> allData;

	// JxBrowser 엔진 및 브라우저 객체
	private Engine engine;
	private Browser browser;
	
	// PostType enum 정의
    public enum PostType {
        공지사항, 강의자료, 과제, 온라인강의
    }

	public LectureDetailWindowInstructor(String userId, int lectureId) {
		mgr = new DBMgr(); // DBMgr 객체를 초기화
		this.userId = userId;
		this.lectureId = lectureId;

		// JxBrowser 엔진 초기화
        engine = Engine.newInstance(
            EngineOptions.newBuilder(HARDWARE_ACCELERATED)
                .licenseKey("OK6AEKNYF23G6Y3HUAYH7MUFDV3AAF00IFLXOC8RCEM6KDCA2GZ4FFAJZYHYXQ1E62U3MGITOXM57OZRVLAS3H81ZKBXSBNLFSFO3MRKU42OSMZMHAMU4T18O0MFQTF109EQO3TW0MIMU9NP7")  // 라이센스 키를 입력해주세요.
                .build());
        browser = engine.newBrowser();

		loadLectureDetails(); // 강의 상세 정보 초기화
		loadLectureData(); // 공지사항, 자료, 온라인 강의, 과제 정보

		// 프레임 설정
		setTitle("강의실 상세 화면 (강사용)");
		setSize(1440, 1024);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);

		// 커스텀 폰트 로드
		try {
			// 폰트 파일 로드
			Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));

			// 메인 패널 생성
			JPanel mainPanel = new JPanel();
			mainPanel.setBounds(100, 150, 1000, 570); // 패널 위치와 크기 설정
			mainPanel.setBackground(Color.WHITE);
			mainPanel.setLayout(null);
			getContentPane().add(mainPanel);

			// 좌측 메뉴 패널 생성
			JPanel menuPanel = new JPanel(new GridLayout(7, 1, 10, 10));
			menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
			menuPanel.setBounds(12, 84, 160, 400); // 메뉴 패널 위치와 크기 설정
			mainPanel.add(menuPanel);

			String[] buttonLabels = { "수업계획서", "공지사항", "강의자료", "온라인 강의", "실시간 강의", "과제", "돌아가기" };

			menuButtons = new JButton[buttonLabels.length];

			for (int i = 0; i < buttonLabels.length; i++) {
				JButton button = new JButton(buttonLabels[i]);
				button.setFont(HFont.deriveFont(Font.BOLD, 20f));
				button.setPreferredSize(new Dimension(140, 40)); // 버튼 크기 조정
				button.setBackground(new Color(235, 212, 212));
				button.setForeground(Color.BLACK);
				button.setOpaque(true);
				button.setBorderPainted(false);
				button.addActionListener(this);
				menuPanel.add(button);
				menuButtons[i] = button;
			}

			// 상단 라벨
			titleLabel = new JLabel("", SwingConstants.LEFT);
			titleLabel.setFont(HFont.deriveFont(Font.BOLD, 25f));
			titleLabel.setBounds(20, 20, 400, 30); // 상단 라벨 위치와 크기 설정
			mainPanel.add(titleLabel);

			// 내용 패널 생성
			contentPanel = new JPanel(new CardLayout());
			contentPanel.setBounds(200, 60, 780, 450); // 내용 패널 위치와 크기 설정
			contentPanel.setBackground(Color.WHITE);
			mainPanel.add(contentPanel);

			// 등록 버튼을 클래스 멤버로 추가
			registerButton = new JButton("등록");
			registerButton.setFont(HFont.deriveFont(Font.BOLD, 25f));
			registerButton.setBounds(900, 20, 80, 40); // 우측 상단에 위치 조정
			registerButton.setBackground(new Color(235, 212, 212));
			registerButton.setForeground(Color.BLACK);
			registerButton.setOpaque(true);
			registerButton.setBorderPainted(false);
			mainPanel.add(registerButton);

		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}

		// 프레임을 화면에 표시
		setVisible(true);

		// ActionListener를 한 번만 추가
		registerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String command = ((JButton) e.getSource()).getActionCommand();
				openRegisterWindow(command, userId);
			}
		});

		// 초기 상태로 첫 번째 버튼을 클릭한 것처럼 보이게 설정
		menuButtons[0].doClick();
	}

	private void loadLectureDetails() {
		// 수업계획서에 필요한 정보 로드
		String[] lectureDetails = mgr.loadLecturePlan(lectureId); // [0] = 교과목명, [1] = 담당강사, [2] = 설명
		this.lectureName = lectureDetails[0];
		this.lectureInstructor = lectureDetails[1];
		this.lectureInfo = lectureDetails[2];
	}

	private void loadLectureData() {
		allData = mgr.loadNoticeData(lectureId); // 모든 데이터를 불러옴

		// 각 유형별로 데이터를 분류
		noticeList = new ArrayList<>();
		onlineLectureList = new ArrayList<>();
		materialList = new ArrayList<>();
		assignmentList = new ArrayList<>();

		for (String[] entry : allData) {
			String postType = entry[2]; // 게시글 유형
			switch (postType) {
			case "공지사항":
				noticeList.add(entry);
				break;
			case "온라인강의":
				onlineLectureList.add(entry);
				break;
			case "강의자료":
				materialList.add(entry);
				break;
			case "과제":
				assignmentList.add(entry);
				break;
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		command = e.getActionCommand();

		contentPanel.removeAll(); // 기존 내용을 지우고 새로운 내용을 추가합니다.

		// 모든 버튼의 배경색을 기본 색상으로 초기화
		for (JButton button : menuButtons) {
			button.setBackground(new Color(235, 212, 212));
		}

		JButton clickedButton = (JButton) e.getSource();
		clickedButton.setBackground(new Color(200, 160, 160)); // 클릭된 버튼의 색상 변경

		titleLabel.setText(command + " | " + lectureName);
		registerButton.setVisible(false);  // 기본적으로 등록 버튼을 숨김
		// "등록" 버튼의 표시 여부를 제어
		if (command.equals("수업계획서")) {
			JPanel lecturePlanPanel = createLecturePlanPanel();
			contentPanel.add(lecturePlanPanel);
		} else if (command.equals("공지사항")) {
			registerButton.setVisible(true); // 공지사항에서는 "등록" 버튼을 표시
			registerButton.setActionCommand("공지사항 등록"); // 공지사항 등록 명령 설정
			contentPanel.add(new createListPanel(noticeList).getScrollPane());
		} else if (command.equals("강의자료")) {
			registerButton.setVisible(true); // 강의자료에서는 "등록" 버튼을 표시
			registerButton.setActionCommand("강의자료 등록"); // 강의자료 등록 명령 설정
			contentPanel.add(new createListPanel(materialList).getScrollPane());
		} else if (command.equals("온라인 강의")) {
			registerButton.setVisible(true); // 온라인 강의에서는 "등록" 버튼을 표시
			registerButton.setActionCommand("온라인 강의 등록"); // 온라인 강의 등록 명령 설정
			contentPanel.add(new createListPanel(onlineLectureList).getScrollPane());
		} else if (command.equals("실시간 강의")) {
			try {
	 			// 폰트 파일 로드
		 		Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));
	            // 실시간 강의에서는 기존 등록 버튼을 숨기고 새로운 버튼을 추가
	            contentPanel.setLayout(null); // Null layout 사용
	            JButton liveButton = new JButton("실시간 강의 개설");
	            // 버튼의 크기
	            int buttonWidth = 400;
	            int buttonHeight = 100;
	            // ContentPanel의 크기
	            int panelWidth = contentPanel.getWidth();
	            int panelHeight = contentPanel.getHeight();
	            // 버튼을 가운데에 배치하기 위한 좌표 계산
	            int xPosition = (panelWidth - buttonWidth) / 2;
	            int yPosition = (panelHeight - buttonHeight) / 2;
	
	            liveButton.setBounds(xPosition, yPosition, buttonWidth, buttonHeight);  // 위치와 크기 설정
	            liveButton.setBackground(new Color(235, 212, 212));  // 배경색 설정
	            liveButton.setForeground(Color.BLACK);  // 글자색 설정
	            liveButton.setFont(HFont.deriveFont(Font.BOLD, 30f));  // 글꼴 설정
	            liveButton.setOpaque(true);
	            liveButton.setBorderPainted(false);
	            
	            // 버튼 클릭 시 웹 브라우저에서 특정 링크를 여는 이벤트 추가
	            liveButton.addActionListener(new ActionListener() {
	                @Override
	                public void actionPerformed(ActionEvent e) {
	                    String url = "https://113.198.238.111:8443/ZoomIntegration/CreateLecture.jsp";  // 여기에 원하는 URL 입력
	                    try {
	                        java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
	                    } catch (IOException ioException) {
	                        JOptionPane.showMessageDialog(null, "링크를 여는 중 오류가 발생했습니다: " + ioException.getMessage());
	                    }
	                }
	            });
	            contentPanel.add(liveButton);  // 새로운 버튼을 contentPanel에 추가
			} catch (FontFormatException | IOException e1) {
				e1.printStackTrace();
			}
		} else if (command.equals("과제")) {
			registerButton.setVisible(true); // 과제에서는 "등록" 버튼을 표시
			registerButton.setActionCommand("과제 등록"); // 과제 등록 명령 설정
			contentPanel.add(new createListPanel(assignmentList).getScrollPane());
		} else if (command.equals("돌아가기")) {
			dispose(); // 현재 창을 닫음
			new LectureMainWindowInstructor(userId); // 돌아가기 버튼이 기존의 메인 윈도우로 돌아가도록 설정
		}

		contentPanel.revalidate();
		contentPanel.repaint();
	}

	// 수업계획서
	private JPanel createLecturePlanPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		// 커스텀 폰트 로드
		try {
			// 폰트 파일 로드
			Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));

			JLabel nameLabel = new JLabel("교과목명  |  " + lectureName);
			nameLabel.setFont(HFont.deriveFont(Font.BOLD, 20f));
			nameLabel.setBounds(50, 30, 400, 30);
			panel.add(nameLabel);

			JLabel codeLabel = new JLabel("강의코드  |  " + lectureId);
			codeLabel.setFont(HFont.deriveFont(Font.BOLD, 20f));
			codeLabel.setBounds(50, 80, 400, 30);
			panel.add(codeLabel);

			JLabel professorLabel = new JLabel("담당강사  |  " + lectureInstructor);
			professorLabel.setFont(HFont.deriveFont(Font.BOLD, 20f));
			professorLabel.setBounds(50, 130, 400, 30);
			panel.add(professorLabel);

			JLabel lectureInfoLabel = new JLabel("강의정보");
			lectureInfoLabel.setFont(HFont.deriveFont(Font.BOLD, 20f));
			lectureInfoLabel.setBounds(50, 180, 400, 30);
			panel.add(lectureInfoLabel);

			JTextArea lectureInfoText = new JTextArea(lectureInfo);
			lectureInfoText.setFont(HFont.deriveFont(Font.BOLD, 20f));
			lectureInfoText.setLineWrap(true);
			lectureInfoText.setWrapStyleWord(true);
			lectureInfoText.setBounds(50, 210, 680, 150);
			lectureInfoText.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			lectureInfoText.setEditable(false); // 처음에는 편집 불가능 상태
			panel.add(lectureInfoText);

			// 수정 및 저장 버튼
			editButton = new JButton("수정");
			editButton.setBounds(520, 370, 100, 20); // 버튼 크기 조정
			editButton.setFont(HFont.deriveFont(Font.BOLD, 15f));
			editButton.setBackground(new Color(235, 212, 212));
			editButton.setForeground(Color.BLACK);
			editButton.setOpaque(true);
			editButton.setBorderPainted(false);
			editButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					lectureInfoText.setEditable(true); // 수정 가능하도록 변경
					lectureInfoText.requestFocus();
				}
			});
			panel.add(editButton);

			saveButton = new JButton("저장");
			saveButton.setBounds(630, 370, 100, 20); // 버튼 크기 조정
			saveButton.setBackground(new Color(235, 212, 212));
			saveButton.setFont(HFont.deriveFont(Font.BOLD, 15f));
			saveButton.setForeground(Color.BLACK);
			saveButton.setOpaque(true);
			saveButton.setBorderPainted(false);
			saveButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					mgr.updatelectureInfo(lectureId, lectureInfoText.getText());
					loadLectureDetails(); // 강의 상세 정보 초기화
					lectureInfoText.setEditable(false); // 저장 후 다시 편집 불가능 상태로 변경
					JOptionPane.showMessageDialog(panel, "강의 정보가 저장되었습니다.");
				}
			});
			panel.add(saveButton);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}

		return panel;
	}

	// "등록" 버튼 클릭 시 열리는 창 구현
    private void openRegisterWindow(String tabName, String userId) {
        // 커스텀 폰트 로드
        try {
            // 폰트 파일 로드
            Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));

            JFrame registerFrame = new JFrame(tabName);
            registerFrame.setSize(600, 500);
            registerFrame.setLayout(null);
            registerFrame.getContentPane().setBackground(Color.WHITE);

            // 제목 입력 필드
            JLabel titleLabel = new JLabel("제목:");
            titleLabel.setBounds(50, 20, 100, 30);
            titleLabel.setFont(HFont.deriveFont(Font.BOLD, 15f));
            registerFrame.add(titleLabel);

            JTextField titleField = new JTextField();
            titleField.setFont(HFont.deriveFont(Font.BOLD, 15f));
            titleField.setBounds(150, 20, 400, 30);
            registerFrame.add(titleField);

            // 내용 입력 필드
            JLabel contentLabel = new JLabel("내용:");
            contentLabel.setFont(HFont.deriveFont(Font.BOLD, 15f));
            contentLabel.setBounds(50, 70, 100, 30);
            registerFrame.add(contentLabel);

            JTextArea contentArea = new JTextArea();
            contentArea.setFont(HFont.deriveFont(Font.BOLD, 15f));
            contentArea.setBounds(150, 70, 400, 200);
            contentArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            contentArea.setLineWrap(true);
            contentArea.setWrapStyleWord(true);
            registerFrame.add(contentArea);

            // 특정 탭에 따라 다르게 구성
            JTextField startDateField = null;
            JTextField endDateField = null;
            JTextField linkField = null;
            JTextField fileField = new JTextField(); // 초기화

            if (tabName.equals("공지사항 등록") || tabName.equals("강의자료 등록") || tabName.equals("과제 등록")) {
                // 첨부 파일 필드
                JLabel fileLabel = new JLabel("첨부파일 :");
                fileLabel.setFont(HFont.deriveFont(Font.BOLD, 15f));
                fileLabel.setBounds(50, 290, 100, 30);
                registerFrame.add(fileLabel);

                fileField.setBounds(150, 290, 300, 30);
                registerFrame.add(fileField);

                JButton fileAddButton = new JButton("+");
                fileAddButton.setFont(HFont.deriveFont(Font.BOLD, 20f));
                fileAddButton.setBounds(460, 290, 50, 30);
                fileAddButton.setBackground(new Color(235, 212, 212));
                fileAddButton.setForeground(Color.BLACK);
                fileAddButton.setOpaque(true);
                fileAddButton.setBorderPainted(false);
                registerFrame.add(fileAddButton);

                // 파일 선택 기능 추가
                fileAddButton.addActionListener(e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    int returnValue = fileChooser.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        fileField.setText(selectedFile.getAbsolutePath());
                    }
                });

                if (tabName.equals("과제 등록")) {
                    // 과제 기간 설정 필드
                    JLabel startDateLabel = new JLabel("시작 날짜:");
                    startDateLabel.setFont(HFont.deriveFont(Font.BOLD, 15f));
                    startDateLabel.setBounds(50, 340, 100, 30);
                    registerFrame.add(startDateLabel);

                    startDateField = new JTextField("YYYY-MM-DD");
                    startDateField.setFont(HFont.deriveFont(Font.BOLD, 15f));
                    startDateField.setBounds(150, 340, 100, 30);
                    registerFrame.add(startDateField);

                    JLabel endDateLabel = new JLabel("종료 날짜:");
                    endDateLabel.setFont(HFont.deriveFont(Font.BOLD, 15f));
                    endDateLabel.setBounds(300, 340, 100, 30);
                    registerFrame.add(endDateLabel);

                    endDateField = new JTextField("YYYY-MM-DD");
                    endDateField.setFont(HFont.deriveFont(Font.BOLD, 15f));
                    endDateField.setBounds(400, 340, 100, 30);
                    registerFrame.add(endDateField);

                    configureDateField(startDateField);
                    configureDateField(endDateField);
                }
            } else if (tabName.equals("온라인 강의 등록")) {
                // 링크 입력 필드
                JLabel linkLabel = new JLabel("링크 :");
                linkLabel.setFont(HFont.deriveFont(Font.BOLD, 15f));
                linkLabel.setBounds(50, 290, 100, 30);
                registerFrame.add(linkLabel);

                linkField = new JTextField();
                linkField.setFont(HFont.deriveFont(Font.BOLD, 15f));
                linkField.setBounds(150, 290, 400, 30);
                registerFrame.add(linkField);
            }

            // 현재 날짜를 가져와 형식 지정
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = currentDate.format(formatter);

            // 등록 버튼
            JButton submitButton = new JButton("등록");
            submitButton.setFont(HFont.deriveFont(Font.BOLD, 20f));
            submitButton.setBounds(460, 390, 80, 30);
            submitButton.setBackground(new Color(235, 212, 212));
            submitButton.setForeground(Color.BLACK);
            submitButton.setOpaque(true);
            submitButton.setBorderPainted(false);

            JTextField finalStartDateField = startDateField;
            JTextField finalEndDateField = endDateField;
            JTextField finalLinkField = linkField;

            submitButton.addActionListener(e -> {
                if (titleField.getText().isEmpty() || contentArea.getText().isEmpty() ||
                        (tabName.equals("과제 등록") && (finalStartDateField.getText().isEmpty() || finalEndDateField.getText().isEmpty())) ||
                        (tabName.equals("온라인 강의 등록") && finalLinkField.getText().isEmpty())) {

                    JOptionPane.showMessageDialog(registerFrame, "모든 필드를 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 날짜 형식 확인
                if (tabName.equals("과제 등록")) {
                    String startDate = finalStartDateField.getText();
                    String endDate = finalEndDateField.getText();

                    if (startDate.equals("YYYY-MM-DD") || endDate.equals("YYYY-MM-DD")) {
                        JOptionPane.showMessageDialog(registerFrame, "날짜를 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // Enum 값을 이용해 postType을 설정
                PostType postType = null;
                String filePath = null;
                if (tabName.contains("공지사항")) {
                    postType = PostType.공지사항;
                    filePath = fileField.getText(); // 공지사항 파일 경로 설정
                } else if (tabName.contains("강의자료")) {
                    postType = PostType.강의자료;
                    filePath = fileField.getText(); // 강의자료 파일 경로 설정
                } else if (tabName.contains("과제")) {
                    postType = PostType.과제;
                    filePath = fileField.getText(); // 과제 파일 경로 설정
                } else if (tabName.contains("온라인 강의")) {
                    postType = PostType.온라인강의;
                    filePath = finalLinkField.getText(); // 온라인 강의의 경우 링크를 filePath에 저장
                }

                // DB 저장 로직 호출
                String dueDate = (tabName.equals("과제 등록")) ? finalEndDateField.getText() : null;
                int uploadId = mgr.insertPost(userId, lectureId, postType.name(), titleField.getText(), contentArea.getText(), filePath, formattedDate, dueDate);

                if (uploadId > 0) {
                    if (tabName.equals("과제 등록")) {
                        // 과제 등록 시 학생들의 미제출 상태 추가
                        mgr.insertSubmitRecords(lectureId, uploadId);
                    }
                    JOptionPane.showMessageDialog(registerFrame, tabName + "이(가) 성공적으로 등록되었습니다.");
                    loadLectureData();
                    registerFrame.dispose();
                    registerFrame.repaint();
                    // 탭을 새로고침하여 새로운 데이터가 보이도록 함
                    refreshTab(tabName.replace(" 등록", ""));
                } else {
                    JOptionPane.showMessageDialog(registerFrame, "등록 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            });
            registerFrame.add(submitButton);
            registerFrame.setVisible(true);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

	private void configureDateField(JTextField dateField) {
		dateField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (dateField.getText().equals("YYYY-MM-DD")) {
					dateField.setText("");
					dateField.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (dateField.getText().isEmpty()) {
					dateField.setForeground(Color.GRAY);
					dateField.setText("YYYY-MM-DD");
				}
			}
		});

		dateField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!Character.isDigit(c) || dateField.getText().length() >= 8) {
					e.consume(); // 숫자가 아니거나 길이가 8자리를 넘으면 입력을 막음
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (dateField.getText().length() == 8) {
					String dateText = dateField.getText();
					String formattedDate = dateText.substring(0, 4) + "-" + dateText.substring(4, 6) + "-"
							+ dateText.substring(6);
					dateField.setText(formattedDate);
				}
			}
		});
	}
	
    private void refreshTab(String tabName) {
        if (tabName.equals("공지사항")) {
            contentPanel.removeAll();
            contentPanel.add(new createListPanel(noticeList).getScrollPane());
        } else if (tabName.equals("강의자료")) {
            contentPanel.removeAll();
            contentPanel.add(new createListPanel(materialList).getScrollPane());
        } else if (tabName.equals("온라인 강의")) {
            contentPanel.removeAll();
            contentPanel.add(new createListPanel(onlineLectureList).getScrollPane());
        } else if (tabName.equals("실시간 강의")) {
            contentPanel.removeAll();
            contentPanel.add(new createListPanel(liveLectureList).getScrollPane());
        } else if (tabName.equals("과제")) {
            contentPanel.removeAll();
            contentPanel.add(new createListPanel(assignmentList).getScrollPane());
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

	// 온라인강의 표시
	private JPanel createOnlineLectureContentPanel(String entry[]) {
    	// [0] = 제목, [1] = 쓴사람, [2] = 유형, [3] = 내용, [4] = 파일경로, [5] = 게시일, [6] = 마감일, [7] = 업로드 번호
		registerButton.setVisible(false); // 내용 보기 시 등록 버튼 숨기기
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.WHITE);
		// 커스텀 폰트 로드
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
		registerButton.setVisible(false); // 내용 보기 시 등록 버튼 숨기기
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBackground(Color.WHITE);
		// 커스텀 폰트 로드
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
					String url = entry[4]; // 실제 파일 URL로 변경
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
		registerButton.setVisible(false); // 내용 보기 시 등록 버튼 숨기기
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBackground(Color.WHITE);
		// 커스텀 폰트 로드
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
			fileLabel.setFont(HFont.deriveFont(Font.BOLD, 20f));
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
			contentArea.setFont(HFont.deriveFont(Font.BOLD, 20f));
			contentArea.setBounds(20, 80, 700, 300);
			contentArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			contentArea.setEditable(false);
			contentArea.setText(entry[3]);
			panel.add(contentArea);
	
			// 첨부파일 클릭 이벤트 처리
			fileLabel.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					String url = entry[4]; // 실제 파일 URL로 변경
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
		registerButton.setVisible(false); // 내용 보기 시 등록 버튼 숨기기
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBackground(Color.WHITE);
		// 커스텀 폰트 로드
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
			fileLabel.setFont(HFont.deriveFont(Font.BOLD, 20f));
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
			contentArea.setFont(HFont.deriveFont(Font.BOLD, 20f));
			contentArea.setBounds(20, 80, 700, 50);
			contentArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			contentArea.setEditable(false);
			contentArea.setText(entry[3]);
			panel.add(contentArea);
	
			// 첨부파일 클릭 이벤트 처리
			fileLabel.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					String url = entry[4]; // 실제 파일 URL로 변경
					try {
	                    java.awt.Desktop.getDesktop().browse(URI.create(url));
	                } catch (IOException ioException) {
	                    JOptionPane.showMessageDialog(null, "링크를 여는 중 오류가 발생했습니다: " + ioException.getMessage());
	                }
				}
			});

	        // 제출 현황 Label
	        JLabel submissionStatusTitle = new JLabel("제출 현황");
	        submissionStatusTitle.setFont(new Font("맑은 고딕", Font.BOLD, 16));
	        submissionStatusTitle.setBounds(20, 140, 200, 30);
	        panel.add(submissionStatusTitle);

	        // 학생 제출 현황 패널
	        JPanel submissionPanel = new JPanel();
	        submissionPanel.setLayout(new BoxLayout(submissionPanel, BoxLayout.Y_AXIS));
	        submissionPanel.setBounds(20, 180, 700, 300);  // 위치와 크기 조정
	        submissionPanel.setBackground(Color.WHITE);
	    	// [0] = 제목, [1] = 쓴사람, [2] = 유형, [3] = 내용, [4] = 파일경로, [5] = 게시일, [6] = 마감일, [7] = 업로드 번호
	        // 과제 제출 테이블 데이터 
	        ArrayList<String[]> submissions = mgr.loadStudentSubmissions(lectureId, entry[7]);
	        
	        // 결과 출력
	        for (String[] submission : submissions) {
	            System.out.println("이름: " + submission[0] + ", 이메일: " + submission[1] + ", 제출일: " + submission[2] + ", 상태: " + submission[3]);
	        }

	        for (String[] student : submissions) {
	            JPanel itemPanel = new JPanel();
	            itemPanel.setLayout(new BorderLayout());
	            itemPanel.setMaximumSize(new Dimension(700, 30)); // 크기 조정
	            itemPanel.setBackground(Color.WHITE);

	            JLabel studentInfoLabel = new JLabel(student[0] + " (" + student[1] + ")");
	            studentInfoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

	            JLabel submissionDateLabel = new JLabel(student[2] + "    ");  // 간격을 더 벌리기 위해 공백 추가
	            submissionDateLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
	            submissionDateLabel.setHorizontalAlignment(SwingConstants.RIGHT);

	            String statusText = student[3];
	            Color statusColor = statusText.equals("제출") ? new Color(255, 99, 71) : Color.GRAY; // 제출 시 빨간색

	            JLabel submissionStatusLabel = new JLabel(statusText);
	            submissionStatusLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
	            submissionStatusLabel.setForeground(statusColor);
	            submissionStatusLabel.setHorizontalAlignment(SwingConstants.RIGHT);

	            JPanel textPanel = new JPanel(new BorderLayout());
	            textPanel.setBackground(Color.WHITE);
	            textPanel.add(studentInfoLabel, BorderLayout.WEST);
	            textPanel.add(submissionDateLabel, BorderLayout.CENTER);
	            textPanel.add(submissionStatusLabel, BorderLayout.EAST);

	            itemPanel.add(textPanel, BorderLayout.CENTER);
	            submissionPanel.add(itemPanel);
	        }

	        JScrollPane scrollPane = new JScrollPane(submissionPanel);
	        scrollPane.setBounds(20, 180, 700, 250);  // 위치와 크기 조정
	        scrollPane.setBorder(BorderFactory.createEmptyBorder());
	        panel.add(scrollPane);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		return panel;
	}

	private class createListPanel {
		private JScrollPane scrollPane;
    	// [0] = 제목, [1] = 쓴사람, [2] = 유형, [3] = 내용, [4] = 파일경로, [5] = 게시일, [6] = 마감일, [7] = 업로드 번호
		public createListPanel(ArrayList<String[]> dataList) {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.setBackground(Color.WHITE);
			// 커스텀 폰트 로드
			try {
				// 폰트 파일 로드
				Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));
		
				for (String[] entry : dataList) {
					JPanel itemPanel = new JPanel();
					itemPanel.setLayout(new BorderLayout());
					itemPanel.setMaximumSize(new Dimension(1000, 50));
					itemPanel.setBackground(Color.WHITE);
					itemPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
	
					JLabel titleLabel = new JLabel(entry[0]);
					titleLabel.setFont(HFont.deriveFont(Font.BOLD, 15f));
					titleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
					titleLabel.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent evt) {
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
				scrollPane.setBounds(0, 0, 780, 400); // 스크롤 패널의 위치와 크기 설정
				scrollPane.setBorder(BorderFactory.createEmptyBorder());
			} catch (FontFormatException | IOException e) {
				e.printStackTrace();
			}
		}
		public JScrollPane getScrollPane() {
			return scrollPane;
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new LectureDetailWindowInstructor(userId, lectureId));
	}
}
