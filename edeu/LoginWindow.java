package edeu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class LoginWindow extends JFrame implements ActionListener {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JRadioButton studentButton;
    private JRadioButton instructorButton;
    private DBMgr mgr;

    public LoginWindow() {
    	// DBMgr 객체를 초기화
        mgr = new DBMgr();
        // 프레임 설정
        setTitle("로그인");
        setSize(1440, 1024);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        // 패널 생성 및 설정
        JPanel panel = new JPanel();
        panel.setBounds(370, 150, 700, 600); // 중앙에 위치하게 설정
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);
        getContentPane().add(panel);
        
        // 커스텀 폰트 로드
        try {
            // 폰트 파일 로드
            Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));

            // 제목 라벨 생성
            JLabel titleLabel = new JLabel("E - DEU", SwingConstants.CENTER);
            titleLabel.setFont(HFont.deriveFont(Font.BOLD, 60f));
            titleLabel.setBounds(150, 50, 400, 60);
            panel.add(titleLabel);

            // 로그인 라벨 생성 (왼쪽 정렬)
            JLabel loginLabel = new JLabel("로그인");
            loginLabel.setFont(HFont.deriveFont(Font.BOLD, 30f));
            loginLabel.setBounds(150, 140, 400, 30); // 패널 폭에 맞게 조정
            panel.add(loginLabel);

            // 이메일 입력 필드 (기본 내용 추가)
            //emailField = new JTextField("aaa@gmail.com");
            emailField = new JTextField("이메일을 입력하세요.");
            emailField.setFont(HFont.deriveFont(Font.PLAIN, 20f));
            emailField.setBounds(150, 200, 400, 40);
            emailField.setForeground(Color.GRAY);
            addEmailFieldFocusListener(); // 포커스 이벤트 처리 추가
            emailField.addActionListener(this); // 엔터키 입력 처리
            panel.add(emailField);

            // 비밀번호 입력 필드 (기본 내용 추가)
            //passwordField = new JPasswordField("qwer1234");
            passwordField = new JPasswordField("비밀번호를 입력하세요.");
            passwordField.setFont(HFont.deriveFont(Font.PLAIN, 20f));
            passwordField.setBounds(150, 260, 400, 40);
            passwordField.setForeground(Color.GRAY);
            passwordField.setEchoChar((char) 0); // 기본 상태에서 비밀번호 입력 필드 내용이 보이도록 설정
            addPasswordFieldFocusListener(); // 포커스 이벤트 처리 추가
            passwordField.addActionListener(this); // 엔터키 입력 처리
            panel.add(passwordField);

            // 라디오 버튼 생성 (학생과 강사 선택)
            studentButton = new JRadioButton("학생", true);
            studentButton.setFont(HFont.deriveFont(Font.PLAIN, 20f));
            studentButton.setBounds(150, 320, 200, 40);
            panel.add(studentButton);

            instructorButton = new JRadioButton("강사");
            instructorButton.setFont(HFont.deriveFont(Font.PLAIN, 20f));
            instructorButton.setBounds(350, 320, 200, 40);
            panel.add(instructorButton);

            // 버튼 그룹 생성 (하나만 선택 가능하도록)
            ButtonGroup group = new ButtonGroup();
            group.add(studentButton);
            group.add(instructorButton);

            // 로그인 버튼
            loginButton = new JButton("로그인");
            loginButton.setBounds(150, 380, 400, 40);
            loginButton.setFont(HFont.deriveFont(Font.PLAIN, 20f));
            loginButton.setBackground(new Color(235, 212, 212)); // 배경색 설정
            loginButton.setForeground(Color.BLACK); // 글자 색상 설정
            loginButton.setOpaque(true); // 버튼 배경색 표시 설정
            loginButton.setBorderPainted(false); // 버튼 테두리 비활성화
            loginButton.addActionListener(this); // 로그인 버튼 이벤트 처리 추가
            panel.add(loginButton);

            // 회원가입 버튼
            signupButton = new JButton("회원가입");
            signupButton.setBounds(150, 440, 400, 40);
            signupButton.setFont(HFont.deriveFont(Font.PLAIN, 20f));
            signupButton.setBackground(Color.DARK_GRAY); // 배경색 설정
            signupButton.setForeground(Color.WHITE); // 글자 색상 설정
            signupButton.setOpaque(true); // 버튼 배경색 표시 설정
            signupButton.setBorderPainted(false); // 버튼 테두리 비활성화
            signupButton.addActionListener(this); // 회원가입 버튼 이벤트 처리 추가
            panel.add(signupButton);
            
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        // 프레임을 화면에 표시
        setVisible(true);
    }

    // 이메일 필드 포커스 이벤트 처리
    private void addEmailFieldFocusListener() {
        emailField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (emailField.getText().equals("이메일을 입력하세요.")) {
                    emailField.setText("");
                    emailField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (emailField.getText().isEmpty()) {
                    emailField.setForeground(Color.GRAY);
                    emailField.setText("이메일을 입력하세요.");
                }
            }
        });
    }

    // 비밀번호 필드 포커스 이벤트 처리
    private void addPasswordFieldFocusListener() {
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (new String(passwordField.getPassword()).equals("비밀번호를 입력하세요.")) {
                    passwordField.setText("");
                    passwordField.setEchoChar('●');
                    passwordField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (new String(passwordField.getPassword()).isEmpty()) {
                    passwordField.setForeground(Color.GRAY);
                    passwordField.setText("비밀번호를 입력하세요.");
                    passwordField.setEchoChar((char) 0); // 기본 상태에서 비밀번호 입력 필드 내용이 보이도록 설정
                }
            }
        });
    }

    // ActionListener 구현
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();

     // 로그인 버튼 클릭 또는 엔터키 입력 처리
        if (obj == loginButton || obj == emailField || obj == passwordField) {
            String enteredEmail = emailField.getText();
            String enteredPassword = new String(passwordField.getPassword());
            String selectedRole = studentButton.isSelected() ? "학생" : "강사";
            // DB에서 ROLE을 확인하여 로그인 검증
            String dbRole = mgr.loginCheck(enteredEmail, enteredPassword);
            if (dbRole == null) {
                JOptionPane.showMessageDialog(this, "이메일 또는 비밀번호가 틀렸습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
            } else if (!dbRole.equals(selectedRole)) {
                JOptionPane.showMessageDialog(this, dbRole + "(으)로 로그인해야 합니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
            } else {
                dispose(); // 로그인 창 닫기
                if (studentButton.isSelected()) {
                    new LectureMainWindow(enteredEmail); // 학생용 메인 창
                } else if (instructorButton.isSelected()) {
                    new LectureMainWindowInstructor(enteredEmail); // 강사용 메인 창
                }
            }
        } else if (obj == signupButton) {
            // 회원가입 버튼 클릭 처리
            dispose(); // 현재 로그인 창을 닫음
            new RegistrationWindow(); // 회원가입 창을 띄움
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginWindow());
    }
}
