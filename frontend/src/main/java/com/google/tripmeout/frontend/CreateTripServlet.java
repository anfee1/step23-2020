package com.google.sps.tripmeout.frontend;

import com.google.tripmeout.frontend.TripModel;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

@WebServlet("/createTrip")
public final class CreateTripServlet extends HttpServlet {
  private final TripStorage storage;
  private static final String USER_TRIPS_ID_KEY = "userID";
  private static final String SINGLE_TRIP_ID_KEY = "tripID";


  @Inject
  public CreateTripServlet(TripStorage storage) {
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
    storage.removeTrip(tripId)
  }
}