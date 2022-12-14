package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class DisabledActionCommand extends CommandBase 
{
    private Runnable run;

    private DisabledActionCommand(Runnable run)
    {
        this.run = run;
    }

    @Override
    public boolean runsWhenDisabled() {return true;}

    @Override
    public void execute() 
    {
        run.run();
    }

    @Override
    public boolean isFinished() {return true;}

    public static DisabledActionCommand of(Runnable run) {return new DisabledActionCommand(run);}
}
