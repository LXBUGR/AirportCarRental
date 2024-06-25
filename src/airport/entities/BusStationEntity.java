package airport.entities;

import airport.AirportCarRentalModel;
import desmoj.core.simulator.Entity;

public class BusStationEntity extends Entity {
    private final BusEntity bus;
    private StationEntity station;
    public BusStationEntity(AirportCarRentalModel owner, String name, boolean showInTrace, BusEntity bus, StationEntity station) {
        super(owner, name, showInTrace);
        this.bus = bus;
        this.station = station;
    }

    public BusEntity getBus() { return bus; }

    public BusStationEntity setStation(StationEntity station) {
        this.station = station;
        return this;
    }

    public StationEntity getStation() { return station; }
}
