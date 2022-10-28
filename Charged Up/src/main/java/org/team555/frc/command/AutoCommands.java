package org.team555.frc.command;

import java.security.InvalidAlgorithmParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * A static class which holds information relating to auto commands.
 * 
 * <p>
 * To use this class, call {@link AutoCommands#add(String, Supplier) AutoCommands.add("name", () -> print("command"))}
 * with a supplier (<a href="https://www.w3schools.com/java/java_lambda.asp">likely a lambda</a>) which returns the command which
 * you want to add to the list of available auto commands, named as is provided.
 * 
 * <p>
 * To retreive an auto command after adding it, call {@link AutoCommands#get(String) AutoCommands.get("name")}.
 * This will return an <b>instance</b> of the added command with the given name, or will throw.
 * 
 * <p>
 * To get a {@link SendableChooser} which represents all of the added commands, use {@link AutoCommands#chooser()}.
 * The default command with which this chooser will be created can be set using {@link AutoCommands#setDefaultCommand(String)}.
 * If this command doesnt exist in the registry of added commands, then an {@link IllegalArgumentException} will be thrown
 * upon calls to chooser().
 * 
 * @author Team 555 (Dylan Rafael)
 * @version 0.5
 * @since 0.5
 */
public final class AutoCommands 
{
    private AutoCommands() {}

    private static final Map<String, Supplier<Command>> commands = new HashMap<>();
    public static Map<String, Supplier<Command>> commands() {return commands;}

    // Settings
    private static String defaultAutoCommand = "Main";

    public static void setDefaultCommand(String value)
    {defaultAutoCommand = value;}
    
    public static void add(String name, Supplier<Command> command)
    {
        commands.put(name, command);
    }
    public static Command get(String name)
    {
        return commands.get(name).get();
    }

    private static SendableChooser<Command> chooserInternal;
    public static SendableChooser<Command> chooser()
    {
        if(chooserInternal != null)
        {
            return chooserInternal;
        }

        var s = new SendableChooser<Command>();

        if(!commands.containsKey(defaultAutoCommand))
        {
            throw new IllegalArgumentException("Default state (" + defaultAutoCommand + ") does not exist!");
        }

        s.setDefaultOption(defaultAutoCommand, get(defaultAutoCommand));

        for(var name : commands.keySet())
        {
            if(name.equals(defaultAutoCommand))
            {
                continue;
            }

            s.addOption(name, get(name));
        }

        chooserInternal = s;

        return s;
    }
}
