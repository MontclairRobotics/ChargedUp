package frc.robot.structure.swerve;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.structure.helpers.Logging;

import static frc.robot.ChargedUp.*;

public class SwerveTrajectoryCommand extends CommandBase
{
    public final Trajectory trajectory;
    private Trajectory realTrajectory;
    private Timer timer;

    public SwerveTrajectoryCommand(Trajectory trajectory)
    {
        this.trajectory = trajectory;
    }

    @Override public void initialize() 
    {
        timer = new Timer();
        timer.start();

        realTrajectory = trajectory.transformBy(
            new Transform2d(
                drivetrain.getRobotPose().getTranslation()
                    .minus(trajectory.getInitialPose().getTranslation()), 
                new Rotation2d()
            )
        );

        Logging.info("Trajectory command started");
    }

    @Override public void execute() 
    {
        var time = timer.get();

        var pose = drivetrain.getRobotPose();
        var target = realTrajectory.sample(time);

        var speeds = drivetrain.driveController.calculate(pose, target, target.poseMeters.getRotation());

        drivetrain.driveFromChassisSpeeds(speeds);
    }

    @Override public void end(boolean interrupted) 
    {
        timer.stop();
        Logging.info("Trajectory command ended");
    }

    @Override public boolean isFinished() 
    {
        return timer.hasElapsed(realTrajectory.getTotalTimeSeconds());
    }
}
