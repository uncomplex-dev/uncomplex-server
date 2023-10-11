package dev.uncomplex.server.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import dev.uncomplex.server.HttpConst;
import dev.uncomplex.server.Request;
import dev.uncomplex.server.Response;
import dev.uncomplex.server.RouteHandler;

/**
 * Copy the file corresponding to the URL to response stream.
 *
 * The FileHandler will look for the file under the directory ./resource. If the
 * file is not found the FileHandler will look for a file resource within the *
 * .jar file.
 *
 * This provides a choice of bundling the file resources or deploying them
 * alongside the .jar file. It also provides the ability to live "patch"
 * individual bundled files without a new deployment.
 *
 * @author jthorpe
 */
public class FileHandler implements RouteHandler {

    private final Map<String, String> pathMap;
    private final String filePath;

    public FileHandler(String filePath) {
        pathMap = new HashMap<>();
        this.filePath = filePath;
    }

    /**
     * Construct a FileHandler with a path map.The path map allows the remapping
     * of path to a new path. For example '/' -> '/index.html'
     *
     * @param filePath
     * @param pathMap
     */
    public FileHandler(String filePath, Map<String, String> pathMap) {
        this.pathMap = pathMap;
        this.filePath = filePath;
    }

    public Map<String, String> getPathMap() {
        return pathMap;
    }

    @Override
    public boolean handle(Request request, Response response) throws IOException {
        var path = request.getURI().toString();
        path = pathMap.getOrDefault(path, path);
        if (path.equals("/")) {
            return false;
        }
        // get requested URI as a file or as a resource if the file is not
        // found
        var f = new File(filePath, path);
        try (var in = new FileInputStream(f)) {
            in.transferTo(response.getStream());
            response.send(HttpConst.STATUS_OK);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
