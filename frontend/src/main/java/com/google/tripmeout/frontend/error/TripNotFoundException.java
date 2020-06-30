package com.google.tripmeout.frontend.error;

import com.google.tripmeout.frontend.error.TripMeOutException;
import java.lang.Throwable;
import javax.servlet.http.HttpServletResponse;

/**
 * exception to throw if a TripModel object is not found in storage 
 */
public class TripNotFoundException extends TripMeOutException {
  public TripNotFoundException(String errorMssg) {
    super(errorMssg);
  }

  public TripNotFoundException(String errorMssg, Throwable cause) {
    super(errorMssg, cause);
  }

  @Override
  public int restStatusCode() {
    return HttpServletResponse.SC_NOT_FOUND;
  }
}
