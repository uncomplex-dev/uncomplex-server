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
    private final String resourcePath;

    public FileHandler() {
        pathMap = new HashMap<>();
        resourcePath = System.getProperty("user.dir");
    }

    public FileHandler(String resourcePath) {
        pathMap = new HashMap<>();
        this.resourcePath = resourcePath;
    }

    
    /**
     * Construct a FileHandler with a path map. The path map allows the
     * remapping of path to a new path. For example '/' -> '/index.html'
     *
     * @param pathMap
     */
    public FileHandler(Map<String, String> pathMap) {
        this.pathMap = pathMap;
        resourcePath = System.getProperty("user.dir");
    }
    
        /**
     * Construct a FileHandler with a path map.The path map allows the
 remapping of path to a new path. For example '/' -> '/index.html'
     *
     * @param resourcePath
     * @param pathMap
     */
    public FileHandler(String resourcePath, Map<String, String> pathMap) {
        this.pathMap = pathMap;
        this.resourcePath = resourcePath;
    }

    public Map<String, String> getPathMap() {
        return pathMap;
    }

    @Override
    public void handle(Request request, Response response) throws IOException {
        var path = request.getURI().toString();
        path = pathMap.getOrDefault(path, path);
        if (path.equals("/")) {
            response.send(HttpConst.STATUS_NOT_FOUND);
            return;
        }
        // get requested URI as a file or as a resource if the file is not
        // found
        var f = new File(resourcePath, path);
        var in = f.exists()
                ? new FileInputStream(f)
                : getClass().getResourceAsStream(path);
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
