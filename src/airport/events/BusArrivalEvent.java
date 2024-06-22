package airport.events;

import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeInstant;
import airport.entities.StationEntity;
import airport.entities.BusEntity;
import airport.entities.PassengerEntity;
import airport.AirportCarRentalModel;

import java.util.Random;

public class BusArrivalEvent extends Event<StationEntity> {
    private final AirportCarRentalModel meinModel;
    private final Random random;

    public BusArrivalEvent(Model model, String name, boolean showInTrace) {
        super(model, name, showInTrace);
        meinModel = (AirportCarRentalModel) model;
        random = new Random();
    }

    @Override
    public void eventRoutine(StationEntity nextStation) throws SuspendExecution {
        BusEntity bus = meinModel.getBus();

        // Update aktuelle Station ID
        bus.setCurrentStationId(nextStation.getId());

        // Bus ankunft loggen beim meinModel
        meinModel.sendTraceNote("Bus arrives at " + nextStation.getName());

        // Passagiere steigen aus wessen Ziel die angekommene Station ist
        bus.removePassengers(nextStation.getId());

        // Passagiere steigen ein(wenn noch Platz im Bus gibts) Kapazität wird beim Busentity festgelegt. 
        while (bus.getPassengerCount() < bus.getCapacity() && !nextStation.getQueue().isEmpty()) {
            bus.addPassenger((PassengerEntity) nextStation.dequeuePassenger());
        }

        // Abfahrt planen 
        double waitingTime = 4.0 + random.nextDouble(); // Warte dauer des busses beim station ( erstmal min. 4 gemacht, je nach bedarf ändern)
        BusLeaveEvent leaveEvent = new BusLeaveEvent(meinModel, "Bus Leave Event", true);
        leaveEvent.schedule(nextStation, new TimeInstant(waitingTime));
    }
}
