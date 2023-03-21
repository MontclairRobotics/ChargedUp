package org.team555.components.subsystems;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import org.team555.ChargedUp;
import org.team555.Commands555;
import org.team555.constants.ElevatorConstants;
import org.team555.constants.PneuConstants;
import org.team555.constants.Ports;
import org.team555.util.LazyDouble;
import org.team555.util.frc.commandrobot.ManagerSubsystemBase;

import static org.team555.constants.StingerConstants.*;


public class Stinger extends ManagerSubsystemBase
{
    private final Solenoid solenoid = new Solenoid(PneuConstants.PH_PORT, PneumaticsModuleType.REVPH, Ports.STINGER_PNEU_PORT);
    private boolean target;

    // private Command moveThenOut(double back)
    // {
    //     return Commands.sequence(
    //         ChargedUp.drivetrain.commands.disableFieldRelative(),
    //         ChargedUp.drivetrain.xPID.goToSetpoint(
    //             new LazyDouble(() -> ChargedUp.drivetrain.getRobotPose().getX() - back), 
    //             ChargedUp.drivetrain
    //         ),
    //         ChargedUp.drivetrain.commands.enableFieldRelative(),
    //         Commands.runOnce(() -> target = true),
    //         Commands.waitSeconds(PNEU_TIME)
    //     );
    // }

    public boolean isOut() {return solenoid.get() == SOLENOID_OUT;}

    public void targetOut() {target = SOLENOID_OUT;}
    public void targetIn() {target = !SOLENOID_OUT;}

    @Override
    public void always() 
    {
        // Logging.info("" + target);
        // solenoid.set(target);

        if(target == SOLENOID_OUT && solenoid.get() == !SOLENOID_OUT)
        {
            if(ChargedUp.elevator.getHeight() >= ElevatorConstants.BUFFER_SPACE_TO_INTAKE)
            {
                solenoid.set(SOLENOID_OUT);
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
        target = !SOLENOID_OUT;
    }
}