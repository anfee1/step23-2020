package com.google.sps;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.List;

@RunWith(JUnit4.class)
public final class InMemoryPlaceVisitStorageTest {

  //Fake Storage ID

  private static final String STORAGE_ID = "12345";

  // PlaceVisitModel objects to use in testing
  private static final PlaceVisitModel LONDON = PlaceVisitModel.builder()
    .setPlaceId("LCY")
    .setName("London, UK")
    .setTripId("a")
    .setUserMark("don't-care")
    .setLatitude(56)
    .setLongitude(23.64)
    .build();

  private static final PlaceVisitModel ROME = PlaceVisitModel.builder()
    .setPlaceId("FCO")
    .setName("Rome, Italy")
    .setTripId("a")
    .setUserMark("must-see")
    .setLatitude(44.32)
    .setLongitude(32.1244)
    .build();

  private static final PlaceVisitModel PARIS = PlaceVisitModel.builder()
    .setPlaceId("CDG")
    .setName("Paris, France")
    .setTripId("a")
    .setUserMark("if-time")
    .setLatitude(48.3288)
    .setLongitude(34)
    .build();

  private static final PlaceVisitModel TOKYO = PlaceVisitModel.builder()
    .setPlaceId("HND")
    .setName("Tokyo, Japan")
    .setTripId("a")
    .setUserMark("must-see")
    .setLatitude(35.32)
    .setLongitude(139.33)
    .build();

    InMemoryPlaceVisitStorage storage = new InMemoryPlaceVisitStorage();
    storage.addPlaceVisit(LONDON);
    storage.addPlaceVisit(ROME);
    storage.addPlaceVisit(PARIS);
    storage.setId(STORAGE_ID);

  /**
   * test that only the places not deleted from storage are the ones in the list returned
   */
  @Test
  public void testGetAllTripsAfterRemove() throws PlaceVisitNotFoundException {
    
    assertThat(storage.getAllUserTrips(STORAGE_ID)).containsExactly(LONDON, ROME, PARIS);
    
    storage.removeTrip(PARIS.placeId());
    assertThat(storage.getAllUserTrips(STORAGE_ID)).containsExactly(LONDON, ROME);
  }

  /**
   * test that a PlaceNotFoundException is thrown when a place deleted not in storage
   * is attempted to be deleted
   */
  @Test
  public void testGetRemovingNotInStorage() throws PlaceVisitNotFoundException {
    Assert.assertThrows(PlaceVisitNotFoundException.class, () -> 
        storage.removeTrip(TOKYO.placeId()));
  }
  
}
