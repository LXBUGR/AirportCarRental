package airport.entities;

import desmoj.core.simulator.*;

public class PassengerEntity extends Entity {

    private final int arrivalId;
    private final int destinationId;
    public PassengerEntity (Model owner, String name, boolean showInTrace, int arrivalId, int destinationId) {
        super(owner, name, showInTrace);
        this.arrivalId = arrivalId;
        this.destinationId = destinationId;
    }

    public int getArrivalId() { return arrivalId; }
    public int getDestinationId() {
        return destinationId;
    }
}
