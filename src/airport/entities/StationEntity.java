package airport.entities;

import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.Queue;

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
        return nextPassenger;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Queue<PassengerEntity> getQueue() {
        return queue;
    }
}
