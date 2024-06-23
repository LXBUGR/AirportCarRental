package airport.events;

import airport.IdManager;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import airport.entities.StationEntity;
import airport.entities.BusEntity;
import airport.AirportCarRentalModel;
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
        int nextStationId = bus.getNextStationId();

        // Ankunft des Buses am nächsten Station planen
        StationEntity nextStation = IdManager.getStation(nextStationId);
        double travelTime = bus.getNextStationDriveTime();

        // Neue ereignis für die Busankunft an der nächsten Station
        BusArrivalEvent arrivalEvent = new BusArrivalEvent(meinModel, "Bus Arrival Event", true);
        arrivalEvent.schedule(nextStation, new TimeSpan(travelTime));
        bus.setDriving(true);
        meinModel.currentBusLeave = null;

        // Bus abfahrt loggen
        meinModel.sendTraceNote("Bus departs from " + stationEntity.getName() + " to " + nextStation.getName());
    }
}
