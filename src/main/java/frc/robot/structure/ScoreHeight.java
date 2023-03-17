package frc.robot.structure;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Commands555;
import frc.robot.ChargedUp;

public enum ScoreHeight 
{
    MID_CONE, MID_CUBE, LOW; 

    public Command getPositioner()
    {
        if(this == MID_CONE) return Commands.sequence(
            Commands555.elevatorToConeMid(),
            Commands555.toggleStinger()
        );
        if(this == MID_CUBE) return Commands.sequence(
            Commands555.elevatorToCubeMid(),
            Commands555.toggleStinger()
        );
        if(this == LOW)  return Commands.sequence(
            Commands555.elevatorToLow(),
            Commands555.toggleStinger()
        );
        
        return Commands.none();     
    }
}
