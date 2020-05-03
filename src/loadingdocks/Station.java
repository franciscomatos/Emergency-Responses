package loadingdocks;

import java.awt.*;

public class Station extends Entity implements Comparable<Station> {

    private Central central;
    // hardcoded for testing purposes
    public Integer availableAmbulances = 2;

    public Station(Point point, Color color){
        super(point, color);
    }

    public void setCentral(Central central) {
        this.central = central;
    }

    public Boolean canReceiveEmergency() {
        // available ambulances => availableAmbulances.size()
        if (availableAmbulances > 0)
            return true;
        return false;
    }

    // right now this method simply decreases a counter for testing purposes
    public void assistEmergency() {
        availableAmbulances -= 1;
    }

    public Integer manhattanDistance(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    @Override
    public int compareTo(Station s) {
        if (point == null || s.point == null || central.getCurrentEmergency() == null) {
            return 0;
        }

        return manhattanDistance(point, central.getCurrentEmergency().point).compareTo(s.manhattanDistance(s.point, central.getCurrentEmergency().point));
    }
}
