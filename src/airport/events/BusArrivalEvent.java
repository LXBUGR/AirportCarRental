package airport.events;

import airport.entities.BusStationEntity;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import airport.entities.StationEntity;
import airport.entities.BusEntity;
import airport.AirportCarRentalModel;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;
import airport.entities.PassengerEntity;

public class BusArrivalEvent extends Event<BusStationEntity> {
    private final AirportCarRentalModel meinModel;

    public BusArrivalEvent(Model model, String name, boolean showInTrace) {
        super(model, name, showInTrace);
        meinModel = (AirportCarRentalModel) model;
    }

    @Override
    public void eventRoutine(BusStationEntity busStation) {
        TimeInstant arrivalTime = meinModel.presentTime();
        BusEntity bus = busStation.getBus();
        StationEntity newStation = busStation.getStation();
        int waitingTime = 0;

        meinModel.sendTraceNoteWithPassengers(bus.getName() + " arrives at " + newStation.getName(), bus.getPassengerCount());

        //update station related bus state
        bus.setCurrentStationId(newStation.getId());
        bus.setDriving(false);
        bus.setLastArrivalTime(arrivalTime.getTimeAsDouble());
        if(bus.getCurrentStationId() == bus.getStartStationId()) {
            bus.endRound();
        }

        //remove Passengers & update passengerSystemTimes
        bus.removePassengers();

        //enqueue Passengers & Update Passenger waiting times
        while (bus.getPassengerCount() < bus.getCapacity() && !newStation.queueEmpty()) {
            PassengerEntity passenger = newStation.dequeuePassenger();
            bus.addPassenger(passenger);
            double waitTime = arrivalTime.getTimeAsDouble() - passenger.getArrivalTime().getTimeAsDouble();
            newStation.recordPassengerWaitTime(waitTime);  //update passengerWaitTime
        }

        //Waiting time if there's space
        if(bus.getPassengerCount() < bus.getCapacity()) {
            waitingTime = meinModel.busWaitingTime;
        }

        //Schedule Leave Event
        BusLeaveEvent leaveEvent = new BusLeaveEvent(meinModel, "Bus Leave Event", true);
        leaveEvent.schedule(busStation, new TimeSpan(waitingTime));
        meinModel.currentBusLeaveEvents.put(bus.getId(), leaveEvent);
    }
}
