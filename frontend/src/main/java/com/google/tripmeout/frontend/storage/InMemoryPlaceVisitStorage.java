package com.google.tripmeout.frontend.storage;
 
import com.google.tripmeout.frontend.PlaceVisitModel;
import com.google.tripmeout.frontend.storage.PlaceVisitStorage;
import com.google.tripmeout.frontend.error.PlaceVisitAlreadyExistsException;
import com.google.tripmeout.frontend.error.PlaceVisitNotFoundException;
import com.google.common.collect.Tables;
import com.google.common.collect.Table;
import com.google.common.collect.HashBasedTable;
import java.util.Map;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class InMemoryPlaceVisitStorage implements PlaceVisitStorage {
  // <placeId, tripId, PlaceVisitModel> 
  Map<String, Map<String, PlaceVisitModel>> storage = new ConcurrentHashMap<>();
 
  @Override
  public void addPlaceVisit(PlaceVisitModel placeVisit) throws PlaceVisitAlreadyExistsException {
    Map<String, PlaceVisitModel> tripsMap = storage.get(placeVisit.placeId());

    if (tripsMap == null) {
      Map<String, PlaceVisitModel> newPlaceMap = new ConcurrentHashMap<>();
      newPlaceMap.put(placeVisit.tripId(), placeVisit);
      storage.put(placeVisit.placeId(), newPlaceMap);

    } else if (tripsMap.get(placeVisit.tripId()) == null) {
      tripsMap.put(placeVisit.tripId(), placeVisit);

    } else {
      throw new PlaceVisitAlreadyExistsException("PlaceVisit " + 
        placeVisit.name() + " already exists for trip " + placeVisit.tripId());
    }
  }

  @Override
  public void removePlaceVisit(String tripId, String placeId) throws PlaceVisitNotFoundException {
    Map<String, PlaceVisitModel> tripsMap = storage.get(placeId);
    if (tripsMap == null || tripsMap.remove(tripId) == null) {
      throw new PlaceVisitNotFoundException("PlaceVisit with id" + placeId + 
          " not found for trip " + tripId);
    }
  }

  @Override
  public PlaceVisitModel getPlaceVisit(String tripId, String placeId) throws PlaceVisitNotFoundException {
    Map<String, PlaceVisitModel> tripsMap = storage.get(placeId);
    if (tripsMap == null || tripsMap.get(tripId) == null) {
      throw new PlaceVisitNotFoundException("PlaceVisit with id" + placeId + 
          " not found for trip " + tripId);
    } else {
      return tripsMap.get(tripId);
    }
    
  }

  @Override
  public boolean changePlaceVisitStatus(String tripId, String placeId, String newStatus) {
    Map<String, PlaceVisitModel> tripsMap = storage.get(placeId);
    if (tripsMap == null || tripsMap.get(tripId) == null) {
      return false;
    } else {
      PlaceVisitModel place = tripsMap.get(tripId);
      PlaceVisitModel updatedPlace = place.toBuilder().setUserMark(newStatus).build();
      tripsMap.put(tripId, updatedPlace);
      return true;
    }
  }

  @Override
  public List<PlaceVisitModel> getTripPlaceVisits(String tripId) {
    List<PlaceVisitModel> tripPlaceVisits = new ArrayList<>();
    storage.forEach((placeId, tripIdMap) -> {
      PlaceVisitModel place = tripIdMap.get(tripId);
      if (place != null && (place.userMark().equals("must-see") || place.userMark().equals("if-time"))) {
        tripPlaceVisits.add(place);
      }
    });

    return tripPlaceVisits;
  }
 
}
