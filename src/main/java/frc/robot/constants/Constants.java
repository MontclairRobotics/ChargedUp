package frc.robot.constants;

import java.util.function.IntFunction;

import com.pathplanner.lib.auto.PIDConstants;
import com.swervedrivespecialties.swervelib.MotorType;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.animation.Animation;
import frc.robot.animation.CircusAnimation;
import frc.robot.animation.MagicAnimation;
import frc.robot.animation.QuickSlowFlash;
import frc.robot.animation.RaceAnimation;
import frc.robot.animation.RainbowAnimation;
import frc.robot.animation.ZoomAnimation;
import frc.robot.animation.DeathAnimation;
import frc.robot.inputs.JoystickAdjuster;
import frc.robot.math.Units555;
import frc.robot.util.Array555;
import frc.robot.util.frc.GameController;
import frc.robot.util.frc.SwerveModuleSpec;



public final class Constants 
{
    private Constants() {}
    
    public static class Auto 
    {
        public static final double MAX_VEL = 0.5;
        public static final double MAX_ACC = 2; // these numbers have origin inside my head
    }
    public static class Robot 
    {
        public static final boolean CHARGER_STATION_INCLINE_INVERT = false;

        public static class ColorSensing
        {
            public static final Color CONE_COLOR = Color.kYellow;
            public static final Color CUBE_COLOR = Color.kPurple;

            public static final double COLOR_CONFIDENCE = 0.7;
        }

        public static class LED
        {
            public static final double DEMO_TIME = 8;
            public static final Animation[] DEMO_ANIMATIONS = 
            {
                MagicAnimation.fire(DEMO_TIME),
                new CircusAnimation(DEMO_TIME),
                new RainbowAnimation(DEMO_TIME),
                MagicAnimation.galaxy(DEMO_TIME),   
                new ZoomAnimation(DEMO_TIME, Color.kLavender),
                new QuickSlowFlash(DEMO_TIME, Color.kBlue),
                new RaceAnimation(DEMO_TIME, Color.kIndigo)
            };
            public static final Animation[] SHUFFLED_ANIMATIONS = Array555.shuffle(DEMO_ANIMATIONS, Animation[]::new);
        }
    }
    
    

    public static class Field
    {
        public static final double CHARGE_ANGLE_RANGE_DEG = 15;
        public static final double CHARGE_ANGLE_DEADBAND = 2.5;
    }
}
