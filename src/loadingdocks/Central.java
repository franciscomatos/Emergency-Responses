package loadingdocks;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Central {

    private static List<Station> stations;
    private static List<Hospital> hospitals;

    public Central(java.util.List<Station> stations, List<Hospital> hospitals){
        this.stations = stations;
        this.hospitals = hospitals;
    }

    public static List<Station> getStations() {
        return stations;
    }

    public static List<Hospital> getHospitals() {
        return hospitals;
    }
}
