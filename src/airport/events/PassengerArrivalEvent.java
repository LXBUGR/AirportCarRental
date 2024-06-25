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

        //Set Passenger System arrival time
        passengerEntity.setArrivalTime(meinModel.presentTime());

        //For each bus check if its here and theres space;
        for(BusEntity bus : meinModel.getBusses()) {
            if (!bus.isDriving() && bus.getCurrentStationId() == passengerEntity.getArrivalId() && bus.getPassengerCount() < bus.getCapacity()) {
                //Passenger boards this bus
                bus.addPassenger(passengerEntity);
                if (bus.getPassengerCount() == bus.getCapacity()) {
                    //Reschedule BusLeaveEvent for this bus if bus is full
                    meinModel.currentBusLeaveEvents.get(bus.getId()).reSchedule(meinModel.presentTime());
                }
                station.recordPassengerWaitTime(0);
                return;
            }
        }

        //Bus not here -> enqueue at Bus Station
        station.enqueuePassenger(passengerEntity);
    }
}
