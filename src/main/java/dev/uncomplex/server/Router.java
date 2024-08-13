package dev.uncomplex.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import static java.net.HttpURLConnection.*;

import java.util.concurrent.ConcurrentSkipListMap;

public class Router implements HttpHandler {

    static class Route {

        public RouteHandler handler;
        public char c;
        public ConcurrentSkipListMap<Character, Route> next;
    }

    private final Route routes = new Route();

    /**
     * Add a public route that does not require authentication
     *
     * @param route
     * @param handler
     */
    public void addRoute(String route, RouteHandler handler) {
        DebugLog.log("Router - Adding route: %s", route);
        if (!route.equals("*") && !route.startsWith("/")) {
            route = "/" + route;
        }
        buildRoute(route, handler);
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            var request = new Request(exchange);
            var response = new Response(exchange);
            // handle cors preflight request
            handlePrefightRequest(exchange);
            if (request.getRequestMethod().equals("OPTIONS")) {
                response.send(HttpConst.STATUS_OK);
                return;
            }

            // find route or 404
            DebugLog.log("Finding handler for route: %s", request.getURI().toString());
            var route = getRoute(request);
            if (route == null) {
                DebugLog.log("Unable to find handler: return 404");
                response.sendText(HTTP_NOT_FOUND, "Not found");
                return;
            }

            DebugLog.log("Handler found");
            var handled = route.handler.handle(request, response);
            if (!handled) {
                DebugLog.log("Handler unable to deal with request: return 404");
                response.send(HttpConst.STATUS_NOT_FOUND);
            }

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
     * Remove route
     *
     * @param route
     */
    public void removeRoute(String route) {
        var node = findRoute(route);
        if (node != null) {
            node.handler = null;  // <== findRoute() now returns null
        }
    }

    Route findRoute(String uri) {
        Route wildcardNode = null;
        Route n = routes;
        int i = 0;
        while (i < uri.length() && n != null) {
            // if wildcard match then record data in case we don't find an exact
            // match later and need to backtrack
            var m = (n.next != null) ? n.next.get('*') : null;
            if (m != null) {
                wildcardNode = m;
            }
            // progress to next exact match node
            var c = uri.charAt(i++);
            n = (n.next != null) ? n.next.get(c) : null;
        }
        // return exact match node or wildcard node (which may be null)
        // if handler == null we have a partial uri match only
        return (n != null && n.handler != null) ? n : wildcardNode;
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
        // if origin is specified we have browser calling directly so return a CORS header
        // otherwise this is a system API call which we can ignore
        var origin = exchange.getRequestHeaders().getFirst(HttpConst.ORIGIN);
        if (origin != null) {
            var headers = exchange.getResponseHeaders();
            headers.add(HttpConst.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            headers.add(HttpConst.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            if (exchange.getRequestMethod().equals("OPTIONS")) {
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
            }
        }
    }

    /**
     * Build route tree
     *
     * @param path
     * @param handler
     */
    private void buildRoute(String path, RouteHandler handler) {
        Route route = routes;
        for (int i = 0; i < path.length(); ++i) {
            if (route.next == null) {
                route.next = new ConcurrentSkipListMap<>();
            }
            var c = path.charAt(i);
            route = route.next.computeIfAbsent(c, d -> new Route());
            route.c = c;
        }
        route.handler = handler;
    }

    /**
     * Get RouteData from request.
     *
     * @param request
     * @return
     */
    private Route getRoute(Request request) {
        var uri = request.getURI().toString();
        return findRoute(uri);
    }

}
