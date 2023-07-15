package dev.uncomplex.server;

/**
 *
 * @author jthorpe
 */
public class HttpConst {

    /*
    Key HTTP response headers that are used by this server or highly likely to
    be required by users.
    see: https://en.wikipedia.org/wiki/List_of_HTTP_header_fields
     */
    public static final String ACCEPT = "Accept";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    public static final String AUTHORIZATION = "Authorization";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String CONTENT_LANGUAGE = "Content-Language";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ORIGIN = "Origin";
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String X_FORWARDED_HOST = "X-Forwarded-Host";
    public static final String X_FORWARDED_PORT = "X-Forwarded-Port";
    public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
    
    /*
    Status codes
    */
    public static final int STATUS_OK = 200;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_NOT_FOUND = 404;
    

    /*
    HTTP Methods
     */
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_TRACE = "TRACE";

    /*
    Standard Content Types with UTF-8 charset
    */
    public static final String CONTENT_TYPE_JSON = "application/json;charset=utf-8";
    public static final String CONTENT_TYPE_XML = "application/xml;charset-utf-8";
    public static final String CONTENT_TYPE_HTML = "text/html;charset=utf-8";
    public static final String CONTENT_TYPE_PLAIN = "text/plain;charset=utf-8";
}
