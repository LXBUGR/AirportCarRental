package airport;

import airport.entities.BusEntity;
import airport.entities.CarRentalEntity;
import airport.entities.TerminalEntity;
import airport.events.BusLeaveEvent;
import airport.events.CarRentalArrivalEvent;
import airport.events.FlightArrivalEvent;
import desmoj.core.simulator.*;
import desmoj.core.dist.*;
import desmoj.core.report.*;
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
    private Tally busRoundTimes;
    private Tally busWaitTimes;
    private Histogram stationWaitTimes;
    private Tally passengerSystemTimes;
    private Histogram busPassengerCount;  // Histogram für Anzahl der Passagiere im Bus
    private Tally station1StayTimes;      // Tally für Aufenthaltsdauer an Station 1
    private Tally station2StayTimes;      // Tally für Aufenthaltsdauer an Station 2
    private Tally carRentalStayTimes;     // Tally für Aufenthaltsdauer an der Autovermietungsstation
    private Tally terminal1PassengerTimes;  // Tally für Verweildauer der Passagiere im System (Terminal 1)
    private Tally terminal2PassengerTimes;  // Tally für Verweildauer der Passagiere im System (Terminal 2)
    private Tally carRentalPassengerTimes;  // Tally für Verweildauer der Passagiere im System (Autovermietung)

    // New Histograms
    private Histogram avgPassengerStayTimes;  // Balkendiagramm der durchschnittlichen Verweildauer der Passagiere
    private Histogram busPassengerTimeSeries; // Liniendiagramm der Anzahl der Passagiere im Bus über die Zeit
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
        flightArrival1.schedule(IdManager.getStation(1), new TimeSpan(arrivalRateTerminal.sample()));

        FlightArrivalEvent flightArrival2 = new FlightArrivalEvent(this, "Flight Arrival Terminal 2", true);
        flightArrival2.schedule(IdManager.getStation(2), new TimeSpan(arrivalRateTerminal.sample()));

        CarRentalArrivalEvent carRentalArrival = new CarRentalArrivalEvent(this, "Car Rental Arrival", true);
        carRentalArrival.schedule(IdManager.getStation(3), new TimeSpan(arrivalRateRental.sample()));
    }


    public void init() {
        // Initialize distributions
        arrivalRateTerminal = new ContDistNormal(this, "Arrival Rate Terminal", 45, 2, true, true);
        arrivalRateRental = new ContDistNormal(this, "Arrival Rate Rental Station", 2, 0.5, true, true);
        travelTime = new ContDistNormal(this, "Travel Time", 5, 0.5, true, true);
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
        busWaitTimes = new Tally(this, "Bus Wait Times", true, true);
        stationWaitTimes = new Histogram(this, "Station Wait Times", 50, 0, 10, true, true);
        passengerSystemTimes = new Tally(this, "Passenger System Times", true, true);
        busPassengerCount = new Histogram(this, "Bus Passenger Count", 20, 0, 20, true, true);  // Histogram für Anzahl der Passagiere im Bus
        station1StayTimes = new Tally(this, "Station 1 Stay Times", true, true);  // Tally für Aufenthaltsdauer an Station 1
        station2StayTimes = new Tally(this, "Station 2 Stay Times", true, true);  // Tally für Aufenthaltsdauer an Station 2
        carRentalStayTimes = new Tally(this, "Car Rental Stay Times", true, true);  // Tally für Aufenthaltsdauer an der Autovermietungsstation
        terminal1PassengerTimes = new Tally(this, "Terminal 1 Passenger Times", true, true);  // Tally für Verweildauer der Passagiere im System (Terminal 1)
        terminal2PassengerTimes = new Tally(this, "Terminal 2 Passenger Times", true, true);  // Tally für Verweildauer der Passagiere im System (Terminal 2)
        carRentalPassengerTimes = new Tally(this, "Car Rental Passenger Times", true, true);  // Tally für Verweildauer der Passagiere im System (Autovermietung)

        // Initialize new histograms
        avgPassengerStayTimes = new Histogram(this, "Avg Passenger Stay Times", 3, 0, 3, true, true);
        busPassengerTimeSeries = new Histogram(this, "Bus Passenger Time Series", 4800, 0, 20, true, true); // Zeitreihe der Passagierzahlen (minütliche Auflösung)
        passengerCountPerRide = new Histogram(this, "Passenger Count Per Ride", 20, 0, 20, true, true);
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
    public Tally getBusWaitTimes() {
        return busWaitTimes;
    }
    public Histogram getStationWaitTimes() {
        return stationWaitTimes;
    }
    public Tally getPassengerSystemTimes() {
        return passengerSystemTimes;
    }
    public Histogram getBusPassengerCount() {
        return busPassengerCount;
    }
    public Tally getStation1StayTimes() {
        return station1StayTimes;
    }
    public Tally getStation2StayTimes() {
        return station2StayTimes;
    }
    public Tally getCarRentalStayTimes() {
        return carRentalStayTimes;
    }
    public Tally getTerminal1PassengerTimes() {
        return terminal1PassengerTimes;
    }
    public Tally getTerminal2PassengerTimes() {
        return terminal2PassengerTimes;
    }
    public Tally getCarRentalPassengerTimes() {
        return carRentalPassengerTimes;
    }
    public Histogram getAvgPassengerStayTimes() {
        return avgPassengerStayTimes;
    }
    public Histogram getBusPassengerTimeSeries() {
        return busPassengerTimeSeries;
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
