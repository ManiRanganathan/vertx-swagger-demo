// package com.mani.experiment;

// import io.vertx.core.DeploymentOptions;
// import io.vertx.core.Vertx;
// import lombok.extern.slf4j.Slf4j;

// /**
//  * Hello world!
//  *
//  */

// @Slf4j
// public class App 
// {
//     public static void main( String[] args )
//     {
//         log.info("Starting the Application");
//         Vertx vertx = Vertx.vertx();

//         DeploymentOptions options = new DeploymentOptions();

//         vertx.deployVerticle(ApplicationVerticle.class, options, handler -> {
//             if (handler.failed()) {
//                 log.error("Verticle not deployed", handler.cause());
//                 System.exit(0);
//             }
//             log.info("Verticle deployed successfully: {}", handler.result());
//         });


//     }
// }
