package frc.robot.structure;

public class Logging 
{
    public static void Info(String message)
    {
        System.out.println(ConsoleColors.WHITE_BRIGHT + "[INFO]: " + message + ConsoleColors.RESET);
    }
    public static void Warning(String message)
    {
        System.out.println(ConsoleColors.YELLOW_UNDERLINED + "[WARNING]: " + message + ConsoleColors.RESET);
    }
    public static void Error(String message)
    {
        System.out.println(ConsoleColors.RED_UNDERLINED + "[ERROR]: " + message + ConsoleColors.RESET);
    }
}
