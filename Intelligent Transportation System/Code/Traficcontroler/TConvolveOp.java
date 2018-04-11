
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

class TConvolveOp extends JFrame
{
	JPanel northPan, centerPan, southPan, subPan1, subPan2;
	private JLabel display;
	DisplayPanel displayPanel;
	JButton loadButton,sharpenButton,blurringButton,edButton,resetButton,BrightenButton;
	ButtonListener B=new ButtonListener();
	public String imgFile=null;
	javax.swing.JFrame selectFileDialog;
	javax.swing.JFileChooser selector;
	Container container;
	public TConvolveOp()
	{
		super("TCONVOLEVOP");
		Dimension dim=null;
		Rectangle abounds=null;
		dim=getToolkit().getScreenSize();
		ImageIcon image = new ImageIcon("");
		display = new JLabel(image);
		display.setPreferredSize(new Dimension(dim.width, dim.height-400));
		northPan = new JPanel();
		northPan.add(display);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add( northPan, BorderLayout.NORTH);
		
		selectFileDialog = new javax.swing.JFrame();
		selector = new javax.swing.JFileChooser();
		selectFileDialog.getContentPane().add(selector, java.awt.BorderLayout.CENTER);
		selectFileDialog.setBounds(300,200,500,400);
		container = getContentPane();		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1,6));
		panel.setBorder(new TitledBorder(
		"Click a Button to perform the desired Action"));

		loadButton = new JButton("Load");
		loadButton.addActionListener(B);

		sharpenButton = new JButton("Sharpen");
		sharpenButton.addActionListener(B);

		blurringButton = new JButton("Blurr");
		blurringButton.addActionListener(B);

		edButton = new JButton("EdgeDetect");
		edButton.addActionListener(B);

		resetButton = new JButton("Reset");
		resetButton.addActionListener(B);

		BrightenButton = new JButton("Brighten");
		BrightenButton.addActionListener(B);	

		panel.add(loadButton);
		panel.add(sharpenButton);
		panel.add(blurringButton);
		panel.add(edButton);
		panel.add(BrightenButton);
		panel.add(resetButton);


		container.add(BorderLayout.SOUTH,panel);
		
		//container.setBackground(new java.awt.Color(200,200,255));
		setForeground(new java.awt.Color(236, 233, 216));
		setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width,java.awt.Toolkit.getDefaultToolkit().getScreenSize().height-30);

		show();
	}

	void resetImgFile()
	{
		imgFile=null;
	}

	public static void main(String args[])
	{
		new TConvolveOp();

	}


	class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			JButton button = (JButton) e.getSource();

			if(button.equals(loadButton))
			{

				selector.addActionListener(new java.awt.event.ActionListener() 
				{
					public void actionPerformed(java.awt.event.ActionEvent evt) 
					{
						selectFileDialog.dispose();
						if(selector.getSelectedFile()!=null)
						{
							if(imgFile!=null)
								container.remove(displayPanel);
							imgFile = selector.getSelectedFile().getAbsolutePath();
							displayPanel = new DisplayPanel(imgFile);
							container.add(displayPanel);   
							displayPanel.repaint();
							
					        }
				         }
			
				});
				selectFileDialog.show();
				
			

			}
			else if (imgFile==null)
				JOptionPane.showMessageDialog( TConvolveOp.this, "No Image Loaded", "", JOptionPane.PLAIN_MESSAGE );
			else if(button.equals(BrightenButton))
			{
				displayPanel.newop();
				displayPanel.repaint();
			}


			else if(button.equals(sharpenButton))
			{
				displayPanel.sharpen();
				displayPanel.repaint();
			}

			else if(button.equals(blurringButton))
			{
				displayPanel.blur();
				displayPanel.repaint();
			}
			else if(button.equals(edButton))
			{
				displayPanel.edgeDetect();
				displayPanel.repaint();
			}
			else if(button.equals(resetButton))

			{
				displayPanel.reset();
				displayPanel.repaint();
			}
		}
	}
}

class DisplayPanel extends JPanel
{
	Image displayImage;
	BufferedImage biSrc;
	BufferedImage biDest;
	BufferedImage bi;
	Graphics2D big;
	String imageFile;

	DisplayPanel(String path)
	{
		//setBackground(Color.black);
		imageFile=path;
		loadImage(path);		
		createBufferedImages();
		bi = biSrc;
		setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width-20,java.awt.Toolkit.getDefaultToolkit().getScreenSize().height-200);
		big.drawImage(displayImage,0,0,this);
				
		
	}

	public void loadImage(String path)
	{
		displayImage = Toolkit.getDefaultToolkit().getImage(path);
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(displayImage,1);
	
		if (displayImage.getWidth(this) == -1 )
		{
			JOptionPane.showMessageDialog(this, "The File is an Image File", "", JOptionPane.PLAIN_MESSAGE );
			imageFile=null;
		}

	}

	public void createBufferedImages()
	{
		biSrc = new BufferedImage(displayImage.getWidth(this),displayImage.getHeight(this),BufferedImage.TYPE_INT_RGB);

		big = biSrc.createGraphics();
		//big.drawImage(displayImage,0,0,this);

		biDest = new BufferedImage(displayImage.getWidth(this),displayImage.getHeight(this),BufferedImage.TYPE_INT_RGB);
	}
	public void newop()
	{
		float data[] = 
		{ -1.0f,-1.0f,-1.0f,
				-1.0f,11.0f,-1.0f,
				-1.0f,-1.0f,-1.0f
		};
		Kernel kernel = new Kernel(3,3,data);
		ConvolveOp convolve = new ConvolveOp (kernel,ConvolveOp.EDGE_NO_OP,null);
		convolve.filter(biSrc,biDest);
		bi =biDest;
	}
	public void sharpen()
	{
		float data[] = 
		{ -1.0f,-1.0f,-1.0f,
				-1.0f,9.0f,-1.0f,
				-1.0f,-1.0f,-1.0f
		};
		Kernel kernel = new Kernel(3,3,data);
		ConvolveOp convolve = new ConvolveOp (kernel,ConvolveOp.EDGE_NO_OP,null);
		convolve.filter(biSrc,biDest);
		bi =biDest;
	}

	public void blur()
	{
		float data[] = 
		{ 0.0625f,0.0125f,0.0625f,
				0.0125f,0.125f,0.0125f,
				0.0625f,0.0125f,0.0625f
		};
		Kernel kernel = new Kernel(3,3,data);
		ConvolveOp convolve = new ConvolveOp (kernel,ConvolveOp.EDGE_NO_OP,null);
		convolve.filter(biSrc,biDest);
		bi =biDest;
	}


	public void edgeDetect()
	{

		float mask[] = 
		{
				-1.0f,0.0f,-1.0f,
				0.0f,4.0f,0.0f,
				-1.0f,0.0f,-1.0f
		};
		Kernel kernel = new Kernel( 3 ,3 ,mask);
		ConvolveOp convolve = new ConvolveOp (kernel,ConvolveOp.EDGE_NO_OP,null);
		convolve.filter(biSrc,biDest);
		bi =biDest;
	}

	public void reset()
	{
		big.setColor(Color.black);
		big.clearRect(0,0,bi.getWidth(this),bi.getHeight(this));
		big.drawImage(displayImage,0,0,this);
		bi = biSrc;
	}

	public void Update(Graphics g)
	{
		g.clearRect(0,0,bi.getWidth(this),bi.getHeight(this));
		paintComponent(g);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;
		g2D.drawImage(bi,0,0,this);
	}
}	
