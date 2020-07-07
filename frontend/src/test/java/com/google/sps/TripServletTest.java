package com.google.sps;

import com.google.tripmeout.frontend.TripServlet;
import org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class MyServletTest {
    private TripServlet servlet;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void setUp() {
        servlet = new CreateTripServlet();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void userIdNotFound() throws ServletException, IOException {
        request.addParameter("userID", "12345");

        servlet.doGet(request, response);
        assertEquals("application/json;", response.getContentType());

    }
}