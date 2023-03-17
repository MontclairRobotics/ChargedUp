package org.team555.util.frc;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.math.system.plant.DCMotor;

public class SimulationUtility 
{
    private SimulationUtility() {}

    public static void simulateNEO(CANSparkMax spark, RelativeEncoder encoder)
    {
        encoder.setPosition(
            encoder.getPosition() + spark.get() * 0.02 * encoder.getPositionConversionFactor() * DCMotor.getNEO(1).freeSpeedRadPerSec
        );
    }
}
