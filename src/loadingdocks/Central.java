package loadingdocks;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Central {

    private static List<Station> stations;
    private static List<Hospital> hospitals;
    private Queue<Emergency> emergencies;
    private Emergency currentEmergency;

    public Central(List<Station> stations, List<Hospital> hospitals){
        this.stations = stations;
        this.hospitals = hospitals;
        this.emergencies = new LinkedList<>();
    }

    public static List<Station> getStations() {
        return stations;
    }

    public static List<Hospital> getHospitals() {
        return hospitals;
    }

    // public Emergency getCurrentEmergency() { return this.currentEmergency; }

    public void addEmergencyToQueue(Emergency e) { this.emergencies.add(e); }

    public Emergency getCurrentEmergency() { return this.emergencies.peek(); }

    public void removeEmergencyFromQueue() { this.emergencies.poll(); }

    public void selectNearestStation() {
        System.out.println("gonna handle emergency at: (" + getCurrentEmergency().point.x + "," + getCurrentEmergency().point.y + ")");
        //currentEmergency = e;
        // we sort according according to the manhattan distance to the emergency
        // simply selecting the nearest isn't enough since that station can be occupied
        // when this happens the request is forwarded to the next nearest station, and so on.
        Collections.sort(stations);
        Collections.sort(hospitals);

        for(int i = 0; i < stations.size(); i++) {
            Station currentNearestStation = stations.get(i);
            System.out.println("nearest station is at: (" + currentNearestStation.point.x + "," + currentNearestStation.point.y + ")");
            System.out.println("available ambulances: " + currentNearestStation.availableAmbulances());
            if(currentNearestStation.canReceiveEmergency()) {
                System.out.println("can receive emergency");
                currentNearestStation.assistEmergency(getCurrentEmergency(), hospitals.get(0));
                removeEmergencyFromQueue();
                return;
            }
        }
        System.out.println("can't receive emergency. Emergency will be kept in queue");
        System.out.println("queue size: " + emergencies.size());

        // if the method reaches this point it means there are no available ambulances, so the request needs to be kept in a queue
    }


}
