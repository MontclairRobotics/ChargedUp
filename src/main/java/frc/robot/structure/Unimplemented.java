package frc.robot.structure;

public class Unimplemented extends Error 
{
    public static <T> T here()
    {
        throw new Unimplemented();
    }
}
