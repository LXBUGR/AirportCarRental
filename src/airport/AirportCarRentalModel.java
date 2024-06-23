package airport;

import airport.entities.BusEntity;
import airport.events.BusLeaveEvent;
import airport.events.CarRentalArrivalEvent;
import airport.events.FlightArrivalEvent;
import desmoj.core.simulator.*;
import desmoj.core.dist.*;
import airport.entities.CarRentalEntity;
import airport.entities.TerminalEntity;

import java.util.ArrayList;
import java.util.List;

public class AirportCarRentalModel extends Model {

    //Distributions
    private ContDist arrivalRateTerminal;
    private ContDist arrivalRateRental;
    private ContDist travelTime;

    //Stations - each contains its queue in the StationEntity class with the method getNextPassenger
    //There should be no need to access them, as after init() the Bus contains all information regarding its schedule
    //Also, these can be accessed by using the IdManager class with the id of the station youre looking for
    private BusEntity bus;

    public BusLeaveEvent currentBusLeave;

    public AirportCarRentalModel(Model owner, String name, boolean showInReport, boolean showInTrace) {
        super(owner, name, showInReport, showInTrace);
    }

    public String description() {
        return "Simulation einer Autovermietung am Flughafen mit zwei Terminals und einem Shuttle-Bus.";
    }

    public void doInitialSchedules() {
        BusLeaveEvent initialBusLeave = new BusLeaveEvent(this, "Initial Bus Leave", true);
        initialBusLeave.schedule(IdManager.getStation((int) bus.getNextStationId()), new TimeInstant(bus.getNextStationDriveTime())); // Schedule bus leave at time 10

        // Schedule initial flight arrival events
        FlightArrivalEvent flightArrival1 = new FlightArrivalEvent(this, "Flight Arrival Terminal 1", true);
        flightArrival1.schedule((TerminalEntity) IdManager.getStation(1), new TimeInstant(arrivalRateTerminal.sample())); // Schedule flight arrival at terminal 1 at time 15

        FlightArrivalEvent flightArrival2 = new FlightArrivalEvent(this, "Flight Arrival Terminal 2", true);
        flightArrival2.schedule((TerminalEntity) IdManager.getStation(2), new TimeInstant(arrivalRateTerminal.sample())); // Schedule flight arrival at terminal 2 at time 20

        // Schedule initial car rental arrival events
        CarRentalArrivalEvent carRentalArrival = new CarRentalArrivalEvent(this, "Car Rental Arrival", true);
        carRentalArrival.schedule((CarRentalEntity) IdManager.getStation(3), new TimeInstant(arrivalRateRental.sample())); // Schedule car rental arrival at time 25
    }

    public void init() {
        // Initialize arrival rates and travel time
        arrivalRateTerminal = new ContDistNormal(this, "Arrival Rate Terminal 1", 20, 2, true, true);
        arrivalRateRental = new ContDistNormal(this, "Arrival Rate Rental Station", 2, 0.5, true, true);
        travelTime = new ContDistNormal(this, "Travel Time", 5, 0.5, true, true);

        arrivalRateTerminal.setNonNegative(true);
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
        Experiment experiment = new Experiment("Airport Rental Experiment");

        AirportCarRentalModel model = new AirportCarRentalModel(null, "Airport Rental Model", true, true);
        model.connectToExperiment(experiment);

        //trace and debug period: 60
        experiment.tracePeriod(new TimeInstant(0.0), new TimeInstant(60));
        experiment.debugPeriod(new TimeInstant(0.0), new TimeInstant(60));

        experiment.setShowProgressBarAutoclose(true);

        TimeInstant startTime = new TimeInstant(0.0);
        TimeInstant endTime = new TimeInstant(4800.0); // 80 Stunden

        experiment.tracePeriod(startTime, new TimeInstant(4800.0)); //TODO: check if those numbers are correct for the interval -
        experiment.debugPeriod(startTime, new TimeInstant(4800.0)); //TODO: (lecture demo uses 0.0 and 60 for experiment time of 240)

        experiment.stop(endTime);

        experiment.start();
        experiment.report();
        experiment.finish();
    }

    public ContDist getArrivalRateTerminal() {
        return arrivalRateTerminal;
    }
    public ContDist getArrivalRateRental() {
        return arrivalRateRental;
    }
    public ContDist getTravelTime() { return travelTime; }
    public BusEntity getBus() {
        return bus;
    }
}
