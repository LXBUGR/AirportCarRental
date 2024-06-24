package airport.events;

import airport.entities.CarRentalEntity;
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
    public void eventRoutine(StationEntity nextStation) throws SuspendExecution {
        TimeInstant arrivalTime = meinModel.presentTime();
        BusEntity bus = meinModel.getBus();

        bus.setCurrentStationId(nextStation.getId());
        meinModel.sendTraceNoteWithPassengers("Bus arrives at " + nextStation.getName());

        // Remove passengers and record stay times
        for (PassengerEntity passenger : bus.getPassengerList()) {
            double stayTime = arrivalTime.getTimeAsDouble() - passenger.getArrivalTime().getTimeAsDouble();
            if (nextStation.getId() == 1) {
                meinModel.getStation1StayTimes().update(stayTime);
            } else if (nextStation.getId() == 2) {
                meinModel.getStation2StayTimes().update(stayTime);
            } else if (nextStation.getId() == 3) {
                meinModel.getCarRentalStayTimes().update(stayTime);
            }
        }
        bus.removePassengers();

        while (bus.getPassengerCount() < bus.getCapacity() && !nextStation.queueEmpty()) {
            PassengerEntity passenger = nextStation.dequeuePassenger();
            bus.addPassenger(passenger);
            double waitTime = ((TimeInstant) arrivalTime).getTimeAsDouble() - passenger.getArrivalTime().getTimeAsDouble();
            nextStation.recordPassengerWaitTime(waitTime);
            meinModel.getPassengerSystemTimes().update(waitTime);
        }

        bus.setDriving(false);

        BusLeaveEvent leaveEvent = new BusLeaveEvent(meinModel, "Bus Leave Event", true);
        leaveEvent.schedule(nextStation, new TimeSpan(5.0));
        meinModel.currentBusLeave = leaveEvent;

        bus.endRound();
        bus.startRound();
    }
}
