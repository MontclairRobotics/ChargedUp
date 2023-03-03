package frc.robot.structure;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Commands2023;

public enum ScoreHeight 
{
    HIGH, MID, LOW; 

    public Command getPositioner()
    {
        if(this == HIGH) return Commands2023.elevatorStingerToHigh();
        if(this == MID)  return Commands2023.elevatorStingerToMid();
        if(this == LOW)  return Commands2023.elevatorStingerToLow();
        
        return Commands.none();     
    }
}