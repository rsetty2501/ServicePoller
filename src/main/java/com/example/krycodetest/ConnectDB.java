package com.example.krycodetest;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

import java.util.List;

public class ConnectDB {

  // data members
  private JDBCClient jdbc;
  String DATABASE_PATH = "service_poller.db";

  public ConnectDB(Vertx vertx) {

    JsonObject config = new JsonObject()
      .put("url", "jdbc:sqlite:" + DATABASE_PATH)
      .put("driver_class", "org.sqlite.JDBC")
      .put("max_pool_size", 50);

    jdbc = JDBCClient.createShared(vertx,config);

  }

  public Future<SQLConnection> getDBConnection() {

    // Create a Future
    return Future.future(futHandler -> jdbc.getConnection(handler -> {
      if (handler.failed()) {
        System.err.println("Did not get the connection...: "
          + handler.cause().getMessage());
      } else {
        System.out.println("Got the connection to DB");

        // Get connection
        SQLConnection connection = handler.result();
        futHandler.complete(connection);

      }

    }));

  }

  public Future<List<JsonArray>> getAllData() {

    String queryStr = "SELECT id, name, url, updated_date_time, status_response FROM service";

    // Query data
    Future<List<JsonArray>> getDataFuture = Future.future(handler -> {

      jdbc.query(queryStr, queryHandler -> {
        if (queryHandler.succeeded()) {
          System.out.println("Query Success");
          ResultSet resultSet = queryHandler.result();
          List<JsonArray> data = resultSet.getResults();

          handler.complete(data);

        } else {
          System.out.println("Query Failed");
          queryHandler.cause();

        }

      });

    });

    return getDataFuture;
  }

  public void insertData(Service service) {

    String queryStr = "INSERT INTO service (name, url, updated_date_time, status_response) VALUES (?, ?, ?, ?)";

    jdbc.queryWithParams(queryStr, new JsonArray().add(service.getName()).add(service.getUrl()).add(service.getUpdatedDateTime()).add(service.getStatusResponse()),handler -> {
      if (handler.succeeded()) {
        System.out.println("Data inserted");
      } else {
        System.out.println("Data insertion failed");
        handler.cause();
      }

    });

  }

  public void deleteData(int id) {

    String queryStr = "DELETE FROM service WHERE id=?";

    jdbc.queryWithParams(queryStr, new JsonArray().add(id), handler -> {
      if (handler.succeeded()) {
        System.out.println("Data deleted");
      } else {
        System.out.println("Data deletion failed");
        handler.cause();
      }

    });

  }


  public void updateData(int id, String dateTime, String status) {

    String queryStr = "UPDATE service SET updated_date_time = ?, status_response = ? WHERE id=?";

    jdbc.queryWithParams(queryStr, new JsonArray().add(dateTime).add(status).add(id), handler -> {
      if (handler.succeeded()) {
        System.out.println("Data updated");
      } else {
        System.out.println("Data updating failed");
        handler.cause();
      }

    });

  }

}
