package com.google.tripmeout.frontend.servlet;
 
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import javax.inject.Singleton;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
@Singleton
public class AuthenticationHandlerServlet extends HttpServlet {
   /**
    *
    */
   private static final long serialVersionUID = 1L;
 
 @Override
 public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
   response.setContentType("application/json;");
 
   UserService userService = UserServiceFactory.getUserService();
   if (userService.isUserLoggedIn()) {
     boolean returnInfo = true;
     Gson gson = new Gson();
     String json = gson.toJson(returnInfo);
     response.getWriter().println(json);
   } else {
 
     boolean returnInfo =false;
     Gson gson = new Gson();
     String json = gson.toJson(returnInfo);
     response.getWriter().println(json);
   }
 }
