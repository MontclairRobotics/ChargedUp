package frc.robot.structure;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Commands2023;
import frc.robot.ChargedUp;

public enum ScoreHeight 
{
    MID, LOW; 

    public Command getPositioner()
    {
        if(this == MID) 
        {
            return Commands.sequence(
                Commands.either(Commands2023.elevatorToConeMid(), Commands2023.elevatorToCubeMid(), () -> ChargedUp.grabber.getHoldingCone()),
                Commands2023.toggleGrabber());
        }
        if(this == LOW)  return Commands.sequence(
            Commands2023.elevatorToLow(),
            Commands2023.toggleStinger()
        );
        
        return Commands.none();     
    }
}
