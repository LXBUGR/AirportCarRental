package airport.events;

import airport.AirportCarRentalModel;
import airport.IdManager;
import airport.entities.BusStationEntity;
import airport.entities.StationEntity;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import airport.entities.BusEntity;
import desmoj.core.simulator.TimeSpan;

public class BusLeaveEvent extends Event<BusStationEntity> {
    private final AirportCarRentalModel meinModel;

    public BusLeaveEvent(Model model, String name, boolean showInTrace) {
        super(model, name, showInTrace);
        meinModel = (AirportCarRentalModel) model;
    }

    @Override
    public void eventRoutine(BusStationEntity busStation) {
        BusEntity bus = busStation.getBus();
        StationEntity nextStation = IdManager.getStation(bus.getNextStationId());

        // Schedule the bus arrival at the next station
        double travelTime = bus.getNextStationDriveTime();

        // Log bus departure
        meinModel.sendTraceNote(bus.getName() +  " departs from " + busStation.getStation().getName() + " to " + nextStation.getName());

        // Schedule a new BusArrivalEvent for the next station
        BusArrivalEvent arrivalEvent = new BusArrivalEvent(meinModel, "Bus Arrival Event", true);
        arrivalEvent.schedule(busStation.setStation(nextStation), new TimeSpan(travelTime));
        bus.setDriving(true);
        meinModel.currentBusLeaveEvents.put(bus.getId(), null);

        //update Tallies und Histogramms
        meinModel.getBusPassengerCount().update(bus.getPassengerCount());
        meinModel.getPassengerCountPerRide().update(bus.getPassengerCount());
        double waitTime = meinModel.presentTime().getTimeAsDouble() - bus.getLastArrivalTime();
        meinModel.getBusWaitTimes(bus.getCurrentStationId()).update(waitTime);

        if(bus.getCurrentStationId() == bus.getStartStationId()) {
            bus.startRound();
        }
    }
}
