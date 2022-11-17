package frc.robot.structure;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.team555.math.MathUtils;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import frc.robot.constants.Drivebase;

import static org.team555.units.StandardUnits.*;

public class SwerveModuleSim 
{
    private final SwerveModule module;


    public SwerveModuleSim(SwerveModule module)
    {
        this.module = module;
    }

    public void setState(SwerveModuleState state)
    {
        module.setState(state);
    }

    public void update()
    {
        //TODO: implement this!
    }
}
