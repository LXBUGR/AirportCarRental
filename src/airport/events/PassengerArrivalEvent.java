package airport.events;

import airport.AirportCarRentalModel;
import airport.IdManager;
import airport.entities.StationEntity;
import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import airport.entities.PassengerEntity;

public class PassengerArrivalEvent extends Event<PassengerEntity> {
    private final AirportCarRentalModel meinModel;

    public PassengerArrivalEvent(Model model, String name, boolean showInTrace) {
        super(model, name, showInTrace);
        meinModel = (AirportCarRentalModel) model;
    }

    @Override
    public void eventRoutine(PassengerEntity passengerEntity) throws SuspendExecution {
        int stationId = passengerEntity.getDestinationId();
        StationEntity station = IdManager.getStation(stationId);
        meinModel.sendTraceNote("Passenger " + passengerEntity.getName() + " arrives at " + station.getName());

        // Angekommene Passagier in die Station Queue hinzufügen
        station.getQueue().add(passengerEntity);
    }
}
