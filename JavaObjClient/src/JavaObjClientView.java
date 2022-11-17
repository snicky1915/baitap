import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class JavaObjClientView extends JFrame {
	private static final long serialVersionUID = 1L;

	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	private Socket socket; // 연결소켓

	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	private JPanel contentPane;
	private JTextArea inputTextArea;
	private String UserName;
	private JButton sendButton;
	private JTextPane chatTextPane;
	private JPanel inputPanel;
	private JPanel upperPanel;
	private JLabel titleLabel;
	private JLabel roomImageLabel;
	private JPanel infoPanel;
	private JPanel bottomPanel;
	private JScrollPane inputScrollPane;
	private JPanel textPanel;
	private JLabel countLabel;
	private JPanel listPanel;
	private JLabel personIconLabel;
	private JScrollPane chatScrollPane;
	private JCheckBox sleepCheckBox;

	private Frame frame;
	private FileDialog fd;
	private JButton imgBtn;

	SendTextAction sendTextAction = new SendTextAction();
	SendImageAction sendImageAction = new SendImageAction();

	ImageIcon icon1 = new ImageIcon("src/icon1.jpg");
	ImageIcon goodIcon = new ImageIcon("src/good.jpg");
	ImageIcon hahaIcon = new ImageIcon("src/haha.jpg");

	private static final String TEXT_SUBMIT = "text-submit";
	private static final String INSERT_BREAK = "insert-break";

	public JavaObjClientView(String username, String ip_addr, String port_no) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(360, 640);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		// Upper panel
		upperPanel = new JPanel();
		upperPanel.setLayout(new BorderLayout(0, 0));
		contentPane.add(upperPanel, BorderLayout.NORTH);

		infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
		upperPanel.add(infoPanel, BorderLayout.WEST);

		roomImageLabel = new JLabel(new ImageIcon("src/Icon48.png"));
		roomImageLabel.setPreferredSize(new Dimension(48, 48));
		infoPanel.add(roomImageLabel);

		textPanel = new JPanel();
		infoPanel.add(textPanel);
		textPanel.setLayout(new BorderLayout(0, 0));

		titleLabel = new JLabel("채팅방");
		titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
		titleLabel.setFont(new Font(".AppleSystemUIFont", Font.PLAIN, 15));
		textPanel.add(titleLabel, BorderLayout.NORTH);

		listPanel = new JPanel();
		listPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		listPanel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "200", "/list");
				sendObject(msg);
			}
		});
		textPanel.add(listPanel, BorderLayout.WEST);

		personIconLabel = new JLabel(new ImageIcon("src/person-fill.png"));
		personIconLabel.setPreferredSize(new Dimension(10, 10));
		listPanel.add(personIconLabel);

		countLabel = new JLabel("0"); // TODO: 사용자 수 카운트
		countLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
		countLabel.setFont(new Font(".AppleSystemUIFont", Font.PLAIN, 12));
		countLabel.setForeground(Color.GRAY);
		listPanel.add(countLabel);

		// Chat panel

		chatScrollPane = new JScrollPane();
		chatScrollPane.setBorder(null);
		contentPane.add(chatScrollPane, BorderLayout.CENTER);

		chatTextPane = new JTextPane();
		chatTextPane.setEditable(true);
		chatTextPane.setFont(new Font(".AppleSystemUIFont", Font.PLAIN, 14));
		chatTextPane.setBackground(SystemColor.window);
		chatTextPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		chatScrollPane.setViewportView(chatTextPane);

		// Input panel
		inputPanel = new JPanel();
		inputPanel.setLayout(new BorderLayout(0, 0));
		contentPane.add(inputPanel, BorderLayout.SOUTH);

		inputScrollPane = new JScrollPane();
		inputScrollPane.setBorder(null);
		inputScrollPane.setPreferredSize(new Dimension(0, 80));
		inputPanel.add(inputScrollPane, BorderLayout.CENTER);

		inputTextArea = new JTextArea();
		inputTextArea.setFont(new Font(".AppleSystemUIFont", Font.PLAIN, 14));
		inputTextArea.setLineWrap(true);
		inputTextArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		InputMap input = inputTextArea.getInputMap();
		KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
		KeyStroke shiftEnter = KeyStroke.getKeyStroke("shift ENTER");
		input.put(shiftEnter, INSERT_BREAK);
		input.put(enter, TEXT_SUBMIT);
		inputTextArea.getActionMap().put(TEXT_SUBMIT, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = inputTextArea.getText();
				sendText(msg);
				inputTextArea.setText("");
				inputTextArea.requestFocus();
			}
		});
		inputScrollPane.setViewportView(inputTextArea);

		bottomPanel = new JPanel();
		bottomPanel.setBackground(Color.WHITE);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		bottomPanel.setLayout(new BorderLayout(0, 0));
		inputPanel.add(bottomPanel, BorderLayout.SOUTH);

		sendButton = new JButton("Send");
		sendButton.setFont(new Font(".AppleSystemUIFont", Font.PLAIN, 12));
		sendButton.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		sendButton.setBackground(Color.BLACK);
		sendButton.setForeground(Color.WHITE);
		sendButton.setOpaque(true);
		bottomPanel.add(sendButton, BorderLayout.EAST);

		imgBtn = new JButton("+");
		imgBtn.setFont(new Font("굴림", Font.PLAIN, 16));
		bottomPanel.add(imgBtn, BorderLayout.WEST);

		sleepCheckBox = new JCheckBox("방해 금지");
		sleepCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					ChatMsg msg = new ChatMsg(UserName, "200", "/sleep");
					sendObject(msg);
				} else {
					ChatMsg msg = new ChatMsg(UserName, "200", "/wakeup");
					sendObject(msg);
				}
			}
		});
		bottomPanel.add(sleepCheckBox);

		// Initialize
		setVisible(true);
		appendText("User " + username + " connecting " + ip_addr + " " + port_no);
		UserName = username;

		imgBtn.addActionListener(sendImageAction);
		sendButton.addActionListener(sendTextAction);
		inputTextArea.requestFocus();

		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			ChatMsg obcm = new ChatMsg(UserName, "100", "Hello");
			sendObject(obcm);

			ListenNetwork net = new ListenNetwork();
			net.start();
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
			appendText("connect error");
		}
	}

	// Server Message를 수신해서 화면에 표시
	class ListenNetwork extends Thread {
		public void run() {
			StyledDocument doc = chatTextPane.getStyledDocument();
			SimpleAttributeSet left = new SimpleAttributeSet();
			StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
			SimpleAttributeSet right = new SimpleAttributeSet();
			StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);

			while (true) {
				try {
					Object obcm = null;
					String msg = null;
					ChatMsg cm;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						msg = String.format("[%s] %s", cm.getId(), cm.getData());
					} else
						continue;
					switch (cm.getCode()) {
					case "200": // chat message
						if (cm.getId().equals(UserName))
							doc.setParagraphAttributes(doc.getLength(), 1, right, false);
						else
							doc.setParagraphAttributes(doc.getLength(), 1, left, false);
						if (cm.getData().equals("(굿)")) {
							appendText("[" + cm.getId() + "]");
							appendImage(goodIcon);
						} else if (cm.getData().equals("(하하)")) {
							appendText("[" + cm.getId() + "]");
							appendImage(hahaIcon);
						} else {
							appendText(msg);
						}
						break;
					case "300": // Image 첨부
						if (cm.getId().equals(UserName))
							doc.setParagraphAttributes(doc.getLength(), 1, right, false);
						else
							doc.setParagraphAttributes(doc.getLength(), 1, left, false);
						appendText("[" + cm.getId() + "]");
						appendImage(cm.img);
						break;
					case "400": // File 첨부
						break;
					case "500": // Emoji 첨부
						break;
					}
				} catch (IOException e) {
					appendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						socket.close();
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝

			}
		}
	}

	// keyboard enter key 치면 서버로 전송
	class SendTextAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button을 누르거나 메시지 입력하고 Enter key 치면
			if (e.getSource() == sendButton || e.getSource() == inputTextArea) {
				String msg = null;
				// msg = String.format("[%s] %s\n", UserName, txtInput.getText());
				msg = inputTextArea.getText();
				sendText(msg);
				inputTextArea.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				inputTextArea.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
			}
		}
	}

	class SendImageAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
			if (e.getSource() == imgBtn) {
				frame = new Frame("이미지첨부");
				fd = new FileDialog(frame, "이미지 선택", FileDialog.LOAD);
				// frame.setVisible(true);
				// fd.setDirectory(".\\");
				fd.setVisible(true);
				// System.out.println(fd.getDirectory() + fd.getFile());
				ChatMsg obcm = new ChatMsg(UserName, "300", "IMG");
				ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
				obcm.setImg(img);
				sendObject(obcm);
			}
		}
	}

	// Append icon in textArea
	public void appendIcon(ImageIcon icon) {
		int len = chatTextPane.getDocument().getLength();
		chatTextPane.setCaretPosition(len);
		chatTextPane.insertIcon(icon);
	}

	// Append text in textArea
	public void appendText(String msg) {
		msg = msg.trim();
		int len = chatTextPane.getDocument().getLength();
		chatTextPane.setCaretPosition(len);
		chatTextPane.replaceSelection(msg + "\n");
	}

	// Append image in textArea
	public void appendImage(ImageIcon ori_icon) {
		int len = chatTextPane.getDocument().getLength();
		chatTextPane.setCaretPosition(len); // place caret at the end (with no selection)
		Image ori_img = ori_icon.getImage();
		int width, height;
		double ratio;
		width = ori_icon.getIconWidth();
		height = ori_icon.getIconHeight();
		// Image가 너무 크면 최대 가로 또는 세로 200 기준으로 축소시킨다.
		if (width > 200 || height > 200) {
			if (width > height) { // 가로 사진
				ratio = (double) height / width;
				width = 200;
				height = (int) (width * ratio);
			} else { // 세로 사진
				ratio = (double) width / height;
				height = 200;
				width = (int) (height * ratio);
			}
			Image new_img = ori_img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			ImageIcon new_icon = new ImageIcon(new_img);
			chatTextPane.insertIcon(new_icon);
		} else
			chatTextPane.insertIcon(ori_icon);
		len = chatTextPane.getDocument().getLength();
		chatTextPane.setCaretPosition(len);
		chatTextPane.replaceSelection("\n");
		// ImageViewAction viewaction = new ImageViewAction();
		// new_icon.addActionListener(viewaction); // 내부클래스로 액션 리스너를 상속받은 클래스로
	}

	// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
	public byte[] makePacket(String msg) {
		byte[] packet = new byte[BUF_LEN];
		byte[] bb = null;
		int i;
		for (i = 0; i < BUF_LEN; i++)
			packet[i] = 0;
		try {
			bb = msg.getBytes("euc-kr");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(0);
		}
		for (i = 0; i < bb.length; i++)
			packet[i] = bb[i];
		return packet;
	}

	// Send text to server
	public void sendText(String msg) {
		ChatMsg obcm = new ChatMsg(UserName, "200", msg);
		sendObject(obcm);
	}

	// Send object to server
	public void sendObject(Object ob) {
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			appendText("SendObject Error");
		}
	}
}
