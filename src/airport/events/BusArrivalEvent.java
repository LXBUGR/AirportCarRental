package airport.events;

import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import airport.entities.StationEntity;
import airport.entities.BusEntity;
import airport.AirportCarRentalModel;
import desmoj.core.simulator.TimeSpan;

public class BusArrivalEvent extends Event<StationEntity> {
    private final AirportCarRentalModel meinModel;

    public BusArrivalEvent(Model model, String name, boolean showInTrace) {
        super(model, name, showInTrace);
        meinModel = (AirportCarRentalModel) model;
    }

    @Override
    public void eventRoutine(StationEntity nextStation) throws SuspendExecution {
        double waitingTime = 0;
        BusEntity bus = meinModel.getBus();

        // Update aktuelle Station ID
        bus.setCurrentStationId(nextStation.getId());

        // Bus ankunft loggen beim meinModel
        meinModel.sendTraceNote("Bus arrives at " + nextStation.getName());

        // Passagiere steigen aus wessen Ziel die angekommene Station ist
        bus.removePassengers();

        // Passagiere steigen ein(wenn noch Platz im Bus gibts) Kapazität wird beim BusEntity festgelegt.
        while (bus.getPassengerCount() < bus.getCapacity() && !nextStation.queueEmpty()) {
            bus.addPassenger(nextStation.dequeuePassenger());
        }

        if(bus.getPassengerCount() < bus.getCapacity()) {
            waitingTime = 5.0; // Warte dauer des busses beim station
        }

        bus.setDriving(false);

        // Abfahrt planen
        BusLeaveEvent leaveEvent = new BusLeaveEvent(meinModel, "Bus Leave Event", true);
        leaveEvent.schedule(nextStation, new TimeSpan(waitingTime));
        meinModel.currentBusLeave = leaveEvent;
    }
}
