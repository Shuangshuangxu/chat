package com.client.view;

//Friend List GUI
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FriendList extends JFrame implements ActionListener, MouseListener{

	//card1
	JPanel jphy1,jphy2,jphy3;
	JButton jphy_jb1,jphy_jb2,jphy_jb3;
	JScrollPane jspl;
	
	//card2
	JPanel jps1,jps2,jps3;
	JButton jps_jb1,jps_jb2,jps_jb3;
	JScrollPane jsp2;	
	
	//card3
	JPanel jpb1,jpb2,jpb3;
	JButton jpb_jb1,jpb_jb2,jpb_jb3;
	JScrollPane jsp3;

	CardLayout cl;
	
	public static void main(String[] args) {
		FriendList friendList=new FriendList();

	}

	public FriendList(){

		//card1
		jphy_jb1=new JButton("My Friend");
		jphy_jb2=new JButton("Stranger");
		jphy_jb2.addActionListener(this);
		jphy_jb3=new JButton("Black");	
		jphy_jb3.addActionListener(this);
		jphy1=new JPanel(new BorderLayout());   		    
		jphy2 = new JPanel(new GridLayout(50,1,4,4));
		JLabel [] jbls1=new JLabel[50];
		for(int i=0;i<jbls1.length;i++){
			
			jbls1[i]=new JLabel( i+1+"", new ImageIcon(),JLabel.LEFT);
			jbls1[i].addMouseListener(this);
			jphy2.add(jbls1[i]);
			
		}
		
		jphy3 =new JPanel(new GridLayout(2,1));
		jphy3.add(jphy_jb2);
		jphy3.add(jphy_jb3);				
		jspl=new JScrollPane(jphy2);		
		jspl = new JScrollPane(jphy2);				
		jphy1.add(jphy_jb1,"North");
		jphy1.add(jspl,"Center");
		jphy1.add(jphy3,"South");
		
		//card2
		jps_jb1=new JButton("My Friend");
		jps_jb1.addActionListener(this);
		jps_jb2=new JButton("Stranger");
		jps_jb3=new JButton("Black");	
		jps_jb3.addActionListener(this);
		
		jps3=new JPanel(new BorderLayout());   		    
		jps2 = new JPanel(new GridLayout(50,1,4,4));
		JLabel [] jbls2=new JLabel[50];
		for(int i=0;i<jbls2.length;i++){
			
			jbls2[i]=new JLabel( i+1+"", new ImageIcon(),JLabel.LEFT);
			jps2.add(jbls2[i]);
			
		}
		
		jps1 =new JPanel(new GridLayout(2,1));
		
		jps1.add(jps_jb1);
		jps1.add(jps_jb2);				
		jsp2=new JScrollPane(jps2);		
		jsp2 = new JScrollPane(jps2);				
		jps3.add(jps1,"North");
		jps3.add(jsp2,"Center");
		jps3.add(jps_jb3,"South");
						
		
		//card3
		jpb_jb1=new JButton("My Friend");
		jpb_jb1.addActionListener(this);
		jpb_jb2=new JButton("Stranger");
		jpb_jb2.addActionListener(this);
		jpb_jb3=new JButton("Black");	
		
  		 
		jpb1=new JPanel(new GridLayout(3,1)); 
		jpb2 = new JPanel(new GridLayout(50,1,4,4));
		JLabel [] jbls3=new JLabel[50];
		for(int i=0;i<jbls3.length;i++){
			
			jbls3[i]=new JLabel( i+1+"", new ImageIcon(),JLabel.LEFT);
			jpb2.add(jbls3[i]);
			
		}
		
		jpb3 =new JPanel(new BorderLayout());
		
		jpb1.add(jpb_jb1);		
		jpb1.add(jpb_jb2);	
		jpb1.add(jpb_jb3);
		jsp3=new JScrollPane(jpb2);		
				
		jpb3.add(jpb1,"North");
		jpb3.add(jsp3,"Center");
	


		
		
		cl=new CardLayout();
		this.setLayout(cl);
		this.add(jphy1,"1");
		this.add(jps3,"2");
		this.add(jpb3,"3");
		
		this.setSize(200,600);
		this.setVisible(true);
        
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource()==jphy_jb2){
			cl.show(this.getContentPane(), "2");
		}else if(arg0.getSource()==jphy_jb3){
			cl.show(this.getContentPane(), "3");
		}else if(arg0.getSource()==jps_jb1){
			cl.show(this.getContentPane(), "1");
		}else if(arg0.getSource()==jps_jb3){
				cl.show(this.getContentPane(), "3");
		}else if(arg0.getSource()==jpb_jb1){
			cl.show(this.getContentPane(), "1");
		}else if(arg0.getSource()==jpb_jb2){
			cl.show(this.getContentPane(), "2");
	}
	
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		if(arg0.getClickCount()==2){
			String friendNo = ((JLabel)arg0.getSource()).getText();
			//System.out.println("you want to talk with "+friedNo+"");
			new QqChat (friendNo);
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
		JLabel jl=(JLabel)arg0.getSource();
		jl.setForeground(Color.red);
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
		JLabel jl=(JLabel)arg0.getSource();
		jl.setForeground(Color.black);
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
	
