package airport;

import airport.entities.BusEntity;
import airport.entities.CarRentalEntity;
import airport.entities.TerminalEntity;
import airport.events.BusLeaveEvent;
import airport.events.CarRentalArrivalEvent;
import airport.events.FlightArrivalEvent;
import desmoj.core.simulator.*;
import desmoj.core.dist.*;
import desmoj.core.statistic.Tally;
import desmoj.core.statistic.Histogram;

import java.util.ArrayList;
import java.util.List;

public class AirportCarRentalModel extends Model {

    // Distributions
    private ContDist arrivalRateTerminal;
    private ContDist arrivalRateRental;
    private ContDist travelTime;
    private ContDist flightPassengers;

    // Bus and station statistics
    private BusEntity bus;

    //Tallies
    private Tally busRoundTimes; //Maximale, Mittlere & Minimale Dauer einer Runde des Busses
    private Tally busWaitTimeT1; //Aufenthaltsdauer des Busses an Terminal 1
    private Tally busWaitTimeT2; //Aufenthaltsdauer des Busses an Terminal 2
    private Tally busWaitTimeC1; //Aufenthaltsdauer des Busses an CarRental 1
    private Tally stationWaitTimeT1; //Wartezeit der Passagiere in jeder Warteschlange Terminal 1
    private Tally stationWaitTimeT2; //Wartezeit der Passagiere in jeder Warteschlange Terminal 2
    private Tally stationWaitTimeC1; //Wartezeit der Passagiere in jeder Warteschlange CarRental 1
    private Tally passengerSystemTimeT1; //Dauer einer Person im System (Angekommen Terminal 1)
    private Tally passengerSystemTimeT2; //Dauer einer Person im System (Angekommen Terminal 1)
    private Tally passengerSystemTimeC1; //Dauer einer Person im System (Angekommen Terminal 1)
    private Tally busPassengerCount;  //Anzahl der Passagiere im Bus

    // Histograms
    private Histogram passengerSystemTimeSeries;  // Balkendiagramm der Verweildauer der Passagiere im System
    private Histogram passengerCountPerRide;  // Histogramm der Passagieranzahl pro Busfahrt

    public BusLeaveEvent currentBusLeave;

    public AirportCarRentalModel(Model owner, String name, boolean showInReport, boolean showInTrace) {
        super(owner, name, showInReport, showInTrace);
    }

    public String description() {
        return "Simulation einer Autovermietung am Flughafen mit zwei Terminals und einem Shuttle-Bus.";
    }

    public void doInitialSchedules() {
        BusLeaveEvent initialBusLeave = new BusLeaveEvent(this, "Initial Bus Leave", true);
        initialBusLeave.schedule(IdManager.getStation(bus.getCurrentStationId()), new TimeSpan(bus.getNextStationDriveTime()));

        FlightArrivalEvent flightArrival1 = new FlightArrivalEvent(this, "Flight Arrival Terminal 1", true);
        flightArrival1.schedule((TerminalEntity) IdManager.getStation(1), new TimeSpan(arrivalRateTerminal.sample()));

        FlightArrivalEvent flightArrival2 = new FlightArrivalEvent(this, "Flight Arrival Terminal 2", true);
        flightArrival2.schedule((TerminalEntity) IdManager.getStation(2), new TimeSpan(arrivalRateTerminal.sample()));

        CarRentalArrivalEvent carRentalArrival = new CarRentalArrivalEvent(this, "Car Rental Arrival", true);
        carRentalArrival.schedule((CarRentalEntity) IdManager.getStation(3), new TimeSpan(arrivalRateRental.sample()));
    }


    public void init() {
        // Initialize distributions
        arrivalRateTerminal = new ContDistNormal(this, "Arrival Rate Terminal", 60, 2, true, true);
        arrivalRateRental = new ContDistNormal(this, "Arrival Rate Rental Station", 2, 0.5, true, true);
        travelTime = new ContDistNormal(this, "Travel Time", 5, 0.3, true, true);
        flightPassengers = new ContDistNormal(this, "Amount Passengers on flight", 20, 5, true, true);

        arrivalRateTerminal.setNonNegative(true);
        arrivalRateRental.setNonNegative(true);
        travelTime.setNonNegative(true);

        // Initialize entities
        List<CarRentalEntity> carRentals = new ArrayList<>();
        List<TerminalEntity> terminals = new ArrayList<>();

        TerminalEntity terminal1 = new TerminalEntity(this, "Terminal 1", true);
        TerminalEntity terminal2 = new TerminalEntity(this, "Terminal 2", true);
        CarRentalEntity carRentalEntity1 = new CarRentalEntity(this, "CarRentalStation", true);

        carRentals.add(carRentalEntity1);
        terminals.add(terminal1);
        terminals.add(terminal2);

        IdManager.initializeIds(terminals, carRentals);

        bus = new BusEntity(this, "Bus", true, 20);
        bus.setCurrentStationId(terminal1.getId());

        bus.addSchedule(1, 2, 5);
        bus.addSchedule(2, 3, 5);
        bus.addSchedule(3, 1, 5);

        // Initialize tallies and histograms for reporting
        busRoundTimes = new Tally(this, "Bus Round Times", true, true);
        busWaitTimeT1 = new Tally(this, "Bus Wait Time Terminal 1", true, true);
        busWaitTimeT2 = new Tally(this, "Bus Wait Time Terminal 2", true, true);
        busWaitTimeC1 = new Tally(this, "Bus Wait Time CarRental 1", true, true);
        stationWaitTimeT1 = new Tally(this, "Station Wait Time Terminal 1",true, true);
        stationWaitTimeT2 = new Tally(this, "Station Wait Time Terminal 2", true, true);
        stationWaitTimeC1 = new Tally(this, "Station Wait Time CarRental 1", true, true);
        passengerSystemTimeT1 = new Tally(this, "Passenger System Times Terminal 1", true, true);
        passengerSystemTimeT2 = new Tally(this, "Passenger System Times Terminal 2", true, true);
        passengerSystemTimeC1 = new Tally(this, "Passenger System Times CarRental 1", true, true);
        busPassengerCount = new Tally(this, "Bus Passenger Count", true, true);  // Histogram für Anzahl der Passagiere im Bus

        // Initialize new histograms
        passengerSystemTimeSeries = new Histogram(this, "Passenger Stay Times", 0, 100, 10, true, true);
        passengerCountPerRide = new Histogram(this, "Passenger Count Per Ride", 20, 0, 10, true, true);
    }

    public ContDist getArrivalRateTerminal() {
        return arrivalRateTerminal;
    }
    public ContDist getArrivalRateRental() {
        return arrivalRateRental;
    }
    public ContDist getTravelTime() {
        return travelTime;
    }
    public ContDist getFlightPassengers() {
        return flightPassengers;
    }
    public BusEntity getBus() {
        return bus;
    }

    public Tally getBusRoundTimes() {
        return busRoundTimes;
    }
    public Tally getBusWaitTimes(int id) {
        return switch (id) {
            case 1 -> busWaitTimeT1;
            case 2 -> busWaitTimeT2;
            case 3 -> busWaitTimeC1;
            default -> throw new RuntimeException("cannot get bus wait time Tally for station id " + id + ", station does not exist");
        };
    }
    public Tally getStationWaitTimes(int id) {
        return switch (id) {
            case 1 -> stationWaitTimeT1;
            case 2 -> stationWaitTimeT2;
            case 3 -> stationWaitTimeC1;
            default -> throw new RuntimeException("cannot get station wait time Tally for station id " + id + ", station does not exist");
        };
    }
    public Tally getPassengerSystemTimes(int id) {
        return switch (id) {
            case 1 -> passengerSystemTimeT1;
            case 2 -> passengerSystemTimeT2;
            case 3 -> passengerSystemTimeC1;
            default -> throw new RuntimeException("cannot get system time Tally for station id " + id + ", station does not exist");
        };
    }
    public Tally getBusPassengerCount() {
        return busPassengerCount;
    }

    public Histogram getPassengerSystemTimeSeries() {
        return passengerSystemTimeSeries;
    }

    public Histogram getPassengerCountPerRide() {
        return passengerCountPerRide;
    }

    public void sendTraceNoteWithPassengers(String note) {
        sendTraceNote(note + " | Passengers in Bus: " + bus.getPassengerCount());
    }

    public static void main(String[] args) {
        Experiment experiment = new Experiment("Airport Rental Experiment");
        AirportCarRentalModel model = new AirportCarRentalModel(null, "Airport Rental Model", true, true);
        model.connectToExperiment(experiment);

        experiment.setShowProgressBar(false);
        experiment.setShowProgressBarAutoclose(true);

        TimeInstant startTime = new TimeInstant(0.0);
        TimeInstant endTime = new TimeInstant(4800.0); // 80 Stunden

        experiment.tracePeriod(startTime, endTime);
        experiment.debugPeriod(startTime, endTime);

        experiment.stop(endTime);

        experiment.start();
        experiment.report();
        experiment.finish();
    }
}
