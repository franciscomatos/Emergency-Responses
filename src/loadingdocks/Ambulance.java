package loadingdocks;

import java.awt.*;

public class Ambulance extends Entity {
    public enum AmbulanceDirection {N, S, E, O, NE, NO, SE, SO}

    public boolean available;
    public boolean patient;
    public Station station;
    public Emergency emergency;
    public Hospital hospital;
    public AmbulanceDirection direction;

    public int stepsLeftToMove;

    public Ambulance (Point point, Color color, Station station) {
        super(point, color);
        this.available = true;
        this.patient = false;
        this.station = station;
        this.direction = AmbulanceDirection.SO;
        stepsLeftToMove = Board.stepsPerCell(this.station.point.x, this.station.point.y);
    }

    public void rescue(Emergency emergency, Hospital hospital) {
        setEmergency(emergency);
        setHospital(hospital);
        setAvailable(false);
    }

    public void decide() {
        if (stepsLeftToMove > 1) {
            stepsLeftToMove--;
            return;
        }
        if (!this.available && !this.patient && this.point.equals(this.emergency.point)) {
            System.out.println("Ambulance picked up patient");
            pickupPatient();
        }
        else if (!this.available && this.patient && this.point.equals(this.hospital.point)) {
            System.out.println("Ambulance dropped patient");
            dropPatient();
            Board.removeBlock(this.emergency.point);
            this.station.finishEmergencyRequest(this);
            this.station.removeEmergency(this.emergency);
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
        else if (!isAvailable() && !this.patient) {
            System.out.println("Moving to patient");
            nextPosition = nextPosition(this.emergency.point);
        }
        else if (!isAvailable() && this.patient) {
            System.out.println("Moving to hospital");
            nextPosition = nextPosition(this.hospital.point);
        }
        Board.updateEntityPosition(this.point, nextPosition);
        this.point = nextPosition;
    }

    public void pickupPatient() {
        setPatient(true);
    }

    public void dropPatient() {
        setPatient(false);
        setAvailable(true);
    }

    /*****************************
     ***** AUXILIARY METHODS *****
     *****************************/

    public Point nextPosition(Point dest) {
        int dX = dest.x - this.point.x;
        int dY = dest.y - this.point.y;
        int nextX = this.point.x + Integer.signum(dX);
        int nextY = this.point.y + Integer.signum(dY);
        if (nextX == -1) {
            nextX = 0;
        }
        if (nextY == -1) {
            nextY = 0;
        }
        updateDirection(dX, dY, nextX, nextY);
        return new Point(nextX, nextY);
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
        this.patient = patient;
    }

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
        return this.patient;
    }

    public Emergency getEmergency() {
        return this.emergency;
    }

    public Hospital getHospital() {
        return this.hospital;
    }
}