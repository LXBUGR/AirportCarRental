package airport.events;

import airport.AirportCarRentalModel;
import airport.IdManager;
import airport.entities.StationEntity;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import airport.entities.BusEntity;
import desmoj.core.simulator.TimeSpan;

public class BusLeaveEvent extends Event<StationEntity> {
    private final AirportCarRentalModel meinModel;

    public BusLeaveEvent(Model model, String name, boolean showInTrace) {
        super(model, name, showInTrace);
        meinModel = (AirportCarRentalModel) model;
    }

    @Override
    public void eventRoutine(StationEntity stationEntity) {
        BusEntity bus = meinModel.getBus();

        // Calculate and update wait time
        double waitTime = meinModel.presentTime().getTimeAsDouble() - bus.getLastArrivalTime();
        meinModel.getBusWaitTimes(bus.getCurrentStationId()).update(waitTime);

        // Schedule the bus arrival at the next station
        StationEntity nextStation = IdManager.getStation(bus.getNextStationId());
        double travelTime = bus.getNextStationDriveTime();

        // Schedule a new BusArrivalEvent for the next station
        BusArrivalEvent arrivalEvent = new BusArrivalEvent(meinModel, "Bus Arrival Event", true);
        arrivalEvent.schedule(nextStation, new TimeSpan(travelTime));
        bus.setDriving(true);
        meinModel.currentBusLeave = null;

        //update passengerCount Tally und Histogramm
        meinModel.getBusPassengerCount().update(bus.getPassengerCount());
        meinModel.getPassengerCountPerRide().update(bus.getPassengerCount());

        // Log bus departure
        meinModel.sendTraceNote("Bus departs from " + stationEntity.getName() + " to " + nextStation.getName());
        if(bus.getCurrentStationId() == 1) {
            bus.startRound();
        }
    }
}
