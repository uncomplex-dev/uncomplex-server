package dev.uncomplex.server.handlers;

import dev.uncomplex.server.Request;
import dev.uncomplex.server.Response;
import dev.uncomplex.server.RouteHandler;
import java.io.IOException;

/**
 * Copy the file corresponding to the URL to response stream.
 *
 * The FileHandler will look for the file under the /root directory relative the
 * application. If the file is not found the FileHandler will look for a file
 * resource within the * .jar file.
 *
 * This provides an easy bundled deployment model with the ability to live
 * "patch" individual files without a new deployment.
 *
 * @author jthorpe
 */
public class FileHandler implements RouteHandler {

    @Override
    public void handle(Request request, Response response) throws IOException {
        
    }

}
