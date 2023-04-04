
package dev.uncomplex.server;

import org.eclipse.jetty.server.Server;


public class Main {
  public static void main(String[] args) throws Exception {

    // Create a server that listens on port 8080.
    Server server = new Server(8080);
    server.setHandler(new ApiHandler());

    // Start the server! 
    server.start();
    System.out.println("Server started!");

    // Keep the main thread alive while the server is running.
    server.join();
  }
}
