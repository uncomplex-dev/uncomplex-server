package dev.uncomplex.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import static java.net.HttpURLConnection.*;

import java.util.HashMap;
import java.util.logging.Logger;

public class Router implements HttpHandler {

    public interface RouteHandler {

        void handle(Request request, Response response) throws IOException;
    }

    private static final Logger LOG = Logger.getLogger(Router.class.getName());
    private static final HashMap<String, RouteData> routes = new HashMap<>();

    public static void registerPublicRoute(String route, RouteHandler handler) {
        if (!route.startsWith("/")) {
            route = "/" + route;
        }
        routes.put(route, new RouteData(handler, false));
    }

    public static void registerSecureRoute(String route, RouteHandler handler) {
        if (!route.startsWith("/")) {
            route = "/" + route;
        }
        routes.put(route, new RouteData(handler, true));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var request = new Request(exchange);
        var response = new Response(exchange);
        // handle cors preflight request
        if (exchange.getRequestMethod().equals("OPTIONS")) {
            handlePrefightRequest(exchange);
            return;
        }

        // find route or 404
        var target = exchange.getRequestURI().toString();
        var routeData = routes.getOrDefault(target, null);
        if (routeData == null) {
            response.send(HTTP_NOT_FOUND, "Not found");
            return;
        }

        // check authorisation or 401
        if (routeData.secure && !isAuthorised(request)) {
            response.send(HTTP_UNAUTHORIZED, "Forbidden");
            return;
        }

        routeData.handler.handle(request, response);
    }

    /**
     * Validate the Authorization header It is assumed that the header contains
     * a JWT.
     *
     * @param request
     * @return true if the client is authorised for this request
     *
     */
    protected boolean isAuthorised(Request request) {
        return false;
    }

    /**
     * CORS preflight requests will be sent by browsers because of the required
     * Authorization header on secure requests
     *
     * See: https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS
     *
     * @param exchange
     * @throws java.io.IOException
     */
    protected void handlePrefightRequest(HttpExchange exchange) throws IOException {
        var headers = exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", exchange.getRequestHeaders().getFirst("origin"));
        headers.add("Access-Control-Allow-Credentials", "true");
        headers.add("Access-Control-Allow-Methods", "OPTIONS, GET, POST");

        headers.add("Access-Control-Allow-Headers",
                "Origin,"
                + "Accept,"
                + "Accept-Language,"
                + "Content-Language,"
                + "Content-Type,"
                + "Authorization,"
                + "X-Forwarded-Host,"
                + "X-Forwarded-For,"
                + "X-Forwarded-Port,"
                + "X-Forwarded-Proto");
        exchange.sendResponseHeaders(200, 0);
    }

    private static record RouteData(RouteHandler handler, boolean secure) {

    }
}
