
package dev.uncomplex.server;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


/**
 *
 * @author jthorpe
 */
public class RouterTest {
    
    RouteHandler ASTER = (r, s) -> { return true; };
    RouteHandler SLASH_ASTER = (r, s) -> { return true; };
    RouteHandler SLASH_ABC = (r, s) -> { return true; };
    RouteHandler SLASH_ABC_ASTER = (r, s) -> { return true; };
    RouteHandler SLASH_ABCD = (r, s) -> { return true; };
    
    public RouterTest() {
    }


    /**
     * Test of build and find routes
     */
    @Test
    public void testRoutes() {
        Router router = new Router();
        router.addRoute("/*", SLASH_ASTER);
        router.addRoute("/abc", SLASH_ABC);
        router.addRoute("/abc*", SLASH_ABC_ASTER);
        router.addRoute("/abcd", SLASH_ABCD);
        assertNull(router.findRoute("/"));
        router.addRoute("*", ASTER);
        assertEquals(router.findRoute("/").handler, ASTER);  
        assertEquals(router.findRoute("/xyz").handler, SLASH_ASTER); // "/*");
        assertEquals( router.findRoute("/abc").handler, SLASH_ABC);
        assertEquals( router.findRoute("/abd").handler, SLASH_ASTER);
        assertEquals( router.findRoute("/abcd").handler, SLASH_ABCD);
        assertEquals( router.findRoute("/abce").handler, SLASH_ABC_ASTER);
        assertEquals( router.findRoute("/abcde").handler, SLASH_ABC_ASTER);
    }


}
