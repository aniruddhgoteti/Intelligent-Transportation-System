import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

class Lane
{    
	int waited;
	int willWaitFor;
	int greenSignalDuration;
	int TrafficDensity;
	int TD1;
	int TD2;
	int TD3;
	int TD4;
	int TD5;
	int RCTD;
	public int WaitageToTD;
	public int WaitageToRCTD;
	public int WaitageToW;
	public Lane(int greensigdur)
	{
		greenSignalDuration=greensigdur;
		TrafficDensity=50;
		TD1=10;
		TD2=10;
		TD3=10;
		TD4=10;
		TD5=10;
		RCTD = 1;
		WaitageToTD = 1;
		WaitageToRCTD = 1;
		WaitageToW= 1;
	}
	public int calcWaitage()
	{
		return (TrafficDensity*WaitageToTD + RCTD * WaitageToRCTD + waited* WaitageToW);
	}
	public void setPref(int WTD)
	{
		WaitageToTD = WTD;
	}
}

class OSSJunction extends JPanel
{
	int currentLane=0;
	int no_of_Lanes=4;

	Lane lanes[]=new Lane[no_of_Lanes];
	JunctionDisplay JD;
	AdjustTrafficDensity A;

	static int yellow=0;
	public OSSJunction(JunctionDisplay JD1,AdjustTrafficDensity A1)
	{
		JD=JD1;
		A=A1;
		for(int i=1;i<no_of_Lanes;i++)
			lanes[i]=new Lane(10);
		lanes[0]=new Lane(40);            //yip bharath
	}

	int PreferableLane()
	{
		int preferableLane=0;
		int weitage=0;
		int currentLaneWeitage=(int) lanes[currentLane].calcWaitage();  //lanes[currentLane].TrafficDensity*lanes[currentLane].WaitageToTD + lanes[currentLane].RCTD*lanes[currentLane].WaitageToRCTD + lanes[currentLane].Waited*lanes[currentLane].WaitageToW;    //Yip Bharath

		for(int i=0;i<no_of_Lanes;i++)
		{
			if(lanes[i].calcWaitage()>weitage)
			{
				preferableLane=i;
				weitage=(int)(lanes[i].calcWaitage());
			}
		}
		if(weitage>(currentLaneWeitage+50))
			return preferableLane;
		else
			return currentLane;
	}
	int calulateRCTD(int TD1, int TD2, int TD3, int TD4, int TD5)
	{
		float Mean = (float)(TD1+TD2+TD3+TD4+TD5)/(float)5.0;
		float numSum =0,term=0;
		int i;
		term = (TD1-Mean);
		numSum += term*term;
		term = (TD2-Mean);
		numSum += term*term;
		term = (TD3-Mean);
		numSum += term*term;
		term = (TD4-Mean);
		numSum += term*term;
		term = (TD5-Mean);
		numSum += term*term;

		return ((int)(numSum/5));

	}
	void changeTime()
	{
		int direction[]=new int[4];
		//java.lang.Math.
		int nextLane;
		if(yellow>0)
		{ 
			if(yellow==1)
				JD.setFgColor(currentLane);
			yellow--;
			return;
		}
		for(int i=0;i<no_of_Lanes;i++)
		{
			lanes[i].waited++;            
			direction[i]=(int)(lanes[i].calcWaitage());
		}

		lanes[currentLane].waited-=1;
		direction[currentLane]=(int)lanes[currentLane].TrafficDensity*lanes[currentLane].WaitageToTD + lanes[currentLane].RCTD*lanes[currentLane].WaitageToRCTD;

		for(int i=0;i<no_of_Lanes;i++)
		{
			lanes[i].TrafficDensity=A.getSliderValue(i);
			A.setValues(i,lanes[i].TrafficDensity,direction[i]);
			lanes[i].TrafficDensity +=((int)((java.lang.Math.random()*10)%4));
			A.setSliderValue(lanes[i].TrafficDensity, i);

			lanes[i].TD5 = lanes[i].TD4;
			lanes[i].TD4 = lanes[i].TD3;
			lanes[i].TD3 = lanes[i].TD2;
			lanes[i].TD2 = lanes[i].TD1;
			lanes[i].TD1 = lanes[i].TrafficDensity;
			if(i != currentLane)
			{
				lanes[i].RCTD = calulateRCTD(lanes[i].TD1, lanes[i].TD2, lanes[i].TD3, lanes[i].TD4,lanes[i].TD5);
			}
			else
			{
				lanes[i].RCTD = 1;
			}
		}	
		if(lanes[currentLane].TrafficDensity>=4) // Traffic Density should not be made negetive
		{
			lanes[currentLane].TrafficDensity-=4;
			A.setSliderValue(lanes[currentLane].TrafficDensity, currentLane);
		}
		if((nextLane=PreferableLane())!=currentLane)
			switchLane(nextLane);


	}

	void switchLane(int nextlane)
	{
		int prevLane=currentLane;
		currentLane=nextlane;
		yellow=2;
		lanes[prevLane].waited=0;
		JD.setCaution(currentLane,prevLane);
	}
}
class GAJunction extends JPanel
{
	int currentLane=0;
	int no_of_Lanes=4;
	Lane lanes[]=new Lane[no_of_Lanes];
	JunctionDisplay JD;
	AdjustTrafficDensity A;

	private void setGreenSignalDuration(int laneno)
	{
		lanes[laneno].greenSignalDuration=A.getSliderValue(laneno);
	}
	public GAJunction(JunctionDisplay JD1,AdjustTrafficDensity A1)
	{
		JD=JD1;
		A=A1;
		for(int i=0;i<no_of_Lanes;i++)
			lanes[i]=new Lane(10);
		lanes[0].waited=-1*lanes[0].greenSignalDuration;       
		for(int i=1;i<no_of_Lanes;i++)
		{
			for(int j=i+1;j!=no_of_Lanes;j++)
				lanes[i].waited+=lanes[j].greenSignalDuration;
			lanes[i].willWaitFor=lanes[i-1].willWaitFor+lanes[i-1].greenSignalDuration;
		}
		for(int i=0;i<no_of_Lanes;i++)
		{
			lanes[0].willWaitFor+=lanes[i].greenSignalDuration;

		}
	}

	void switchLane() {
		int prevLane=currentLane;
		currentLane=(currentLane+1)%no_of_Lanes;
		lanes[currentLane].waited=-1*lanes[currentLane].greenSignalDuration;
		setGreenSignalDuration(prevLane);
		lanes[currentLane].willWaitFor=lanes[prevLane].willWaitFor+lanes[prevLane].greenSignalDuration; 
	}

	void changeTime()
	{
		int direction[]=new int[4];
		for(int i=0;i<no_of_Lanes;i++)
		{
			/*if(lanes[i].TrafficDensity == 0) 
				lanes[i].waited = 0;   */			// bharath Yip
			lanes[i].waited++;
			lanes[i].willWaitFor--;
			direction[i]=lanes[i].willWaitFor;	
		}
		direction[currentLane]=-1*lanes[currentLane].waited;
		JD.display(direction[0], direction[1], direction[2],direction[3]);
		if(lanes[currentLane].waited==-3)
		{
			JD.setCaution(currentLane,(currentLane+1)%no_of_Lanes);
		}
		if(lanes[currentLane].waited==0)
		{
			switchLane();
			JD.setFgColor(currentLane);
		}
		for(int i=0;i<no_of_Lanes;i++)
			A.setTrafficDensity(i,A.getSliderValue(i));
	}    
}


class AdjustTrafficDensity extends JPanel
{
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JSlider jSlider3;
	private javax.swing.JSlider jSlider4;
	private javax.swing.JSlider jSlider1;
	private javax.swing.JSlider jSlider2;
	private JSlider jSliders[];
	private JLabel SignalDurations[];
	public AdjustTrafficDensity()
	{

		jPanel1 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();

		jSlider1 = new javax.swing.JSlider();
		jPanel3 = new javax.swing.JPanel();
		jLabel8 = new javax.swing.JLabel();
		jSlider3 = new javax.swing.JSlider();
		jPanel2 = new javax.swing.JPanel();
		jLabel7 = new javax.swing.JLabel();
		jSlider2 = new javax.swing.JSlider();
		jPanel4 = new javax.swing.JPanel();
		jLabel9 = new javax.swing.JLabel();
		jSlider4 = new javax.swing.JSlider();
		jSliders=new JSlider[4];
		SignalDurations=new JLabel[4];
		jSliders[0]=jSlider1;
		jSliders[1]=jSlider2;
		jSliders[2]=jSlider3;
		jSliders[3]=jSlider4;
		SignalDurations[0]=jLabel2;
		SignalDurations[1]=jLabel7;
		SignalDurations[2]=jLabel8;
		SignalDurations[3]=jLabel9;
		setLayout(null);

		setBorder(new javax.swing.border.TitledBorder("Adjust Traffic Density"));
		jPanel1.setLayout(null);

		jPanel1.setBorder(new javax.swing.border.TitledBorder("North"));
		jLabel2.setText("Green Signal Duration: 10 sec");
		jPanel1.add(jLabel2);
		jLabel2.setBounds(30, 80, 160, 15);
		jSlider1.setMajorTickSpacing(20);
		jSlider1.setMinorTickSpacing(1);
		jSlider1.setPaintLabels(true);
		jSlider1.setSnapToTicks(true);
		jSlider1.setToolTipText("Traffic Density");
		jSlider1.setValue(30);   		//Yip Bharath
		jPanel1.add(jSlider1);
		jSlider1.setBounds(20, 20, 170, 50);
		add(jPanel1);
		jPanel1.setBounds(40, 20, 200, 110);

		jPanel3.setLayout(null);

		jPanel3.setBorder(new javax.swing.border.TitledBorder("South"));
		jLabel8.setText("Green Signal Duration: 10 sec");
		jPanel3.add(jLabel8);
		jLabel8.setBounds(30, 80, 160, 15);
		jSlider3.setMajorTickSpacing(20);
		jSlider3.setMinorTickSpacing(1);
		jSlider3.setPaintLabels(true);
		jSlider3.setSnapToTicks(true);
		jSlider3.setToolTipText("Traffic Density");
		jSlider3.setValue(35);
		jPanel3.add(jSlider3);
		jSlider3.setBounds(20, 20, 170, 50);
		add(jPanel3);
		jPanel3.setBounds(40, 180, 200, 110);

		jPanel2.setLayout(null);

		jPanel2.setBorder(new javax.swing.border.TitledBorder("West"));
		jLabel7.setText("Green Signal Duration: 10 sec");
		jPanel2.add(jLabel7);
		jLabel7.setBounds(30, 80, 160, 15);
		jSlider2.setMajorTickSpacing(20);
		jSlider2.setMinorTickSpacing(1);
		jSlider2.setPaintLabels(true);
		jSlider2.setSnapToTicks(true);
		jSlider2.setToolTipText("Traffic Density");
		jSlider2.setValue(20);
		jPanel2.add(jSlider2);
		jSlider2.setBounds(10, 20, 170, 50);
		add(jPanel2);
		jPanel2.setBounds(40, 500, 200, 110);

		jPanel4.setLayout(null);

		jPanel4.setBorder(new javax.swing.border.TitledBorder("East"));
		jLabel9.setText("Green Signal Duration: 10 sec");
		jPanel4.add(jLabel9);
		jLabel9.setBounds(30, 80, 160, 15);
		jSlider4.setMajorTickSpacing(20);
		jSlider4.setMinorTickSpacing(1);
		jSlider4.setPaintLabels(true);
		jSlider4.setSnapToTicks(true);
		jSlider4.setToolTipText("Traffic Density");
		jSlider4.setValue(10);
		jPanel4.add(jSlider4);
		jSlider4.setBounds(10, 20, 170, 50);

		add(jPanel4);
		jPanel4.setBounds(40, 340, 200, 110);

		setBackground(new java.awt.Color(160,160,160));
		setBounds(650, 20, 300, 640);

	}
	int getSliderValue(int laneno)
	{
		return jSliders[laneno].getValue();
	}
	void setSliderValue(int value,int laneno)
	{
		jSliders[laneno].setValue(value);
	}
	void setTrafficDensity(int laneno,int val)
	{
		SignalDurations[laneno].setText("Average TD : "+Integer.toString(val));
	}
	void setValues(int laneno,int TD,int weitage)
	{
		SignalDurations[laneno].setText("TD : "+Integer.toString(TD)+"     Weitage  :  "+Integer.toString(weitage));
	}
}

class JunctionDisplay extends JPanel
{
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	JLabel Labels[];

	public JunctionDisplay()
	{

		setLayout(null);
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		Labels=new JLabel[4];
		setOpaque(false);
		add(new JLabel());
		jLabel3.setBackground(new java.awt.Color(153, 153, 153));
		jLabel3.setForeground(new java.awt.Color(0, 255, 44));
		jLabel3.setIcon(new javax.swing.ImageIcon("E:\\MyProjects\\Green.JPG"));       
		jLabel3.setIconTextGap(0);
		jLabel3.setOpaque(true);
		jLabel3.setText("  ");
		add(jLabel3);
		jLabel3.setBounds(85,20,55,30);
		Labels[0]=jLabel3;

		jLabel4.setBackground(new java.awt.Color(153, 153, 153));
		jLabel4.setForeground(new java.awt.Color(204, 0, 0));
		jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel4.setIcon(new javax.swing.ImageIcon("E:\\MyProjects\\Red.JPG"));
		jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
		jLabel4.setIconTextGap(0);
		jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jLabel4.setOpaque(true);
		jLabel4.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
		jLabel4.setText("  ");
		add(jLabel4);
		jLabel4.setBounds(20,65, 30, 50);
		Labels[1]=jLabel4;

		jLabel6.setBackground(new java.awt.Color(153, 153, 153));
		jLabel6.setForeground(new java.awt.Color(204, 0, 0));
		jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel6.setIcon(new javax.swing.ImageIcon("E:\\MyProjects\\Red.JPG"));
		jLabel6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
		jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jLabel6.setOpaque(true);
		jLabel6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jLabel6.setText("  ");
		add(jLabel6);
		jLabel6.setBounds(150, 80, 33, 50);
		Labels[3]=jLabel6;

		jLabel5.setBackground(new java.awt.Color(153, 153, 153));
		jLabel5.setForeground(new java.awt.Color(204, 0, 0));
		jLabel5.setIcon(new javax.swing.ImageIcon("E:\\MyProjects\\Red.JPG"));
		jLabel5.setIconTextGap(0);
		jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
		jLabel5.setOpaque(true);
		jLabel5.setText("     ");
		add(jLabel5);
		jLabel5.setBounds(70, 140, 55, 30);
		Labels[2]=jLabel5;
	}
	void display(int n,int w,int s,int e)
	{
		jLabel3.setText(String.valueOf(n));
		jLabel4.setText(String.valueOf(w));
		jLabel5.setText(String.valueOf(s));
		jLabel6.setText(String.valueOf(e));
	}
	void setCaution(int currentLane,int nextLane)
	{
		Labels[currentLane].setIcon(new javax.swing.ImageIcon("E:\\MyProjects\\Yellow.JPG"));
		Labels[currentLane].setForeground(Color.YELLOW);
		Labels[nextLane].setIcon(new javax.swing.ImageIcon("E:\\MyProjects\\Yellow.JPG"));
		Labels[nextLane].setForeground(Color.YELLOW);
	}
	void setFgColor(int lane)
	{
		jLabel3.setIcon(new javax.swing.ImageIcon("E:\\MyProjects\\Red.JPG"));
		jLabel3.setForeground(Color.RED); 
		jLabel4.setIcon(new javax.swing.ImageIcon("E:\\MyProjects\\Red.JPG"));
		jLabel4.setForeground(Color.RED); 
		jLabel5.setIcon(new javax.swing.ImageIcon("E:\\MyProjects\\Red.JPG"));
		jLabel5.setForeground(Color.RED); 
		jLabel6.setIcon(new javax.swing.ImageIcon("E:\\MyProjects\\Red.JPG"));
		jLabel6.setForeground(Color.RED); 

		Labels[lane].setIcon(new javax.swing.ImageIcon("E:\\MyProjects\\Green.JPG"));
		Labels[lane].setForeground(Color.GREEN);
	}
}


class Run extends Thread
{ 
	OSSJunction OssJ;
	GAJunction GaJ;
	int scheduling=0;
	public Run(OSSJunction J1)
	{
		OssJ=J1;
		scheduling=1;
	}
	public Run(GAJunction J1)
	{
		GaJ=J1;
		scheduling=0;
	}
	public void run() 
	{
		while(true)
		{
			if(scheduling == 0)
				GaJ.changeTime();
			else
				OssJ.changeTime();
			try{
				sleep(1000);
			}
			catch(Exception e){}

		}
	}

}