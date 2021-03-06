package emergencyresponses;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;


public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;

	static JTextField speed, emergenciesRandomness, blueAmbulances, yellowAmbulances, redAmbulances, hospitalsMaxCapacity,
					patientReleaseFactor, lostEmergenciesRandomness;
	static JPanel boardPanel;
	static JButton run, reset, step;
	static JLabel emergenciesQueue, emergenciesCompleted, lostEmergencies, hospitalsFull;
	static JButton setEmergenciesRandomness, setBlueAmbulances, setYellowAmbulances, setRedAmbulances,
					setHospitalsMaxCapacity, setPatientReleaseFactor, setLostEmergenciesRandomness, setAmbulanceDecision, setAmbulanceBehavior;
	private int nX, nY;

	public class Cell extends JPanel {

		private static final long serialVersionUID = 1L;

		public Entity entity = null;

        @Override
        protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (entity != null){
				g.setColor(entity.color);
				if (entity instanceof  Station){
					g.setColor(Color.WHITE);
					g.drawString("S", 4, 12);
				}
				else if (entity instanceof  Hospital){
					g.setColor(Color.WHITE);
					g.drawString("H", 4, 12);
				}
	            else if (entity instanceof Ambulance && !entity.point.equals(((Ambulance) entity).station.point)){
	            	switch(((Ambulance) entity).direction) {
						// draw a triangle. min value is [1, 4, 4],[3,1,4].
						case N: g.fillPolygon(new int[]{2, 9, 15}, new int[]{9, 2, 9}, 3); break;
						case S: g.fillPolygon(new int[]{2, 9, 15}, new int[]{2, 9, 2}, 3); break;
		    			case E: g.fillPolygon(new int[]{2, 9, 2}, new int[]{2, 9, 15}, 3); break;
		    			case O: g.fillPolygon(new int[]{15, 9, 15}, new int[]{2, 9, 15}, 3); break;
						case NE: g.fillPolygon(new int[]{4, 15, 15}, new int[]{4, 4, 15}, 3); break;
						case NO: g.fillPolygon(new int[]{4, 15, 4}, new int[]{4, 4, 15}, 3); break;
						case SE: g.fillPolygon(new int[]{15, 4, 15}, new int[]{4, 15, 15}, 3); break;
						default: g.fillPolygon(new int[]{4, 4, 15}, new int[]{4, 15, 15}, 3); break;
					}
				}
				else if (entity instanceof Emergency){
					if (((Emergency) entity).gravity.equals(Emergency.EmergencyGravity.Low)){
						g.setColor(new Color(0,100,0)); //darkgreen
						g.drawString("L", 4, 12);
					}
					else if (((Emergency) entity).gravity.equals(Emergency.EmergencyGravity.Medium)){
						g.setColor(Color.white);
						g.drawString("M", 4, 12);
					}
					else {
						g.setColor(Color.RED);
						g.drawString("H", 4, 12);
					}
				}
			}
        }

	}

	public GUI() {
		setTitle("Emergency Responses");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setSize(1200, 800);
		add(createTopPanel());
		add(blueAmbulancesPanel());
		add(yellowAmbulancesPanel());
		add(redAmbulancesPanel());

		Board.initialize();
		Board.associateGUI(this);

		add(emergenciesQueuePanel());
		add(emergenciesRandomnessPanel());
		add(hospitalsMaxCapacityPanel());
		add(emergenciesCompleted());
		add(lostEmergenciesPanel());
		add(patientReleaseFactor());
		add(lostEmergenciesRandomness());
		add(ambulancesDecision());
		add(ambulancesBehavior());



		add(hospitalsFullPanel());

		boardPanel = new JPanel();
		boardPanel.setSize(new Dimension(700,700));
		boardPanel.setLocation(new Point(20,60));

		nX = Board.nX;
		nY = Board.nY;
		GridLayout grid = new GridLayout(nX,nY);
		boardPanel.setLayout(grid);
		for(int i=0; i<nX; i++)
			for(int j=0; j<nY; j++)
				boardPanel.add(new Cell());

		displayBoard();
		Board.displayObjects();
		update();
		add(boardPanel);
	}

	public void displayBoard() {
		for(int i=0; i<nX; i++){
			for(int j=0; j<nY; j++){
				int row=nY-j-1, col=i;
				Block block = Board.getBlock(new Point(i,j));
				JPanel p = ((JPanel)boardPanel.getComponent(row*nX+col));
				p.setBackground(block.color);
				p.setBorder(BorderFactory.createLineBorder(Color.white));
			}
		}
	}

	public void removeObject(Entity object) {
		int row=nY-object.point.y-1, col=object.point.x;
		Cell p = (Cell)boardPanel.getComponent(row*nX+col);
		if (object instanceof Emergency){
			p.setBackground(Color.lightGray);
		}
		p.setBorder(BorderFactory.createLineBorder(Color.white));
		p.entity = null;
	}

	public void displayObject(Entity object) {
		int row=nY-object.point.y-1, col=object.point.x;
		Cell p = (Cell)boardPanel.getComponent(row*nX+col);
		if (object instanceof Emergency){
			p.setBackground(object.color);
		}
		else if (object instanceof Station){
			emergenciesCompleted.setText("Emergencies completed: " + Board.getEmergenciesCompleted());
		}
		p.setBorder(BorderFactory.createLineBorder(Color.white));
		p.entity = object;
		emergenciesQueue.setText("Emergencies in wait: " + Board.getEmergenciesInQueue());
		lostEmergencies.setText("Lost Emergencies: " + Board.getLostEmergencies());
		hospitalsFull.setText("Hospitals Full: " + Board.getHospitalsFull() + "/" + Board.getHospitals().size());
	}

	public void update() {
		boardPanel.invalidate();
	}

	private Component createTopPanel() {
		JPanel panel = new JPanel();
		panel.setSize(new Dimension(400,50));
		panel.setLocation(new Point(0,0));

		step = new JButton("Step");
		panel.add(step);
		step.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(run.getText().equals("Run")) Board.step();
				else Board.stop();
			}
		});
		reset = new JButton("Reset");
		panel.add(reset);
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Board.reset();
			}
		});
		run = new JButton("Run");
		panel.add(run);
		run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(run.getText().equals("Run")){
					int time = -1;
					try {
						time = Integer.valueOf(speed.getText());
					} catch(Exception e){
						JTextPane output = new JTextPane();
						output.setText("Please insert an integer value to set the time per step\nValue inserted = "+speed.getText());
						JOptionPane.showMessageDialog(null, output, "Error", JOptionPane.PLAIN_MESSAGE);
					}
					if(time>0){
						Board.run(time);
	 					run.setText("Stop");
					}
 				} else {
					Board.stop();
 					run.setText("Run");
 				}
			}
		});
		speed = new JTextField("time per step in [1,100]");
		speed.setMargin(new Insets(5,5,5,5));
		panel.add(speed);

		return panel;
	}

	private Component hospitalsMaxCapacityPanel() {
		JPanel panel = new JPanel();
		panel.setSize(new Dimension(275,50));
		panel.setLocation(new Point(725,100));

		JLabel label = new JLabel("Hospitals Max Capacity");
		panel.add(label);
		hospitalsMaxCapacity = new JTextField("1");
		hospitalsMaxCapacity.setMargin(new Insets(5,5,5,5));
		hospitalsMaxCapacity.setColumns(5);
		Board.setHospitalsMaxCapacity(Integer.parseInt(hospitalsMaxCapacity.getText()));
		panel.add(hospitalsMaxCapacity);

		setHospitalsMaxCapacity = new JButton("Set");
		panel.add(setHospitalsMaxCapacity);
		setHospitalsMaxCapacity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					Board.setHospitalsMaxCapacity(Integer.parseInt(hospitalsMaxCapacity.getText()));
					System.out.println("hospitals max capacity: " + Board.getHospitalsMaxCapacity());
				}catch(Exception e){
					JTextPane output = new JTextPane();
					output.setText("Please insert an valid integer value in Hospitals Max Capacity\nValue inserted = "+hospitalsMaxCapacity.getText());
					JOptionPane.showMessageDialog(null, output, "Error", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});

		return panel;
	}

	private Component emergenciesRandomnessPanel() {
		JPanel panel = new JPanel();
		panel.setSize(new Dimension(300,50));
		panel.setLocation(new Point(725,50));

		JLabel label = new JLabel("Emergencies Randomness");
		panel.add(label);
		emergenciesRandomness = new JTextField("2");
		emergenciesRandomness.setMargin(new Insets(5,5,5,5));
		emergenciesRandomness.setColumns(5);
		Board.setEmergenciesRandomness(Integer.parseInt(emergenciesRandomness.getText()));
		panel.add(emergenciesRandomness);

		setEmergenciesRandomness = new JButton("Set");
		panel.add(setEmergenciesRandomness);
		setEmergenciesRandomness.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0){
				try{
					Board.setEmergenciesRandomness(Integer.parseInt(emergenciesRandomness.getText()));
					System.out.println("emergencies randomness: " + Board.getEmergenciesRandomness());
				}catch(Exception e){
					JTextPane output=new JTextPane();
					output.setText("Please insert an valid integer value in Emergencies Randomness\nValue inserted = "+emergenciesRandomness.getText());
					JOptionPane.showMessageDialog(null,output,"Error",JOptionPane.PLAIN_MESSAGE);
				}
			}
		});

		return panel;
	}

	private Component patientReleaseFactor() {
		JPanel panel = new JPanel();
		panel.setSize(new Dimension(325,50));
		panel.setLocation(new Point(725,150));

		JLabel label = new JLabel("Patients Release Factor");
		panel.add(label);
		patientReleaseFactor = new JTextField("2");
		patientReleaseFactor.setMargin(new Insets(5,5,5,5));
		patientReleaseFactor.setColumns(5);
		Board.setPatientReleaseFactor(Integer.parseInt(patientReleaseFactor.getText()));
		panel.add(patientReleaseFactor);

		setPatientReleaseFactor = new JButton("Set");
		panel.add(setPatientReleaseFactor);
		setPatientReleaseFactor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0){
				try{
					Board.setPatientReleaseFactor(Integer.parseInt(patientReleaseFactor.getText()));
					System.out.println("hospitals capacity randomness: " + Board.getPatientReleaseFactor());
				}catch(Exception e){
					JTextPane output=new JTextPane();
					output.setText("Please insert an valid integer value in Patient Release Factor\nValue inserted = "+patientReleaseFactor.getText());
					JOptionPane.showMessageDialog(null,output,"Error",JOptionPane.PLAIN_MESSAGE);
				}
			}
		});

		return panel;
	}

	private Component lostEmergenciesRandomness() {
		JPanel panel = new JPanel();
		panel.setSize(new Dimension(325,50));
		panel.setLocation(new Point(725,200));

		JLabel label = new JLabel("Lost Emergencies Randomness");
		panel.add(label);
		lostEmergenciesRandomness = new JTextField("2");
		lostEmergenciesRandomness.setMargin(new Insets(5,5,5,5));
		lostEmergenciesRandomness.setColumns(5);
		Board.setEmergenciesRandomness(Integer.parseInt(lostEmergenciesRandomness.getText()));
		panel.add(lostEmergenciesRandomness);

		setLostEmergenciesRandomness = new JButton("Set");
		panel.add(setLostEmergenciesRandomness);
		setLostEmergenciesRandomness.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0){
				try{
					Board.setLostEmergenciesRandomness(Integer.parseInt(lostEmergenciesRandomness.getText()));
					System.out.println("lost emergencies  randomness: " + Board.getLostEmergenciesRandomness());
				}catch(Exception e){
					JTextPane output=new JTextPane();
					output.setText("Please insert an valid integer value in Lost Emergencies Randomness\nValue inserted = "+lostEmergenciesRandomness.getText());
					JOptionPane.showMessageDialog(null,output,"Error",JOptionPane.PLAIN_MESSAGE);
				}
			}
		});

		return panel;
	}

	private Component blueAmbulancesPanel() {
		JPanel panel = new JPanel();
		panel.setSize(new Dimension(280,50));
		panel.setLocation(new Point(725,300));

		JLabel label = new JLabel("Blue Ambulances");
		panel.add(label);
		blueAmbulances = new JTextField("1");
		blueAmbulances.setMargin(new Insets(5,5,5,5));
		blueAmbulances.setColumns(5);
		Board.setBlueAmbulances(Integer.parseInt(blueAmbulances.getText()));
		panel.add(blueAmbulances);

		setBlueAmbulances = new JButton("Set");
		panel.add(setBlueAmbulances);
		setBlueAmbulances.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					Board.setBlueAmbulances(Integer.parseInt(blueAmbulances.getText()));
				}catch(Exception e){
					e.printStackTrace();
					JTextPane output = new JTextPane();
					output.setText("Please insert an valid integer value in Blue ambulances\nValue inserted = "+blueAmbulances.getText());
					JOptionPane.showMessageDialog(null, output, "Error", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});

		return panel;
	}

	private Component yellowAmbulancesPanel() {
		JPanel panel = new JPanel();
		panel.setSize(new Dimension(280,50));
		panel.setLocation(new Point(725,350));

		JLabel label = new JLabel("Yellow Ambulances");
		panel.add(label);
		yellowAmbulances = new JTextField("0");
		yellowAmbulances.setMargin(new Insets(5,5,5,5));
		yellowAmbulances.setColumns(5);
		Board.setYellowAmbulances(Integer.parseInt(yellowAmbulances.getText()));
		panel.add(yellowAmbulances);

		setYellowAmbulances = new JButton("Set");
		panel.add(setYellowAmbulances);
		setYellowAmbulances.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					Board.setYellowAmbulances(Integer.parseInt(yellowAmbulances.getText()));
				}catch(Exception e){
					JTextPane output = new JTextPane();
					output.setText("Please insert an valid integer value in Yellow ambulances\nValue inserted = "+yellowAmbulances.getText());
					JOptionPane.showMessageDialog(null, output, "Error", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});

		return panel;
	}

	private Component redAmbulancesPanel() {
		JPanel panel = new JPanel();
		panel.setSize(new Dimension(280,50));
		panel.setLocation(new Point(725,400));

		JLabel label = new JLabel("Red Ambulances");
		panel.add(label);
		redAmbulances = new JTextField("0");
		redAmbulances.setMargin(new Insets(5,5,5,5));
		redAmbulances.setColumns(5);
		Board.setRedAmbulances(Integer.parseInt(redAmbulances.getText()));
		panel.add(redAmbulances);

		setRedAmbulances = new JButton("Set");
		panel.add(setRedAmbulances);
		setRedAmbulances.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					Board.setRedAmbulances(Integer.parseInt(redAmbulances.getText()));
				}catch(Exception e){
					JTextPane output = new JTextPane();
					output.setText("Please insert an valid integer value in Red ambulances\nValue inserted = "+redAmbulances.getText());
					JOptionPane.showMessageDialog(null, output, "Error", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});

		return panel;
	}

	private Component ambulancesDecision() {
		JPanel panel = new JPanel();
		panel.setSize(new Dimension(225,75));
		panel.setLocation(new Point(725,450));

		JLabel label = new JLabel("Ambulances Decision");
		panel.add(label);

		ButtonGroup group = new ButtonGroup();
		JRadioButton centralizedDecision = new JRadioButton("Centralized");
		panel.add(centralizedDecision);
		group.add(centralizedDecision);
		JRadioButton decentralized = new JRadioButton("Decentralized");
		panel.add(decentralized);
		group.add(decentralized);

		setAmbulanceDecision = new JButton("Set");
		panel.add(setAmbulanceDecision);

		centralizedDecision.setSelected(true); // default behavior
		Board.setAmbulancesDecision(Board.AmbulancesDecision.Centralized); // default behavior

		setAmbulanceDecision.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (centralizedDecision.isSelected()){
					Board.setAmbulancesDecision(Board.AmbulancesDecision.Centralized);
				}
				else if (decentralized.isSelected()){
					Board.setAmbulancesDecision(Board.AmbulancesDecision.Decentralized);
				}
			}
		});

		return panel;
	}

	private Component ambulancesBehavior() {
		JPanel panel = new JPanel();
		panel.setSize(new Dimension(225,75));
		panel.setLocation(new Point(725,550));

		JLabel label = new JLabel("Ambulances Behavior");
		panel.add(label);

		ButtonGroup group = new ButtonGroup();
		JRadioButton conservativeDecision = new JRadioButton("Conservative");
		panel.add(conservativeDecision);
		group.add(conservativeDecision);
		JRadioButton riskyDecision = new JRadioButton("Risky");
		panel.add(riskyDecision);
		group.add(riskyDecision);

		setAmbulanceBehavior = new JButton("Set");
		panel.add(setAmbulanceBehavior);

		riskyDecision.setSelected(true); // default behavior
		Board.setAmbulancesBehavior(Board.AmbulancesBehavior.Risky); // default behavior

		setAmbulanceBehavior.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (conservativeDecision.isSelected()){
					Board.setAmbulancesBehavior(Board.AmbulancesBehavior.Conservative);
				}
				else if (riskyDecision.isSelected()){
					Board.setAmbulancesBehavior(Board.AmbulancesBehavior.Risky);
				}
			}
		});

		return panel;
	}


	private Component emergenciesQueuePanel() {
		emergenciesQueue = new JLabel("Emergencies in queue: 0");
		emergenciesQueue.setSize(new Dimension(500,50));
		emergenciesQueue.setLocation(new Point(600,-15));

		return emergenciesQueue;
	}

	private Component emergenciesCompleted() {
		emergenciesCompleted = new JLabel("Emergencies Completed: 0");
		emergenciesCompleted.setSize(new Dimension(500,50));
		emergenciesCompleted.setLocation(new Point(600,0));

		return emergenciesCompleted;
	}

	private Component lostEmergenciesPanel() {
		lostEmergencies = new JLabel("Emergencies Lost: 0");
		lostEmergencies.setSize(new Dimension(500,50));
		lostEmergencies.setLocation(new Point(600,15));

		return lostEmergencies;
	}

	private Component hospitalsFullPanel() {
		hospitalsFull = new JLabel("Hospitals Full: " + Board.getHospitalsFull() + "/" + Board.getHospitals().size());
		hospitalsFull.setSize(new Dimension(500,50));
		hospitalsFull.setLocation(new Point(600,30));

		return hospitalsFull;
	}
}
