package org.team555.util.frc.commandrobot;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public abstract class ManagerSubsystemBase extends SubsystemBase implements Manager
{
    public ManagerSubsystemBase()
    {
        CommandRobot.registerManager(this);
    }

    public void reset() {}
    public void always() {}
    public void initialize() {}
    public void whenInactive() {}
    public void whenActive() {}

    public static boolean subsystemHasCommand(Subsystem subsystem)
    {
        return CommandScheduler.getInstance().requiring(subsystem) != null;
    }
}
