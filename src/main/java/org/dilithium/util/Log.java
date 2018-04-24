package org.dilithium.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handles logging information.
 */
public class Log
{
    private static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void sendStartupMessage()
    {
        LOGGER.log(Level.INFO, "-----------------------------------------");
        LOGGER.log(Level.INFO, "Dilithium core is starting up: ");
        LOGGER.log(Level.INFO, "-----------------------------------------");
    }

    public static void sendCLIStartupMessage()
    {
        LOGGER.log(Level.INFO, "\n-----------------------------------------");
        LOGGER.log(Level.INFO, "Dilithium cli is starting up: ");
        LOGGER.log(Level.INFO, "-------------------------------------------");
    }

    public static void log(Level level, String message)
    {
        LOGGER.log(level, message);
    }

}
