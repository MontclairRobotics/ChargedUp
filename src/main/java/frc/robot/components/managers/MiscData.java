package frc.robot.components.managers;

import com.revrobotics.REVPhysicsSim;

import edu.wpi.first.wpilibj.RobotBase;
import frc.robot.util.frc.commandrobot.ManagerBase;

public class MiscData extends ManagerBase
{
    private double lastTime;
    private double fps;

    public double fps() {return fps;}

    @Override
    public void always() 
    {
        if(RobotBase.isSimulation())
        {
            REVPhysicsSim.getInstance().run();
        }

        fps = 1 / (System.currentTimeMillis() / 1000.0 - lastTime);
        lastTime = System.currentTimeMillis() / 1000.0;
    }
}
