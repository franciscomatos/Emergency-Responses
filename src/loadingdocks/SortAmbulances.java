package loadingdocks;

import java.util.Comparator;

class SortAmbulances implements Comparator<Ambulance>
{
    public int compare(Ambulance a, Ambulance b)
    {
        return a.ambulanceType.compareTo(b.ambulanceType) +  (a.getTimeToReachHospital() - b.getTimeToReachHospital());
    }
}
