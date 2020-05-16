package loadingdocks;

import java.awt.*;

public class Hospital extends Entity implements Comparable<Hospital>{

    private Integer currentLotation;
    private Integer maxLotation;
    public Central central;

    public Hospital(Point point, Color color, int lotation) {
        super (point, color);
        this.currentLotation = lotation;
        this.maxLotation = lotation;
    }

    public void setCentral(Central central) {
        this.central = central;
    }

    public Integer getLotation() { return this.currentLotation; }

    public void decreaseLotation() { this.currentLotation--;}

    public void increaseLotation() {
        if(this.currentLotation < this.maxLotation)
            this.currentLotation++;
    }

    public Boolean canReceivePatient() { return getLotation() > 0; }

    public Integer manhattanDistance(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    @Override
    public int compareTo(Hospital hospital) {
        if (point == null || hospital.point == null || central.getCurrentEmergency() == null) {
            return 0;
        }

        return manhattanDistance(point, central.getCurrentEmergency().point).compareTo(hospital.manhattanDistance(hospital.point, central.getCurrentEmergency().point));
    }

}
