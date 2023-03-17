package frc.robot.components.managers;

import java.time.Instant;
import java.util.Random;

import com.revrobotics.REVPhysicsSim;

import edu.wpi.first.math.filter.LinearFilter;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.util.frc.commandrobot.CommandRobot;
import frc.robot.util.frc.commandrobot.ManagerBase;

public class MiscData extends ManagerBase
{
    private LinearFilter fpsFilter = LinearFilter.movingAverage(50);
    private double lastFps;

    public double fps() {return lastFps;}

    @Override
    public void always() 
    {
        if(RobotBase.isSimulation())
        {
            REVPhysicsSim.getInstance().run();
        }

        lastFps = fpsFilter.calculate(1 / CommandRobot.deltaTime());
    }
}
