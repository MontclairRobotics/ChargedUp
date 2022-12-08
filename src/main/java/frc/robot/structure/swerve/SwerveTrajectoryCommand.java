package frc.robot.structure.swerve;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.structure.SwerveTrajectory;
import frc.robot.structure.helpers.Logging;

import static frc.robot.ChargedUp.*;

public class SwerveTrajectoryCommand extends CommandBase
{
    private final Timer m_timer = new Timer();
  private final Trajectory m_trajectory;
  private final Rotation2d m_desiredRotation;
  private final boolean isAbsolute;

  public SwerveTrajectoryCommand(
      SwerveTrajectory trajectory, boolean isAbsolute) {

        addRequirements(drivetrain);
          
        m_trajectory = trajectory.innerTrajectory;
        m_desiredRotation = trajectory.targetRotation;

        this.isAbsolute = isAbsolute;
  }

  @Override
  public void initialize() {
    m_timer.reset();
    m_timer.start();

    if(isAbsolute)
    {
        drivetrain.setPose(m_trajectory.getInitialPose());
    }
  }

  @Override
  public void execute() {
    double curTime = m_timer.get();
    var desiredState = m_trajectory.sample(curTime);

    var targetChassisSpeeds =
        drivetrain.driveController.calculate(drivetrain.getRobotPose(), desiredState, m_desiredRotation);

    drivetrain.driveFromChassisSpeeds(targetChassisSpeeds);
  }

  @Override
  public void end(boolean interrupted) {
    m_timer.stop();
  }

  @Override
  public boolean isFinished() {
    return m_timer.hasElapsed(m_trajectory.getTotalTimeSeconds());
  }
}
