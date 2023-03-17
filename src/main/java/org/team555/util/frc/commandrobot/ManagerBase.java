package org.team555.util.frc.commandrobot;

public abstract class ManagerBase implements Manager
{
    public ManagerBase()
    {
        CommandRobot.registerManager(this);
    }

    public void reset() {}
    public void initialize() {}
}
