package chat;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class Server extends JFrame {
	private JButton listen;
	private JButton exit;
	private JToolBar jtb;
	private JTextArea messageArea;
	private JTextField message;
	private JButton broadcast;
	private JComboBox combo;
	

	
	//服务器的ServerSocket对象
	private ServerSocket serverSocket;
	
	Vector<String> nicks = new Vector<String>();
	//记录接受消息线程的Vector
	private Vector<ReceiveClientMsgThread> threads = new Vector();
	
	//服务器的监听线程
	ListenerThread lt;
	
	

	class ListenerThread extends Thread{
		
		public void run(){
			try{
				serverSocket = new ServerSocket(8888);
			}catch(IOException ex){
				messageArea.append(ex.toString());
				return;
			}
			while(true){
				Socket socket = null;
				try{
					socket = serverSocket.accept();
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);
					String nick = br.readLine();
					//判断昵称是否重复
					if(isRepeated(nick)){
						pw.println("昵称重复，请输入新昵称后重新连接！");
						pw.close();
						br.close();
						socket.close();
					}else{
						//向客户端发送成功连接的信息
						pw.println("已经成功连接到服务器！");
						messageArea.append(nick+"已经成功连接到服务器！\n\r");
						messageArea.setCaretPosition(messageArea.getText().length());//光标定位到最后
						//改变广播按钮和信息发送文本框状态
						message.setEditable(true);
						broadcast.setEnabled(true);
						message.setFocusable(true);
						//将昵称保存下来加入Vector
						
						nicks.add(nick);
						
						 //遍历Vector中的元素
						 combo.removeAllItems();
						  for(int i = 0;i < nicks.size();i++){
							  combo.addItem(nicks.get(i));  
						  }
						//维持这个客户端的通信
						ReceiveClientMsgThread t = new ReceiveClientMsgThread(socket,nick);
						t.start();
						//将线程加入Vector threads
						threads.add(t);
					}
				}catch(IOException ex){
					if(serverSocket.isClosed()){
						return;
					}else{
						messageArea.append(ex.toString()+"\n\r");
					}
				}
			}
		}
	}
	
	//判断昵称重复
	private boolean isRepeated(String nick){
		if(nicks.contains(nick)){
			return true;
		}else
			return false;
	}
	//接收消息的线程类
	class ReceiveClientMsgThread extends Thread{
		//和客户端通信的Socket对象
		private Socket socket;
		private String nick;
		ReceiveClientMsgThread(Socket socket,String nick){
			this.socket = socket;
			this.nick = nick;
		}
		public Socket getSocket(){
			return socket;
		}
		public void run(){
			try{
				BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter socketOut = new PrintWriter(socket.getOutputStream(),true);
				while(true){
					//接收客户端发来的信息
					String s = socketIn.readLine();
					StringBuilder rsb = new StringBuilder();
					//判断客户端是否退出聊天
					if(s.equals("@断开连接@")){
						//如果是客户端发来的断开信息
						
						rsb.append("#有人断开#\n");
						rsb.append(nick+"\n");
						//rsb.append()
						threads.remove(this);
						nicks.remove(nick);
						socketIn.close();
						socketOut.close();
						socket.close();

						if(threads.size() == 0){
							message.setEditable(false);
							broadcast.setEnabled(false);
						}
						messageArea.append(nick+"断开连接\n\r");
					}
					else if(s.equals("@公聊@"))
					{
						//如果是正常的客户端信息
						rsb.append("#公聊#\n");
						rsb.append(nick+"\n");
						String msg = socketIn.readLine();
						rsb.append(msg+"\n");
						System.out.print(rsb.toString());
						sendMsgToAll(rsb.toString());
						messageArea.append(nick+"说: \r\n");
						messageArea.append(msg+"\n\r");
					}
					else if(s.equals("@私聊@"))
					{
						rsb.append("#私聊#\n");
						String toNick = socketIn.readLine();
						String msg = socketIn.readLine();
						rsb.append(nick+"\n");
						rsb.append(toNick+"\n");
						rsb.append(msg+"\n");
						sendMsgToPerson(rsb.toString(),nicks.indexOf(toNick));
						messageArea.append(nick+"对"+toNick+"说: \n\r");
						messageArea.append(msg+"\n\r");
					}
					else if(s.equals("@取得nicks@"))
					{
						rsb.append("#get nicks#\n");
						for(int i=0;i<nicks.size();i++)
						{
							if(i!=0)
								rsb.append("|");
							rsb.append(nicks.get(i));
						}
						rsb.append("\n");
						sendMsgToPerson(rsb.toString(),threads.indexOf(this));
					}
					//在显示区域显示收到的信息
					//messageArea.append(s+"\r\n");
					messageArea.setCaretPosition(messageArea.getText().length());
					//广播收到的信息到其他客户端

				}
			}catch(Exception ex){
				if(! socket.isClosed())
					messageArea.append(ex.toString()+"\r\n");
			}
		}
	}
	
	//为按钮编写监听器类ButtonListener
	class ButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == listen){
			messageArea.setText("服务器已经启动......\n\r");
			
			//实例化监听器线程并启动
			lt = new ListenerThread();
			lt.start();
			//改变按钮状态
			listen.setEnabled(false);
			exit.setEnabled(true);}
			
			if(e.getSource() == broadcast || e.getSource() == message){
				String s = message.getText();
				s = "服务器广播："+s;
				messageArea.append(s+"\n\r");
				try{
					for(int i = 0;i<threads.size();i++){
						PrintWriter pw = new PrintWriter(((ReceiveClientMsgThread)
								(threads.get(i))).getSocket().getOutputStream(),true);
						pw.println("#服务器广播#");
						pw.println(message.getText());
					}
				}catch(IOException ex){
					messageArea.append(ex.toString()+"\r\n");
				}
				message.setText("");
				}
				if(e.getSource() == exit){
					try{
						//向所有客户端发送断开信息“@@bye**”并进行相应的关闭操作
						for(int i = 0;i<threads.size();i++){
							Socket socket = ((ReceiveClientMsgThread)threads.get(i)).getSocket();
							PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);
							BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							pw.println("#服务器断开#");
							pw.close();
							br.close();
							socket.close();
						}
						//关闭serverSoket对象
						serverSocket.close();
						messageArea.append("服务器已经断开！");
					}catch(IOException ex){
						messageArea.append(ex.toString()+"\r\n");
					}
					//删除threads和nicks中所有的元素
				threads.removeAllElements();
				nicks.remove(nicks);
				combo.removeAll();
				combo.addItem("所有人");
				//恢复按钮文本框到初始状态
				listen.setEnabled(true);
				exit.setEnabled(false);
				message.setEditable(false);
				broadcast.setEnabled(false);
				}
			}
			
		}
		

	
	
	//构造方法

	public Server(){
//		设置服务器窗口的标题
		super("网络聊天——服务器");
		//初始化界面元素
		listen = new JButton("监听");
		exit = new JButton("断开");
		jtb = new JToolBar();
		messageArea = new JTextArea();
		message = new JTextField(10);
		broadcast = new JButton("广播");
		
		combo = new JComboBox();
		//设置界面布局
		Container c = this.getContentPane();
		jtb.add(listen);
		jtb.add(exit);
		c.add(jtb,BorderLayout.NORTH);
		JScrollPane jsp = new JScrollPane(messageArea);
		c.add(jsp);

		JPanel jp = new JPanel();
		jp.add(message);
		jp.add(broadcast);
		jp.add(combo);
		c.add(jp,BorderLayout.SOUTH);
		

		
		//初始化
		exit.setEnabled(false);
		messageArea.setEditable(false);
		message.setEnabled(true);
		broadcast.setEnabled(false);
		combo.setEnabled(true);
		
		//为监听按钮注册监听器
		ButtonListener bl = new ButtonListener();
		listen.addActionListener(bl);
		
		//为广播按钮和信息发送文本框注册监听器
		broadcast.addActionListener(bl);
		message.addActionListener(bl);
		
		//为断开按钮注册监听器
		exit.addActionListener(bl);
		
	
	}
	
	private void sendMsgToAll(String msg)
	{
		for(int i = 0;i<threads.size();i++){
			PrintWriter pw;
			try {
				pw = new PrintWriter(((ReceiveClientMsgThread)
						(threads.get(i))).getSocket().getOutputStream(),true);
				pw.println(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void sendMsgToPerson(String msg,int index)
	{
		PrintWriter pw;
		try {
			pw = new PrintWriter(((ReceiveClientMsgThread)
					(threads.get(index))).getSocket().getOutputStream(),true);
			pw.println(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args) {
		Server server = new Server();
		server.setSize(400,300);
		server.setLocation(200,200);
		server.setDefaultCloseOperation(3);
		server.setVisible(true);
	}

}
