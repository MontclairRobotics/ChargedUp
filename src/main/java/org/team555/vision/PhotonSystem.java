package org.team555.vision;

import edu.wpi.first.math.geometry.Pose2d;
import org.team555.constants.PhotonConstants;
import org.team555.structure.DetectionType;
import edu.wpi.first.wpilibj.Timer;

import java.util.function.Supplier;

import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.targeting.PhotonPipelineResult;

public class PhotonSystem extends VisionSystem
{
    PhotonCamera photonCamera = new PhotonCamera(PhotonConstants.CAMERA_NAME);
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
        // // Change the name of your camera here to whatever it is in the PhotonVision UI.
        // try 
        // {
        //     // Attempt to load the AprilTagFieldLayout that will tell us where the tags are on the field.
        //     AprilTagFieldLayout fieldLayout = AprilTagFieldLayout.loadFromResource(AprilTagFields.k2023ChargedUp.m_resourceFile);
            
        //     // Create pose estimator
        //     // photonPoseEstimator = new PhotonPoseEstimator(
        //     //     fieldLayout, 
        //     //     PoseStrategy.MULTI_TAG_PNP, 
        //     //     photonCamera, 
        //     //     PhotonConstants.ROBOT_TO_CAM
        //     // );

        //     // photonPoseEstimator.setMultiTagFallbackStrategy(PoseStrategy.LOWEST_AMBIGUITY);
        // } 
        // catch (IOException e) 
        // {
        //     // The AprilTagFieldLayout failed to load. We won't be able to estimate poses if we don't know
        //     // where the tags are.
        //     DriverStation.reportError("Failed to load AprilTagFieldLayout", e.getStackTrace());
        //     photonPoseEstimator = null;
        // }
    }

    public void updateEstimatedPose(Pose2d prev) 
    {
        if (photonPoseEstimator == null) {return;}

        photonPoseEstimator.setReferencePose(prev);
        lastPose = photonPoseEstimator.update().orElse(null);
    }

    public Pose2d getEstimatedPose()
    {
        if (lastPose == null) return new Pose2d();
        return lastPose.estimatedPose.toPose2d();
    }
    public double getTimestampSeconds()
    {
        if (lastPose == null) return Timer.getFPGATimestamp();
        return lastPose.timestampSeconds;
    }

    public boolean hasObject()
    {
        if (lastResult == null) return false;
        return lastResult.hasTargets();
    }
    public double getObjectAX() 
    {
        if (lastResult == null) return 0;
        return lastResult.getBestTarget().getPitch();
    }

    public void resetPose(Pose2d pose)
    {
        if (photonPoseEstimator == null) return;
        photonPoseEstimator.setLastPose(pose);
    }

    public String getCameraStreamURL()
    {
        return PhotonConstants.PHOTON_URL;
    }

    @Override
    public double getObjectAY()
    {
        if (lastResult == null) return 0;
        return lastResult.getBestTarget().getYaw();
    }
    @Override
    public double getPipeline()
    {
        if (photonCamera == null) return 0;
        return photonCamera.getPipelineIndex();
    }
    @Override
    public void setPipeline(double pipeline)
    {
        if (photonCamera == null) return;
        photonCamera.setPipelineIndex((int) pipeline);
    }

    @Override
    public boolean currentPipelineMatches(DetectionType type)
    {
        int pipe = (int) getPipeline();

        if      (type == DetectionType.CUBE)      return (pipe == CUBE_PIPE);
        else if (type == DetectionType.CONE)      return (pipe == CONE_PIPE);
        else if (type == DetectionType.TAPE)      return (pipe == TAPE_PIPE);
        else if (type == DetectionType.APRIL_TAG) return (pipe == APRIL_TAG_PIPE);

        else return false;
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
