package org.dilithium.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * This class handles logging information.
 */
public class Log
{
    private static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    
    static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LogFormatter());
        LogManager.getLogManager().reset();
        LOGGER.addHandler(handler);
    }

    public static void sendStartupMessage()
    {
        LOGGER.log(Level.INFO, "\n------------------------------------------\n"
                             + "Dilithium core is starting up: \n"
                             + "-------------------------------------------");
    }

    public static void sendCLIStartupMessage()
    {
        LOGGER.log(Level.INFO, "\n------------------------------------------\n"
                             + "Dilithium cli is starting up: \n"
                             + "-------------------------------------------");
    }

    public static void log(Level level, String message)
    {
        LOGGER.log(level, message);
    }

}
