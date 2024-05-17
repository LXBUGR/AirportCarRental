import desmoj.core.simulator.*;
import java.util.ArrayList;

public class BusProcess extends SimProcess {

    private ProcessQueue<PassengerProcess> queueTerminal1;
    private ProcessQueue<PassengerProcess> queueTerminal2;
    private ProcessQueue<PassengerProcess> queueRental;
    private int busCapacity;
    private ArrayList<PassengerProcess> passengers;

    public BusProcess(Model owner, String name, boolean showInTrace,
                      ProcessQueue<PassengerProcess> queueTerminal1,
                      ProcessQueue<PassengerProcess> queueTerminal2,
                      ProcessQueue<PassengerProcess> queueRental,
                      int busCapacity) {
        super(owner, name, showInTrace);
        this.queueTerminal1 = queueTerminal1;
        this.queueTerminal2 = queueTerminal2;
        this.queueRental = queueRental;
        this.busCapacity = busCapacity;
        this.passengers = new ArrayList<>();
    }

    @Override
    public void lifeCycle() throws SuspendExecution {
        while (true) {
            // Drive to Rental Station
            driveToStation("Rental Station", queueRental);
            hold(new TimeSpan(5.0)); // Stay at Rental Station for at least 5 minutes

            // Drive to Terminal 1
            driveToStation("Terminal 1", queueTerminal1);
            hold(new TimeSpan(5.0)); // Stay at Terminal 1 for at least 5 minutes

            // Drive to Terminal 2
            driveToStation("Terminal 2", queueTerminal2);
            hold(new TimeSpan(5.0)); // Stay at Terminal 2 for at least 5 minutes
        }
    }

    private void driveToStation(String stationName, ProcessQueue<PassengerProcess> queue) throws SuspendExecution {
        sendTraceNote("Bus is driving to " + stationName);
        double travelTime = ((AirportRentalModel) getModel()).travelTime.sample();
        hold(new TimeSpan(travelTime)); // Simulate travel time

        sendTraceNote("Bus arrived at " + stationName);

        // Unload passengers for this station
        unloadPassengers(stationName);

        // Load new passengers if there is space
        loadPassengers(queue);
    }

    private void unloadPassengers(String stationName) {
        ArrayList<PassengerProcess> toUnload = new ArrayList<>();

        for (PassengerProcess passenger : passengers) {
            if (passenger.getDestination().equals(stationName)) {
                toUnload.add(passenger);
            }
        }

        passengers.removeAll(toUnload);

        for (PassengerProcess passenger : toUnload) {
            sendTraceNote("Passenger " + passenger.getName() + " gets off at " + stationName);
            passenger.activate(new TimeSpan(0.0)); // Activate passenger to continue their process
        }
    }

    private void loadPassengers(ProcessQueue<PassengerProcess> queue) throws SuspendExecution {
        while (passengers.size() < busCapacity && !queue.isEmpty()) {
            PassengerProcess passenger = queue.first();
            queue.remove(passenger);
            passengers.add(passenger);

            sendTraceNote("Passenger " + passenger.getName() + " boards the bus");
        }
    }
}
