package airport.events;

import airport.AirportCarRentalModel;
import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import airport.entities.PassengerEntity;

import java.util.List;

public class FlightArrivalEvent extends Event<PassengerEntity> {
    private final AirportCarRentalModel meinModel;
    public FlightArrivalEvent(Model model, String name, boolean showInTrace) {
        super(model, name, showInTrace);
        meinModel = (AirportCarRentalModel) model;
    }

    @Override
    public void eventRoutine(PassengerEntity passengerEntities) throws SuspendExecution {

    }
}
