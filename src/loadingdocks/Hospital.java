package loadingdocks;

import java.awt.*;

public class Hospital extends Entity implements Comparable<Hospital>{

    private Integer currentCapacity;
    private Integer maxCapacity;
    public Central central;

    public Hospital(Point point, Color color, int maxCapacity) {
        super (point, color);
        this.currentCapacity = 0;
        this.maxCapacity = maxCapacity;
    }

    public void setCentral(Central central) {
        this.central = central;
    }

    public Integer getCurrentCapacity() { return this.currentCapacity; }

    public void decreaseCurrentCapacity() {
        if (this.currentCapacity > 0) {
            this.currentCapacity--;
        }
    }

    public void increaseCurrentCapacity() {
        if(this.currentCapacity < this.maxCapacity)
            this.currentCapacity++;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity){
        maxCapacity = maxCapacity;
    }

    public void setCurrentCapacity(Integer currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public Boolean canReceivePatient() { return currentCapacity < maxCapacity; }

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
