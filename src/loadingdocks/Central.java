package loadingdocks;

import java.util.*;
import java.util.List;

public class Central {

    private static List<Station> stations;
    private static List<Hospital> hospitals;
    private List<Emergency> emergencies;
    private Emergency currentEmergency;

    public Central(List<Station> stations, List<Hospital> hospitals){
        this.stations = stations;
        this.hospitals = hospitals;
        this.emergencies = new ArrayList<>();
    }

    public static List<Station> getStations() {
        return stations;
    }

    public static List<Hospital> getHospitals() {
        return hospitals;
    }

    // public Emergency getCurrentEmergency() { return this.currentEmergency; }

    public void addEmergencyToQueue(Emergency e) { this.emergencies.add(e); }

    public Emergency getCurrentEmergency() { return this.emergencies.get(0); }

    // public Emergency removeEmergencyFromQueue() { return this.emergencies.poll(); }

    public void removeEmergency(Emergency e) { this.emergencies.remove(e); }

    public int getEmergenciesInQueue(){
        return emergencies.size();
    }

    public void selectNearestStation() {
        if(emergencies.isEmpty()) {
            System.out.println("no current emergencies to handle");
            return;
        }
        System.out.println("gonna handle emergency at: (" + getCurrentEmergency().point.x + "," + getCurrentEmergency().point.y + ")");
        //currentEmergency = e;
        // we sort according according to the manhattan distance to the emergency
        // simply selecting the nearest isn't enough since that station can be occupied
        // when this happens the request is forwarded to the next nearest station, and so on.
        Collections.sort(stations);
        Collections.sort(hospitals);
        Hospital decidedHospital = null;

        for(int i = 0; i < hospitals.size(); i++) {
            Hospital currentNearestHospital = hospitals.get(i);
            System.out.println("nearest hospital is at: (" + currentNearestHospital.point.x + "," + currentNearestHospital.point.y + ")");
            System.out.println("Lotation: " + currentNearestHospital.getCurrentLotation());
            if(currentNearestHospital.canReceivePatient()) {
                System.out.println("can receive patient");
                decidedHospital = currentNearestHospital;
                //decidedHospital.increaseCapacity();
                break;
            }
        }

        // if there is no available hospital the request stays in the queue
        if (decidedHospital == null) {
            System.out.println("can't receive emergency due to the lack of hospitals. Emergency will be kept in queue");
            System.out.println("queue size: " + emergencies.size());
            return;
        }

        for(int i = 0; i < stations.size(); i++) {
            Station currentNearestStation = stations.get(i);
            System.out.println("nearest station is at: (" + currentNearestStation.point.x + "," + currentNearestStation.point.y + ")");
            System.out.println("available ambulances: " + currentNearestStation.availableAmbulances());
            if(currentNearestStation.canReceiveEmergency() && currentNearestStation.closestAmbulance != null) {
                System.out.println("can receive emergency");
                System.out.println("closest ambulance: (" + currentNearestStation.closestAmbulance.point.x + "," + currentNearestStation.closestAmbulance.point.y + ")");

                // we insert the hospital call here because we don't want to add one patient to the hospital
                // if there are no available ambulances
                Patient patient = new Patient(decidedHospital.getReleaseFactor(), false);
                decidedHospital.addPatient(patient);
                currentNearestStation.assistEmergency(getCurrentEmergency(), decidedHospital, patient);
                removeEmergency(getCurrentEmergency());
                System.out.println("queue size: " + emergencies.size());
                return;
            }
        }

        System.out.println("can't receive emergency due to the lack of ambulances. Emergency will return to the end of the queue");
        Emergency failedEmergency = getCurrentEmergency();
        emergencies.remove(failedEmergency); // remove from the head
        emergencies.add(failedEmergency); // add to the tail
        System.out.println("queue size: " + emergencies.size());

        // if the method reaches this point it means there are no available ambulances, so the request needs to be kept in a queue
    }


}
