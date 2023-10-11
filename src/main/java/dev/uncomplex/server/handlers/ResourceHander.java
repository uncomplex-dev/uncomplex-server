package dev.uncomplex.server.handlers;

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
 * The Resource will look for the file as as resource within the packaged .jar
 * file
 *
 *
 * @author jthorpe
 */
public class ResourceHander implements RouteHandler {

    private final Map<String, String> pathMap;
    private final String resourcePath;

    /**
     * Resource found as a child of resourcePath
     *
     * @param resourcePath
     */
    public ResourceHander(String resourcePath) {
        pathMap = new HashMap<>();
        this.resourcePath = resourcePath;
    }

    /**
     * Construct a Resource with a path map.The path map allows the remapping of
     * path to a new path. For example '/' -> '/index.html'
     *
     * @param resourcePath
     * @param pathMap
     */
    public ResourceHander(String resourcePath, Map<String, String> pathMap) {
        this.pathMap = pathMap;
        this.resourcePath = resourcePath;
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
        var resPath = String.join("/", resourcePath, path);
        var in = getClass().getResourceAsStream(resPath);
        if (in != null) {
            try (in) {
                in.transferTo(response.getStream());
                response.send(HttpConst.STATUS_OK);
                return true;
            }
        }
        return false;
    }
}
