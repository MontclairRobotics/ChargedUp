package frc.robot.framework.commandrobot;

public abstract class ManagerBase implements Manager
{
    public ManagerBase()
    {
        CommandRobot.registerManager(this);
    }

    public void reset() {}
    public void initialize() {}
}
