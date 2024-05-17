import desmoj.core.simulator.*;

public class StationQueue extends SimProcess {

    private ProcessQueue<PassengerProcess> queue;
    private String stationName;

    public StationQueue(Model owner, String name, boolean showInTrace, ProcessQueue<PassengerProcess> queue) {
        super(owner, name, showInTrace);
        this.queue = queue;
        this.stationName = name;
    }

    @Override
    public void lifeCycle() throws SuspendExecution {
        while (true) {
            // Wait for a passenger to arrive at the station
            if (queue.isEmpty()) {
                passivate();
            }

            // When a passenger arrives, they will be handled by the bus
            hold(new TimeSpan(0.0));
        }
    }

    public void passengerArrives(PassengerProcess passenger) {
        sendTraceNote("Passenger " + passenger.getName() + " arrives at " + stationName);
        queue.insert(passenger);

        // Activate the StationQueue to handle the arriving passenger
        if (!isScheduled()) {
            activate();
        }
    }
}
