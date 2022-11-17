import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class JavaObjClientMain extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField usernameTextField;
	private Myaction action = new Myaction();

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JavaObjClientMain frame = new JavaObjClientMain();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public JavaObjClientMain() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(360, 640);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setLayout(new GridBagLayout());
		setContentPane(contentPane);

		JLabel subLabel = new JLabel("Email or Phone Number");
		subLabel.setFont(new Font(".AppleSystemUIFont", Font.PLAIN, 10));
		subLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
		subLabel.setBackground(Color.WHITE);
		subLabel.setForeground(Color.GRAY);
		subLabel.setOpaque(true);
		subLabel.setPreferredSize(new Dimension(256, 24));
		GridBagConstraints gbc_subLabel = new GridBagConstraints();
		gbc_subLabel.gridy = 0;
		contentPane.add(subLabel, gbc_subLabel);

		usernameTextField = new JTextField();
		usernameTextField.setFont(new Font(".AppleSystemUIFont", Font.PLAIN, 14));
		usernameTextField.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
		usernameTextField.setPreferredSize(new Dimension(256, 40));
		usernameTextField.addActionListener(action);
		GridBagConstraints gbc_usernameTextField = new GridBagConstraints();
		gbc_usernameTextField.gridy = 1;
		contentPane.add(usernameTextField, gbc_usernameTextField);

		JButton loginButton = new JButton("Log in");
		loginButton.setFont(new Font(".AppleSystemUIFont", Font.PLAIN, 14));
		loginButton.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		loginButton.setBackground(Color.BLACK);
		loginButton.setForeground(Color.WHITE);
		loginButton.setOpaque(true);
		loginButton.setPreferredSize(new Dimension(256, 40));
		loginButton.addActionListener(action);
		GridBagConstraints gbc_loginButton = new GridBagConstraints();
		gbc_loginButton.gridy = 2;
		contentPane.add(loginButton, gbc_loginButton);
	}

	class Myaction implements ActionListener {
		private final String serverIP = "localhost";
		private final String serverPort = "30000";

		@Override
		public void actionPerformed(ActionEvent e) {
			String username = usernameTextField.getText().trim();
			String ip_addr = serverIP;
			String port_no = serverPort;
			new JavaObjClientView(username, ip_addr, port_no);
			setVisible(false);
		}
	}
}
