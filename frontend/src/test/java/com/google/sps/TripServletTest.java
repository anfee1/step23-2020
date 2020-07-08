package com.google.sps;

import static com.google.common.truth.Subject.*;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import com.google.tripmeout.frontend.TripModel;
import com.google.tripmeout.frontend.TripServlet;
import com.google.tripmeout.frontend.error.TripNotFoundException;
import com.google.tripmeout.frontend.storage.TripStorage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TripServletTest {
  private TripServlet servlet;

  // PlaceVisitModel objects to use in testing
  private static final TripModel PARIS = TripModel.builder()
                                             .setId("CDG")
                                             .setName("Paris, France")
                                             .setUserId("12345")
                                             .setLocationLat(48.3288)
                                             .setLocationLong(34.0)
                                             .build();

  @Mock TripStorage storage;

  @Mock HttpServletRequest request;

  @Mock HttpServletResponse response;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    servlet = new TripServlet(storage);
  }

  @Test
  public void returnsEmptyListForIdWithoutTrips() throws ServletException, IOException {
    when(request.getParameter("user-id")).thenReturn("123");

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    when(response.getWriter()).thenReturn(pw);

    servlet.doGet(request, response);

    List<TripModel> expectedResult = new ArrayList<TripModel>();
    when(storage.getAllUserTrips(any())).thenReturn(expectedResult);

    TripModel result = new Gson().fromJson(sw.getBuffer().toString().trim(), TripModel.class);
    assertThat(result.toString()).isEqualTo(expectedResult);
  }

  @Test
  public void returnsListForIdWithTrips() throws ServletException, IOException {
    when(request.getParameter("user-id")).thenReturn("12345");

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    when(response.getWriter()).thenReturn(pw);

    servlet.doGet(request, response);

    List<TripModel> expectedResult = new ArrayList<TripModel>();
    expectedResult.add(PARIS);
    when(storage.getAllUserTrips(any())).thenReturn(expectedResult);

    TripModel result = new Gson().fromJson(sw.getBuffer().toString().trim(), TripModel.class);
    assertThat(result.toString()).isEqualTo(expectedResult.toString());
  }

}