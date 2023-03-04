package frc.robot.components.subsystems.stinger;

import com.revrobotics.CANSparkMax;
import com.revrobotics.REVPhysicsSim;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxRelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.simulation.EncoderSim;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismObject2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.ChargedUp;
import frc.robot.constants.StingerConstants;
import frc.robot.constants.Constants;
import frc.robot.constants.Ports;
import frc.robot.constants.SimulationConstants;
import frc.robot.math.Math555;
import frc.robot.util.LazyDouble;
import frc.robot.util.frc.LimitSwitch;
import frc.robot.util.frc.Logging;
import frc.robot.util.frc.PIDMechanism;
import frc.robot.util.frc.SimulationUtility;
import frc.robot.util.frc.commandrobot.ManagerSubsystemBase;

import static frc.robot.constants.StingerConstants.*;

import java.util.function.DoubleSupplier;


public class PneuStinger implements Stinger
{
    private final Solenoid solenoid = new Solenoid(PneumaticsModuleType.REVPH, Ports.STINGER_PNEU_PORT);

    @Override
    public Command in() {return Commands.runOnce(() -> solenoid.set(false)).andThen(Commands.waitSeconds(PNEU_TIME));}
    @Override
    public Command outMid() 
    {
        return Commands.sequence(
            ChargedUp.drivetrain.commands.disableFieldRelative(),
            ChargedUp.drivetrain.xPID.goToSetpoint(
                new LazyDouble(() -> ChargedUp.drivetrain.getRobotPose().getX() - (HIGH_LENGTH - MID_LENGTH)), 
                ChargedUp.drivetrain
            ),
            ChargedUp.drivetrain.commands.enableFieldRelative(),
            Commands.runOnce(() -> solenoid.set(true)),
            Commands.waitSeconds(PNEU_TIME)
            
        );
    }
    @Override
    public Command outHigh() {return Commands.runOnce(() -> solenoid.set(true));}

    @Override
    public boolean isOut() {return solenoid.get();}

    @Override
    public boolean requiresDriveOffset() {return true;}
}