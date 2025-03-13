package datatypes;

public class RouteSegment {
    private String mode; // e.g., "Walk", "Bus", "Transfer"
    private String details; // e.g., "Bus 21 via Downtown" or "Transfer to Line 5"
    private int duration; // Duration of the segment in minutes

    // Constructor
    public RouteSegment(String mode, String details, int duration) {
        this.mode = mode;
        this.details = details;
        this.duration = duration;
    }

    // Getters and Setters
    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        // This can be customized as needed for display purposes
        return mode + " to " + details + " for " + duration + " minutes";
    }
}

