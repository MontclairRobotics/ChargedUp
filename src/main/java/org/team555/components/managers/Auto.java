package org.team555.components.managers;

import org.team555.util.frc.Logging;
import org.team555.util.frc.commandrobot.ManagerBase;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.EnumSet;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableEvent.Kind;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SuppliedValueWidget;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.FieldObject2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import org.team555.ChargedUp;
import org.team555.Commands555;

/**
 * AutoCommandsManager
 */
public class Auto extends ManagerBase
{
    private static final ShuffleboardTab autoTab = Shuffleboard.getTab("Auto");
    public static ShuffleboardTab getAutoTab() {return autoTab;}

    private final SendableChooser<String> chooseStart;
    private final GenericEntry leaveCommunity;
    private final GenericEntry scoreTwice;
    private final GenericEntry balance;
    private final GenericEntry pointless;
    private final GenericEntry field;
    private final GenericEntry autoStringEntry;
    private final GenericEntry scoresLowForStartEntry;

    private String autoString = "";
    private Pose2d startPose = new Pose2d();
    private final FieldObject2d start;
    
    public Auto()
    {
        chooseStart = new SendableChooser<String>();
        chooseStart.setDefaultOption("Cone 2", "Cone 3"); //TODO: ask drive team the most logical way of naming these
        chooseStart.addOption("Cone 3", "Cone 4");
        chooseStart.addOption("Cone 6", "Cone 6");

        autoTab.add("Starting Position", chooseStart)
            .withPosition(0,0)
            .withSize(2, 1);
        
        leaveCommunity = autoTab.add("Leave Community", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .withPosition(0, 1)
            .withSize(2, 1).getEntry();
        scoreTwice = autoTab.add("Score Twice", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .withPosition(0,2)
            .withSize(2, 1).getEntry();
        balance = autoTab.add("Balance", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .withPosition(0, 3)
            .withSize(2, 1).getEntry();
        pointless = autoTab.add("Pointless", false)
            .withWidget(BuiltInWidgets.kToggleButton)
            .withPosition(8, 0)
            .withSize(2, 1).getEntry();

        autoTab
            .addString("Recent Log", Logging::mostRecentLog)
            .withWidget(BuiltInWidgets.kTextView)
            .withSize(3, 1)
            .withPosition(0, 4);

        
        field = autoStringEntry = autoTab.add("Command String", autoString)
            .withWidget(BuiltInWidgets.kTextView)
            .withPosition(8, 1)
            .withSize(2, 1)
            .getEntry();
        scoresLowForStartEntry = autoTab
            .add("First Score is Cone Mid", false)
            .withWidget(BuiltInWidgets.kBooleanBox)
            .withPosition(8, 2)
            .withSize(2, 1)
            .getEntry();

        // autoTab.add(ChargedUp.getField()).withSize(7, 4).withPosition(2, 0);
        start = ChargedUp.field.getObject("Start");
    }

    private Command command = null;

    public Command get()
    {
        if(command == null)
        {
            Logging.errorNoTrace("Tried to get an auto command when none was created!");
            return Commands.none();
        }

        return command;
    }

    private void updateAutoCommand()
    {
        String str = getAutoString(
            chooseStart.getSelected(), 
            leaveCommunity.getBoolean(false), 
            scoreTwice.getBoolean(false), 
            balance.getBoolean(false)
        );
        
        if      (chooseStart.getSelected().equals("Cone 3")) startPose = new Pose2d(1.85, 3.85, Rotation2d.fromDegrees(180));
        else if (chooseStart.getSelected().equals("Cone 4")) startPose = new Pose2d(1.85, 3.30, Rotation2d.fromDegrees(180));
        else if (chooseStart.getSelected().equals("Cone 6")) startPose = new Pose2d(1.85, 0.45, Rotation2d.fromDegrees(180));
        
        if (DriverStation.getAlliance() == Alliance.Red)
        {
            // startPose = startPose.relativeTo(new Pose2d(16.5, 8, Rotation2d.fromDegrees(180)));
            if      (chooseStart.getSelected().equals("Cone 3")) startPose = new Pose2d(16.5-1.85, 3.85, Rotation2d.fromDegrees(180));
            else if (chooseStart.getSelected().equals("Cone 4")) startPose = new Pose2d(16.5-1.85, 3.30, Rotation2d.fromDegrees(180));
            else if (chooseStart.getSelected().equals("Cone 6")) startPose = new Pose2d(16.5-1.85, 0.45, Rotation2d.fromDegrees(180));
        }
        start.setPose(startPose);

        command = Commands555.buildAuto(str);
        scoresLowForStartEntry.setBoolean(Commands555.firstScoreIsMid(str));

        if(command != null)
        {
            Logging.info("Created the autonomous sequence '" + str + "' successfully!");
        }
        else 
        {
            command = Commands.none();
        }
    }

    String previous = "";

    @Override
    public void always() 
    {
        String current = getAutoString(
            chooseStart.getSelected(), 
            leaveCommunity.getBoolean(false), 
            scoreTwice.getBoolean(false), 
            balance.getBoolean(false)
        );

        if(!current.equals(previous))
        {
            updateAutoCommand();
        }

        previous = current;
        autoString = current;

        pointless.setBoolean(false);
        if (autoString != null) autoStringEntry.setString(autoString);
    }

    //orange juice because i said so. "Cesca is so awesome" - Dylan & Abe (simultaneously)
    

    //// SEQUENCE PARSING ///////

    /**
     * Returns a parseable auto string using inputted parameters retrieved from Shuffleboard
     * Auto string is parsed using {@link #lex}
     * 
     * @param start where the robot starts, 1 for Cone 3, 2 for Cone 4, 3 for Cone 6
     * @param exitComm if the robot exits the community during auto
     * @param scoreTwice if the robot scores twice
     * @param balance if the robot balances
     * @return the parseable auto string
     */
     
    private static String getAutoString(String start, boolean exitComm, boolean scoreTwice, boolean balance)
    {
        String str = "";

        switch(start) 
        {
            case "Cone 3": 
                str += "1";
                if(!exitComm) break; 

                str += "A";
                if(scoreTwice) str += "4";
                break;

            case "Cone 4": 
                str += "2";
                break;

            case "Cone 6": 
                str += "3";
                if(!exitComm) break; 
               
                str += "C";
                if (scoreTwice) str += "5";
                break;

            default:
                Logging.errorNoTrace("Invalid start position: " + start + ", go read a self-help book :)");
                return null;
        } 

        if(balance) str += "B";

        return str;
    }

    /**
     * Lex an autonomous sequence string into its components.
     * Skips over any whitespace characters and appends modifiers 
     * to their bases ("!A" remains conjoined while " A" becomes "A").
     * 
     * @param str The autonomous sequence string
     * @return The components of the path (i.e. '1', 'A', or '!1'), or null if lexing fails
     */
    private static String[] lex(String str)
    {
        if(str.length() == 0)
        {
            Logging.errorNoTrace("Empty auto command provided.");
            return null;
        }

        ArrayList<String> out = new ArrayList<String>();
        boolean isExclaimed = false;

        for(int i = 0; i < str.length(); i++)
        {
            char c = str.charAt(i);

            // Skip whitespace
            if(Character.isWhitespace(c))
            {
                continue;
            }
            // If we have a valid position, add it
            else if(c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == 'A' || c == 'B' || c == 'C' || c == 'a' || c == 'b' || c == 'c')
            {
                c = Character.toUpperCase(c);//test

                if(isExclaimed) out.add("!" + c);
                else            out.add(""  + c);

                isExclaimed = false;
            }
            // Check exclaimed
            else if(c == '!')
            {
                if(isExclaimed)
                {
                    Logging.errorNoTrace("Dual '!' present in command string! '" + str + "'");
                    return null;
                }

                isExclaimed = true;
            }
            // Error otherwise
            else 
            {
                Logging.errorNoTrace("Unexpected character " + c + " in command string! '" + str + "'");
                return null;
            }
        }

        if(isExclaimed)
        {
            Logging.errorNoTrace("Unterminated exclamation expression in command string '" + str + "'");
            return null;
        }

        return out.toArray(String[]::new);
    }
    
    /**
     * Parse an autonomous sequence string into the commands which it will execute.
     * 
     * First, this method lexes the inpur using {@link #lex(String)}, then
     * generates the list of commands which the sequence will compris, including
     * both actions like "A" or "B" and transitions like "AB" and "1C".
     * 
     * Skips any commands attributed with '!'.
     * 
     * @param str The autonomous sequence string
     * @return A list which contains identifiers for the commands which comprise the autonomous routine, 
     * or null if lexing or parsing fails
     */
    public static String[] parse(String str) 
    {
        //hi
        // Lex
        String[] lex = lex(str);

        // Handle errors
        if(lex == null) 
        {
            return null;
        }

        // Parse
        ArrayList<String> output = new ArrayList<String>();

        for (int i = 0; i < lex.length-1; i++)
        {
            String commd = lex[i];
            String trans = lex[i] + lex[i+1];

            if(!commd.contains("!")) 
            {
                output.add(commd);
            }

            output.add(trans.replace("!", ""));
        }

        if(!lex[lex.length - 1].contains("!"))
        {
            output.add(lex[lex.length - 1]);
        }

        return output.toArray(String[]::new);
    }
}