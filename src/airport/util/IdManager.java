package airport.util;

import airport.entities.CarRentalEntity;
import airport.entities.StationEntity;
import airport.entities.TerminalEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class IdManager {
    private static final Random random = new Random();
    private static int maxIdTerminal;
    private static int maxIdCarRental;
    private static Map<Integer, StationEntity> idToStationMap;


    //Scans list of terminals and carRentals and does the following:
    //  assign unique id to each of them
    //  add station to idToStation map, which maps if of a station to its station object
    //Thus, with this class one can easily access the station objects using only their id
    public static void initializeIds(Collection<TerminalEntity> terminals, Collection<CarRentalEntity> carRentals) {
        maxIdTerminal = terminals.size();
        maxIdCarRental = maxIdTerminal + carRentals.size();
        idToStationMap = new HashMap<>();
        int currentId = 1;
        for(TerminalEntity terminalEntity: terminals) {
            terminalEntity.setId(currentId);
            idToStationMap.put(currentId, terminalEntity);
            currentId++;
        }
        for(CarRentalEntity carRentalEntity: carRentals) {
            carRentalEntity.setId(currentId);
            idToStationMap.put(currentId, carRentalEntity);
            currentId++;
        }
    }

    //Use when creating Passengers in FlightArrivalEvent
    public static Integer getRandomTerminalId() {
        return random.nextInt(maxIdTerminal - 1) + 1;
    }

    //Use when creating Passengers in CarRentalArrivalEvent
    public static Integer getRandomCarRentalId() {
        return random.nextInt(maxIdCarRental - (maxIdTerminal+1)) + maxIdTerminal + 1;
    }

    //Use to retrieve Station object for any given station id
    public static StationEntity getStation(Integer id) {
        return idToStationMap.get(id);
    }
}
