package org.team555_deprecated.frc;

import edu.wpi.first.wpilibj.util.Color;
import frc.robot.framework.color.RawColor;

public final class RawColorUtil 
{
    private RawColorUtil() {}

    public static Color wpiFrom(RawColor color)
    {
        var rgb = color.rgb();
        return new Color
        (
            rgb.r / 255.0,
            rgb.b / 255.0,
            rgb.g / 255.0
        );
    }
}
