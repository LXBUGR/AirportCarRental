package airport.events;

import airport.AirportCarRentalModel;
import airport.IdManager;
import airport.entities.BusEntity;
import airport.entities.StationEntity;
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
    public void eventRoutine(PassengerEntity passengerEntity) {
        StationEntity station = IdManager.getStation(passengerEntity.getArrivalId());
        meinModel.sendTraceNote("Passenger " + passengerEntity.getName() + " arrives at " + station.getName());

        BusEntity bus = meinModel.getBus();
        if(!bus.isDriving() && bus.getCurrentStationId() == passengerEntity.getArrivalId() && bus.getPassengerCount() < bus.getCapacity()) {
            //Angekommenen Passagier steigt in Bus ein
            bus.addPassenger(passengerEntity);
            if(bus.getPassengerCount() == bus.getCapacity()) {
               meinModel.currentBusLeave.reSchedule(meinModel.presentTime());
            }
        } else {
            // Angekommene Passagier in die Station Queue hinzufügen
            station.enqueuePassenger(passengerEntity);
        }
    }
}
