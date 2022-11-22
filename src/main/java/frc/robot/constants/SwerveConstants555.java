package frc.robot.constants;

import org.team555.units.Quantity;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.DriverStation;

import static org.team555.units.StandardUnits.*;

import frc.robot.structure.Logging;
import frc.swervelib.SwerveConstants;


public class SwerveConstants555 
{
    public static void build()
    {
        SwerveConstants.MAX_FWD_REV_SPEED_MPS = Drivebase.MAX_SPEED_MPS;
        SwerveConstants.MAX_STRAFE_SPEED_MPS  = Drivebase.MAX_STRAFE_SPEED_MPS;

        SwerveConstants.MAX_ROTATE_SPEED_RAD_PER_SEC = Drivebase.MAX_TURN_SPEED_RAD_PER_S;
        Logging.Info("maxOmega = " + SwerveConstants.MAX_ROTATE_SPEED_RAD_PER_SEC);

        SwerveConstants.MAX_VOLTAGE = Drivebase.MAX_VOLTAGE_V;

        SwerveConstants.TRACKWIDTH_METERS  = Drivebase.WHEEL_BASE_W_M;
        SwerveConstants.TRACKLENGTH_METERS = Drivebase.WHEEL_BASE_H_M;

        SwerveConstants.MASS_kg  = RobotSpec.ROBOT_MASS_KG;
        SwerveConstants.MOI_KGM2 = RobotSpec.ROBOT_MOMENT_KG_M2;
        
        Logging.Info("mass =   " + SwerveConstants.MASS_kg);
        Logging.Info("moment = " + SwerveConstants.MOI_KGM2);

        SwerveConstants.DFLT_START_POSE = new Pose2d(5,2.5,new Rotation2d(0));

        SwerveConstants.KINEMATICS = new SwerveDriveKinematics(
            new Translation2d( SwerveConstants.TRACKWIDTH_METERS/2, -SwerveConstants.TRACKLENGTH_METERS/2), //FL
            new Translation2d( SwerveConstants.TRACKWIDTH_METERS/2,  SwerveConstants.TRACKLENGTH_METERS/2), //FR
            new Translation2d(-SwerveConstants.TRACKWIDTH_METERS/2, -SwerveConstants.TRACKLENGTH_METERS/2), //BL
            new Translation2d(-SwerveConstants.TRACKWIDTH_METERS/2,  SwerveConstants.TRACKLENGTH_METERS/2)  //BR
        );
        
        SwerveConstants.TRAJECTORYXkP = 0;
        SwerveConstants.TRAJECTORYYkP = 0;

        SwerveConstants.XPIDCONTROLLER = new PIDController(SwerveConstants.TRAJECTORYXkP, 0, 0);
        SwerveConstants.YPIDCONTROLLER = new PIDController(SwerveConstants.TRAJECTORYYkP, 0, 0);

        SwerveConstants.THETACONTROLLERCONSTRAINTS = new TrapezoidProfile.Constraints(SwerveConstants.MAX_ROTATE_SPEED_RAD_PER_SEC, Math.PI/2);
        SwerveConstants.THETACONTROLLERkP = 0;
    }
}
