import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

class OnSituation extends JFrame
{
	private javax.swing.JLabel jLabel1;
	private javax.swing.JButton jButton1;
	private JunctionDisplay JD;
	private OSSJunction J;
	private AdjustTrafficDensity A;


	public static void main(String args[])
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		new OnSituation().show();
	}
	public OnSituation()
	{
		super("On Situation Scheduling");
		initComponents();
	}

	private void initComponents() {

		jLabel1 = new javax.swing.JLabel();       
		jButton1 = new javax.swing.JButton("Preferences");
		jButton1.setBounds(70, 600, 150, 40);
		getContentPane().setLayout(null);
		getContentPane().setBackground(new java.awt.Color(200,200,255));
		setForeground(new java.awt.Color(236, 233, 216));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JD=new JunctionDisplay();
		A=new AdjustTrafficDensity();
		J=new OSSJunction(JD,A);
		jButton1.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				new Preferences(J.lanes).show();
			}
		} );
		jLabel1.setIcon(new javax.swing.ImageIcon("RoadMap2.JPG"));
		jLabel1.setBorder(new javax.swing.border.TitledBorder("Signelling"));

		JD.setBounds(200, 250, 200,200);
		getContentPane().add(JD);
		getContentPane().add(A);
		getContentPane().add(jLabel1);
		getContentPane().add(jButton1);
		jLabel1.setBounds(65, 130, 470, 420);
		pack();        
		resize(1024,750);
		Run R=new Run(J);
		R.start(); 
	}
}