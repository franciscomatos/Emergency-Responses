package loadingdocks;

public class Patient {

    private Integer releaseFactor;
    private Boolean inHospital;

    public Patient(Integer releaseFactor, Boolean inHospital) {
        this.releaseFactor = releaseFactor;
        this.inHospital = inHospital;
    }

    public void setInHospital(Boolean inHospital) {
        this.inHospital = inHospital;
    }

    public Boolean inHospital() { return this.inHospital; }

    public Boolean toBeReleased() { return this.releaseFactor == 0; }

    public void decreaseHospitalTime() { this.releaseFactor--; }

    @Override
    public String toString() {
        return "releaseFactor:" + releaseFactor + "\tinHospital:" + inHospital.toString();
    }
}
