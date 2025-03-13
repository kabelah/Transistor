package datatypes;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class JourneyData {
    private final int initialWalkTime;
    private final int totalWaitTime;
    private final LinkedHashMap<String, Integer> busRoutes;
    private final int finalWalkTime;
    private final int totalJourneyTime;
    private final boolean hasTransfers;
    private final ArrayList<String> transferDetails;
    private final LocalTime arrivalTime;
    private final ArrayList<String> busNumbers;
    private final String detailMessage;

    // Updated constructor to accept the new fields
    public JourneyData(int initialWalkTime, int totalWaitTime, LinkedHashMap<String, Integer> busRoutes,
                       int finalWalkTime, int totalJourneyTime, boolean hasTransfers,
                       ArrayList<String> transferDetails, LocalTime arrivalTime, ArrayList<String> busNumbers) {
        this.initialWalkTime = initialWalkTime;
        this.totalWaitTime = totalWaitTime;
        this.busRoutes = busRoutes;
        this.finalWalkTime = finalWalkTime;
        this.totalJourneyTime = totalJourneyTime;
        this.hasTransfers = hasTransfers;
        this.transferDetails = transferDetails;
        this.arrivalTime = arrivalTime;
        this.busNumbers = busNumbers;
        
        // Generate a default detail message
        if (busRoutes != null && !busRoutes.isEmpty()) {
            this.detailMessage = "Walk " + initialWalkTime + " minutes to " + busRoutes.entrySet().iterator().next().getKey();
        } else {
            this.detailMessage = "Walking route only";
        }
    }
    
    // New constructor for simple walking route
    public JourneyData(List<String> busNumbers, List<Integer> durations, List<String> stopNames, 
                       String detailMessage, int totalWalkTime) {
        this.initialWalkTime = totalWalkTime;
        this.totalWaitTime = 0;
        this.busRoutes = new LinkedHashMap<>();
        this.finalWalkTime = 0;
        this.totalJourneyTime = totalWalkTime;
        this.hasTransfers = false;
        this.transferDetails = new ArrayList<>();
        this.arrivalTime = LocalTime.now().plusMinutes(totalWalkTime);
        this.busNumbers = new ArrayList<>(busNumbers);
        this.detailMessage = detailMessage;
    }

    // Getters for all fields
    public int getInitialWalkTime() {
        return initialWalkTime;
    }

    public int getTotalWaitTime() {
        return totalWaitTime;
    }

    public LinkedHashMap<String, Integer> getBusRoutes() {
        return busRoutes;
    }

    public int getFinalWalkTime() {
        return finalWalkTime;
    }

    public int getTotalJourneyTime() {
        return totalJourneyTime;
    }

    public boolean hasTransfers() {
        return hasTransfers;
    }

    public ArrayList<String> getTransferDetails() {
        return transferDetails;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }
    
    public ArrayList<String> getBusNumbers() {
        return busNumbers;
    }
    
    // Alias for getTotalJourneyTime for compatibility
    public int getTotal() {
        return totalJourneyTime;
    }
    
    // Getter for the detail message
    public String getDetailMessage() {
        return detailMessage;
    }
}
