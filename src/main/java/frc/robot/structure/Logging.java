package frc.robot.structure;

public class Logging 
{
    public static final int LOG_CAPACITY = 100;

    private static String[] logs = new String[LOG_CAPACITY];
    private static int currentLog = 0;

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

            logs[LOG_CAPACITY] = s;
        }
    }

    public static String logString()
    {
        var s = new StringBuilder();

        for(var line : logs)
        {
            if(line == null) break;
            s.append(line).append("\n\r");
        }

        return s.toString();
    }

    public static void Info(String message)
    {
        var s = "[INFO]: " + message;

        System.out.println(ConsoleColors.WHITE_BRIGHT + s + ConsoleColors.RESET);
        addLog(s);
    }
    public static void Warning(String message)
    {
        var s = "[WARNING]: " + message;

        System.out.println(ConsoleColors.YELLOW_UNDERLINED + s + ConsoleColors.RESET);
        addLog(s);
    }
    public static void Error(String message)
    {
        var s = "[ERROR]: " + message;

        System.out.println(ConsoleColors.RED_UNDERLINED + s + ConsoleColors.RESET);
        addLog(s);
    }
}
