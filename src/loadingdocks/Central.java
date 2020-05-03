package loadingdocks;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Central {

    private static List<Station> stations;
    private static List<Hospital> hospitals;
    private Emergency currentEmergency;

    public Central(List<Station> stations, List<Hospital> hospitals){
        this.stations = stations;
        this.hospitals = hospitals;
    }

    public static List<Station> getStations() {
        return stations;
    }

    public static List<Hospital> getHospitals() {
        return hospitals;
    }

    public Emergency getCurrentEmergency() { return this.currentEmergency; }

    public Station selectNearestStation(Emergency e) {
        currentEmergency = e;
        // we sort according according to the manhattan distance to the emergency
        // simply selecting the nearest isn't enough since that station can be occupied
        // when this happens the request is forwarded to the next nearest station, and so on.
        Collections.sort(stations);

        for(int i = 0; i < stations.size(); i++) {
            Station currentNearestStation = stations.get(i);
            System.out.println("nearest station is at: (" + currentNearestStation.point.x + "," + currentNearestStation.point.y + ")");
            System.out.println("available ambulances: " + currentNearestStation.availableAmbulances);
            if(currentNearestStation.canReceiveEmergency()) {
                System.out.println("can receive emergency");
                currentNearestStation.assistEmergency();
                return currentNearestStation;
            }
            System.out.println("can't receive emergency");
        }
        // if the method reaches this point it means there are no available ambulances, so the request needs to be kept in a queue
        return null;
    }


}
