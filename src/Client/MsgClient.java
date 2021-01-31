package Client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MsgClient {
	
	private static JTextArea ta;
	private static JTextField t;
	private static BufferedReader reader;
	private static PrintWriter writer;
	private static String login;

	public static void main(String[] args) {
		go();

	}

	private static void go() {
		
		ImageIcon img = new ImageIcon("src/logo.png");
		JOptionPane.showMessageDialog(null, img, "Messenger",-1);
		login = JOptionPane.showInputDialog("Введите логин:");
		
		JFrame f = new JFrame("Messenger: "+login);
		f.setResizable(false);
		f.setLocationRelativeTo(null);
		JPanel p = new JPanel();
		ta = new JTextArea(15, 30);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		ta.setEditable(false);
		JScrollPane sp = new JScrollPane(ta);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		t = new JTextField(20);
		
		JButton b = new JButton("Отправить");
		b.addActionListener(new Sender());
			
		p.add(sp);
		p.add(t);
		p.add(b);
		setNet();
		
		Thread thread = new Thread(new Listener());
		thread.start();
		
		f.getContentPane().add(BorderLayout.CENTER,p);
		f.setSize(350, 315);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		

	}
	
	private static class Listener implements Runnable{

		@Override
		public void run() {
			String msg;
			try{
				while((msg = reader.readLine())!=null){
					ta.append(msg + "\n");
				}
			}catch(Exception ex){}
			
		}
		
	}
	
	private static class Sender implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			
			String msg = login + ": " + t.getText();
			writer.println(msg);
			writer.flush();
			
			t.setText("");
			t.requestFocus();
			
				
		}
		
	}

	private static void setNet() {
		try {
			Socket socket = new Socket("127.0.0.1",5000);
			InputStreamReader is = new InputStreamReader(socket.getInputStream());
			reader = new BufferedReader(is);
			writer = new PrintWriter(socket.getOutputStream());
			
		} catch (Exception ex){}
	}

}
