//Demonstrating menus
import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

public class Main extends JFrame
{
	private JLabel display;
	JPanel northPan, centerPan, southPan, subPan1, subPan2;
//	public static Main app;
	JButton tdBtn, ioBtn, tcBtn;
	private static Main gui = null;

	public static Main getMain(){
		if (gui==null){
			gui=new Main();
		}
		return gui;
	}

	private Main()
	{
		super( "Traffic Monitoring" );

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		northPan=new JPanel();
		JMenuBar bar = new JMenuBar(); // create menubar
		setJMenuBar( bar ); // set the menubar for the JFrame
		// create File menu and Exit menu item
		subPan1=new JPanel();
		subPan2=new JPanel();
		JMenu fileMenu = new JMenu( "File" );
		fileMenu.setMnemonic( 'F' );
		JMenuItem exitItem = new JMenuItem( "Exit" );
		exitItem.setMnemonic( 'x' );
		exitItem.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				System.exit(0);
			}
		});
		fileMenu.add( exitItem );
		bar.add( fileMenu ); // add File menu


		JMenu OperationMenu = new JMenu( "Operation" );
		OperationMenu.setMnemonic( 'O' );
		JMenuItem VehicleItem = new JMenuItem( "Traffic Density" );
		VehicleItem.setMnemonic( 'T' );
		VehicleItem.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				new TrafficDensity().show();
			}
		} );
		OperationMenu.add( VehicleItem );


		JMenuItem ImageItem = new JMenuItem("Image Operations");
		ImageItem.setMnemonic('I');
		ImageItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				new TConvolveOp().show();
			}
		});

		OperationMenu.add(ImageItem);
		OperationMenu.addSeparator();
		bar.add(OperationMenu);

		JMenu Control = new JMenu( "Traffic Control" );
		Control.setMnemonic('C');

		JMenuItem GradAdapt=new JMenuItem ("Gradual Adaptation");
		GradAdapt.setMnemonic('G');
		Control.add(GradAdapt);
		GradAdapt.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				new GradualAdaptation().show();
			}
		} );

		JMenuItem OSS=new JMenuItem ("On-Situation");
		OSS.setMnemonic('O');
		Control.add(OSS);
		OSS.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				new OnSituation().show();
			}
		} );

		JMenuItem Manual=new JMenuItem ("Manual");
		Manual.setMnemonic('M');
		Control.add(Manual);
		Manual.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				new Manual().show();
			}
		} );
		OperationMenu.add( Control );
		bar.add(OperationMenu);
		JMenu HelpMenu = new JMenu( "Help" );
		HelpMenu.setMnemonic( 'H' );
		JMenuItem aboutItem = new JMenuItem( "About..." );
		aboutItem.setMnemonic( 'A' );
		aboutItem.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				JOptionPane.showMessageDialog(Main.this, "Traffic Management by Image Proocessing\n" +
						"\n                                  By \n\n" +
						"          " +
						"          Bharath S H                1PI03IS023\n\n\n" +
                                                "          Jagdish gupta            1PI03IS041\n" +
						"          Guide:        Mr.Vishwanath N P \n" +
						"                                     Lecturer,\n" +
						"                                  IS & E, PESIT\n\n", "About", JOptionPane.PLAIN_MESSAGE );
			}
		} );
		HelpMenu.add( aboutItem );
		bar.add(HelpMenu);

		Dimension dim=null;
		Rectangle abounds=null;
		dim=getToolkit().getScreenSize();
		abounds=getBounds();
		ImageIcon image = new ImageIcon("./Images/MainScreen1.jpg");
		display = new JLabel(image);
		display.setPreferredSize(new Dimension(dim.width, dim.height-400));
		northPan.add(display);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add( northPan, BorderLayout.NORTH);
                //jagdish
                
		setForeground(new java.awt.Color(236, 233, 216));            
		
		centerPan=new JPanel();
		display=new JLabel("Welcome to Traffic Management System");
		display.setFont(new Font("serif",Font.ITALIC,45));
		display.setForeground(new Color(55,82,189));
		//display.setBackground(new java.awt.Color(200,200,255));
		centerPan.add(display);
		getContentPane().add(centerPan,  BorderLayout.CENTER);

		southPan=new JPanel();

		tcBtn=new JButton("Traffic Control Systems");
		tcBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				gui.setVisible(false);
				new TrafficControlGUI();
			}
		});

		ioBtn=new JButton("Image Enhancement Operations");
		ioBtn.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				gui.setVisible(false); 
				new ImageOperationGUI();
			}
		} );

		tdBtn=new JButton("Exit");
		tdBtn.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		} );

		southPan.setLayout(new BorderLayout());

		subPan2.setLayout(new GridLayout(1,3));
		subPan2.add(new JPanel());
		JPanel pan = new JPanel();
		pan.setLayout(new GridLayout(4,1));
		pan.add(tcBtn);
		pan.add(ioBtn);
		pan.add(tdBtn);
		subPan2.add(pan);
		subPan2.add(new JPanel());
		southPan.add(subPan2,BorderLayout.SOUTH);
		southPan.add(subPan1,BorderLayout.WEST);

		getContentPane().add( southPan,  BorderLayout.SOUTH);
		int wd=dim.width-250;
		int wd1=wd/2;
		int ht=dim.height-150;
		int ht1=ht/2;
		this.setSize(wd,ht );
		this.setLocation((dim.width-abounds.width)/2-wd1,(dim.height-abounds.height)/2-ht1);
		this.setVisible(true);
		/** Aswath - end **/
	}

	public static void main( String args[] )
	{
		gui = new Main();

		gui.addWindowListener( new WindowAdapter()
		{
			public void windowClosing( WindowEvent e )
			{
				System.exit( 0 );
			}
		} );
	}
}

