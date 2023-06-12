package dev.uncomplex.server.handlers;

import dev.uncomplex.server.HttpConst;
import dev.uncomplex.server.Request;
import dev.uncomplex.server.Response;
import dev.uncomplex.server.RouteHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Copy the file corresponding to the URL to response stream.
 *
 * The FileHandler will look for the file under the /root directory relative the
 * application. If the file is not found the FileHandler will look for a file
 * resource within the * .jar file.
 *
 * This provides a choice of bundling the file resources or deploying them
 * alongside the .jar file. It also provides the ability to live "patch"
 * individual bundled files without a new deployment.
 *
 * @author jthorpe
 */
public class FileHandler implements RouteHandler {

    @Override
    public void handle(Request request, Response response) throws IOException {
        var path = request.getURI().toString();
        if (path.equals("/")) {
            response.send(HttpConst.STATUS_NOT_FOUND);
            return;
        }
        var in = getClass().getResourceAsStream(path);
        if (in == null) {
            var f = new File("resources", path);
            if (f.exists()) {
                in = new FileInputStream(f);
            }
        }
        if (in == null) {
            response.send(HttpConst.STATUS_NOT_FOUND);
            return;
        }
        try {
            in.transferTo(response.getBody());
            response.send(HttpConst.STATUS_OK);
        } finally {
            in.close();
        }
    }

}
