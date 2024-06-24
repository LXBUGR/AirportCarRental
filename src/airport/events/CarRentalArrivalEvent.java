package airport.events;

import airport.AirportCarRentalModel;
import airport.IdManager;
import airport.entities.CarRentalEntity;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;
import airport.entities.PassengerEntity;

public class CarRentalArrivalEvent extends Event<CarRentalEntity> {
    private final AirportCarRentalModel meinModel;

    public CarRentalArrivalEvent(Model model, String name, boolean showInTrace) {
        super(model, name, showInTrace);
        meinModel = (AirportCarRentalModel) model;
    }

    @Override
    public void eventRoutine(CarRentalEntity carRentalEntity) {
        PassengerEntity passenger = new PassengerEntity(meinModel, "Passagier CarRental", true, carRentalEntity.getId(), IdManager.getRandomTerminalId());
        PassengerArrivalEvent event = new PassengerArrivalEvent(meinModel, "Passenger arrived at busstop of " + carRentalEntity.getName() + " at " + meinModel.presentTime(), true);
        event.schedule(passenger, new TimeSpan(1));

        CarRentalArrivalEvent arrivalEvent = new CarRentalArrivalEvent(meinModel, "Car Rental Arrival", true);
        arrivalEvent.schedule(carRentalEntity, new TimeSpan(meinModel.getArrivalRateRental().sample()));
        meinModel.sendTraceNote("Car Rental Arrival Event scheduled for " + carRentalEntity.getName());
    }
}
