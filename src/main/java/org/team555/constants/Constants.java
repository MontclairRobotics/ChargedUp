package org.team555.constants;

import com.pathplanner.lib.PathConstraints;

import edu.wpi.first.wpilibj.util.Color;
import org.team555.animation2.AnimationReel;
import org.team555.animation2.CircusAnimation;
import org.team555.animation2.FadeTransition;
import org.team555.animation2.MagicAnimation;
import org.team555.animation2.QuickSlowFlash;
import org.team555.animation2.RaceAnimation;
import org.team555.animation2.RainbowAnimation;
import org.team555.animation2.ZoomAnimation;
import org.team555.util.frc.Tunable;



public final class Constants 
{
    private Constants() {}
    
    public static class Auto 
    {
        public static final Tunable<Double> MAX_VEL = Tunable.of(4, "auto.max_vel");
        public static final Tunable<Double> MAX_ACC = Tunable.of(1.8, "auto.max_acc");

        public static PathConstraints constraints()
        {
            return new PathConstraints(
                MAX_VEL.get(), 
                MAX_ACC.get()
            );
        }

        public static final Tunable<Double> DRIVE_TIME_BEFORE_BALANCE = Tunable.of(3.1, "drive.auto_before_balance");
        public static final Tunable<Double> DRIVE_TIME_AFTER_BALANCE_CLIP = Tunable.of(1, "drive.auto_after_balance_clip");
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
            public static final double DEMO_TIME = 10;
            public static final double TRANS_TIME = 1;

            public static final AnimationReel DEMO_ANIMATION = new AnimationReel(DEMO_TIME, TRANS_TIME, new FadeTransition(), 
                MagicAnimation.fire(),
                new CircusAnimation().randomized(),
                new RainbowAnimation().randomized(),
                MagicAnimation.galaxy(),
                new QuickSlowFlash(Color.kAquamarine),
                new RaceAnimation(Color.kIndigo).randomized()
            );
        }
    }
    
    

    public static class Field
    {
        public static final double CHARGE_ANGLE_RANGE_DEG = 15;
        public static final Tunable<Double> CHARGE_ANGLE_DEADBAND = Tunable.of(5, "drive.charge_angle_deadband"); //field is 2.5
    }
}