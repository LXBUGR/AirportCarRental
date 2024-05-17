import desmoj.core.simulator.*;
import desmoj.core.dist.*;

public class PassengerGenerator extends SimProcess {

    private ContDistExponential arrivalRate;
    private StationQueue stationQueue;

    public PassengerGenerator(Model owner, String name, boolean showInTrace, ContDistExponential arrivalRate, StationQueue stationQueue) {
        super(owner, name, showInTrace);
        this.arrivalRate = arrivalRate;
        this.stationQueue = stationQueue;
    }

    @Override
    public void lifeCycle() throws SuspendExecution {
        while (true) {
            // Wait for the next passenger arrival
            hold(new TimeSpan(arrivalRate.sample()));

            // Create a new passenger
            PassengerProcess passenger = new PassengerProcess(getModel(), "Passenger", true, stationQueue.getName());
            stationQueue.passengerArrives(passenger);
        }
    }
}
