package loadingdocks;

import java.awt.Color;
import java.awt.Point;
import java.util.*;

import loadingdocks.Block.Shape;

/**
 * Environment
 * @author Rui Henriques
 */
public class Board {

	/** The environment */
	
	public static int nX = 30, nY = 30;
	private static Block[][] board;
	private static Entity[][] objects;
	private static Entity[][] blueAmbulancesObjects;
	private static Entity[][] redAmbulancesObjects;
	private static Entity[][] yellowAmbulancesObjects;

	private static List<Station> stations;

	private static List<Hospital> hospitals;
	private static List<Ambulance> ambulances;

	private static List<Emergency> emergencies;
	private static Central central;

	private static Random rand = new Random();

	private static int blueAmbulances;
	private static int yellowAmbulances;
	private static int redAmbulances;
	private static int emergenciesRandomness;
	private static int hospitalsMaxCapacity;

	private static int lostEmergencies = 0;

	private static int stepCounter = 1;
	
	
	/****************************
	 ***** A: SETTING BOARD *****
	 ****************************/
	
	public static void initialize() {

		/**
		* Create stations
		* */
		stations = new ArrayList<>();
		stations.add(new Station(new Point(1, 3), Color.BLUE, blueAmbulances, yellowAmbulances, redAmbulances));
		stations.add(new Station(new Point(7, 4), Color.BLUE, blueAmbulances, yellowAmbulances, redAmbulances));
		/*stations.add(new Station(new Point(7, 15), Color.BLUE, blueAmbulances, yellowAmbulances, redAmbulances));
		stations.add(new Station(new Point(19, 4), Color.BLUE, blueAmbulances, yellowAmbulances, redAmbulances));
		stations.add(new Station(new Point(18, 8), Color.BLUE, blueAmbulances, yellowAmbulances, redAmbulances));
		stations.add(new Station(new Point(22, 6), Color.BLUE, blueAmbulances, yellowAmbulances, redAmbulances));
		stations.add(new Station(new Point(20, 11), Color.BLUE, blueAmbulances, yellowAmbulances, redAmbulances));
		stations.add(new Station(new Point(20, 17), Color.BLUE, blueAmbulances, yellowAmbulances, redAmbulances));
		stations.add(new Station(new Point(26, 17), Color.BLUE, blueAmbulances, yellowAmbulances, redAmbulances));
		stations.add(new Station(new Point(28, 16), Color.BLUE, blueAmbulances, yellowAmbulances, redAmbulances));
		*/
		/**
		 * Create Hospitals
		 * */
		hospitals = new ArrayList<>();
		hospitals.add(new Hospital(new Point(2, 2), Color.red, 1));
		hospitals.add(new Hospital(new Point(9, 1), Color.red, 1));
		hospitals.add(new Hospital(new Point(20, 7), Color.red, 1));
		hospitals.add(new Hospital(new Point(21, 9), Color.red, 1));
		hospitals.add(new Hospital(new Point(19, 10), Color.red, 1));
		hospitals.add(new Hospital(new Point(18, 13), Color.red, 1));
		hospitals.add(new Hospital(new Point(16, 15), Color.red, 1));
		hospitals.add(new Hospital(new Point(16, 20), Color.red, 1));


		/**
		 * Initialize Emergencies
		 * */
		emergencies = new ArrayList<>();

		/**
		 * Create Central
		 * */
		central = new Central(stations, hospitals);

		ambulances = new ArrayList<>();
		for(Station station : stations){
			ambulances.addAll(station.getAmbulances());
		}


		/** A: create board */
		board = new Block[nX][nY];
		for(int i=0; i<nX; i++)
			for(int j=0; j<nY; j++) {
				if (isOcean(i,j)){
					board[i][j] = new Block(Shape.ocean, Color.cyan);
				}
				else{
					board[i][j] = new Block(Shape.free, Color.lightGray);
				}
			}

		objects = new Entity[nX][nY];

		for (Station s : stations){
			s.setCentral(central);
			board[s.point.x][s.point.y] = new Block(Shape.station, s.color);
			objects[s.point.x][s.point.y] = s;
		}

		for (Hospital h : hospitals){
			h.setCentral(central);
			board[h.point.x][h.point.y] = new Block(Shape.hospital, h.color);
			objects[h.point.x][h.point.y] = h;
		}

		blueAmbulancesObjects = new Entity[nX][nY];
		for(Ambulance ambulance : ambulances) {
			if (ambulance.ambulanceType == Ambulance.AmbulanceType.blue){
				blueAmbulancesObjects[ambulance.point.x][ambulance.point.y] = ambulance;
			}
		}

		redAmbulancesObjects = new Entity[nX][nY];
		for(Ambulance ambulance : ambulances) {
			if (ambulance.ambulanceType == Ambulance.AmbulanceType.red){
				redAmbulancesObjects[ambulance.point.x][ambulance.point.y] = ambulance;
			}
		}

		yellowAmbulancesObjects = new Entity[nX][nY];
		for(Ambulance ambulance : ambulances) {
			if (ambulance.ambulanceType == Ambulance.AmbulanceType.yellow){
				yellowAmbulancesObjects[ambulance.point.x][ambulance.point.y] = ambulance;
			}
		}
	}
	
	/****************************
	 ***** B: BOARD METHODS *****
	 ****************************/

	public static int getLostEmergencies() {
		return Board.lostEmergencies;
	}

	public static int getHospitalsMaxCapacity() {
		return hospitalsMaxCapacity;
	}

	public static void setHospitalsMaxCapacity(int hospitalsMaxCapacity) {
		Board.hospitalsMaxCapacity = hospitalsMaxCapacity;
		if (hospitals != null){
			for(Hospital hospital : hospitals){
				hospital.setMaxCapacity(hospitalsMaxCapacity);
			}
		}
	}

	public static int getEmergenciesRandomness() {
		return emergenciesRandomness;
	}

	public static void setEmergenciesRandomness(int emergenciesRandomness) {
		Board.emergenciesRandomness = emergenciesRandomness;
	}

	public static int getBlueAmbulances() {
		return blueAmbulances;
	}

	public static void setBlueAmbulances(int blueAmbulances) {
		if (blueAmbulances > Board.blueAmbulances){
			Board.blueAmbulances = blueAmbulances;
			if (stations != null){
				for(Station station: stations){
					ambulances.removeAll(station.getBlueAmbulances());
					station.addBlueAmbulances(blueAmbulances);
					ambulances.addAll(station.getBlueAmbulances());
				}
			}
		}
		else if(blueAmbulances < Board.blueAmbulances){
			Board.blueAmbulances = blueAmbulances;
			if (stations != null){
				for(Station station: stations){
					ambulances.addAll(station.getBlueAmbulances());
					station.removeBlueAmbulances(blueAmbulances);
					ambulances.addAll(station.getBlueAmbulances());
				}
			}
		}
	}

	public static int getYellowAmbulances() {
		return yellowAmbulances;
	}

	public static void setYellowAmbulances(int yellowAmbulances) {
		if (yellowAmbulances > Board.yellowAmbulances){
			Board.yellowAmbulances = yellowAmbulances;
			if (stations != null){
				for(Station station: stations){
					ambulances.removeAll(station.getYellowAmbulances());
					station.addYellowAmbulances(yellowAmbulances);
					ambulances.addAll(station.getYellowAmbulances());
				}
			}
		}
		else if (yellowAmbulances < Board.yellowAmbulances){
			Board.yellowAmbulances = yellowAmbulances;
			if (stations != null){
				for(Station station: stations){
					ambulances.removeAll(station.getYellowAmbulances());
					station.removeYellowAmbulances(yellowAmbulances);
					ambulances.addAll(station.getYellowAmbulances());
				}
			}
		}
	}

	public static int getRedAmbulances() {
		return redAmbulances;
	}

	public static void setRedAmbulances(int redAmbulances) {
		if (redAmbulances > Board.redAmbulances){
			Board.redAmbulances = redAmbulances;
			if (stations != null){
				for(Station station: stations){
					ambulances.removeAll(station.getRedAmbulances());
					station.addRedAmbulances(redAmbulances);
					ambulances.addAll(station.getRedAmbulances());
				}
			}
		}
		else if (redAmbulances < Board.redAmbulances){
			Board.redAmbulances = redAmbulances;
			if (stations != null){
				for(Station station: stations){
					ambulances.removeAll(station.getRedAmbulances());
					station.removeRedAmbulances(redAmbulances);
					ambulances.addAll(station.getRedAmbulances());
				}
			}
		}
	}

	public static int getEmergenciesInQueue(){
		return central.getEmergenciesInQueue();
	}
	
	public static Entity getEntity(Point point) {
		return objects[point.x][point.y];
	}

	public static Entity getEntity(Point point, Ambulance.AmbulanceType ambulanceType) {
		if (ambulanceType == Ambulance.AmbulanceType.blue){
			return blueAmbulancesObjects[point.x][point.y];
		}
		else if (ambulanceType == Ambulance.AmbulanceType.red){
			return redAmbulancesObjects[point.x][point.y];
		}

		return yellowAmbulancesObjects[point.x][point.y];
	}

	public static void removeEmergency(Emergency emergency){
		emergencies.remove(emergency);
		removeBlock(emergency.point);
		removeEntity(emergency.point);
	}

	public static Block getBlock(Point point) {
		return board[point.x][point.y];
	}
	public static void removeBlock(Point point) {
		board[point.x][point.y] = new Block(Shape.free, Color.lightGray);
	}
	public static void insertBlock(Point point, Shape shape, Color color) {
		board[point.x][point.y] = new Block(shape, color);
	}
	public static void updateEntityPosition(Point point, Point newpoint) {
		objects[newpoint.x][newpoint.y] = objects[point.x][point.y];
		objects[point.x][point.y] = null;
	}

	public static void updateEntityPosition(Point point, Point newpoint, Ambulance.AmbulanceType ambulanceType) {
		if (ambulanceType == Ambulance.AmbulanceType.blue){
			blueAmbulancesObjects[newpoint.x][newpoint.y] = blueAmbulancesObjects[point.x][point.y];
			blueAmbulancesObjects[point.x][point.y] = null;
		}
		else if (ambulanceType == Ambulance.AmbulanceType.red){
			redAmbulancesObjects[newpoint.x][newpoint.y] = redAmbulancesObjects[point.x][point.y];
			redAmbulancesObjects[point.x][point.y] = null;
		}
		else{
			yellowAmbulancesObjects[newpoint.x][newpoint.y] = yellowAmbulancesObjects[point.x][point.y];
			yellowAmbulancesObjects[point.x][point.y] = null;
		}
	}

	public static void removeEntity(Point point) {
		objects[point.x][point.y] = null;
	}
	public static void insertEntity(Entity entity, Point point) {
		objects[point.x][point.y] = entity;
	}

	public static boolean isOcean(int i, int j) {
		return ((j < 0 || i < 0) || (j == 0 && i >= 13) || ( j <= 1 && i >= 17) || ( j <= 2 && i >= 24) || ( j <= 4 && i >= 25) ||
				( j <= 5 && i >= 26) || ( j <= 7 && i >= 27) || ( j <= 8 && i >= 29));
	}

	public static int stepsPerCell(int i, int j) {
		if (j <= 18) {
			if (i >= 13 && i <= 23)
				return 3;
			if ((i >= 3 && i <= 12) || (i >= 23 && i <= 29))
				return 2;
		}
		return 1;
	}

	public static List<Hospital> getHospitals() {
		return hospitals;
	}

	public static int getHospitalsFull(){
		int hospitalsFull = 0;
		for(Hospital hospital : hospitals){
			if (hospital.getCurrentCapacity() == Board.hospitalsMaxCapacity){
				hospitalsFull++;
			}
		}

		return hospitalsFull;
	}

	public static int getMediumTimeToReachHospital(){
		int timeToReachHospital = 0;
		for(Ambulance ambulance : ambulances){
			timeToReachHospital+= ambulance.getTimeToReachHospital();
		}

		return timeToReachHospital/ambulances.size();
	}

	/***********************************
	 ***** C: ELICIT AGENT ACTIONS *****
	 ***********************************/
	
	private static RunThread runThread;
	private static GUI GUI;

	public static int getTime() {
		return time;
	}

	private static int time = 1;

	public static class RunThread extends Thread {

		public RunThread(int time){
			Board.time = time*time;
		}
		
	    public void run() {
	    	while(true){
	    		step();
				try {
					sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    	}
	    }
	}
	
	public static void run(int time) {
		Board.runThread = new RunThread(time);
		Board.runThread.start();
	}

	public static void reset() {
		stepCounter = 1;
		removeObjects();
		initialize();
		GUI.displayBoard();
		displayObjects();	
		GUI.update();
	}

	public static void step() {
		removeObjects();
		for (Ambulance ambulance : ambulances) {
			ambulance.decide();
		}
		displayObjects();
		GUI.update();
		stepCounter++;
	}

	public static void stop() {
		runThread.interrupt();
		runThread.stop();
	}

	public static void displayObjects(){

		// TODO: Change this!
		if ((time % emergenciesRandomness == 0) || (stepCounter % emergenciesRandomness == 0)){
			int random = rand.nextInt(hospitals.size());
			hospitals.get(random).decreaseCurrentCapacity();
		}

		if ((central.getEmergenciesInQueue() != 0) && (central.getEmergenciesInQueue() % (emergenciesRandomness*10) == 0)){
			Emergency e = central.removeEmergencyFromQueue();
			if (e != null){
				removeEmergency(e);
				lostEmergencies++;
			}
		}

		if ((time % emergenciesRandomness == 0) || (stepCounter % emergenciesRandomness == 0)){
			int x = -1;
			int y = -1;
			while (isOcean(x, y)) {
				x = rand.nextInt(nX);
				y = rand.nextInt(nY);
			}
			if (getEntity(new Point(x,y)) == null){
				int randomGravity = rand.nextInt(Emergency.EmergencyGravity.values().length);
				Emergency.EmergencyGravity gravity = Collections.unmodifiableList(Arrays.asList(Emergency.EmergencyGravity.values())).get(randomGravity);
				Emergency emergency = new Emergency(new Point(x, y), Color.orange, gravity);
				emergencies.add(emergency);

				insertBlock(emergency.point, Shape.emergency, emergency.color);
				insertEntity(emergency, emergency.point);

				// central receives the emergency request and selects nearest station
				central.addEmergencyToQueue(emergency);
				GUI.displayObject(emergency);
			}
		}
		central.selectNearestStation();
		for (Emergency emergency: emergencies) GUI.displayObject(emergency);
		for(Station station : stations) GUI.displayObject(station);
		for(Hospital hospital : hospitals) GUI.displayObject(hospital);
		for (Ambulance ambulance : ambulances) {
			updateEntityPosition(ambulance.point, ambulance.point, ambulance.ambulanceType);
			GUI.displayObject(ambulance);
		}
	}
	
	public static void removeObjects(){
		for(Emergency emergency: emergencies) GUI.removeObject(emergency);
		for(Ambulance ambulance: ambulances) GUI.removeObject(ambulance);
	}
	
	public static void associateGUI(GUI graphicalInterface) {
		GUI = graphicalInterface;
	}
}
