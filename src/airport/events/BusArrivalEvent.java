package airport.events;

import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import airport.entities.StationEntity;
import airport.entities.BusEntity;
import airport.AirportCarRentalModel;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;
import airport.entities.PassengerEntity;

public class BusArrivalEvent extends Event<StationEntity> {
    private final AirportCarRentalModel meinModel;

    public BusArrivalEvent(Model model, String name, boolean showInTrace) {
        super(model, name, showInTrace);
        meinModel = (AirportCarRentalModel) model;
    }

    @Override
    public void eventRoutine(StationEntity nextStation) {
        TimeInstant arrivalTime = meinModel.presentTime();
        BusEntity bus = meinModel.getBus();
        double waitingTime = 0;

        bus.setCurrentStationId(nextStation.getId());
        bus.setDriving(false);
        bus.setLastArrivalTime(arrivalTime.getTimeAsDouble());
        meinModel.sendTraceNoteWithPassengers("Bus arrives at " + nextStation.getName());

        bus.removePassengers(); //remove passengers; updates passengerSystemTimes

        while (bus.getPassengerCount() < bus.getCapacity() && !nextStation.queueEmpty()) {
            PassengerEntity passenger = nextStation.dequeuePassenger();
            bus.addPassenger(passenger);
            double waitTime = arrivalTime.getTimeAsDouble() - passenger.getArrivalTime().getTimeAsDouble();
            nextStation.recordPassengerWaitTime(waitTime);  //update passengerWaitTime
        }

        if(bus.getPassengerCount() < bus.getCapacity()) {
            waitingTime = 5.0; // Warte dauer des busses beim station
        }

        BusLeaveEvent leaveEvent = new BusLeaveEvent(meinModel, "Bus Leave Event", true);
        leaveEvent.schedule(nextStation, new TimeSpan(waitingTime));
        meinModel.currentBusLeave = leaveEvent;

        if(bus.getCurrentStationId() == 1) {
            bus.endRound();
        }
    }
}
