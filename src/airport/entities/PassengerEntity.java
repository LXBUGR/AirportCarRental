package airport.entities;

import desmoj.core.simulator.*;

public class PassengerEntity extends Entity {
    private final int destinationId;
    public PassengerEntity (Model owner, String name, boolean showInTrace, int destinationId) {
        super(owner, name, showInTrace);
        this.destinationId = destinationId;
    }

    public int getDestinationId() {
        return destinationId;
    }
}
