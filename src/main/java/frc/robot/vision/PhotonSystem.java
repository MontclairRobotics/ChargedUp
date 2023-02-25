package frc.robot.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SynchronousInterrupt;
import frc.robot.Constants;
import frc.robot.Constants.*;
import frc.robot.structure.DetectionType;
import frc.robot.util.frc.commandrobot.ManagerBase;
import frc.robot.vision.VisionSystem;

import java.io.IOException;
import java.util.Optional;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;

public class PhotonSystem extends ManagerBase implements VisionSystem
{
    PhotonCamera photonCamera = new PhotonCamera(Robot.PhotonVision.CAMERA_NAME);
    PhotonPoseEstimator photonPoseEstimator;

    EstimatedRobotPose lastPose;
    PhotonPipelineResult lastResult;

    private DetectionType target = DetectionType.NONE;
    public static final int CUBE_PIPE = 0;
    public static final int CONE_PIPE = 1;
    public static final int APRIL_TAG_PIPE = 2;
    public static final int TAPE_PIPE = 3;


    public PhotonSystem() 
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

    public void resetPose(Pose2d pose)
    {
        photonPoseEstimator.setLastPose(pose);
    }

    public String getCameraStreamURL()
    {
        return Robot.PhotonVision.PHOTON_URL;
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
    public DetectionType getCurrentType() 
    {
        if(hasObject()) return target;
        return DetectionType.NONE;
    }

    @Override
    public DetectionType getTargetType() 
    {
        return target;
    }

    @Override
    public void setTargetType(DetectionType type) 
    {
        target = type;

        if(type == DetectionType.APRIL_TAG)
        {
            setPipeline(APRIL_TAG_PIPE);
        }
        else if(type == DetectionType.CUBE)
        {
            setPipeline(CUBE_PIPE);
        }
        else if(type == DetectionType.CONE)
        {
            setPipeline(CONE_PIPE);
        }
        else if(type == DetectionType.TAPE)
        {
            setPipeline(TAPE_PIPE);
        }
    }
}
