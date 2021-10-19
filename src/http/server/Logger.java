package http.server;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Debug class that helps printing messages in terminal.
 */
public class Logger {
    
    public static final String ANSI_RESET  = "\u001B[0m";

    public static final String ANSI_RED    = "\u001B[31m";
    public static final String ANSI_GREEN  = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE   = "\u001B[34m";
    public static final String ANSI_WHITE  = "\u001B[37m";

    public static final String TAG_ERROR = "ERROR";
    public static final String TAG_WARNING = "WARNING";
    public static final String TAG_DEBUG = "DEBUG";

    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    private static void display(String referer, String message, String color, String tag) {
        Timestamp t = new Timestamp(System.currentTimeMillis());
        System.out.println(color + "[" + tag + "] " + sdf.format(t) + " | " + referer + ": " + message + Logger.ANSI_RESET);
    }

    public static void error(String referer, String message) {display(referer, message, ANSI_RED, TAG_ERROR);}
    public static void warning(String referer, String message) {display(referer, message, ANSI_YELLOW, TAG_WARNING);}
    public static void debug(String referer, String message) {display(referer, message, ANSI_BLUE, TAG_DEBUG);}

}

