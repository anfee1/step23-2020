package com.google.tripmeout.frontend.storage;

import com.google.tripmeout.frontend.PlaceVisitModel;
import com.google.tripmeout.frontend.error.PlaceVisitAlreadyExistsException;
import com.google.tripmeout.frontend.error.PlaceVisitNotFoundException;
import com.google.tripmeout.frontend.error.TripNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class InMemoryPlaceVisitStorage implements PlaceVisitStorage {
  // <tripId, id, PlaceVisitModel>
  Map<String, Map<String, PlaceVisitModel>> placesByTripIdByPlaceId = new ConcurrentHashMap<>();

  @Override
  public void addPlaceVisit(PlaceVisitModel placeVisit) throws PlaceVisitAlreadyExistsException {
    try {
      placesByTripIdByPlaceId.compute(placeVisit.tripId(), (tripKey, placesMap) -> {
        if (placesMap == null) {
          Map<String, PlaceVisitModel> newPlaceMap = new ConcurrentHashMap<>();
          newPlaceMap.put(placeVisit.id(), placeVisit);
          return newPlaceMap;
        }
        if (placesMap.get(placeVisit.id()) != null) {
          // cannot throw checked exception in here so wrap in RuntimeException
          throw new RuntimeException(
              new PlaceVisitAlreadyExistsException("PlaceVisit '" + placeVisit.placeName()
                  + "' already exists for trip '" + placeVisit.tripId() + "'"));
        }
        placesMap.put(placeVisit.id(), placeVisit);
        return placesMap;
      });
    } catch (RuntimeException e) {
      // check if RuntimeException is actually because of PlaceVisitAlreadyExistsException
      // if yes, throw PlaceVisitAlreadyExistsExceptions
      // else, throw original RuntimeExeption
      if (e.getCause() instanceof PlaceVisitAlreadyExistsException) {
        throw(PlaceVisitAlreadyExistsException) e.getCause();
      }
      throw e;
    }
  }

  @Override
  public void removePlaceVisit(String tripId, String placeVisitId)
      throws PlaceVisitNotFoundException {
    Map<String, PlaceVisitModel> placesMap = placesByTripIdByPlaceId.get(tripId);
    if (placesMap == null) {
      throw new PlaceVisitNotFoundException(
          String.format(("PlaceVisit with id '%s' not found for trip '%s'"), placeVisitId, tripId));
    }

    if (placesMap.remove(placeVisitId) == null) {
      throw new PlaceVisitNotFoundException(
          String.format(("PlaceVisit with id '%s' not found for trip '%s'"), placeVisitId, tripId));
    }
  }

  @Override
  public Optional<PlaceVisitModel> getPlaceVisit(String tripId, String placeVisitId) {
    return Optional.ofNullable(placesByTripIdByPlaceId.get(tripId))
        .map(placesMap -> placesMap.get(placeVisitId));
  }

  @Override
  public PlaceVisitModel updateUserMarkOrAddPlaceVisit(
      PlaceVisitModel placeVisit, PlaceVisitModel.UserMark newStatus) {
    AtomicReference<PlaceVisitModel> alreadyInStorage = new AtomicReference<>();

    Map<String, PlaceVisitModel> placesMap = placesByTripIdByPlaceId.computeIfAbsent(
        placeVisit.tripId(), (tripKey) -> new ConcurrentHashMap<>());

    placesMap.compute(placeVisit.id(), (placeKey, place) -> {
      if (place != null) {
        PlaceVisitModel updatedPlace = place.toBuilder().setUserMark(newStatus).build();
        alreadyInStorage.set(updatedPlace);
        return updatedPlace;
      } else {
        PlaceVisitModel updatedPlace = placeVisit.toBuilder().setUserMark(newStatus).build();
        alreadyInStorage.set(updatedPlace);
        return updatedPlace;
      }
    });

    return alreadyInStorage.get();
  }

  @Override
  public List<PlaceVisitModel> getTripPlaceVisits(String tripId) {
    Map<String, PlaceVisitModel> placesMap = placesByTripIdByPlaceId.get(tripId);
    List<PlaceVisitModel> tripPlaceVisits = new ArrayList<>();
    if (placesMap == null) {
      return tripPlaceVisits;
    }
    for (PlaceVisitModel place : placesMap.values()) {
      if (place != null) {
        tripPlaceVisits.add(place);
      }
    }

    return tripPlaceVisits;
  }

  @Override
  public void removeTripPlaceVisits(String tripId) throws TripNotFoundException {
    if (placesByTripIdByPlaceId.remove(tripId) == null) {
      throw new TripNotFoundException(String.format(("Trip with id '%s' not found."), tripId));
    }
  }
}
