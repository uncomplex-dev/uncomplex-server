package dev.uncomplex.server;

public class DebugLog {
    public static void log(String format, Object... params) {
        if (System.getProperties().containsKey("DEBUG_LOGGING")) {
            System.out.printf(format, params);
            System.out.println();
        }
    }

    public static void log(Throwable exception) {
        if (System.getProperties().containsKey("DEBUG_LOGGING")) {
            exception.printStackTrace(System.out);
            System.out.println();
        }
    }

}
