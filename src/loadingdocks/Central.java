package loadingdocks;

import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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

    public Emergency getCurrentEmergency() {
        return this.currentEmergency;
        //return this.emergencies.get(0);
    }

    // public Emergency removeEmergencyFromQueue() { return this.emergencies.poll(); }

    public void removeEmergency(Emergency e) { this.emergencies.remove(e); }

    public int getEmergenciesInQueue(){
        return emergencies.size();
    }

    public List<Emergency> getEmergencies() { return this.emergencies; }

    public void sendEmergenciesToAmbulances(){
        if(emergencies.isEmpty()) {
            System.out.println("no current emergencies to handle");
            return;
        }

        System.out.println("Going to send ambulances using behavior :" + Board.getAmbulancesBehavior());
        sendEmergenciesToAmbulancesInternal(Emergency.EmergencyGravity.Low);
        sendEmergenciesToAmbulancesInternal(Emergency.EmergencyGravity.Medium);
        sendEmergenciesToAmbulancesInternal(Emergency.EmergencyGravity.High);

    }

    private void sendEmergenciesToAmbulancesInternal(Emergency.EmergencyGravity gravity){
        System.out.println("Going to send emergencies of type " + gravity + " to ambulances.");
        // does it make a difference emergencies.get(0).EmergencyGravity to select the order in which the ambulances are selected ??
        List<Emergency> emergenciesToHandle = getAvailableEmergenciesPerGravity(emergencies, gravity);
        List<Ambulance> availableAmbulances = getAvailableAmbulancesPerGravity(gravity);

        if (emergenciesToHandle.isEmpty() || availableAmbulances.isEmpty()){
            System.out.println("There are no emergencies of type " + gravity + " to send to ambulances.");
            return;
        }
        if (emergenciesToHandle.size() >= availableAmbulances.size()){
            System.out.println("There are more emergencies of type " + gravity + " than available ambulances.");
            emergenciesToHandle = emergenciesToHandle.subList(0, availableAmbulances.size());
        }

        for(Ambulance a : availableAmbulances){
            a.calculateDistanceToEmergencies(emergenciesToHandle);
            a.minDistanceToEmergency();
        }

        for(Ambulance a : availableAmbulances){
            a.handleConflicts(availableAmbulances);
        }
    }

    public List<Ambulance> getAvailableAmbulancesPerGravity(Emergency.EmergencyGravity gravity){
        // if low emergency, any ambulance can do it
        if (gravity == Emergency.EmergencyGravity.Low){
            List<Ambulance> ambulances = Board.getBlueAmbulancesAvailable();
            if (Board.getAmbulancesBehavior() == Board.AmbulancesBehavior.Risky){
                ambulances.addAll(Board.getYellowAmbulancesAvailable());
                ambulances.addAll(Board.getRedAmbulancesAvailable());
            }
            return ambulances;
        }
        // if medium, only yellow and red
        else if (gravity == Emergency.EmergencyGravity.Medium){
            List<Ambulance> ambulances = Board.getYellowAmbulancesAvailable();
            if (Board.getAmbulancesBehavior() == Board.AmbulancesBehavior.Risky){
                ambulances.addAll(Board.getRedAmbulancesAvailable());
            }
            return ambulances;
        }
        // if high, only red
        else return Board.getRedAmbulancesAvailable();
    }

    public List<Emergency> getAvailableEmergenciesPerGravity(List<Emergency> emergencies, Emergency.EmergencyGravity gravity){
        List<Emergency> emergenciesList = new ArrayList<>();
        for (Emergency emergency : emergencies){
            if (emergency.gravity == gravity){
                emergenciesList.add(emergency);
            }
        }
        return emergenciesList;
    }

    public void selectNearestStation() {
        if(emergencies.isEmpty()) {
            System.out.println("no current emergencies to handle");
            return;
        }

        for(Emergency e: emergencies) {
            this.currentEmergency = e;
            System.out.println("gonna handle emergency at: (" + getCurrentEmergency().point.x + "," + getCurrentEmergency().point.y + ")");

            // we sort according to the manhattan distance to the emergency
            // simply selecting the nearest isn't enough since that station can be occupied
            // when this happens the request is forwarded to the next nearest station, and so on.
            Collections.sort(stations);
            Collections.sort(hospitals);
            Hospital decidedHospital = null;

            for (Hospital h: hospitals) {
                System.out.println("nearest hospital is at: (" + h.point.x + "," + h.point.y + ")");
                System.out.println("Lotation: " + h.getCurrentLotation());
                if (h.canReceivePatient()) {
                    System.out.println("can receive patient");
                    decidedHospital = h;
                    //decidedHospital.increaseCapacity();
                    break;
                }
            }

            // if there is no available hospital the request stays in the queue and we analyse the next request in queue
            if (decidedHospital == null) {
                System.out.println("can't receive emergency due to the lack of hospitals. Emergency will be kept in queue. Analysing next emergency.");
                System.out.println("queue size: " + emergencies.size());

                //Emergency failedEmergency = getCurrentEmergency();
                //emergencies.remove(failedEmergency); // remove from the head
                //emergencies.add(failedEmergency); // add to the tail
                //System.out.println("Queue head: (" + getCurrentEmergency().point.x + "," + getCurrentEmergency().point.y + ")");
                //System.out.println("Queue tail: (" + emergencies.get(emergencies.size() - 1).point.x + "," + emergencies.get(emergencies.size() - 1).point.y + ")");
                continue;
            }

            for (Station s: stations) {
                //Station currentNearestStation = stations.get(i);
                System.out.println("nearest station is at: (" + s.point.x + "," + s.point.y + ")");
                System.out.println("available ambulances: " + s.availableAmbulances());
                if (s.canReceiveEmergency() && s.closestAmbulance != null) {
                    System.out.println("can receive emergency");
                    System.out.println("closest ambulance: (" + s.closestAmbulance.point.x + "," + s.closestAmbulance.point.y + ")");

                    // we insert the hospital call here because we don't want to add one patient to the hospital
                    // if there are no available ambulances
                    Patient patient = new Patient(decidedHospital.getReleaseFactor(), false);
                    decidedHospital.addPatient(patient);
                    s.assistEmergency(getCurrentEmergency(), decidedHospital, patient);
                    removeEmergency(getCurrentEmergency());
                    System.out.println("queue size: " + emergencies.size());
                    return;
                }
            }

            System.out.println("can't receive emergency due to the lack of ambulances. Emergency will be kept in queue. Analysing next emergency.");
            //Emergency failedEmergency = getCurrentEmergency();
            //emergencies.remove(failedEmergency); // remove from the head
            //emergencies.add(failedEmergency); // add to the tail
            //System.out.println("Queue head: (" + getCurrentEmergency().point.x + "," + getCurrentEmergency().point.y + ")");
            //System.out.println("Queue tail: (" + emergencies.get(emergencies.size() - 1).point.x + "," + emergencies.get(emergencies.size() - 1).point.y + ")");
            System.out.println("queue size: " + emergencies.size());
        }

        // if the method reaches this point it means there are no available ambulances, so the request needs to be kept in a queue
    }


}
