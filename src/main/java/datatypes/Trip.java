package datatypes;

public class Trip {
    private final int trip_id;
    private final int start_id;
    private final int end_id;
    private final String arrival_time;
    private final String departure_time;
    private Trip next;


    public Trip(int trip_id, int start_id, int end_id, String arrival_time, String departure_time) {
        this.trip_id = trip_id;
        this.start_id = start_id;
        this.end_id = end_id;
        this.arrival_time = arrival_time;
        this.departure_time = departure_time;

    }
    public int getTrip_id() {
        return trip_id;
    }

    public int getStart_id() {
        return start_id;
    }

    public int getEnd_id() {
        return end_id;

    }

    public String getArrival_time() {
        return arrival_time;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public void setNext(Trip next){
        this.next = next;
    }

    public Trip getNext(){
        return this.next;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return Integer.toString(getTrip_id());
    }


}
