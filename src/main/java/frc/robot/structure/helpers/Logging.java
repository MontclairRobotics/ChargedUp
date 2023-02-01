package frc.robot.structure.helpers;

/**
 * Handle general logging for the robot.
 */
public class Logging 
{
    /**
     * The amount of logs that can be reported by {@link #logString()} into a single message.
     */
    public static final int LOG_CAPACITY = 100;

    private static String[] logs = new String[LOG_CAPACITY];
    private static int currentLog = 0;

    /**
     * Add a log to the list to be reported from.
     */
    private static void addLog(String s)
    {
        if(currentLog < LOG_CAPACITY)
        {
            logs[currentLog] = s;
            currentLog++;
        }
        else
        {
            for(int i = 0; i < LOG_CAPACITY-1; i++)
            {
                logs[i] = logs[i+1];
            }

            logs[LOG_CAPACITY-1] = s;
        }
    }

    /**
     * Get a report of the most recent logs, capped at {@link #LOG_CAPACITY}.
     * Seperates each log by a newline.
     */
    public static String logString()
    {
        StringBuilder s = new StringBuilder();

        for(String line : logs)
        {
            if(line == null) break;
            s.append(line).append("\n\r");
        }

        return s.toString();
    }

    /**
     * Print and store an informational log.
     */
    public static void info(String message)
    {
        String s = "[INFO]: " + message;

        System.out.println(ConsoleColors.WHITE_BRIGHT + s + ConsoleColors.RESET);
        addLog(s);
    }
    
    /**
     * Print and store a warning log.
     */
    public static void warning(String message)
    {
        String s = "[WARNING]: " + message;

        System.out.println(ConsoleColors.YELLOW_UNDERLINED + s + ConsoleColors.RESET);
        addLog(s);
    }
    
    /**
     * Print and store an erroneous log.
     */
    public static void error(String message)
    {
        String s = "[ERROR]: " + message;

        System.out.println(ConsoleColors.RED_UNDERLINED + s + ConsoleColors.RESET);
        addLog(s);
    }
    
    /**
     * Print and store a fatal log.
     */
    public static void fatal(String message)
    {
        String s = "[FATAL]: " + message;

        System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + s + ConsoleColors.RESET);
        addLog(s);
    }
}
