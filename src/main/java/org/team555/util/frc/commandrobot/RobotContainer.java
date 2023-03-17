package org.team555.util.frc.commandrobot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public abstract class RobotContainer 
{
    public abstract void initialize();
    public void reset() {}

    public Command getAuto() {return Commands.none();}
}
