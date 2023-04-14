package dev.uncomplex.server;



public class Main {

    public static void main(String[] args) throws Exception {

        // Create a server that listens on port 8080.
        var server = new Server(8180);
        var router = new Router();
        Router.registerPublicRoute("/test", (q, r) -> {});

        server.setHandler(router);
        server.start();
    }
}
