package airport.events;

import airport.AirportCarRentalModel;
import airport.IdManager;
import airport.entities.CarRentalEntity;
import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import airport.entities.PassengerEntity;

public class CarRentalArrivalEvent extends Event<PassengerEntity> {
    private final AirportCarRentalModel meinModel;

    public CarRentalArrivalEvent(Model model, String name, boolean showInTrace) {
        super(model, name, showInTrace);
        meinModel = (AirportCarRentalModel) model;
    }

    @Override
    public void eventRoutine(PassengerEntity passengerEntity) throws SuspendExecution {
        CarRentalEntity carRentalStation = (CarRentalEntity) IdManager.getStation(passengerEntity.getDestinationId());
        meinModel.sendTraceNote("Passenger " + passengerEntity.getName() + " arrives at " + carRentalStation.getName());

        // Neue Kunde kommt beim Mietstation an und wird an die queue hinzugefügt
        carRentalStation.getQueue().add(passengerEntity);
    }
}
