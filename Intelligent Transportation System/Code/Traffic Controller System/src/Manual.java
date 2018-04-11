import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
class RunManual extends Thread
{ 
	JunctionDisplayManual J;
	public RunManual(JunctionDisplayManual J1)
	{
		J=J1;
	}
	public void run() 
	{
		while(true)
		{
			J.changeTime();
			try{
				sleep(1000);
			}
			catch(Exception e){}

		}
	}

}


class JunctionDisplayManual extends JPanel implements ActionListener
{
	private javax.swing.JButton jButton3;
	private javax.swing.JButton jButton4;
	private javax.swing.JButton jButton5;
	private javax.swing.JButton jButton6;
	JButton Buttons[];
	static int currentLane;
	static int Latency;
	void setCaution(int currentLane,int nextLane)
	{
		Buttons[currentLane].setIcon(new javax.swing.ImageIcon("Yellow.JPG"));
		Buttons[currentLane].setForeground(Color.YELLOW);
		Buttons[nextLane].setIcon(new javax.swing.ImageIcon("Yellow.JPG"));
		Buttons[nextLane].setForeground(Color.YELLOW);
		Buttons[currentLane].repaint();
		Buttons[nextLane].repaint();
	}
	void changeTime()
	{
		if(Latency>0)
		{
			Latency--;
			if(Latency==0)
			{
				jButton3.setIcon(new javax.swing.ImageIcon("Red.JPG"));
				jButton3.setForeground(Color.RED); 
				jButton4.setIcon(new javax.swing.ImageIcon("Red.JPG"));
				jButton4.setForeground(Color.RED); 
				jButton5.setIcon(new javax.swing.ImageIcon("Red.JPG"));
				jButton5.setForeground(Color.RED); 
				jButton6.setIcon(new javax.swing.ImageIcon("Red.JPG"));
				jButton6.setForeground(Color.RED); 
				Buttons[currentLane].setIcon(new javax.swing.ImageIcon("Green.JPG"));
				Buttons[currentLane].setForeground(Color.GREEN);
			}
		}

	}
	public void actionPerformed(ActionEvent e) 
	{        
		JButton Source=(JButton)(e.getSource());
		int nextLane=currentLane;
		for(int i=0;i<4;i++)
		{
			if(Source.equals(Buttons[i]))
			{
				nextLane=i;
				break;
			}
		}
		if(nextLane==currentLane) return;
		setCaution(currentLane, nextLane);
		Latency=2;        
		currentLane=nextLane;
	}   

	public JunctionDisplayManual()
	{

		setLayout(null);
		jButton3 = new javax.swing.JButton();
		jButton4 = new javax.swing.JButton();
		jButton6 = new javax.swing.JButton();
		jButton5 = new javax.swing.JButton();
		Buttons=new JButton[4];
		Buttons[0]=jButton3;
		Buttons[1]=jButton4;
		Buttons[2]=jButton6;
		Buttons[3]=jButton5;
		setOpaque(false);
		jButton3.setBackground(new java.awt.Color(153, 153, 153));
		jButton3.setForeground(new java.awt.Color(0, 255, 44));
		jButton3.setIcon(new javax.swing.ImageIcon("Green.JPG"));       
		jButton3.setIconTextGap(0);
		jButton3.setOpaque(true);
		add(jButton3);
		jButton3.addActionListener(this);
		jButton3.setBounds(85,20,35,35);

		jButton4.setBackground(new java.awt.Color(153, 153, 153));
		jButton4.setForeground(new java.awt.Color(204, 0, 0));
		jButton4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jButton4.setIcon(new javax.swing.ImageIcon("Red.JPG"));
		jButton4.setIconTextGap(0);
		jButton4.setOpaque(true);
		add(jButton4);
		jButton4.addActionListener(this);
		jButton4.setBounds(20,80, 35, 35);

		jButton6.setBackground(new java.awt.Color(153, 153, 153));
		jButton6.setForeground(new java.awt.Color(204, 0, 0));
		jButton6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jButton6.setIcon(new javax.swing.ImageIcon("Red.JPG"));
		jButton6.setOpaque(true);
		add(jButton6);
		jButton6.addActionListener(this);
		jButton6.setBounds(150, 80, 35, 35);

		jButton5.setBackground(new java.awt.Color(153, 153, 153));
		jButton5.setForeground(new java.awt.Color(204, 0, 0));
		jButton5.setIcon(new javax.swing.ImageIcon("Red.JPG"));
		jButton5.setIconTextGap(0);
		jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
		jButton5.setOpaque(true);
		add(jButton5);
		jButton5.addActionListener(this);
		jButton5.setBounds(85, 140, 35, 35);
	}

}
class Manual extends JFrame
{
	private javax.swing.JLabel jLabel1;


	private JunctionDisplayManual JD;


	public static void main(String args[])
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		new Manual().show();
	}
	public Manual()
	{
		super("Manual");
		initComponents();
	}

	private void initComponents() {

		jLabel1 = new javax.swing.JLabel();       
		RunManual R;
		getContentPane().setLayout(null);
		setForeground(new java.awt.Color(236, 233, 216));
		/**********Change Color*******/
		getContentPane().setBackground(new java.awt.Color(200,200,255));
		setForeground(new java.awt.Color(236, 233, 216));
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JD=new JunctionDisplayManual();
		R=new RunManual(JD);        
		R.start();
		jLabel1.setIcon(new javax.swing.ImageIcon("RoadMap2.JPG"));
		jLabel1.setBorder(new javax.swing.border.TitledBorder("Signelling"));

		JD.setBounds(175, 140, 200,200);
		getContentPane().add(JD);

		getContentPane().add(jLabel1);
		jLabel1.setBounds(40, 20, 470, 420);
		pack();        

		setBounds(200,150,550,500);


	}
}