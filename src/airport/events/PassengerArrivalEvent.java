package airport.events;

import airport.AirportCarRentalModel;
import airport.IdManager;
import airport.entities.BusEntity;
import airport.entities.PassengerEntity;
import airport.entities.StationEntity;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;

public class PassengerArrivalEvent extends Event<PassengerEntity> {
    private final AirportCarRentalModel meinModel;

    public PassengerArrivalEvent(Model model, String name, boolean showInTrace) {
        super(model, name, showInTrace);
        meinModel = (AirportCarRentalModel) model;
    }

    @Override
    public void eventRoutine(PassengerEntity passengerEntity) {
        StationEntity station = IdManager.getStation(passengerEntity.getArrivalId());
        meinModel.sendTraceNote("Passenger " + passengerEntity.getName() + " arrives at " + station.getName());
        passengerEntity.setArrivalTime(meinModel.presentTime());

        BusEntity bus = meinModel.getBus();
        if (!bus.isDriving() && bus.getCurrentStationId() == passengerEntity.getArrivalId() && bus.getPassengerCount() < bus.getCapacity()) {
            bus.addPassenger(passengerEntity);
            if (bus.getPassengerCount() == bus.getCapacity()) {
                meinModel.currentBusLeave.reSchedule(meinModel.presentTime());
            }
            station.recordPassengerWaitTime(0);
        } else {
            station.enqueuePassenger(passengerEntity);
        }
    }
}
