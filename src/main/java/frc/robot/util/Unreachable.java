package frc.robot.util;

public class Unreachable extends Error 
{
    public static <T> T here()
    {
        throw new Unreachable();
    }
}