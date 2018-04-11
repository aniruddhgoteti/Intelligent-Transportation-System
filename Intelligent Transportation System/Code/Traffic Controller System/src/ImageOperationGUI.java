
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImageOperationGUI extends JFrame {

	JPanel northPan, centerPan, southPan, subPan1, subPan2;
	JLabel display;
	public static TrafficControlGUI app;
	JButton exitBtn, ftsBtn, msBtn,tdBtn,sasBtn;

	public ImageOperationGUI(){

		super( "ImageOperationGUI" );

		northPan=new JPanel();
		subPan1=new JPanel();
		subPan2=new JPanel();

		Dimension dim=null;
		Rectangle abounds=null;
		dim=getToolkit().getScreenSize();
		abounds=getBounds();
		ImageIcon image = new ImageIcon("Images/MainScreen21.jpg");
		display = new JLabel(image);
		display.setPreferredSize(new Dimension(dim.width, dim.height-400));
		northPan.add(display);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add( northPan, BorderLayout.NORTH);

		centerPan=new JPanel();
		display=new JLabel(" Image Operations");
		display.setFont(new Font("serif",Font.ITALIC,45));
		display.setForeground(new Color(55,82,189));
		centerPan.add(display);
		getContentPane().add(centerPan,  BorderLayout.CENTER);

		southPan=new JPanel();
		
		msBtn=new JButton("Traffic Density Calculation");
		msBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new TrafficDensity().show();
			}
		});

		ftsBtn=new JButton("Image Enhancement");
		ftsBtn.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent e )
			{
				new TConvolveOp().show();
			}
		} );
		
		exitBtn=new JButton("Exit");
		exitBtn.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.getMain().setVisible(true);
				dispose();
			}
		} );

		southPan.setLayout(new BorderLayout());

		subPan2.setLayout(new GridLayout(1,4));
		subPan2.add(new JPanel());
		JPanel pan = new JPanel();
		pan.setLayout(new GridLayout(5,1));
		pan.add(msBtn);
		pan.add(ftsBtn);
		pan.add(exitBtn);
		
		subPan2.add(pan);
		subPan2.add(new JPanel());
		southPan.add(subPan2,BorderLayout.SOUTH);
		southPan.add(subPan1,BorderLayout.WEST);

		getContentPane().add( southPan,  BorderLayout.SOUTH);
		int wd=dim.width-100;
		int wd1=wd/2;
		int ht=dim.height-150;
		int ht1=ht/2;
		this.setSize(wd,ht );
		this.setLocation((dim.width-abounds.width)/2-wd1,(dim.height-abounds.height)/2-ht1);
		this.setVisible(true);
	}
}
