
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.event.*;


class TrafficDensity extends javax.swing.JFrame
{
	private static BufferedImage inputImage;
	EdgeTest Compcount = new EdgeTest();
	private File imageFile; // The image selected in the selectFileDialog

	private ImageComponent imageComponent = new ImageComponent();

	private javax.swing.JButton loadFileButton;
	private javax.swing.JButton count;

	private javax.swing.JButton road1;

	private javax.swing.JFrame selectFileDialog;
	private javax.swing.JFileChooser selector;


	private java.awt.Container	imageContainer = new Container();
	private java.awt.Container	buttonContainer = new Container();



	private final int	WINDOW_WIDTH = 600;
	private final int	WINDOW_HEIGHT = 400;
	Dimension dim=null;
	Rectangle abounds=null;

	/* Init the components in the frame */
	public TrafficDensity()
	{
		super( "Traffic Density" );
		dim=getToolkit().getScreenSize();
		abounds=getBounds();

		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		Container contentPane = getContentPane();
		Rectangle bounds = new Rectangle((screenSize.width - WINDOW_WIDTH)/2,
				(screenSize.height - WINDOW_WIDTH)/2,
				WINDOW_WIDTH, WINDOW_HEIGHT);
		contentPane.setLayout(new java.awt.BorderLayout());
		setBounds(bounds);

		//initialize file selection dialog stuff
		selectFileDialog = new javax.swing.JFrame();
		selector = new javax.swing.JFileChooser();
		loadFileButton = new javax.swing.JButton();
		count = new javax.swing.JButton();
		road1 = new javax.swing.JButton();

		selector.addActionListener(new java.awt.event.ActionListener() {          //scroll bar
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				selectorActionPerformed(evt);
			}
		});
		selectFileDialog.getContentPane().add(selector, java.awt.BorderLayout.CENTER);
		selectFileDialog.setBounds(bounds);



		// setup images container
		imageContainer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
		imageContainer.add(imageComponent, null);
		contentPane.add(new JScrollPane(imageContainer), java.awt.BorderLayout.CENTER);		//scroll bar

		// setup load button
		loadFileButton.setText("Load");
		count.setText("Traffic Density");
		
		//  congestion.setText("Congestion");

		road1.setText("" + 0);

		loadFileButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				selectFileDialog.show();
			}
		});

		count.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				int count;
				count=Compcount.getComponentCount(imageFile);  
				road1.setText("" + count);
			}
		});



		buttonContainer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT ));

		buttonContainer.add(loadFileButton, null);
		buttonContainer.add(count, null);
		buttonContainer.add(road1, null);

		contentPane.add(buttonContainer, java.awt.BorderLayout.SOUTH);

		this.setLocation((dim.width-abounds.width)/2-300,(dim.height-abounds.height)/2-200);
		this.setVisible(true);
	}

	public void loadFile() throws IOException {
		imageComponent.clearImages();
		imageComponent.addImage(inputImage);
		imageComponent.repaint();
	}

	/**
	 * Button in file selector is clicked, then load the selected image.
	 */
	private void selectorActionPerformed(java.awt.event.ActionEvent evt) {
		BufferedImage image;
		imageFile = selector.getSelectedFile();

		try {
			selectFileDialog.dispose();
			image = ImageIO.read(imageFile);
			inputImage=image;
			loadFile();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}


}

