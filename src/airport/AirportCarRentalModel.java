package airport;

import airport.entities.BusEntity;
import airport.entities.BusStationEntity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AirportCarRentalModel extends Model {

    // Distributions
    private ContDist arrivalRateTerminal;
    private ContDist arrivalRateRental;
    private ContDist travelTime;
    private ContDist flightPassengers;

    // Bus and station statistics
    private final BusEntity[] busArray;
    public Map<Integer, BusLeaveEvent> currentBusLeaveEvents;
    public int busWaitingTime;

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

    public AirportCarRentalModel(Model owner, String name, boolean showInReport, boolean showInTrace, int busAmount, int busWaitingTime) {
        super(owner, name, showInReport, showInTrace);
        busArray = new BusEntity[busAmount];
        currentBusLeaveEvents = new HashMap<>();
        this.busWaitingTime = busWaitingTime;
    }

    public String description() {
        return "Simulation einer Autovermietung am Flughafen mit zwei Terminals und einem Shuttle-Bus.";
    }

    public void doInitialSchedules() {
        for(BusEntity bus : busArray) {
            BusLeaveEvent initialBusLeave = new BusLeaveEvent(this, "Initial Bus Leave", true);
            initialBusLeave.schedule(new BusStationEntity(this, "BusStationEntity", false ,bus, IdManager.getStation(bus.getCurrentStationId())), new TimeSpan(busWaitingTime));
        }

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

        // Initialize stations
        List<CarRentalEntity> carRentals = new ArrayList<>();
        List<TerminalEntity> terminals = new ArrayList<>();

        TerminalEntity terminal1 = new TerminalEntity(this, "Terminal 1", true);
        TerminalEntity terminal2 = new TerminalEntity(this, "Terminal 2", true);
        CarRentalEntity carRentalEntity1 = new CarRentalEntity(this, "CarRentalStation", true);

        carRentals.add(carRentalEntity1);
        terminals.add(terminal1);
        terminals.add(terminal2);

        IdManager.initializeStationIds(terminals, carRentals);

        //Initialize buses
        for(int i = 0; i < busArray.length; i++) {
            int startStation = (i % 3) + 1;
            BusEntity bus = new BusEntity(this, "Bus " + (i+1), true, i + 1, 20, startStation);
            busArray[i] = bus;
            bus.setCurrentStationId(startStation);
            bus.addSchedule(1, 2, 5);
            bus.addSchedule(2, 3, 5);
            bus.addSchedule(3, 1, 5);
            IdManager.addBus(bus);
        }

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
    public BusEntity[] getBusses() {
        return busArray;
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

    public void sendTraceNoteWithPassengers(String note, int passengerCount) {
        sendTraceNote(note + " | Passengers in Bus: " + passengerCount);
    }

    public static void main(String[] args) {
        int[] busAmounts = {1, 2};
        int[] busWaitingTimes = {5, 4, 3};
        int repetitions = 10;

        for (int busAmount : busAmounts) {
            for (int busWaitingTime : busWaitingTimes) {
                // Accumulators for average calculations
                double totalBusRoundTimes = 0;
                double totalBusWaitTimeT1 = 0;
                double totalBusWaitTimeT2 = 0;
                double totalBusWaitTimeC1 = 0;
                double totalStationWaitTimeT1 = 0;
                double totalStationWaitTimeT2 = 0;
                double totalStationWaitTimeC1 = 0;
                double totalPassengerSystemTimeT1 = 0;
                double totalPassengerSystemTimeT2 = 0;
                double totalPassengerSystemTimeC1 = 0;
                double totalBusPassengerCount = 0;

                double totalPassengerStayTimes = 0;
                double totalPassengerCountRides = 0;

                for (int i = 0; i < repetitions; i++) {
                    Experiment experiment = new Experiment("Airport Rental Experiment");
                    AirportCarRentalModel model = new AirportCarRentalModel(null, "Airport Rental Model", true, true, busAmount, busWaitingTime);
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

                    totalBusRoundTimes += model.getBusRoundTimes().getMean();
                    totalBusWaitTimeT1 += model.getBusWaitTimes(1).getMean();
                    totalBusWaitTimeT2 += model.getBusWaitTimes(2).getMean();
                    totalBusWaitTimeC1 += model.getBusWaitTimes(3).getMean();
                    totalStationWaitTimeT1 += model.getStationWaitTimes(1).getMean();
                    totalStationWaitTimeT2 += model.getStationWaitTimes(2).getMean();
                    totalStationWaitTimeC1 += model.getStationWaitTimes(3).getMean();
                    totalPassengerSystemTimeT1 += model.getPassengerSystemTimes(1).getMean();
                    totalPassengerSystemTimeT2 += model.getPassengerSystemTimes(2).getMean();
                    totalPassengerSystemTimeC1 += model.getPassengerSystemTimes(3).getMean();
                    totalBusPassengerCount += model.getBusPassengerCount().getMean();

                    // Manually track histogram data for averaging
                    totalPassengerStayTimes += model.getPassengerSystemTimeSeries().getMean();
                    totalPassengerCountRides += model.getPassengerCountPerRide().getMean();
                }

                // Calculate averages
                System.out.println("Average results for " + busAmount + " Bus(es) and " + busWaitingTime + " mins waiting time:");
                System.out.println("Average Bus Round Times: " + totalBusRoundTimes / repetitions);
                System.out.println("Average Bus Wait Time Terminal 1: " + totalBusWaitTimeT1 / repetitions);
                System.out.println("Average Bus Wait Time Terminal 2: " + totalBusWaitTimeT2 / repetitions);
                System.out.println("Average Bus Wait Time CarRental 1: " + totalBusWaitTimeC1 / repetitions);
                System.out.println("Average Station Wait Time Terminal 1: " + totalStationWaitTimeT1 / repetitions);
                System.out.println("Average Station Wait Time Terminal 2: " + totalStationWaitTimeT2 / repetitions);
                System.out.println("Average Station Wait Time CarRental 1: " + totalStationWaitTimeC1 / repetitions);
                System.out.println("Average Passenger System Time Terminal 1: " + totalPassengerSystemTimeT1 / repetitions);
                System.out.println("Average Passenger System Time Terminal 2: " + totalPassengerSystemTimeT2 / repetitions);
                System.out.println("Average Passenger System Time CarRental 1: " + totalPassengerSystemTimeC1 / repetitions);
                System.out.println("Average Bus Passenger Count: " + totalBusPassengerCount / repetitions);
                System.out.println("Average Passenger Stay Times: " + totalPassengerStayTimes / repetitions);
                System.out.println("Average Passenger Count Per Ride: " + totalPassengerCountRides / repetitions);
            }
        }
    }
}
