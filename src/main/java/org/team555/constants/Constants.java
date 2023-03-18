package org.team555.constants;

import com.pathplanner.lib.PathConstraints;

import edu.wpi.first.wpilibj.util.Color;
import org.team555.animation.Animation;
import org.team555.animation.CircusAnimation;
import org.team555.animation.MagicAnimation;
import org.team555.animation.QuickSlowFlash;
import org.team555.animation.RaceAnimation;
import org.team555.animation.RainbowAnimation;
import org.team555.animation.ZoomAnimation;
import org.team555.util.Array555;
import org.team555.util.frc.Tunable;



public final class Constants 
{
    private Constants() {}
    
    public static class Auto 
    {
        public static final Tunable<Double> MAX_VEL = Tunable.of(3, "auto.max_vel");
        public static final Tunable<Double> MAX_ACC = Tunable.of(2.0, "auto.max_acc");

        public static PathConstraints constraints()
        {
            return new PathConstraints(MAX_VEL.get(), MAX_ACC.get());
        }
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
