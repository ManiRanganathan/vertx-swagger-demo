package com.mani.experiment;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.Status;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.ext.healthchecks.HealthCheckHandler;
import io.vertx.rxjava3.ext.healthchecks.HealthChecks;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.ext.web.handler.StaticHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    



    Router router = Router.router(vertx);
    controller(router);
    
    vertx.createHttpServer()
        .requestHandler(router)
        .rxListen(8080).subscribe(server -> {
          log.info("Server started listening at : {}", server.actualPort());
          startPromise.complete();
        }, err -> {
          log.error("Server couldn't start: {}", err.getMessage(), err);
          startPromise.fail(err);
        });
  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    
    stopPromise.complete();
  }


  public void controller(Router router) {
    
    router.route().handler(this::logRequest);
    router.route("/").handler(this::pingResponse);
    router.route("/v3/api-docs").handler(this::readOpenAPIYamlFile);
    router.route("/v3.1/api-docs").handler(this::readOpenAPI31YamlFile);
    router.route("/swagger/*").handler(StaticHandler.create("swagger"));
    router.route("/webjars/*").handler(StaticHandler.create("META-INF/resources/webjars/swagger-ui/4.11.1").setCachingEnabled(false));
    reportHealth(router);
  }

  public void logRequest(RoutingContext ctx) {
      final String address = ctx.request().connection().remoteAddress().toString();
      log.debug("Connected from {}", address);
      ctx.next();
  }

  public void pingResponse(RoutingContext ctx) {
    ctx.response().end(new JsonObject().put("message", "Welcome to Vertx Experiment").encodePrettily());
  }

  public void reportHealth(Router router) {
    HealthCheckHandler healthCheckHandler = HealthCheckHandler
      .createWithHealthChecks(HealthChecks.create(vertx));
    // Status can provide addition data provided as JSON

    healthCheckHandler.register("my-procedure-name", 1000, promise -> {
      promise.complete(Status.OK(new JsonObject().put("available-memory", "2mb")));
    });

    healthCheckHandler.register("my-second-procedure-name", 1000, promise -> {
      promise.complete(Status.KO(new JsonObject().put("load", 99)));
    });
    router.get("/health").handler(healthCheckHandler);
  }

  public void readOpenAPIYamlFile(RoutingContext ctx) {
    vertx.fileSystem().rxReadFile("swagger/apidoc.yaml").subscribe(file -> {
      log.info("Load the file succesfully");
      ctx.request().response().end(file);
    }, e-> {
      log.error("Failed to load a file");
    });
  }

  public void readOpenAPI31YamlFile(RoutingContext ctx) {
    vertx.fileSystem().rxReadFile("swagger/openapi-3.1.yaml").subscribe(file -> {
      log.info("Load the openapi-3.1 file succesfully");
      ctx.request().response().end(file);
    }, e-> {
      log.error("Failed to load a file");
    });
  }

}