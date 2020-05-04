package loadingdocks;

import java.awt.*;

public class Hospital extends Entity implements Comparable<Hospital>{

    public Central central;
    public Hospital(Point point, Color color) {
        super (point, color);
    }

    public void setCentral(Central central) {
        this.central = central;
    }

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
