package loadingdocks;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Ambulance extends Entity implements Comparable<Ambulance>{

    public enum AmbulanceDirection {N, S, E, O, NE, NO, SE, SO}
    public enum AmbulanceType {
        blue(1),
        yellow(2),
        red(3);

        public final int label;

        private AmbulanceType(int label) {
            this.label = label;
        }
    }

    public boolean available;
    public boolean hasPatient;
    public Station station;
    public Emergency emergency;
    public Hospital hospital;
    public AmbulanceDirection direction;
    public AmbulanceType ambulanceType;

    public int timeToReachHospital;

    public int stepsLeftToMove;

    private Patient currentPatient = null;

    private static Random rand = new Random();

    public Ambulance (Point point, Color color, AmbulanceType ambulanceType, Station station)
    {
        super(point, color);
        this.available = true;
        this.hasPatient = false;
        this.station = station;
        this.direction = AmbulanceDirection.SO;
        this.ambulanceType = ambulanceType;
        stepsLeftToMove = Board.stepsPerCell(this.station.point.x, this.station.point.y);
        this.timeToReachHospital = 0;
    }

    public void rescue(Emergency emergency, Hospital hospital, Patient patient) {
        setEmergency(emergency);
        setHospital(hospital);
        setCurrentPatient(patient);
        setAvailable(false);
    }

    public void decide() {
        if ((this.emergency == null) || (this.station == null)|| (this.hospital == null)){
            return;
        }
        if (stepsLeftToMove > 1) {
            stepsLeftToMove--;
            return;
        }
        if (!this.available && !this.hasPatient && this.point.equals(this.emergency.point)) {
            System.out.println("Ambulance picked up patient");
            pickupPatient();
            this.station.finishEmergencyRequest(this);
            Board.removeEmergency(this.emergency);
        }
        else if (!this.available && this.hasPatient && this.point.equals(this.hospital.point)) {
            System.out.println("Ambulance dropped patient");
            dropPatient();
        }
        else if (!this.available || this.available && !this.point.equals((this.station.point))) {
            move();
            stepsLeftToMove = Board.stepsPerCell(this.point.x, this.point.y);
        }
    }

    public void move() {
        Point nextPosition = this.point;
        if (isAvailable()) {
            System.out.println("Moving to station");
            nextPosition = nextPosition(this.station.point);
        }
        else if (!isAvailable() && !this.hasPatient) {
            System.out.println("Moving to patient");
            nextPosition = nextPosition(this.emergency.point);
        }
        else if (!isAvailable() && this.hasPatient) {
            System.out.println("Moving to hospital");
            nextPosition = nextPosition(this.hospital.point);
        }
        Board.updateEntityPosition(this.point, nextPosition, this.ambulanceType);
        Board.removeObject(this);
        this.point = nextPosition;
        Board.displayObject(this);
        this.point = nextPosition;
    }

    public void pickupPatient() {
        setPatient(true);
    }

    public void dropPatient() {
        setPatient(false);
        setAvailable(true);
        this.currentPatient.setInHospital(true);
    }

    /*****************************
     ***** AUXILIARY METHODS *****
     *****************************/

    public Point nextPosition(Point dest) {
        int dX = dest.x - this.point.x;
        int dY = dest.y - this.point.y;
        int nextX = this.point.x + Integer.signum(dX);
        int nextY = this.point.y + Integer.signum(dY);

        while((Board.isOcean(nextX, nextY)) || !isFreeCell(nextX, nextY)){
            nextX = this.point.x + rand.nextInt(2 + 1) - 1;
            nextY = this.point.y + rand.nextInt(2 + 1) - 1;

            if ((nextX > (this.point.x + 1)) || (nextX < (this.point.x - 1))){
                nextX = this.point.x;
            }
            if ((nextY > (this.point.y + 1)) || (nextY < (this.point.y - 1))){
                nextY = this.point.y;
            }
        }
        if (nextX == -1) {
            nextX = 0;
        }
        if (nextY == -1) {
            nextY = 0;
        }
        updateDirection(dX, dY, nextX, nextY);
        return new Point(nextX, nextY);
    }

    private boolean isFreeCell(int nextX, int nextY){
        if (this.point.x == nextX && this.point.y == nextY){
            return false;
        }
        if (Board.isOutOfBoard(new Point(nextX, nextY))){
            return false;
        }
        Entity entity = Board.getEntity(new Point(nextX, nextY));
        if (entity == null){ // || entity instanceof Emergency
            return true;
        }
        else if (isAtHospital(new Point(nextX, nextY)) ||
                isAtEmergency(new Point(nextX, nextY)) ||
                isAtStation(new Point(nextX, nextY)))
        {
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isAtStation(Point point){
        return this.station.point.x == point.x && this.station.point.y == point.y;
    }

    public boolean isAtEmergency(Point point){
        return this.emergency.point.x == point.x && this.emergency.point.y == point.y;
    }

    public boolean isAtHospital(Point point){
        return this.hospital.point.x == point.x && this.hospital.point.y == point.y;
    }

    public void updateDirection(int dX, int dY, int nextX, int nextY) {
        int signX = Integer.signum(dX);
        int signY = Integer.signum(dY);

        if (signX == 0) {
            if (signY > 0)
                this.direction = AmbulanceDirection.N;
            else if (signY < 0)
                this.direction = AmbulanceDirection.S;
        }
        else if (signY == 0) {
            if (signX > 0)
                this.direction = AmbulanceDirection.E;
            else if (signX < 0)
                this.direction = AmbulanceDirection.O;
        }
        else if (signY > 0) {
            if (signX > 0)
                this.direction = AmbulanceDirection.NE;
            else if (signX < 0)
                this.direction = AmbulanceDirection.NO;
        }
        else if (signY < 0) {
            if (signX > 0)
                this.direction = AmbulanceDirection.SE;
            else if (signX < 0)
                this.direction = AmbulanceDirection.SO;
        }
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setPatient(boolean patient) {
        this.hasPatient = patient;
    }

    public void setCurrentPatient(Patient patient) { this.currentPatient = patient; }

    public void setEmergency(Emergency emergency) {
        this.emergency = emergency;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public boolean isAvailable() {
        return this.available;
    }

    public boolean isPatient() {
        return this.hasPatient;
    }

    public Emergency getEmergency() {
        return this.emergency;
    }

    public Hospital getHospital() {
        return this.hospital;
    }

    public AmbulanceType getAmbulanceType() {
        return ambulanceType;
    }

    public int getTimeToReachHospital() {
        return timeToReachHospital;
    }

    public void setAmbulanceType(AmbulanceType ambulanceType) {
        this.ambulanceType = ambulanceType;
    }

    @Override
    public int compareTo(Ambulance a) {
        return Integer.compare(this.ambulanceType.label, a.ambulanceType.label);
    }
}