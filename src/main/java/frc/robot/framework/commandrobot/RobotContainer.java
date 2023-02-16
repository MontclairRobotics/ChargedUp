package frc.robot.framework.commandrobot;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public abstract class RobotContainer 
{
    public abstract void initialize();
    public void reset() {}

    public Command getAuto() {return Commands.none();}
}
