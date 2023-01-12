package frc.robot;

import org.team555.frc.command.Commands;

import edu.wpi.first.wpilibj2.command.Command;

public class Commands2023 
{
    // put commands here
    public static Command doNothing() 
    {
        return Commands.instant(() -> {});
    }
}
