package airport.entities;

import desmoj.core.simulator.*;

public class PassengerEntity extends Entity {

    private final int arrivalId;
    private final int destinationId;
    private TimeInstant arrivalTime;

    public PassengerEntity(Model owner, String name, boolean showInTrace, int arrivalId, int destinationId) {
        super(owner, name, showInTrace);
        this.arrivalId = arrivalId;
        this.destinationId = destinationId;
        //this.arrivalTime = presentTime(); // Save the time when the passenger is created
    }

    public int getArrivalId() {
        return arrivalId;
    }

    public int getDestinationId() {
        return destinationId;
    }

    public TimeInstant getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(TimeInstant arrivalTime) { this.arrivalTime = arrivalTime; }
}
