package loadingdocks;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Station extends Entity implements Comparable<Station>{

    public enum Action { SelectAmbulance, SendAmbulance, RefuseRequest}
    public enum AmbulanceType { blue, yellow, red}

    public HashMap<Ambulance, AmbulanceType> ambulanceList = new HashMap<Ambulance, AmbulanceType>();

    public HashMap<Ambulance, Emergency> emergencyRequests = new HashMap<Ambulance, Emergency>();

    public List<Emergency> emergencies = new ArrayList<>();

    public int blueAmbulances;
    public int redAmbulances;
    public int yellowAmbulances;

    public Central central;

    public Station(Point point, Color color, int blueAmbulances, int redAmbulances, int yellowAmbulances){

        super(point, color);
        this.blueAmbulances = blueAmbulances;
        this.redAmbulances = redAmbulances;
        this.yellowAmbulances = yellowAmbulances;

        for(int i = 0; i < blueAmbulances; i++){
            ambulanceList.put(new Ambulance(new Point(point.x, point.y), Color.blue, this), AmbulanceType.blue);
        }

        for(int i = 0; i < redAmbulances; i++){
            ambulanceList.put(new Ambulance(new Point(point.x, point.y), Color.red, this), AmbulanceType.red);
        }

        for(int i = 0; i < yellowAmbulances; i++){
            ambulanceList.put(new Ambulance(new Point(point.x, point.y), Color.yellow, this), AmbulanceType.yellow);
        }
    }

    public HashMap<Ambulance, Emergency> getEmergencyRequests() {
        return emergencyRequests;
    }

    public HashMap<Ambulance, AmbulanceType> getAmbulanceList() {
        return ambulanceList;
    }

    public List<Ambulance> getAmbulances() {
        return new ArrayList<Ambulance>(ambulanceList.keySet());
    }

    public void startEmergencyRequest(Ambulance _ambulance, Emergency emergency){
        emergencyRequests.put(_ambulance, emergency);
    }

    public void finishEmergencyRequest(Ambulance _ambulance){
        emergencyRequests.remove(_ambulance);
    }

    public void addEmergency(Emergency emergency){
        emergencies.add(emergency);
    }

    public void removeEmergency(Emergency emergency){
        emergencies.remove(emergency);
    }

    public void stationDecision(){
        if (availableAmbulances() > 0){
            // do something
        }
        else{
            refuseRequest();
        }
    }

    public void stationPerception(){
        if (availableAmbulances() > 0){
            stationDecision();
        }
    }

    /**
     *
     * Sensors
     */

    public boolean isAmbulanceAvailable(Ambulance _ambulance){
        return emergencyRequests.get(_ambulance) == null;
    }

    public int availableAmbulances(){
        return ambulanceList.size() - emergencyRequests.size();
    }

    public boolean hasEmergency(){
        return !emergencies.isEmpty();
    }

    public int blueAmbulancesAvailable(){
        int availableAmbulances = this.blueAmbulances;
        for (Ambulance _ambulance : ambulanceList.keySet()){
            if (ambulanceList.get(_ambulance) == AmbulanceType.blue && emergencyRequests.get(_ambulance) != null){
                availableAmbulances--;
            }
        }
        return availableAmbulances;
    }

    public int redAmbulancesAvailable(){
        int availableAmbulances = this.redAmbulances;
        for (Ambulance _ambulance : ambulanceList.keySet()){
            if (ambulanceList.get(_ambulance) == AmbulanceType.red && emergencyRequests.get(_ambulance) != null){
                availableAmbulances--;
            }
        }
        return availableAmbulances;
    }

    public int yellowAmbulancesAvailable(){
        int availableAmbulances = this.yellowAmbulances;
        for (Ambulance _ambulance : ambulanceList.keySet()){
            if (ambulanceList.get(_ambulance) == AmbulanceType.yellow && emergencyRequests.get(_ambulance) != null){
                availableAmbulances--;
            }
        }
        return availableAmbulances;
    }

    /**
     *
     * Actuators
     */

    public Ambulance selectAmbulance(){

        if (blueAmbulancesAvailable() > 0){
            for (Ambulance _ambulance : ambulanceList.keySet()){
                if (ambulanceList.get(_ambulance) == AmbulanceType.blue && emergencyRequests.get(_ambulance) == null){
                    return _ambulance;
                }
            }
        }
        else if (yellowAmbulancesAvailable() > 0){
            for (Ambulance _ambulance : ambulanceList.keySet()){
                if (ambulanceList.get(_ambulance) == AmbulanceType.yellow && emergencyRequests.get(_ambulance) == null){
                    return _ambulance;
                }
            }
        }
        else if (redAmbulancesAvailable() > 0){
            for (Ambulance _ambulance : ambulanceList.keySet()){
                if (ambulanceList.get(_ambulance) == AmbulanceType.red && emergencyRequests.get(_ambulance) == null){
                    return _ambulance;
                }
            }
        }
        throw new UnsupportedOperationException("Failed to Select an ambulance. There isn't a single ambulance available.");
    }

    public void sendAmbulance(Ambulance ambulance, Emergency emergency){
        // sends an ambulance to an emergency point.
        // Board.getEntity(emergency.point); what should be done with this ?
        // startEmergencyRequest(ambulance, emergency);
        // ambulance.GoToEmergency(emergency);

    }

    public void refuseRequest(){
        // send boolean to central so that the central knows the request was refused.
        // central.stationRefusedRequest(this);
    }

    public void setCentral(Central central) {
        this.central = central;
    }

    public Boolean canReceiveEmergency() {
        return availableAmbulances() > 0;
    }

    // right now this method simply decreases a counter for testing purposes
    public void assistEmergency(Emergency emergency, Hospital hospital) {
        Ambulance ambulance = selectAmbulance();
        addEmergency(emergency);
        startEmergencyRequest(ambulance, emergency);
        ambulance.rescue(emergency, hospital);
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
