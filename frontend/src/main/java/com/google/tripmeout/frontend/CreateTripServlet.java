package com.google.sps.tripmeout.frontend;

import com.google.tripmeout.frontend.TripModel;

@WebServlet("/createTrip")
public final class CreateTripServlet extends HttpServlet {
  private final TripStorage storage;
  private static final String USER_TRIPS_ID_KEY = "userID";


  @Inject
  public CreateTripServlet(TripStorage storage) {
    this.storage = storage;
  }
    
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");
    response.getWriter().print(new Gson().toJson(storage.getAllUserTrips()));
  }

  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
    storage.removeTrip()
  }
}