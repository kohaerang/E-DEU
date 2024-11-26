package edeu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;

public class RegistrationWindow extends JFrame implements ActionListener {
    private JTextField idField;
    private JPasswordField passwordField;
    private JPasswordField passwordchkField;
    private JTextField nameField;
    private JTextField emailField;
    private JButton signupButton;
    private JButton loginButton;
    private JRadioButton studentButton;
    private JRadioButton instructorButton;
    private DBMgr mgr;

    public RegistrationWindow() {
    	// DBMgr 객체를 초기화
        mgr = new DBMgr();
        // 프레임 설정
        setTitle("회원가입");
        setSize(1440, 1024);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        // 패널 생성 및 설정
        JPanel panel = new JPanel();
        panel.setBounds(370, 100, 700, 750); // 중앙에 위치하게 설정
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);
        getContentPane().add(panel);
        try {
 			// 폰트 파일 로드
 			Font HFont = Font.createFont(Font.TRUETYPE_FONT, new File("../Edeu/NanumBarunpenB.TTF"));
			
	
	        // 회원가입 라벨 생성
	        JLabel signupLabel = new JLabel("회원 가입");
	        signupLabel.setFont(HFont.deriveFont(Font.PLAIN, 30f));
	        signupLabel.setBounds(150, 50, 400, 40); // 패널 폭에 맞게 조정
	        panel.add(signupLabel);
	
	        // 아이디 입력 필드
	        emailField = new JTextField("이메일을 입력하세요.");
	        emailField.setBounds(150, 110, 400, 40);
	        emailField.setFont(HFont.deriveFont(Font.PLAIN, 15f));
	        emailField.setForeground(Color.GRAY);
	        addFocusListenerToField(emailField, "이메일을 입력하세요.");
	        panel.add(emailField);
	
	        // 비밀번호 입력 필드
	        passwordField = new JPasswordField("비밀번호를 입력하세요.");
	        passwordField.setBounds(150, 170, 400, 40);
	        passwordField.setFont(HFont.deriveFont(Font.PLAIN, 15f));
	        passwordField.setForeground(Color.GRAY);
	        passwordField.setEchoChar((char) 0); // 기본 상태에서 비밀번호 입력 필드 내용이 보이도록 설정
	        addPasswordFieldFocusListener(passwordField, "비밀번호를 입력하세요.");
	        panel.add(passwordField);
	
	        // 비밀번호 확인 입력 필드
	        passwordchkField = new JPasswordField("비밀번호를 확인하세요.");
	        passwordchkField.setBounds(150, 230, 400, 40);
	        passwordchkField.setFont(HFont.deriveFont(Font.PLAIN, 15f));
	        passwordchkField.setForeground(Color.GRAY);
	        passwordchkField.setEchoChar((char) 0); // 기본 상태에서 비밀번호 입력 필드 내용이 보이도록 설정
	        addPasswordFieldFocusListener(passwordchkField, "비밀번호를 확인하세요.");
	        panel.add(passwordchkField);
	
	        // 이름 입력 필드
	        nameField = new JTextField("이름을 입력하세요.");
	        nameField.setFont(HFont.deriveFont(Font.PLAIN, 15f));
	        nameField.setBounds(150, 290, 400, 40);
	        nameField.setForeground(Color.GRAY);
	        addFocusListenerToField(nameField, "이름을 입력하세요.");
	        panel.add(nameField);
	
	        // 학번 입력 필드
	        idField = new JTextField("학번을 입력하세요.");
	        idField.setFont(HFont.deriveFont(Font.PLAIN, 15f));
	        idField.setBounds(150, 350, 400, 40);
	        idField.setForeground(Color.GRAY);
	        addFocusListenerToField(idField, "학번을 입력하세요.");
	        panel.add(idField);
	
	        // 라디오 버튼 생성
	        studentButton = new JRadioButton("학생");
	        studentButton.setFont(HFont.deriveFont(Font.PLAIN, 15f));
	        studentButton.setHorizontalAlignment(SwingConstants.CENTER);
	        studentButton.setBounds(150, 410, 200, 30);
	        panel.add(studentButton);
	
	        instructorButton = new JRadioButton("강사");
	        instructorButton.setFont(HFont.deriveFont(Font.PLAIN, 15f));
	        instructorButton.setHorizontalAlignment(SwingConstants.CENTER);
	        instructorButton.setBounds(350, 410, 200, 30);
	        panel.add(instructorButton);
	
	        // 버튼 그룹 생성 (하나만 선택 가능하도록)
	        ButtonGroup group = new ButtonGroup();
	        group.add(studentButton);
	        group.add(instructorButton);
	
	        // 로그인 버튼
	        loginButton = new JButton("돌아가기");
	        loginButton.setFont(HFont.deriveFont(Font.BOLD, 20f));
	        loginButton.setBounds(150, 460, 400, 40);
	        loginButton.setBackground(new Color(235, 212, 212)); // 배경색 설정
	        loginButton.setForeground(Color.BLACK); // 글자 색상 설정
	        loginButton.setOpaque(true); // 버튼 배경색 표시 설정
	        loginButton.setBorderPainted(false); // 버튼 테두리 비활성화
	        loginButton.addActionListener(this); // 로그인 버튼 이벤트 처리 추가
	        panel.add(loginButton);
	
	        // 회원가입 버튼
	        signupButton = new JButton("회원가입");
	        signupButton.setFont(HFont.deriveFont(Font.BOLD, 20f));
	        signupButton.setBounds(150, 520, 400, 40);
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

    // 텍스트 필드에 포커스 이벤트 처리 추가
    private void addFocusListenerToField(JTextField field, String placeholder) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }

    // 비밀번호 필드에 포커스 이벤트 처리 추가
    private void addPasswordFieldFocusListener(JPasswordField field, String placeholder) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('●');
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (new String(field.getPassword()).isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                    field.setEchoChar((char) 0);
                }
            }
        });
    }

    // ActionListener 구현
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();

        if (obj == signupButton) {
        	String selectedRole = studentButton.isSelected() ? "학생" : "강사";
        	// 비밀번호를 문자열로 변환
            String password = new String(passwordField.getPassword());
            String passwordCheck = new String(passwordchkField.getPassword());

            // 비밀번호와 비밀번호 확인이 일치하는지 확인
            if (!password.equals(passwordCheck)) {
                JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // DB로 회원 정보 전송
            mgr.userjoin(emailField.getText(), password, nameField.getText(), idField.getText(), selectedRole);
            
            // 회원가입 완료 메시지
            JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다.", "회원가입", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // 현재 창을 닫음
            new LoginWindow(); // 회원가입 후 로그인 창으로 이동
        } else if (obj == loginButton) {
            dispose();
            new LoginWindow();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistrationWindow());
    }
}
