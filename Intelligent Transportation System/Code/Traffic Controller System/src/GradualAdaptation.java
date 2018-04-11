import javax.swing.JFrame;


class GradualAdaptation extends JFrame
{
	private javax.swing.JLabel jLabel1;


	private JunctionDisplay JD;
	private GAJunction J;
	private AdjustTrafficDensity A;


	public static void main(String args[])
	{
		new GradualAdaptation();
	}
	public GradualAdaptation()
	{
		super("GradualAdaptation");
		initComponents();
		show();
	}

	private void initComponents() {

		jLabel1 = new javax.swing.JLabel();       
		getContentPane().setLayout(null);
		setForeground(new java.awt.Color(236, 233, 216));
		/*********Change color************/
		getContentPane().setBackground(new java.awt.Color(200,200,255));
		setForeground(new java.awt.Color(236, 233, 216));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JD=new JunctionDisplay();
		A=new AdjustTrafficDensity();
		J=new GAJunction(JD,A);

		jLabel1.setIcon(new javax.swing.ImageIcon("RoadMap2.JPG"));
		jLabel1.setBorder(new javax.swing.border.TitledBorder("Signelling"));

		JD.setBounds(175, 140, 200,200);
		getContentPane().add(JD);
		getContentPane().add(A);
		getContentPane().add(jLabel1);

		jLabel1.setBounds(40, 20, 470, 420);
		pack();        
		resize(1024,750);
		Run R=new Run(J);
		R.start(); 
	}
}