package com.example.krycodetest;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class RouteHandler {

  private Router router;
  private ConnectDB connectDB;
  private HashMap<Integer, Service> services;

  public RouteHandler(Router router, ConnectDB connectDB, HashMap<Integer, Service> services) {

    this.router = router;
    this.connectDB = connectDB;
    this.services = services;

  }

  public void setRoutes() {

    router.route("/*").handler(StaticHandler.create());

    router.get("/api/service").handler(this::getServices);

    router.post("/api/service").handler(this::postService);

    router.delete("/api/service").handler(this::deleteService);

  }

  private void getServices(RoutingContext routingContext) {

    // Get data from database
    Future<List<JsonArray>> listFuture = connectDB.getAllData();

    // Send the data to requested service
    listFuture.onComplete(handler -> {

      List<JsonArray> data = listFuture.result();

      services.clear();

      for(int i = 0; i < data.size();i++) {
        JsonArray data1 = data.get(i);
        Object id = data1.getInteger(0);
        Object name = data1.getString(1);
        Object url = data1.getString(2);
        Object updatedDateTime = data1.getString(3);
        Object statusResponse = data1.getString(4);

        // Store the data in Service object
        Service service = new Service(name.toString(),url.toString(),updatedDateTime.toString(),statusResponse.toString());
        services.put(Integer.valueOf(id.toString()), service);

      }

      List<JsonObject> jsonServices = services
        .entrySet()
        .stream()
        .map(service ->
          new JsonObject()
            .put("id", service.getKey())
            .put("name", service.getValue().getName())
            .put("url", service.getValue().getUrl())
            .put("dateTime", service.getValue().getUpdatedDateTime())
            .put("status", service.getValue().getStatusResponse()))
        .collect(Collectors.toList());

      routingContext.response()
        .putHeader("content-type", "application/json")
        .end(new JsonArray(jsonServices).encode());

    });

  }

  private void postService(RoutingContext routingContext) {

    JsonObject jsonObject = routingContext.getBodyAsJson();

    // Get the post request data
    String name = jsonObject.getString("serviceName");
    String url = jsonObject.getString("url");
    String statusResponse = "UNKNOWN"; // Initially status unknown

    // Get current date as String
    DateTime dateTime = new DateTime();
    String currDateTime = dateTime.getCurrDateTime();

    // Store all data in the service object
    Service service = new Service(name, url, currDateTime, statusResponse);

    connectDB.insertData(service);
    routingContext.response()
      .putHeader("content-type", "text/plain")
      .end("OK");

  }

  private void deleteService(RoutingContext routingContext) {

    JsonObject jsonObject = routingContext.getBodyAsJson();

    // Get id
    String idStr = jsonObject.getString("id");
    int id = Integer.parseInt(idStr);

    connectDB.deleteData(id);
    routingContext.response()
      .putHeader("content-type", "text/plain")
      .end("OK");

  }

}
