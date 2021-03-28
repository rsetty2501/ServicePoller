package com.example.krycodetest;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.sql.SQLConnection;

public class CreateDBTable {

  private Vertx vertx;

  public CreateDBTable(Vertx vertx){
    this.vertx = vertx;
  }

  public void createTable() {

    ConnectDB connectDb = new ConnectDB(vertx);

    // Get connection to database
    Future<SQLConnection> sqlConnectionFuture = connectDb.getDBConnection();

    sqlConnectionFuture.onComplete(handler -> {
      SQLConnection connection = sqlConnectionFuture.result();

      // Create table with the connection
      connection.query("CREATE TABLE if NOT EXISTS service" +
        "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "name VARCHAR(255) NOT NULL," +
        "url VARCHAR(255) NOT NULL," +
        "updated_date_time VARCHAR(255) NOT NULL," +
        "status_response VARCHAR(255) NOT NULL" +
        " )", connhandler -> {
        if (connhandler.succeeded()) {
          System.out.println("Table created!");
        } else {
          System.out.println("Table creation failed!");
          connhandler.cause();
        }

      });

    });

  }

}
