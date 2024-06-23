package airport.events;

import airport.AirportCarRentalModel;
import airport.IdManager;
import airport.entities.TerminalEntity;
import desmoj.core.simulator.*;
import airport.entities.PassengerEntity;

public class FlightArrivalEvent extends Event<TerminalEntity> {
    private final AirportCarRentalModel meinModel;

    public FlightArrivalEvent(Model model, String name, boolean showInTrace) {
        super(model, name, showInTrace);
        meinModel = (AirportCarRentalModel) model;
    }

    @Override
    public void eventRoutine(TerminalEntity terminal) {
        int passengerCount = (int) Math.round(meinModel.getFlightPassengers().sample());
        for(int i = 0; i < passengerCount; i++) {
            PassengerEntity passenger = new PassengerEntity(meinModel, "Passagier Terminal " + terminal.getId(), true, terminal.getId(), IdManager.getRandomCarRentalId());
            PassengerArrivalEvent arrivalEvent = new PassengerArrivalEvent(meinModel, "Passenger arrived at busstop of " + terminal.getName(), true);
            arrivalEvent.schedule(passenger, new TimeSpan(1));
        }
        meinModel.sendTraceNote(  passengerCount+ " Passengers " + " arrive at " + terminal.getName() + " from a flight at " + presentTime());

        FlightArrivalEvent arrivalEvent = new FlightArrivalEvent(meinModel, "Flight Arrival" + terminal.getName(), true);
        arrivalEvent.schedule(terminal, new TimeSpan(meinModel.getArrivalRateTerminal().sample()));
    }
}
