package com.client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Chat extends JFrame {
	
	JTextArea jta;
	JTextField jtf;
	JButton jb;
	JPanel jp;
	
	public static void main(String[] args) {

		Chat chat = new Chat("1"); 		
		
	}
	
	public Chat(String friend)
	{
	
		jta = new JTextArea();
		jtf = new JTextField(15);
		jb = new JButton("Send");
		jp = new JPanel();
		jp.add(jtf);
		jp.add(jb);
		
		this.add(jta,"Center");
		this.add(jp, "South");
		this.setTitle("you are talking with "+friend+"");
		this.setIconImage((new ImageIcon().getImage()));
		this.setSize(400,400);
	    this.setVisible(true);
		
	}	

}
