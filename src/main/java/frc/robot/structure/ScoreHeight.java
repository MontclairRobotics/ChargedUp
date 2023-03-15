package frc.robot.structure;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Commands2023;
import frc.robot.ChargedUp;

public enum ScoreHeight 
{
    MID_CONE, MID_CUBE, LOW; 

    public Command getPositioner()
    {
        if(this == MID_CONE)  return Commands.sequence(
            Commands2023.elevatorToConeMid(),
            Commands2023.toggleStinger()
        );
        if(this == MID_CUBE) return Commands.sequence(
            Commands2023.elevatorToCubeMid(),
            Commands2023.toggleStinger()
        );
        if(this == LOW)  return Commands.sequence(
            Commands2023.elevatorToLow(),
            Commands2023.toggleStinger()
        );
        
        return Commands.none();     
    }
}
