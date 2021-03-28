package com.example.krycodetest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.HashMap;

public class MainVerticle extends AbstractVerticle {

  private ConnectDB connector;
  private PeriodicServicePoller poller;
  private RouteHandler routeHandler;
  private HashMap<Integer, Service> services = new HashMap<>();

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    // Connect to database
    connector = new ConnectDB(vertx);

    // Route handling
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create()); // Gather entire request body
    routeHandler = new RouteHandler(router, connector, services);
    routeHandler.setRoutes();

    // Periodic service poller
    poller = new PeriodicServicePoller(vertx, connector,services);
    poller.setPeriodicPoller();

    // Create HttpServer
    vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8080");
      } else {
        startPromise.fail(http.cause());
      }
    });


  }
}
