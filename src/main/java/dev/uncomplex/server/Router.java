package dev.uncomplex.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import static java.net.HttpURLConnection.*;

import java.util.HashMap;

public class Router implements HttpHandler {

    public interface RouteHandler {

        void handle(Request request, Response response) throws IOException;
    }

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
        try {
            var request = new Request(exchange);
            var response = new Response(exchange);
            // handle cors preflight request
            if (request.getRequestMethod().equals(HttpConst.METHOD_OPTIONS)) {
                handlePrefightRequest(exchange);
                return;
            }

            // find route or 404
            var routeData = getRoute(request);
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
        } finally {
            // consume any residual request data, flush response data and close
            // exchange
            try (exchange) {
                exchange.getRequestBody().transferTo(OutputStream.nullOutputStream());
                exchange.getResponseBody().flush();
            }
        }
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
     * Get RouteData from request URI
     * @param request
     * @return 
     */
    protected RouteData getRoute(Request request) {
        var target = request.getURI().toString();
        return routes.getOrDefault(target, null);
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
        headers.add(HttpConst.ACCESS_CONTROL_ALLOW_ORIGIN, exchange.getRequestHeaders().getFirst(HttpConst.ORIGIN));
        headers.add(HttpConst.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        headers.add(HttpConst.ACCESS_CONTROL_ALLOW_METHODS,
                HttpConst.METHOD_GET + ","
                + HttpConst.METHOD_OPTIONS + ","
                + HttpConst.METHOD_POST + ",");

        headers.add(HttpConst.ACCESS_CONTROL_ALLOW_HEADERS,
                HttpConst.ORIGIN + ","
                + HttpConst.ACCEPT + ","
                + HttpConst.ACCEPT_LANGUAGE + ","
                + HttpConst.CONTENT_LANGUAGE + ","
                + HttpConst.CONTENT_TYPE + ","
                + HttpConst.AUTHORIZATION + ","
                + HttpConst.X_FORWARDED_HOST + ","
                + HttpConst.X_FORWARDED_FOR + ","
                + HttpConst.X_FORWARDED_PORT + ","
                + HttpConst.X_FORWARDED_PROTO);
        exchange.sendResponseHeaders(HttpConst.STATUS_OK, 0);
    }

    private static record RouteData(RouteHandler handler, boolean secure) {

    }
}
