package frc.robot.components.managers;

import frc.robot.util.frc.Logging;
import frc.robot.util.frc.commandrobot.ManagerBase;

import java.util.ArrayList;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.ChargedUp;
import frc.robot.Commands2023;

/**
 * AutoCommandsManager
 */
public class Auto extends ManagerBase
{
    private GenericEntry subscriber;
    private static final ShuffleboardTab autoTab = Shuffleboard.getTab("Auto");
    public static ShuffleboardTab getAutoTab() {return autoTab;}
    
    public Auto()
    {
        subscriber = ChargedUp.getMainTab()
            .add("Auto Command", "Enter a command here!")
            .withPosition(0, 3)
            .withSize(2, 1)
            .withWidget(BuiltInWidgets.kTextView)
            .getEntry();
        
        SendableChooser<String> chooseStart = new SendableChooser<String>();
        chooseStart.addOption("Left", "Left");
        chooseStart.addOption("Middle", "Middle");
        chooseStart.addOption("Right", "Right");
        autoTab.add("Starting Position", chooseStart)
            .withPosition(0,0)
            .withSize(2, 1);
        
        autoTab.addBoolean("Leave Community", () -> false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .withPosition(0, 1)
            .withSize(2, 1);
        autoTab.addBoolean("Score Twice", () -> false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .withPosition(0,2)
            .withSize(2, 1);
        autoTab.addBoolean("Balance", () -> false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .withPosition(0, 3)
            .withSize(2, 1);
    }

    private String previous = "";
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
        String str = subscriber.getString("");

        command = Commands2023.buildAuto(str);

        if(command != null)
        {
            Logging.info("Created the autonomous sequence '" + str + "' successfully!");
        }
        else 
        {
            command = Commands.none();
        }
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

    //orange juice because i said so. "Cesca is so awesome" - Dylan & Abe (simultaneously)
    

    //// SEQUENCE PARSING ///////

    /**
     * Returns a parseable auto string using inputted parameters retrieved from Shuffleboard
     * Auto string is parsed using {@link #lex}
     * 
     * @param start where the robot starts, 1 for Left, 2 for Middle, 3 for Right
     * @param mobility if the robot moves during auto
     * @param scoreTwice if the robot scores twice
     * @param balance if the robot balances
     * @return the parseable auto string
     */
     
    private static String getAutoString(String start, boolean mobility, boolean scoreTwice, boolean balance)
    {
        String str = ""; 
        start = start.toLowerCase();

        switch(start) 
        {
            case "left": 
                str += "1";
                if(!mobility) return str; 

                str += "A";
                if(scoreTwice) str += "4";
                break;

            case "middle": 
                str += "2";
                if(!mobility) return str; 
                break;

            case "right": 
                str += "3";
                if(!mobility) return str; 
               
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
     * Sequence string is generated using {@link #getAutoString(String, boolean, boolean, boolean)}
     * Skips over any whitespace characters and appends modifiers 
     * to their bases ("!A" remains conjoined while " A" becomes "A").
     * 
     * @return The components of the path (i.e. '1', 'A', or '!1'), or null if lexing fails
     */
    public static String[] lex() 
    {
        //TODO: get values from shuffleboard
        return lexFromString(getAutoString("Left", true, false, true));
    }

    /**
     * Lex an autonomous sequence string into its components.
     * Skips over any whitespace characters and appends modifiers 
     * to their bases ("!A" remains conjoined while " A" becomes "A").
     * 
     * @param str The autonomous sequence string
     * @return The components of the path (i.e. '1', 'A', or '!1'), or null if lexing fails
     */
    public static String[] lex(String str) 
    {
        return lexFromString(str);     
    }

    private static String[] lexFromString(String str)
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
                c = Character.toUpperCase(c);

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