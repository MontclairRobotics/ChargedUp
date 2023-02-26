package frc.robot.components.managers;

import com.revrobotics.REVPhysicsSim;

import edu.wpi.first.wpilibj.RobotBase;
import frc.robot.util.frc.commandrobot.ManagerBase;

public class SimulationHooks extends ManagerBase
{
    @Override
    public void always() 
    {
        if(RobotBase.isSimulation())
        {
            REVPhysicsSim.getInstance().run();
        }
    }
}
