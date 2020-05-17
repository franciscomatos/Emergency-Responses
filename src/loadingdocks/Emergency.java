package loadingdocks;

import java.awt.*;

public class Emergency extends Entity {
    public enum EmergencyGravity {Low, Medium, High};
    public EmergencyGravity gravity;
    public Emergency(Point point, Color color, EmergencyGravity gravity) {
        super(point, color);
        this.gravity = gravity;
    }
}
