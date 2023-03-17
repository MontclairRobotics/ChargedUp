package org.team555.util.frc;

import java.time.Instant;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.DriverStation;
import org.team555.util.StackTrace555;

/**
 * Handle general logging for the robot.
 */
public class Logging 
{
    /**
     * The amount of logs that can be reported by {@link #allLogs()} into a single message.
     */
    public static final int LOG_CAPACITY = 100;

    private static String[] logs = new String[LOG_CAPACITY];
    private static int currentLog = 0;

    /**
     * Add a log to the list to be reported from.
     */
    private static void addLog(String s)
    {
        s += " @" + Instant.now().toEpochMilli();

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
    public static String allLogs()
    {
        StringBuilder s = new StringBuilder();

        for(String line : logs)
        {
            if(line == null) break;
            s.append(line).append("\n\r");
        }

        return s.toString();
    }
    public static String[] allLogsArr()
    {
        ArrayList<String> list = new ArrayList<String>();

        for(String line : logs)
        {
            if(line == null) break;
            list.add(line);
        }

        if (list.size() == 0) list.add("<none here!>");
        return list.toArray(String[]::new);
    }
    public static String mostRecentLog()
    {
        int idx = currentLog-1 >=0 && currentLog-1 < LOG_CAPACITY ? currentLog-1 : 0;
        return logs[idx] == null ? "" : logs[idx];
    }

    /**
     * Print and store an informational log.
     */
    public static void info(Object message)
    {
        String s = message.toString();

        System.out.println(s);
        addLog(s);
    }
    
    /**
     * Print and store a warning log.
     */
    public static void warning(Object message)
    {
        String s = message.toString();
        StackTraceElement[] trace = StackTrace555.trace(1);

        DriverStation.reportWarning(s, trace);
        addLog(s);
    }
    /**
     * Print and store a warning log without any trace.
     */
    public static void warningNoTrace(Object message)
    {
        String s = message.toString();

        DriverStation.reportWarning(s, false);
        addLog(s);
    }
    
    /**
     * Print and store an erroneous log.
     */
    public static void error(Object message)
    {
        String s = message.toString();
        StackTraceElement[] trace = StackTrace555.trace(1);

        DriverStation.reportError(s, trace);
        addLog(s);
    }
    /**
     * Print and store an erroneous log without any trace.
     */
    public static void errorNoTrace(Object message)
    {
        String s = message.toString();

        DriverStation.reportError(s, false);
        addLog(s);
    }
    
    /**
     * Print and store a fatal log.
     */
    public static void fatal(Object message)
    {
        String s = message.toString();
        StackTraceElement[] trace = StackTrace555.trace(1);

        DriverStation.reportError(s, trace);
        addLog(s);
    }
    /**
     * Print and store a fatal log without any trace.
     */
    public static void fatalNoTrace(Object message)
    {
        String s = message.toString();

        DriverStation.reportError(s, false);
        addLog(s);
    }
}
