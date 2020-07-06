package com.google.sps.tripmeout.frontend;

import com.google.tripmeout.frontend.TripModel;
import com.google.tripmeout.frontend.storage.TripStorage;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public final class TripServlet extends HttpServlet {
  private final TripStorage storage;
  private static final String USER_TRIPS_ID_KEY = "userID";
  private static final String SINGLE_TRIP_ID_KEY = "tripID";


  @Inject
  public TripServlet(TripStorage storage) {
    this.storage = storage;
  }
    
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query(USER_TRIPS_ID_KEY);
    PreparedQuery userID = datastore.prepare(query);
    
    response.setContentType("application/json;");
    response.getWriter().print(new Gson().toJson(storage.getAllUserTrips(userID)));
  }

  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query(SINGLE_TRIP_ID_KEY);
    PreparedQuery tripId = datastore.prepare(query);
    storage.removeTrip(tripId);
  }
}