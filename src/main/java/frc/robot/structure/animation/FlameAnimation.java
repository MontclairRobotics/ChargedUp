package frc.robot.structure.animation;

import java.util.Random;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import frc.robot.framework.Math555;
import frc.robot.framework.PerlinNoise;

public class FlameAnimation extends Animation
{
    public FlameAnimation(double length) 
    {
        super(length);

        movementDir = new Rotation2d(new Random().nextDouble(Math.PI));
    }

    public static final double WRAP_TIME = 1.2;
    public static final double STEP = 50;
    public static final double SIZE = 70;
    
    private Rotation2d movementDir;
    private PerlinNoise noise = new PerlinNoise();

    @Override
    public void run(AddressableLEDBuffer ledBuffer) 
    {
        Translation2d center = new Translation2d(STEP * timer.get() / WRAP_TIME, movementDir);

        for(int i = 0; i < ledBuffer.getLength(); i++)
        {
            double inorm = (1.0 * i) / ledBuffer.getLength();

            Rotation2d angle = new Rotation2d(2.0 * Math.PI * inorm);

            Translation2d offset = new Translation2d(SIZE, angle);
            Translation2d position = center.plus(offset);

            double t = noise.noise(position.getX(), position.getY()) / 2 + 0.5;

            int hue = Math555.lerp(0, 10,  Math.pow(t, 2.0));
            int val = Math555.lerp(0, 255, Math.pow(t, 4.0));

            ledBuffer.setHSV(i, hue, 255, val);
        }   
    } 
}
