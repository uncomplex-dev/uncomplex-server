
package dev.uncomplex.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;


/**
 *
 * @author jthorpe
 */
public class RouterTest {
    
    
    public RouterTest() {
    }


    /**
     * Test of build and find routes
     */
    @Test
    public void testRoutes() {
        Router.registerPublicRoute("/*", (r, s) -> {});
        Router.registerPublicRoute("/abc", (r, s) -> {});
        Router.registerPublicRoute("/abc*", (r, s) -> {});
        Router.registerPublicRoute("/abcd", (r, s) -> {});
        assertEquals(Router.findRoute("/"), null);
        Router.registerPublicRoute("*", (r, s) -> {});
        assertEquals(Router.findRoute("/").route(), "*");
        assertEquals(Router.findRoute("/xyz").route(), "/*");
        assertEquals( Router.findRoute("/abc").route(), "/abc");
        assertEquals( Router.findRoute("/abd").route(), "/*");
        assertEquals( Router.findRoute("/abcd").route(), "/abcd");
        assertEquals( Router.findRoute("/abce").route(), "/abc*");
        assertEquals( Router.findRoute("/abcde").route(), "/abc*");
    }


}
