package loadingdocks;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Hospital extends Entity implements Comparable<Hospital>{

    private Integer maxCapacity;
    private Integer releaseFactor; // parameter to be given at the beginning of simulation
    // array of ints representing time to release each patient
    private List<Patient> patients;
    public Central central;

    public Hospital(Point point, Color color, int maxCapacity) {
        super (point, color);
        this.maxCapacity = maxCapacity;
        this.patients = new ArrayList<>();
        this.releaseFactor = 5; // right now is hardcoded to 10
    }

    public void setCentral(Central central) {
        this.central = central;
    }

    public Integer getCurrentLotation() { return this.patients.size(); }

    public void addPatient(Patient patient) {
        this.patients.add(patient);
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public Integer getReleaseFactor() { return this.releaseFactor; }

    public void setMaxCapacity(int maxCapacity){
        this.maxCapacity = maxCapacity;
    }

    public void updatePatients() {
        // decreases time for every patient, removes the ones that got to 0 and updates current capacity
        for(Patient patient: this.patients) {
            if(patient.inHospital()) patient.decreaseHospitalTime();
        }

        Iterator<Patient> patientsIterator = patients.iterator();
        while (patientsIterator.hasNext()) {
            Patient p = patientsIterator.next();
            if(p.inHospital() && p.toBeReleased()) patientsIterator.remove();
        }

        //setCurrentCapacity(getMaxCapacity() - this.patients.size());

        System.out.println("Updated patients. Now there are " + this.patients.size() + " patients:");
        System.out.println(this.patients.toString());
    }

    public Boolean canReceivePatient() { return this.patients.size() < maxCapacity; }

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
