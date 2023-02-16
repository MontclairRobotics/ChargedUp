package org.team555_deprecated.frc.command;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;

import static edu.wpi.first.wpilibj2.command.CommandGroupBase.*;

public final class Commands 
{
    private Commands() {}

    public static InstantCommand instant(Runnable r, Subsystem... requirements)
    {
        return new InstantCommand(r, requirements);
    }

    public static RunCommand run(Runnable r, Subsystem... requirements)
    {
        return new RunCommand(r, requirements);
    }

    public static RunCommand block(Subsystem... requirements)
    {
        return new RunCommand(() -> {}, requirements);
    }

    public static WaitCommand waitFor(double sec, Subsystem... requirements)
    {
        var wf = new WaitCommand(sec);
        wf.addRequirements(requirements);

        return wf;
    }

    public static WaitUntilCommand waitUntil(BooleanSupplier s, Subsystem... requirements)
    {
        var wu = new WaitUntilCommand(s);
        wu.addRequirements(requirements);

        return wu;
    }

    public static StartEndCommand startEnd(Runnable s, Runnable e, Subsystem... requirements)
    {
        return new StartEndCommand(s, e, requirements);
    }

    public static Command startMidEnd(Runnable s, Runnable m, Runnable e, Subsystem... requirements)
    {
        return sequence(
            instant(s, requirements), run(m, requirements), instant(e, requirements)
        );
    }

    public static PrintCommand print(String s)
    {
        return new PrintCommand(s);
    }

    public static ConditionalCommand conditional(Command a, Command b, BooleanSupplier c)
    {
        return new ConditionalCommand(a, b, c);
    }

    public static Command runForTime(double time, Runnable r, Subsystem... requirements)
    {
        return deadline(
            waitFor(time),
            run(r, requirements)
        );
    }
    public static Command runForTime(double time, Command c)
    {
        return deadline(
            waitFor(time),
            c
        );
    }

    public static Command runUntil(BooleanSupplier s, Runnable r, Subsystem... requirements)
    {
        return deadline(
            waitUntil(s),
            run(r, requirements)
        );
    }
    public static Command runUntil(BooleanSupplier s, Command c)
    {
        return deadline(
            waitUntil(s),
            c
        );
    }

    public static FunctionalCommand functional(Runnable init, Runnable exec, Consumer<Boolean> end, BooleanSupplier finished, Subsystem... requirements)
    {
        return new FunctionalCommand(init, exec, end, finished, requirements);
    }
}
