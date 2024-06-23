package airport.events;

import airport.AirportCarRentalModel;
import airport.IdManager;
import airport.entities.CarRentalEntity;
import airport.entities.TerminalEntity;
import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.*;
import airport.entities.PassengerEntity;

public class FlightArrivalEvent extends Event<TerminalEntity> {
    private final AirportCarRentalModel meinModel;

    public FlightArrivalEvent(Model model, String name, boolean showInTrace) {
        super(model, name, showInTrace);
        meinModel = (AirportCarRentalModel) model;
    }

    @Override
    public void eventRoutine(TerminalEntity terminal) throws SuspendExecution {
        //TODO anzahl der Passagiere randomisieren
        int passengerCount = 10;
        for(int i = 0; i < passengerCount; i++) {
            PassengerEntity passenger = new PassengerEntity(meinModel, "Passagier Terminal", true, terminal.getId(), IdManager.getRandomCarRentalId());
            PassengerArrivalEvent arrivalEvent = new PassengerArrivalEvent(meinModel, "Passenger arrived at busstop of " + terminal.getName(), true);
            arrivalEvent.schedule(passenger, new TimeSpan(1));
        }
        meinModel.sendTraceNote(  passengerCount+ " Passengers " + " arrive at " + terminal.getName() + " from a flight at " + presentTime());

        FlightArrivalEvent arrivalEvent = new FlightArrivalEvent(meinModel, "Flight Arrival" + terminal.getName(), true);
        arrivalEvent.schedule(terminal, new TimeSpan(meinModel.getArrivalRateTerminal1().sample()));
    }
}
