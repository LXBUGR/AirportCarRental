package airport.events;

import airport.AirportCarRentalModel;
import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import airport.entities.StationEntity;

public class BusArrivalEvent extends Event<StationEntity> {
    private final AirportCarRentalModel meinModel;
    public BusArrivalEvent(Model model, String name, boolean showInTrace) {
        super(model, name, showInTrace);
        meinModel = (AirportCarRentalModel) model;
    }

    @Override
    public void eventRoutine(StationEntity stationEntity) throws SuspendExecution {

    }
}
