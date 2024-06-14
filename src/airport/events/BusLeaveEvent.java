package airport.events;

import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import airport.entities.StationEntity;
import airport.AirportCarRentalModel;

public class BusLeaveEvent extends Event<StationEntity> {
    private final AirportCarRentalModel meinModel;
    public BusLeaveEvent(Model model, String name, boolean showInTrace) {
        super(model, name, showInTrace);
        meinModel = (AirportCarRentalModel) model;
    }

    @Override
    public void eventRoutine(StationEntity stationEntity) throws SuspendExecution {

    }
}
