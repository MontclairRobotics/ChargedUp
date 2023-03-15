package frc.robot.components.subsystems;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.ChargedUp;
import frc.robot.Commands2023;
import frc.robot.constants.ElevatorConstants;
import frc.robot.constants.PneuConstants;
import frc.robot.constants.Ports;
import frc.robot.util.LazyDouble;
import frc.robot.util.frc.commandrobot.ManagerSubsystemBase;

import static frc.robot.constants.StingerConstants.*;


public class Stinger extends ManagerSubsystemBase
{
    private final Solenoid solenoid = new Solenoid(PneuConstants.PH_PORT, PneumaticsModuleType.REVPH, Ports.STINGER_PNEU_PORT);
    private boolean target;

    private Command moveThenOut(double back)
    {
        return Commands.sequence(
            ChargedUp.drivetrain.commands.disableFieldRelative(),
            ChargedUp.drivetrain.xPID.goToSetpoint(
                new LazyDouble(() -> ChargedUp.drivetrain.getRobotPose().getX() - back), 
                ChargedUp.drivetrain
            ),
            ChargedUp.drivetrain.commands.enableFieldRelative(),
            Commands.runOnce(() -> target = true),
            Commands.waitSeconds(PNEU_TIME)
        );
    }

    public Command in() {return Commands.runOnce(() -> target = false).andThen(Commands.waitSeconds(PNEU_TIME));}
    
    public Command outLow() {return moveThenOut(MID_LENGTH - LOW_LENGTH);}
    
    public Command outMid() {return Commands.runOnce(() -> target = true).andThen(Commands.waitSeconds(PNEU_TIME));}
    
    public Command outHigh() {return Commands2023.log("DO NOT");}

    public boolean isOut() {return solenoid.get();}

    public boolean requiresDriveOffset() {return true;}

    @Override
    public void always() 
    {
        // Logging.info("" + target);
        // solenoid.set(target);

        if(target == true && solenoid.get() == false)
        {
            if(ChargedUp.elevator.getHeight() >= ElevatorConstants.BUFFER_SPACE_TO_INTAKE)
            {
                solenoid.set(true);
            }
        }
        else 
        {
            solenoid.set(target);
        }
    }

    @Override
    public void reset() 
    {
        target = false;
    }
}