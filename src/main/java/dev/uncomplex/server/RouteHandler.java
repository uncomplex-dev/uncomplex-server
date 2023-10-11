package dev.uncomplex.server;

import java.io.IOException;

/**
 *
 * @author jthorpe
 */
public interface RouteHandler {
    boolean handle(Request request, Response response) throws IOException;
}
