package emergencyresponses;

import java.awt.*;

public class Emergency extends Entity {

    public enum EmergencyGravity {Low, Medium, High}
    public EmergencyGravity gravity;
    private int stepsToLose;

    public Emergency(Point point, Color color, EmergencyGravity gravity, int stepsToLose) {
        super(point, color);
        this.gravity = gravity;
        this.stepsToLose = stepsToLose;
    }

    public void updateEmergency() {
        this.stepsToLose --;
    }

    public boolean hasExpired() {
        return this.stepsToLose == 0;
    }
}
