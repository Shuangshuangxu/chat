package chat;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import chat.Server.ReceiveClientMsgThread;

public class Client extends JFrame{
	private JButton connect;
	private JButton exit;
	private JTextField address;
	private JTextField port;
	private JTextField nick;
	private JToolBar jtb;
	private JTextArea messageArea;
	private JTextField message;
	private JButton send;
	private JCheckBox jcb;
	private JComboBox jb;
	
	

	private Socket socket;

	private Vector<String> nicks = new Vector();
	
	
	
	class ButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == connect){
				
		
				String addr = address.getText();
				int p = Integer.parseInt(port.getText());
		
				if(nick.getText().equals("")){
					messageArea.setText("请输入昵称！");
					return;
				}
				try{
				
					socket = new Socket(addr,p);
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);
		
					pw.println(nick.getText());
					
					String s = br.readLine();
					messageArea.setText(s+"\n\r");
					if(s.equals("昵称重复，请输入新昵称后重新连接！")){
						br.close();
						socket.close();
					}else{
						//已经成功连接到服务器
						connect.setEnabled(false);
						exit.setEnabled(true);
						address.setEditable(false);
						port.setEditable(false);
						nick.setEditable(false);
						message.setEditable(true);
						message.setEnabled(true);
						messageArea.setEnabled(true);
						messageArea.setEditable(true);
						send.setEnabled(true);
						jcb.setEnabled(true);
						//维持和服务器的通信
						new ReceiveServerMsgThread().start();
						

					}
				}catch(Exception ex){
					if(socket == null){
						messageArea.setText("连接失败，请重试！");
					}else{
						messageArea.append(ex.toString()+"\r\n");
					}
				}
			}
			if(e.getSource() == send || e.getSource() == message){
				String s=message.getText();
				if(jcb.isSelected())
				{
					try{
					    PrintWriter pw=new PrintWriter(socket.getOutputStream(),true);
					    pw.println("@私聊@");
					    pw.println((String)jb.getSelectedItem());
					    pw.println(s);
					    message.setText(null);
					    messageArea.append("你对"+(String)jb.getSelectedItem()+"说:\n\r");
					    messageArea.append(s+"\n\r");
					    }catch(IOException ex){
					    messageArea.append(ex.toString()+"\r\n");
					    }
				}
				else
				{
					try{
					    PrintWriter pw=new PrintWriter(socket.getOutputStream(),true);
					    pw.println("@公聊@");
					    pw.println(s);
					    message.setText(null);
					    }catch(IOException ex){
					    messageArea.append(ex.toString()+"\r\n");
					    }
				}	
			    message.setText("");
				}
			if(e.getSource() == jcb){
				if(jcb.isSelected())
				{
					//jb.setEditable(true);
					jb.setEnabled(true);
				}
				else
				{
					//jb.setEditable(false);
					jb.setEnabled(false);
				}
			}
			
			if(e.getSource() == exit){
				try{
					//向所有客户端发送断开信息“@@bye**”
					PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);
					pw.println("@断开连接@");
					pw.close();
						socket.close();

				}catch(IOException ex){
					messageArea.append(ex.toString()+"\r\n");
				}
				//恢复按钮和文本框到初始状态
				connect.setEnabled(true);
				exit.setEnabled(false);
				address.setEditable(true);
				port.setEditable(true);
				nick.setEditable(false);
				message.setEditable(false);
				send.setEnabled(false);
				nicks.removeAllElements();
			}
		}
		
	}
	
	//接收信息线程
	class ReceiveServerMsgThread extends Thread{
		
		public Socket getSocket(){
			return socket;
		}
		
		public void run(){
			try{
				BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter socketOut = new PrintWriter(socket.getOutputStream(),true);
				while(true){
					//接收服务器发来的信息
					String s  = socketIn.readLine();
					//判断服务器是否断开
					if(s.equals("#服务器断开#")){
						//如果是服务器发来的断开信息
						s = "服务器已经退出！";
						socketIn.close();
						socketOut.close();
						socket.close();
						socket = null;
						//恢复按钮的状态
						connect.setEnabled(true);
						exit.setEnabled(false);
						address.setEditable(true);
						port.setEditable(true);
						nick.setEditable(false);
						message.setEditable(false);
						send.setEnabled(false);
					}
					else if(s.equals("#公聊#"))
					{
						String nickName = socketIn.readLine();
						String msg = socketIn.readLine();
						messageArea.append(nickName+"说: \r\n");
						messageArea.append(msg+"\n\r");
					}
					else if(s.equals("#私聊#"))
					{
						String nickName = socketIn.readLine();
						String toNick = socketIn.readLine();
						String msg = socketIn.readLine();
						messageArea.append(nickName+"对"+toNick+"说: \n\r");
						messageArea.append(msg+"\n\r");
					}
					else if(s.equals("#get nicks#"))
					{
						String strNickList = socketIn.readLine();
						String[] nickList = strNickList.split("\\|");
						nicks.clear();
						for(int i=0;i<nickList.length;i++)
						{
							nicks.add(nickList[i]);
						}
					}
					else if(s.equals("#服务器广播#"))
					{
						String msg = socketIn.readLine();
						messageArea.append("服务器广播: \n\r");
						messageArea.append(msg);
					}
				}
			}catch(Exception ex){
				if(socket != null && !socket.isClosed()){
					messageArea.append(ex.toString()+"\r\n");
					
				}
			}
		}
	}
	
	class PopupMenuAdapter implements PopupMenuListener
	{
		public void popupMenuWillBecomeVisible(PopupMenuEvent e)
		{
			try{
			    PrintWriter pw=new PrintWriter(socket.getOutputStream(),true);
			    pw.println("@取得nicks@");
			    }catch(IOException ex){
			    messageArea.append(ex.toString()+"\r\n");
			    }
		}
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
		{
			//System.out.println("size="+jb.getItemCount());
			//System.out.println("count="+nicks.size());
		}
		public void popupMenuCanceled(PopupMenuEvent e)
		{
			
		}
	}
	
	//构造方法
	public Client(){
		super("网络聊天——客户端");
		//初始化界面
		connect = new JButton("连接");
		exit = new JButton("断开");
		address = new JTextField("127.0.0.1",6);
		port = new JTextField("8888",2);
		nick = new JTextField("",4);
		jtb = new JToolBar();
		messageArea = new JTextArea();
		message = new JTextField(25);
		send = new JButton("发送");
		
		jcb = new JCheckBox("私聊");
		jb = new JComboBox(nicks);
		
		//设置界面布局
		Container c = this.getContentPane();
		jtb.add(connect);
		jtb.add(exit);
		jtb.add(new JLabel(" 服务器 :"));
		jtb.add(address);
		jtb.add(new JLabel(" 端口 ："));
		jtb.add(port);
		jtb.add(new JLabel(" 昵称 :"));
		jtb.add(nick);
		c.add(jtb,BorderLayout.NORTH);
		JScrollPane jsp = new JScrollPane(messageArea);
		c.add(jsp);
	
		JPanel jp = new JPanel();
		jp.add(message);
		jp.add(send);
		jp.add(jcb);
		jp.add(jb);
		c.add(jp,BorderLayout.SOUTH);
		
		
		//初始化
		connect.setEnabled(true);
		exit.setEnabled(false);
		messageArea.setEnabled(false);
		message.setEnabled(false);
		send.setEnabled(false);
		jb.setEnabled(false);
		jcb.setEnabled(false);
		
		//为连接按钮注册监听器
		ButtonListener bl = new ButtonListener();
		connect.addActionListener(bl);
		send.addActionListener(bl);
		exit.addActionListener(bl);
		jcb.addActionListener(bl);
		//jb.addActionListener(bl);
		jb.addPopupMenuListener(new PopupMenuAdapter());
		jb.setPreferredSize(new Dimension(150,30));
		}
	public static void main(String[] args){
		Client client = new Client();
		client.setSize(630,300);
		client.setLocation(200,200);
		client.setDefaultCloseOperation(3);
		client.setVisible(true);
	}

}
