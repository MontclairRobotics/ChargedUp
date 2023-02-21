package frc.robot.subsystems.managers;

import frc.robot.framework.commandrobot.ManagerBase;
import frc.robot.structure.helpers.Logging;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Commands2023;

/**
 * AutoCommandsManager
 */
public class Auto extends ManagerBase
{
    private NetworkTableEntry subscriber;
    
    public Auto()
    {
        subscriber = NetworkTableInstance.getDefault()
            .getTable("Main")
            .getEntry("Auto");
        subscriber.setString("");
        // guys i have 1 sub on youtube dot commercial website this is so crazy
    }

    private String previous = "";
    private Command command = null;

    public Command get()
    {
        if(command == null)
        {
            Logging.errornt("Tried to get an auto command when none was created!");
            return Commands.none();
        }

        return command;
    }

    private void updateAutoCommand()
    {
        String str = subscriber.getString("");

        command = Commands2023.buildAuto(str);

        Logging.info("Created the autonomous sequence '" + str + "'");
    }

    @Override
    public void always() 
    {
        String current = subscriber.getString("");

        if(!current.equals(previous))
        {
            updateAutoCommand();
        }

        previous = current;
    }
}