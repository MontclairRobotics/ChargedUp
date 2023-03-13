package frc.robot.util.frc.commandrobot;

public interface Manager 
{
    void always();
    default void reset() {}
    default void initialize() {}
}
