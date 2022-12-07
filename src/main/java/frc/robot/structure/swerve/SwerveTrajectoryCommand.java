package frc.robot.structure.swerve;

import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

import static frc.robot.ChargedUp.*;

public class SwerveTrajectoryCommand extends CommandBase
{
    public final Trajectory trajectory;
    private Timer timer;

    public SwerveTrajectoryCommand(Trajectory trajectory)
    {
        this.trajectory = trajectory;
    }

    @Override public void initialize() 
    {
        timer = new Timer();
    }

    @Override public void execute() 
    {
        var time = timer.get();

        var pose = drivetrain.odometry.getPoseMeters();
        var target = trajectory.sample(time);

        var speeds = drivetrain.driveController.calculate(pose, target, target.poseMeters.getRotation());

        drivetrain.driveFromChassisSpeeds(speeds);
    }

    @Override public void end(boolean interrupted) 
    {
        timer.stop();
    }

    @Override public boolean isFinished() 
    {
        return timer.hasElapsed(trajectory.getTotalTimeSeconds());
    }
}
