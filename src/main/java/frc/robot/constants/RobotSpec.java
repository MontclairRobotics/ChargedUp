package frc.robot.constants;

import edu.wpi.first.math.util.Units;
import frc.robot.Units555;

public final class RobotSpec 
{
    private RobotSpec() {}
    
    public static final double ROBOT_MASS_KG      = Units.lbsToKilograms(60);
    public static final double ROBOT_MOMENT_KG_M2 = Units555.lbFt2ToKgM2(150);

    public static final double TREAD_STATIC_FRICTION_COEF  = 0.80;
    public static final double TREAD_KINETIC_FRICTION_COEF = 0.65;

    public static final double NORMAL_FORCE_ON_MODULE_N = Units.lbsToKilograms(30) * 9.81 / 4;
}
