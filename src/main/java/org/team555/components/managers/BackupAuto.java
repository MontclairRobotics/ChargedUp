package org.team555.components.managers;

import java.util.HashMap;

import org.team555.ChargedUp;
import org.team555.Commands555;
import org.team555.constants.Constants;
import org.team555.util.HashMaps;
import org.team555.util.frc.Logging;
import org.team555.util.frc.Trajectories;
import org.team555.util.frc.commandrobot.ManagerBase;
import static org.team555.Commands555.*;

import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.auto.SwerveAutoBuilder;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.FieldObject2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;

public class BackupAuto extends ManagerBase
{
    public static final ShuffleboardTab autoTab = Shuffleboard.getTab("Backup Auto");
    public ShuffleboardTab getAutoTab() {return autoTab;}

    private final GenericEntry refresh;
    private final GenericEntry commandView;

    private final SendableChooser<String> chooser;

    FieldObject2d position   = ChargedUp.field.getObject("Positition");
    FieldObject2d drawnTrajectory = ChargedUp.field.getObject("Trajectory");

    private CommandBase command;

    public BackupAuto()
    {
        chooser = new SendableChooser<String>();

        chooser.setDefaultOption("Score Cone", "Score Mid Peg");
        chooser.addOption("Score Cube", "Score Mid Shelf");

        chooser.addOption("[WALL] Mobility", "backup.1A");
        chooser.addOption("[WALL] Mobility + Score Cube", "backup.1A4");
        chooser.addOption("[WALL] Mobility + Score Cube + Balance", "backup.1A4B");
        chooser.addOption("[WALL] Mobility + Balance", "backup.1AB");
        chooser.addOption("[WALL] Balance", "backup.1B");


        chooser.addOption("[JUDGE] Mobility", "backup.3C");
        chooser.addOption("[JUDGE] Mobility + Score Cube", "backup.3C5");
        chooser.addOption("[JUDGE] Mobility + Score Cube + Balance", "backup.3C5B");
        chooser.addOption("[JUDGE] Mobility + Balance", "backup.3CB");
        chooser.addOption("[JUDGE] Balance", "backup.3B");

        chooser.addOption("[MIDDLE] Balance", "backup.2B");

        refresh = autoTab.add("Refresh", false)
            .withWidget(BuiltInWidgets.kToggleButton)
            .withSize(1, 1)
            .withPosition(0, 0)
            .getEntry();

        commandView = autoTab.add("View Command", "idk")
            .withWidget(BuiltInWidgets.kTextView)
            .withSize(2, 1)
            .withPosition(1, 0)
            .getEntry();

        autoTab.add("Auto Chooser", chooser)
            .withSize(3, 2)
            .withPosition(0, 1);
    }

    @Override
    public void always() 
    {
        if (refresh.getBoolean(false))
        {
            updateCommand();
        }
    }

    public void updateCommand()
    {
        String selected = chooser.getSelected();
        if (selected == null) return;

        String str = command == null ? "NOTHING YET!!" : command.getName();
        commandView.setString(str);
        
        if (selected.equals("Score Mid Peg"))
        {
            command = scoreMidPeg(true);
            return;
        }   
        if (selected.equals("Score Mid Shelf")) 
        {
            command = scoreMidShelf(true);
            return;
        }

        PathPlannerTrajectory trajectory = Trajectories.get(selected, Constants.Auto.constraints());
        Trajectory drawing = trajectory;
        HashMap<String, Command> markers = HashMaps.of(
            "Score Cube", scoreMidShelf(false),
            "Pick Up", pickup(),
            "Intake Off", Commands.sequence(stopShwooper(), closeGrabber()),
            "Balance", balance()
        );
        SwerveAutoBuilder builder = ChargedUp.drivetrain.commands.autoBuilder(markers);
        
        
        if (DriverStation.getAlliance() == Alliance.Red)
        {
            drawing = drawing.relativeTo(new Pose2d(16.5, 8, Rotation2d.fromDegrees(180)));
        }
        drawnTrajectory.setTrajectory(drawing);

        command = scoreMidPeg(true).andThen(ChargedUp.drivetrain.commands.trajectory(builder, trajectory)).withName(selected);
    }

    public Command get() 
    {
        if(command == null)
        {
            Logging.errorNoTrace("Tried to get an auto command when none was created!");
            return Commands.none();
        }
        return command;
    }
    
}
