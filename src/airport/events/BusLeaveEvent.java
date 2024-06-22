package airport.events;

import airport.IdManager;
import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeInstant;
import airport.entities.StationEntity;
import airport.entities.BusEntity;
import airport.AirportCarRentalModel;

public class BusLeaveEvent extends Event<StationEntity> {
    private final AirportCarRentalModel meinModel;

    public BusLeaveEvent(Model model, String name, boolean showInTrace) {
        super(model, name, showInTrace);
        meinModel = (AirportCarRentalModel) model;
    }

    @Override
    public void eventRoutine(StationEntity stationEntity) throws SuspendExecution {
        BusEntity bus = meinModel.getBus();
        double nextStationId = bus.getNextStationId();

        // Ankunft des Buses am nächsten Station planen
        StationEntity nextStation = IdManager.getStation((int) nextStationId);
        double travelTime = bus.getNextStationDriveTime();

        // Neue ereignis für die Busankunft an der nächsten Station
        BusArrivalEvent arrivalEvent = new BusArrivalEvent(meinModel, "Bus Arrival Event", true);
        arrivalEvent.schedule(nextStation, new TimeInstant(travelTime));

        // Bus abfahrt loggen
        meinModel.sendTraceNote("Bus departs from " + stationEntity.getName() + " to " + nextStation.getName());
    }
}
