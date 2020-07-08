package com.google.tripmeout.frontend;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.tripmeout.frontend.TripModel;
import com.google.tripmeout.frontend.storage.TripStorage;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public final class TripServlet extends HttpServlet {
  private final TripStorage storage;
  private static final String USER_ID_QUERY_PARAM = "user-id";
  private static final String TRIP_ID_QUERY_PARAM = "trip-id";

  @Inject
  public TripServlet(TripStorage storage) {
    this.storage = storage;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String userID = request.getParameter(USER_ID_QUERY_PARAM);

    response.setContentType("application/json;");
    response.getWriter().print(new Gson().toJson(storage.getAllUserTrips(userID)));
  }

  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String tripId = request.getParameter(TRIP_ID_QUERY_PARAM);

    storage.removeTrip(tripId);
  }
}