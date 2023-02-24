package frc.robot.components.managers;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SynchronousInterrupt;
import frc.robot.Constants.*;
import frc.robot.util.frc.commandrobot.ManagerBase;
import frc.robot.vision.VisionSystem;

import java.io.IOException;
import java.util.Optional;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;

public class Photon extends ManagerBase implements VisionSystem
{
    PhotonCamera photonCamera = new PhotonCamera(Robot.PhotonVision.CAMERA_NAME);
    PhotonPoseEstimator photonPoseEstimator;

    EstimatedRobotPose lastPose;
    PhotonPipelineResult lastResult;

    public Photon() 
    {
        // Change the name of your camera here to whatever it is in the PhotonVision UI.
        try 
        {
            // Attempt to load the AprilTagFieldLayout that will tell us where the tags are on the field.
            AprilTagFieldLayout fieldLayout = AprilTagFieldLayout.loadFromResource(AprilTagFields.k2023ChargedUp.m_resourceFile);
            
            // Create pose estimator
            photonPoseEstimator = new PhotonPoseEstimator(
                fieldLayout, 
                PoseStrategy.MULTI_TAG_PNP, 
                photonCamera, 
                Robot.PhotonVision.ROBOT_TO_CAM
            );

            photonPoseEstimator.setMultiTagFallbackStrategy(PoseStrategy.LOWEST_AMBIGUITY);
        } 
        catch (IOException e) 
        {
            // The AprilTagFieldLayout failed to load. We won't be able to estimate poses if we don't know
            // where the tags are.
            DriverStation.reportError("Failed to load AprilTagFieldLayout", e.getStackTrace());
            photonPoseEstimator = null;
        }
    }

    public void updateEstimatedPose(Pose2d prev) 
    {
        if (photonPoseEstimator == null) {return;}

        photonPoseEstimator.setReferencePose(prev);
        lastPose = photonPoseEstimator.update().orElseThrow();
    }

    public Pose2d getEstimatedPose()
    {
        return lastPose.estimatedPose.toPose2d();
    }
    public double getTimestampSeconds()
    {
        return lastPose.timestampSeconds;
    }

    public boolean hasObject()
    {
        return lastResult.hasTargets();
    }
    public double getObjectAX() 
    {
        return lastResult.getBestTarget().getPitch();
    }
    @Override
    public double getObjectAY()
    {
        return lastResult.getBestTarget().getYaw();
    }
    @Override
    public double getPipeline()
    {
        return photonCamera.getPipelineIndex();
    }
    @Override
    public void setPipeline(double pipeline)
    {
        photonCamera.setPipelineIndex((int) pipeline);
    }

    @Override
    public void always() 
    {
        lastResult = photonCamera.getLatestResult();
    }

    @Override
    public DetectionType getCurrentType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DetectionType getTargetType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTargetType(DetectionType type) {
        // TODO Auto-generated method stub
        
    }
}
