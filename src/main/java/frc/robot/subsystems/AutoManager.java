package frc.robot.subsystems;

import frc.robot.framework.commandrobot.ManagerBase;
import frc.robot.structure.helpers.Logging;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.DoubleSubscriber;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.PubSubOption;
import edu.wpi.first.networktables.StringSubscriber;
import edu.wpi.first.networktables.StringTopic;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.ChargedUp;
import frc.robot.Commands2023;

/**
 * AutoCommandsManager
 */
public class AutoManager extends ManagerBase
{
    private StringSubscriber subscriber;
    
    public AutoManager()
    {
        StringTopic topic = NetworkTableInstance.getDefault()
            .getTable("Main")
            .getStringTopic("Auto");

        subscriber = topic.subscribe(null); // guys i have 1 sub on youtube dot commercial website this is so crazy
    }

    private String previous = null;
    private Command command = null;

    public Command get()
    {
        if(command == null)
        {
            Logging.error("Tried to get an auto command when none was created!");
            return Commands.none();
        }

        return command;
    }

    private void updateAutoCommand()
    {
        String str = subscriber.get();
        if(str == null)
        {
            Logging.error("Auto command entry was empty!");
            return;
        }

        command = Commands2023.buildAuto(str);

        Logging.info("Created the autonomous sequence '" + str + "'");
    }

    @Override
    public void always() 
    {
        String current = subscriber.get();

        if(!(current == null && previous == null || current.equals(previous)))
        {
            updateAutoCommand();
        }

        previous = current;
    }
}