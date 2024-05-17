import desmoj.core.simulator.*;
import desmoj.core.dist.*;

public class AirportCarRentalModel extends Model {
    private ContDistExponential arrivalRateTerminal1;
    private ContDistExponential arrivalRateTerminal2;
    private ContDistExponential arrivalRateRental;
    private ContDistUniform travelTime;

    private ProcessQueue<PassengerProcess> queueTerminal1;
    private ProcessQueue<PassengerProcess> queueTerminal2;
    private ProcessQueue<PassengerProcess> queueRental;

    private StationQueue terminal1Queue;
    private StationQueue terminal2Queue;
    private StationQueue rentalQueue;

    private int busCapacity = 20;

    public AirportRentalModel(Model owner, String name, boolean showInReport, boolean showInTrace) {
        super(owner, name, showInReport, showInTrace);
    }

    public String description() {
        return "Simulation einer Autovermietung am Flughafen mit zwei Terminals und einem Shuttle-Bus.";
    }

    public void doInitialSchedules() {
        // Initialize passenger generators
        new PassengerGenerator(this, "Terminal1 Passenger Generator", true, arrivalRateTerminal1, terminal1Queue).activate();
        new PassengerGenerator(this, "Terminal2 Passenger Generator", true, arrivalRateTerminal2, terminal2Queue).activate();
        new PassengerGenerator(this, "Rental Station Passenger Generator", true, arrivalRateRental, rentalQueue).activate();

        // Initialize bus process
        BusProcess bus = new BusProcess(this, "Shuttle Bus", true, terminal1Queue, terminal2Queue, rentalQueue, busCapacity);
        bus.activate();
    }

    public void init() {
        // Initialize arrival rates and travel time
        arrivalRateTerminal1 = new ContDistExponential(this, "Arrival Rate Terminal 1", 10, true, true);
        arrivalRateTerminal2 = new ContDistExponential(this, "Arrival Rate Terminal 2", 15, true, true);
        arrivalRateRental = new ContDistExponential(this, "Arrival Rate Rental Station", 20, true, true);
        travelTime = new ContDistUniform(this, "Travel Time", 5, 10, true, true);

        // Initialize queues
        queueTerminal1 = new ProcessQueue<PassengerProcess>(this, "Terminal 1 Queue", true, true);
        queueTerminal2 = new ProcessQueue<PassengerProcess>(this, "Terminal 2 Queue", true, true);
        queueRental = new ProcessQueue<PassengerProcess>(this, "Rental Station Queue", true, true);

        // Initialize station queues
        terminal1Queue = new StationQueue(this, "Terminal 1", true, queueTerminal1);
        terminal2Queue = new StationQueue(this, "Terminal 2", true, queueTerminal2);
        rentalQueue = new StationQueue(this, "Rental Station", true, queueRental);
    }

    public static void main(String[] args) {
        Experiment.setEpsilon(0.001);
        Experiment.setAccuracy(0.001);

        AirportRentalModel model = new AirportRentalModel(null, "Airport Rental Model", true, true);
        Experiment experiment = new Experiment("Airport Rental Experiment");

        model.connectToExperiment(experiment);

        experiment.setShowProgressBarAutoclose(true);

        TimeInstant startTime = new TimeInstant(0.0);
        TimeInstant endTime = new TimeInstant(4800.0); // 80 Stunden

        experiment.stop(endTime);
        experiment.tracePeriod(startTime, endTime);
        experiment.debugPeriod(startTime, endTime);

        experiment.start();
        experiment.report();
        experiment.finish();
    }
}
