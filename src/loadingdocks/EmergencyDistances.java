package loadingdocks;

public class EmergencyDistances implements Comparable<EmergencyDistances>{
    private int ambulanceDistance;
    private Emergency emergency;

    private Ambulance ambulance;

    public EmergencyDistances(Emergency emergency, int ambulanceDistance){
        this.emergency = emergency;
        this.ambulanceDistance = ambulanceDistance;
    }

    public Emergency getEmergency() {
        return emergency;
    }

    public void setEmergency(Emergency emergency) {
        this.emergency = emergency;
    }

    public int getAmbulanceDistance() {
        return ambulanceDistance;
    }

    public void setAmbulanceDistance(int ambulanceDistance) {
        this.ambulanceDistance = ambulanceDistance;
    }
    @Override
    public int compareTo(EmergencyDistances s) {
        if (s == null || s.emergency == null || s.emergency.point == null) {
            return 0;
        }

        return Integer.compare(this.ambulanceDistance, s.ambulanceDistance);
    }

}
