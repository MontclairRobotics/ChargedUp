package org.team555.constants;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.I2C;

public class GrabberConstants
{
    private GrabberConstants() {}

    public static final I2C.Port COLOR_SENSOR_PORT = I2C.Port.kMXP;

    public static final boolean SOLENOID_DEFAULT_STATE = true;
    public static final boolean PSI_SOLENOID_DEFAULT_STATE = false;

    public static final double GRABBER_HEIGHT_NO_OBJECT = Units.feetToMeters(19.0/12);
    public static final double GRABBER_OBJECT_DANGLE = Units.feetToMeters(5.0/12);

    public static final double GRABBER_HEIGHT_WITH_OBJECT = GRABBER_HEIGHT_NO_OBJECT + GRABBER_OBJECT_DANGLE;
}