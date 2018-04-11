import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class Preferences extends JFrame
{
	private JLabel wtd, rctd, wwt;
	

	private static JTextField wtdN = new JTextField("1");
	private static JTextField wtdS = new JTextField("1");
	private static JTextField wtdE = new JTextField("1");
	private static JTextField wtdW = new JTextField("1");
	private static JTextField wwtN = new JTextField("1");
	private static JTextField wwtS = new JTextField("1");
	private static JTextField wwtE = new JTextField("1");
	private static JTextField wwtW = new JTextField("1");
	private static JTextField rctdN = new JTextField("1");
	private static JTextField rctdS = new JTextField("1");
	private static JTextField rctdE = new JTextField("1");
	private static JTextField rctdW = new JTextField("1");
	
	private JButton okBtn;
	private Lane pLanes[];
    	
	public Preferences(Lane lanes[]){
		super("On Situation Scheduling");
		pLanes = lanes;
		initComponents();
	}
	
	private void initComponents() 
	{
		wtd = new JLabel("Weitage to TD: ");  
		wwt = new JLabel("Weightage to Waiting Time: ");  
		rctd = new JLabel("Weitage to RCTD");  
		
		okBtn = new JButton("OK");
		JPanel inputPanel = new JPanel(new GridLayout(4,5));
		
		inputPanel.add(new JLabel());
		inputPanel.add(new JLabel("North"));
		inputPanel.add(new JLabel("South"));
		inputPanel.add(new JLabel("East"));
		inputPanel.add(new JLabel("West"));
		
		inputPanel.add(wtd);
		inputPanel.add(wtdN);
		inputPanel.add(wtdS);
		inputPanel.add(wtdE);
		inputPanel.add(wtdW);
		
		inputPanel.add(wwt);
		inputPanel.add(wwtN);
		inputPanel.add(wwtS);
		inputPanel.add(wwtE);
		inputPanel.add(wwtW);
		
		inputPanel.add(rctd);
		inputPanel.add(rctdN);
		inputPanel.add(rctdS);
		inputPanel.add(rctdE);
		inputPanel.add(rctdW);
		
		getContentPane().setLayout(new FlowLayout());
		
		
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/******WTD*******/
				int temp = Integer.parseInt(wtdN.getText());
				pLanes[0].WaitageToTD = temp;
				
				temp = Integer.parseInt(wtdW.getText());
				pLanes[1].WaitageToTD = temp;
				
				temp = Integer.parseInt(wtdS.getText());
				pLanes[2].WaitageToTD = temp;
				
				temp = Integer.parseInt(wtdE.getText());
				pLanes[3].WaitageToTD = temp;
                         /*****WWT****/  
				 temp = Integer.parseInt(wwtN.getText());
				pLanes[0].WaitageToW = temp;
				
				temp = Integer.parseInt(wwtW.getText());
				pLanes[1].WaitageToW = temp;
				
				temp = Integer.parseInt(wwtS.getText());
				pLanes[2].WaitageToW = temp;
				
				temp = Integer.parseInt(wwtE.getText());
				pLanes[3].WaitageToW = temp;
				
				/********RCTD*******/

				 temp = Integer.parseInt(rctdN.getText());
				pLanes[0].WaitageToRCTD = temp;
				
				temp = Integer.parseInt(rctdW.getText());
				pLanes[1].WaitageToRCTD = temp;
				
				temp = Integer.parseInt(rctdS.getText());
				pLanes[2].WaitageToRCTD = temp;
				
				temp = Integer.parseInt(rctdE.getText());
				pLanes[3].WaitageToRCTD = temp;
				
				System.out.println(pLanes[0].WaitageToTD);
				dispose();
			}
		});
		getContentPane().add(inputPanel);
		//getContentPane().add(jTextField1);
		getContentPane().add(okBtn);
		pack();
		resize(720,150);
	}
}