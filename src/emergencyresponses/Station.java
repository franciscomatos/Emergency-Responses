package emergencyresponses;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Station extends Entity implements Comparable<Station>{

    public HashMap<Ambulance, Emergency> emergencyRequests = new HashMap<>();

    public List<Ambulance> ambulances = new ArrayList<>();

    public Central central;

    public Ambulance closestAmbulance;

    public int emergenciesCompleted;

    public Station(Point point, Color color, int blueAmbulances, int yellowAmbulances, int redAmbulances){

        super(point, color);

        for(int i = 0; i < blueAmbulances; i++){
            this.ambulances.add(new Ambulance(new Point(point.x, point.y), Color.blue, Ambulance.AmbulanceType.blue, this));
        }

        for(int i = 0; i < redAmbulances; i++){
            this.ambulances.add(new Ambulance(new Point(point.x, point.y), Color.red, Ambulance.AmbulanceType.red, this));
        }

        for(int i = 0; i < yellowAmbulances; i++){
            this.ambulances.add(new Ambulance(new Point(point.x, point.y), Color.yellow, Ambulance.AmbulanceType.yellow, this));
        }

        emergenciesCompleted = 0;
    }

    public void addBlueAmbulances(int ambulancesToAdd){
        for(int i = 0; i < ambulancesToAdd; i++){
            this.ambulances.add(new Ambulance(new Point(point.x, point.y), Color.blue, Ambulance.AmbulanceType.blue, this));
        }
    }

    public void addRedAmbulances(int ambulancesToAdd){
        for(int i = 0; i < ambulancesToAdd; i++){
            this.ambulances.add(new Ambulance(new Point(point.x, point.y), Color.red, Ambulance.AmbulanceType.red, this));
        }
    }

    public void addYellowAmbulances(int ambulancesToAdd){
        for(int i = 0; i < ambulancesToAdd; i++){
            this.ambulances.add(new Ambulance(new Point(point.x, point.y), Color.yellow, Ambulance.AmbulanceType.yellow, this));
        }
    }

    public void removeBlueAmbulances(int ambulancesToRemove){
        for (Iterator<Ambulance> iterator = ambulances.iterator(); iterator.hasNext();) {
            Ambulance ambulance = iterator.next();
            if(ambulance.available && ambulance.ambulanceType == Ambulance.AmbulanceType.blue &&
                    ambulance.isAtStation(ambulance.point) && ambulancesToRemove > 0){
                iterator.remove();
                ambulancesToRemove--;
            }
        }
    }

    public void removeRedAmbulances(int ambulancesToRemove){
        for (Iterator<Ambulance> iterator = ambulances.iterator(); iterator.hasNext(); ) {
            Ambulance ambulance = iterator.next();
            if (ambulance.available && ambulance.ambulanceType == Ambulance.AmbulanceType.red &&
                    ambulance.isAtStation(ambulance.point) && ambulancesToRemove > 0) {
                iterator.remove();
                ambulancesToRemove--;
            }
        }
    }

    public void removeYellowAmbulances(int ambulancesToRemove){
        for (Iterator<Ambulance> iterator = ambulances.iterator(); iterator.hasNext(); ) {
            Ambulance ambulance = iterator.next();
            if (ambulance.available && ambulance.ambulanceType == Ambulance.AmbulanceType.yellow &&
                    ambulance.isAtStation(ambulance.point) && ambulancesToRemove > 0) {
                iterator.remove();
                ambulancesToRemove--;
            }
        }
    }

    public int getEmergenciesCompleted() {
        return emergenciesCompleted;
    }

    public void setEmergenciesCompleted(int emergenciesCompleted) {
        this.emergenciesCompleted = emergenciesCompleted;
    }

    public int getMediumTimeToReachHospital(){
        int timeToReachHospital = 0;
        for(Ambulance ambulance : ambulances){
            timeToReachHospital+= ambulance.getTimeToReachHospital();
        }

        return timeToReachHospital/ambulances.size();
    }

    public HashMap<Ambulance, Emergency> getEmergencyRequests() {
        return emergencyRequests;
    }

    public List<Ambulance> getAmbulances() {
        return ambulances;
    }

    public List<Ambulance> getClosestGravityAmbulances(Emergency.EmergencyGravity gravity){
        // if low emergency, any ambulance can do it
        if (gravity == Emergency.EmergencyGravity.Low){
            List<Ambulance> ambulances = getBlueAmbulances();
            ambulances.addAll(getYellowAmbulances());
            ambulances.addAll(getRedAmbulances());
            return ambulances;
        }
        // if medium, only yellow and red
        else if (gravity == Emergency.EmergencyGravity.Medium){
            List<Ambulance> ambulances = getYellowAmbulances();
            ambulances.addAll(getRedAmbulances());
            return ambulances;
        }
        // if high, only red
        else return getRedAmbulances();
    }

    public List<Ambulance> getBlueAmbulances() {
        List<Ambulance> _ambulances = new ArrayList<>();
        for (Ambulance ambulance: ambulances){
            if (ambulance.ambulanceType == Ambulance.AmbulanceType.blue){
                _ambulances.add(ambulance);
            }
        }
        return _ambulances;
    }

    public List<Ambulance> getYellowAmbulances() {
        List<Ambulance> _ambulances = new ArrayList<>();
        for (Ambulance ambulance: ambulances){
            if (ambulance.ambulanceType == Ambulance.AmbulanceType.yellow){
                _ambulances.add(ambulance);
            }
        }
        return _ambulances;
    }

    public List<Ambulance> getRedAmbulances() {
        List<Ambulance> _ambulances = new ArrayList<>();
        for (Ambulance ambulance: ambulances){
            if (ambulance.ambulanceType == Ambulance.AmbulanceType.red){
                _ambulances.add(ambulance);
            }
        }
        return _ambulances;
    }

    public void startEmergencyRequest(Ambulance _ambulance, Emergency emergency){
        emergencyRequests.put(_ambulance, emergency);
    }

    public void finishEmergencyRequest(Ambulance _ambulance){
        emergencyRequests.remove(_ambulance);
        emergenciesCompleted++;
    }

    /**
     *
     * Sensors
     */

    public boolean isAmbulanceAvailable(Ambulance _ambulance){
        return emergencyRequests.get(_ambulance) == null;
    }

    public int availableAmbulances(){
        return getAmbulances().size() - emergencyRequests.size();
    }

    /**
     *
     * Actuators
     */

    public void setCentral(Central central) {
        this.central = central;
    }

    public Boolean canReceiveEmergency() {
        return availableAmbulances() > 0;
    }

    public void assistEmergency(Emergency emergency, Hospital hospital, Patient patient) {
        startEmergencyRequest(closestAmbulance, emergency);
        closestAmbulance.rescue(emergency, hospital, patient);
    }

    public Integer manhattanDistance(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    public List<Ambulance> getAmbulances(Emergency.EmergencyGravity gravity){
        List<Ambulance> ambulances = new ArrayList<>();
        switch (gravity){
            case Low:
                ambulances = getBlueAmbulances();
                break;
            case Medium:
                ambulances = getYellowAmbulances();
                break;
            case High:
                ambulances = getRedAmbulances();
                break;
        }

        return ambulances;
    }

    public Integer minimumAmbulanceDistance() {
        Integer minimumDistance = Integer.MAX_VALUE;
        closestAmbulance = null;
        Emergency e = central.getCurrentEmergency();

        if (Board.getAmbulancesBehavior() == Board.AmbulancesBehavior.Conservative){
            //System.out.println("conservativeDecision selected.");
            for(Ambulance ambulance: getAmbulances(e.gravity)) {
                if(ambulance.available) {
                    Integer currentDistance = manhattanDistance(ambulance.point, e.point);
                    if (currentDistance < minimumDistance) {
                        minimumDistance = currentDistance;
                        closestAmbulance = ambulance;
                    }
                }
            }
        }
        else if (Board.getAmbulancesBehavior() == Board.AmbulancesBehavior.Risky){
            //System.out.println("risky selected.");
            for (Ambulance ambulance : getClosestGravityAmbulances(e.gravity)) {
                if (ambulance.available) {
                    Integer currentDistance = manhattanDistance(ambulance.point, e.point);
                    if (currentDistance < minimumDistance) {
                        minimumDistance = currentDistance;
                        closestAmbulance = ambulance;
                    }
                }
            }
        }
        return minimumDistance;
    }

    @Override
    public int compareTo(Station s) {
        if (point == null || s.point == null || central.getCurrentEmergency() == null) {
            return 0;
        }

        return minimumAmbulanceDistance().compareTo(s.minimumAmbulanceDistance());
    }
}