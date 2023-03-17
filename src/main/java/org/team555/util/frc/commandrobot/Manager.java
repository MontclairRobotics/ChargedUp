package org.team555.util.frc.commandrobot;

public interface Manager 
{
    void always();
    default void reset() {}
    default void initialize() {}
}
