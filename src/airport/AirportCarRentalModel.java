package airport;

import airport.entities.BusEntity;
import desmoj.core.simulator.*;
import desmoj.core.dist.*;
import airport.entities.CarRentalEntity;
import airport.entities.TerminalEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AirportCarRentalModel extends Model {

    //Distributions
    private ContDist arrivalRateTerminal1;
    private ContDist arrivalRateTerminal2;
    private ContDist arrivalRateRental;

    //Stations - each contains its queue in the StationEntity class with the method getNextPassenger
    //There should be no need to access them, as after init() the Bus contains all information regarding its schedule
    //Also, these can be accessed by using the IdManager class with the id of the station youre looking for
    private BusEntity bus;

    public AirportCarRentalModel(Model owner, String name, boolean showInReport, boolean showInTrace) {
        super(owner, name, showInReport, showInTrace);
    }

    public String description() {
        return "Simulation einer Autovermietung am Flughafen mit zwei Terminals und einem Shuttle-Bus.";
    }

    public void doInitialSchedules() {
        //TODO: schedule initial java.events

        //new PassengerGenerator(this, "Terminal1 Passenger Generator", true, arrivalRateTerminal1, terminal1Queue).activate();
        //new PassengerGenerator(this, "Terminal2 Passenger Generator", true, arrivalRateTerminal2, terminal2Queue).activate();
        //new PassengerGenerator(this, "Rental Station Passenger Generator", true, arrivalRateRental, rentalQueue).activate();
    }

    public void init() {
        // Initialize arrival rates and travel time
        arrivalRateTerminal1 = new ContDistExponential(this, "Arrival Rate Terminal 1", 10, true, true);
        arrivalRateTerminal2 = new ContDistExponential(this, "Arrival Rate Terminal 2", 15, true, true);
        arrivalRateRental = new ContDistExponential(this, "Arrival Rate Rental Station", 20, true, true);
        ContDist travelTime = new ContDistNormal(this, "Travel Time", 5, 10, true, true);

        arrivalRateTerminal1.setNonNegative(true);
        arrivalRateTerminal2.setNonNegative(true);
        arrivalRateRental.setNonNegative(true);
        travelTime.setNonNegative(true);

        //Initialize Terminals and Carrentals
        List<CarRentalEntity> carRentals = new ArrayList<>();
        List<TerminalEntity> terminals = new ArrayList<>();

        //Init terminals and carRentals
        TerminalEntity terminal1 = new TerminalEntity(this, "Terminal 1", true);
        TerminalEntity terminal2 = new TerminalEntity(this, "Terminal 2", true);
        CarRentalEntity carRentalEntity1 = new CarRentalEntity(this, "CarRentalStation",true);

        //populate lists
        carRentals.add(carRentalEntity1);
        terminals.add(terminal1);
        terminals.add(terminal2);

        //Initialize ids of terminals and carRentals, add them to idToStationMap in IdManager,
        // so that they can be accessed there by their id
        IdManager.initializeIds(terminals, carRentals);

        bus = new BusEntity(this, "Bus", true, 20);
        bus.setCurrentStationId(terminal1.getId());

        //Create Schedule of Bus
        bus.addSchedule(1, 2, 5);   //Terminal 1 -> Terminal 2 (5 mins)
        bus.addSchedule(2, 3, 5);   //Terminal 2 -> CarRental 1 (5 mins)
        bus.addSchedule(3, 1, 5);   //CarRental 1 -> Terminal 1 (5 mins)
    }

    public static void main(String[] args) {
        //Time Unit used in simulation and scheduling: Minutes
        Experiment.setEpsilon(TimeUnit.MINUTES);

        Experiment experiment = new Experiment("Airport Rental Experiment");

        AirportCarRentalModel model = new AirportCarRentalModel(null, "Airport Rental Model", true, true);
        model.connectToExperiment(experiment);

        //trace and debug period: 60
        experiment.tracePeriod(new TimeInstant(0.0), new TimeInstant(60));
        experiment.debugPeriod(new TimeInstant(0.0), new TimeInstant(60));

        experiment.setShowProgressBarAutoclose(true);

        TimeInstant startTime = new TimeInstant(0.0);
        TimeInstant endTime = new TimeInstant(4800.0); // 80 Stunden

        experiment.stop(endTime);
        experiment.tracePeriod(startTime, endTime); //TODO: check if those numbers are correct for the interval -
        experiment.debugPeriod(startTime, endTime); //TODO: (lecture demo uses 0.0 and 60 for experiment time of 240)

        experiment.start();
        experiment.report();
        experiment.finish();
    }

    public ContDist getArrivalRateTerminal1() {
        return arrivalRateTerminal1;
    }
    public ContDist getArrivalRateTerminal2() {
        return arrivalRateTerminal2;
    }
    public ContDist getArrivalRateRental() {
        return arrivalRateRental;
    }
    public BusEntity getBus() {
        return bus;
    }
}
