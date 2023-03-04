package frc.robot.components.subsystems.stinger;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;

public interface Stinger extends Subsystem
{
    // public Command selfDestruct();
    public Command in();
    public Command outMid();
    public Command outHigh();
    
    public boolean isOut();

    public boolean requiresDriveOffset();
    // if pneu, out mid offsets drive
    // if motor, out mid goes to mid
}
