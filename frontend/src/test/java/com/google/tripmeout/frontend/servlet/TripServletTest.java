package com.google.tripmeout.frontend.servlet;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.tripmeout.frontend.TripModel;
import com.google.tripmeout.frontend.error.TripAlreadyExistsException;
import com.google.tripmeout.frontend.error.TripNotFoundException;
import com.google.tripmeout.frontend.serialization.GsonTripModelTypeAdapter;
import com.google.tripmeout.frontend.storage.InMemoryTripModelStorage;
import java.io.IOException;
import javax.inject.Provider;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TripServletTest {
  private Gson gson;
  private static final String STUB_USER_ID = "userId";
  private static final Provider<String> STUB_USER_ID_PROVIDER = new Provider<String>() {
    @Override
    public String get() {
      return STUB_USER_ID;
    }
  };

  private final static TripModel PARIS = TripModel.builder()
                                             .setId("CDG")
                                             .setName("Paris, France")
                                             .setUserId(STUB_USER_ID)
                                             .setPlacesApiPlaceId("places-api-place-id")
                                             .build();

  @Mock HttpServletRequest request;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    this.gson = new GsonBuilder()
                    .registerTypeAdapter(TripModel.class, new GsonTripModelTypeAdapter())
                    .create();
  }

  @Test
  public void doGet_badId_returnsErrorCode()
      throws ServletException, IOException, TripNotFoundException, TripAlreadyExistsException {
    InMemoryTripModelStorage storage = new InMemoryTripModelStorage();
    storage.addTrip(PARIS);
    FakeHttpServletResponse response = new FakeHttpServletResponse();
    TripServlet servlet = new TripServlet(storage, gson, STUB_USER_ID_PROVIDER);

    when(request.getRequestURI()).thenReturn("/trips/Not_CDG");

    servlet.doGet(request, response);
    assertThat(response.getStatus()).isEqualTo(404);
  }

  @Test
  public void doGet_badURI_returnsErrorCode()
      throws ServletException, IOException, TripNotFoundException, TripAlreadyExistsException {
    InMemoryTripModelStorage storage = new InMemoryTripModelStorage();
    storage.addTrip(PARIS);
    FakeHttpServletResponse response = new FakeHttpServletResponse();
    TripServlet servlet = new TripServlet(storage, gson, STUB_USER_ID_PROVIDER);

    when(request.getRequestURI()).thenReturn("/bad_trips_uri/CDG");

    servlet.doGet(request, response);
    assertThat(response.getStatus()).isEqualTo(400);
  }

  @Test
  public void doGet_goodId_returnsTrip()
      throws ServletException, IOException, TripNotFoundException, TripAlreadyExistsException {
    InMemoryTripModelStorage storage = new InMemoryTripModelStorage();
    storage.addTrip(PARIS);
    FakeHttpServletResponse response = new FakeHttpServletResponse();
    TripServlet servlet = new TripServlet(storage, gson, STUB_USER_ID_PROVIDER);

    when(request.getRequestURI()).thenReturn("/trips/CDG");

    servlet.doGet(request, response);

    TripModel responseTrip = gson.fromJson(response.getResponseString(), TripModel.class);
    assertThat(responseTrip).isEqualTo(PARIS);
  }

  @Test
  public void doDelete_goodId_returnsSuccessCode()
      throws ServletException, IOException, TripNotFoundException, TripAlreadyExistsException {
    InMemoryTripModelStorage storage = new InMemoryTripModelStorage();
    storage.addTrip(PARIS);
    FakeHttpServletResponse response = new FakeHttpServletResponse();
    TripServlet servlet = new TripServlet(storage, gson, STUB_USER_ID_PROVIDER);

    when(request.getRequestURI()).thenReturn("/trips/CDG");

    servlet.doDelete(request, response);
    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  public void doDelete_badId_returnsNotFoundError()
      throws ServletException, IOException, TripNotFoundException, TripAlreadyExistsException {
    InMemoryTripModelStorage storage = new InMemoryTripModelStorage();
    storage.addTrip(PARIS);
    FakeHttpServletResponse response = new FakeHttpServletResponse();
    TripServlet servlet = new TripServlet(storage, gson, STUB_USER_ID_PROVIDER);

    when(request.getRequestURI()).thenReturn("/trips/Not_CDG");

    servlet.doDelete(request, response);
    assertThat(response.getStatus()).isEqualTo(404);
  }

  @Test
  public void doDelete_badURI_returnsBadRequestError()
      throws ServletException, IOException, TripNotFoundException, TripAlreadyExistsException {
    InMemoryTripModelStorage storage = new InMemoryTripModelStorage();
    storage.addTrip(PARIS);
    FakeHttpServletResponse response = new FakeHttpServletResponse();
    TripServlet servlet = new TripServlet(storage, gson, STUB_USER_ID_PROVIDER);

    when(request.getRequestURI()).thenReturn("/bad_trips_uri/CDG");

    servlet.doDelete(request, response);
    assertThat(response.getStatus()).isEqualTo(400);
  }

  @Test
  public void doGet_incorrectUserForTrip_returnsForbiddenError() throws Exception {
    InMemoryTripModelStorage storage = new InMemoryTripModelStorage();
    storage.addTrip(TripModel.builder()
                        .setId("FakeId")
                        .setName("Name")
                        .setUserId("John Doe")
                        .setPlacesApiPlaceId("Hello World")
                        .build());
    FakeHttpServletResponse response = new FakeHttpServletResponse();
    TripServlet servlet = new TripServlet(storage, gson, STUB_USER_ID_PROVIDER);

    when(request.getRequestURI()).thenReturn("/trips/FakeId");

    servlet.doGet(request, response);

    assertThat(response.getStatus()).isEqualTo(403);
  }
}
