package loadingdocks;

import java.awt.*;

public class Ambulance extends Entity {
    private boolean _available;
    private boolean _patient;
    private Station _station;
    private Emergency _emergency;
    private Hospital _hospital;

    public Ambulance (Point point, Color color, Station station) {
        super(point, color);
        _available = true;
        _patient = false;
        _station = station;
    }

    public void rescue(Emergency emergency, Hospital hospital) {
        setEmergency(emergency);
        setHospital(hospital);
        setAvailable(false);
    }

    public void decide() {
        //System.out.println("Patient " + _patient);
        //System.out.println("Emergency " + _emergency.point);
        //System.out.println("Hospital " + _hospital.point);

        if (!_available && !_patient && this.point.equals(_emergency.point)) {
            System.out.println("Picked up patient");
            pickupPatient();
        }
        else if (!_available && _patient && this.point.equals(_hospital.point)) {
            System.out.println("Dropped patient");
            dropPatient();
        }
        else if (!_available || _available && !this.point.equals((_station.point))) {
            move();
        }
        else {
            System.out.println("At station");
        }

    }

    public void move() {
        Point nextPosition = this.point;
        if (isAvailable()) {
            System.out.println("Moving to station");
            nextPosition = nextPosition(_station.point);
        }
        else if (!isAvailable() && !_patient) {
            System.out.println("Moving to patient");
            nextPosition = nextPosition(_emergency.point);
        }
        else if (!isAvailable() && _patient) {
            System.out.println("Moving to hospital");
            nextPosition = nextPosition(_hospital.point);
        }
        Board.updateEntityPosition(this.point, nextPosition);
        this.point = nextPosition;
        //System.out.println("Ambulance moved to " + this.point);
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
        if (nextX == 0)
            nextX = 1;
        if (nextY == 0)
            nextY = 1;
        return new Point(nextX, nextY);
    }

    public void setAvailable(boolean available) {
        _available = available;
    }

    public void setPatient(boolean patient) {
        _patient = patient;
    }

    public void setEmergency(Emergency emergency) {
        _emergency = emergency;
    }

    public void setHospital(Hospital hospital) {
        _hospital = hospital;
    }

    public boolean isAvailable() {
        return _available;
    }

    public boolean isPatient() {
        return _patient;
    }

    public Emergency getEmergency() {
        return _emergency;
    }

    public Hospital getHospital() {
        return _hospital;
    }
}
