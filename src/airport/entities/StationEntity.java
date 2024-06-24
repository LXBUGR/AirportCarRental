package airport.entities;

import desmoj.core.simulator.*;
import airport.AirportCarRentalModel;

public class StationEntity extends Entity {
    private final Queue<PassengerEntity> queue;
    private int id;

    public StationEntity(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);
        queue = new Queue<>(owner, "Queue for " + name, true, true);
    }

    public PassengerEntity dequeuePassenger() {
        PassengerEntity nextPassenger = queue.first();
        queue.remove(nextPassenger);
        sendTraceNote("Passenger dequeued: " + nextPassenger.getName() + " from " + getName());
        return nextPassenger;
    }

    public void enqueuePassenger(PassengerEntity passenger) {
        queue.insert(passenger);
        sendTraceNote("Passenger enqueued: " + passenger.getName() + " at " + getName());
    }

    public boolean queueEmpty() {
        return queue.isEmpty();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void recordPassengerWaitTime(double waitTime) {
        ((AirportCarRentalModel) getModel()).getStationWaitTimes().update(waitTime);
        sendTraceNote("Passenger wait time recorded: " + waitTime + " at " + getName());
    }
}
