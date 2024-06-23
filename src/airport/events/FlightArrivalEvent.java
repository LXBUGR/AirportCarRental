package airport.events;

import airport.AirportCarRentalModel;
import airport.IdManager;
import airport.entities.TerminalEntity;
import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import airport.entities.PassengerEntity;

public class FlightArrivalEvent extends Event<PassengerEntity> {
    private final AirportCarRentalModel meinModel;

    public FlightArrivalEvent(Model model, String name, boolean showInTrace) {
        super(model, name, showInTrace);
        meinModel = (AirportCarRentalModel) model;
    }

    @Override
    public void eventRoutine(PassengerEntity passengerEntity) throws SuspendExecution {
        int terminalId = passengerEntity.getDestinationId();
        TerminalEntity terminal = (TerminalEntity) IdManager.getStation(terminalId);
        meinModel.sendTraceNote("Passenger " + passengerEntity.getName() + " arrives at " + terminal.getName() + " from a flight.");

        // Angekommene Passagier in die Queue von Terminal hinzufügen
        terminal.getQueue().insert(passengerEntity);
    }
}
