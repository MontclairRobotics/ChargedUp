package org.team555.vision;

import static org.team555.constants.LimelightConstants.*;

import java.util.function.Supplier;

import org.team555.structure.DetectionType;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;

public class LimelightSystem extends VisionSystem
{
    private DetectionType target = DetectionType.DEFAULT;
    
    public static final int CONE_CUBE_PIPE  = 0;
    public static final int TAPE_RETRO_PIPE = 1;
    public static final int APRIL_TAG_PIPE  = 2;
    
    public static final int CONE_ID = 0;
    public static final int CUBE_ID = 1;
    public static final int NONE_ID = 2;

    private static Pose2d toPose2D(double[] inData)
    {
        if(inData.length < 6)
        {
            System.err.println("Bad LL 2D Pose Data!");
            return new Pose2d();
        }
        Translation2d tran2d = new Translation2d(inData[0], inData[1]);
        Rotation2d r2d = new Rotation2d(Units.degreesToRadians(inData[5]));
        return new Pose2d(tran2d, r2d);
    }
    
    private NetworkTableEntry getEntry(String name)
    {
        return NetworkTableInstance.getDefault()
            .getTable("limelight")
            .getEntry(name);
    }


    public LimelightSystem()
    {
        getEntry("camerapose_robotspace").setDoubleArray(new double[] {
            FORWARD_OFFSET, 
            SIDE_OFFSET, 
            UP_OFFSET, 
            0.0, 
            0.0, 
            0.0
        });
    }

    public String getCameraStreamURL()
    {
        return LIMELIGHT_URL;
    }

    @Override
    public void always() 
    {
        if(target == DetectionType.DEFAULT)
        {
            setPipelineTo(getDefaultInternal());
        }
    }

    @Override
    public void updateEstimatedPose(Pose2d prev) {}

    @Override
    public Pose2d getEstimatedPose() 
    {
        return hasObject() 
            ? toPose2D(getEntry("botpose").getDoubleArray(new double[6])) 
            : new Pose2d();
    }

    @Override
    public double getTimestampSeconds() 
    {
        return (System.currentTimeMillis() - getEntry("cl").getDouble(0) - getEntry("tl").getDouble(0))/1000.0;
    }

    @Override
    public boolean hasObject() 
    {
        return getEntry("tv").getDouble(0) == 1;
        // return Lime?lightHelpers.getTV("");
    }

    @Override
    public DetectionType getCurrentType()
    {
        if(getEntry("camMode").getDouble(-1) == 0) return DetectionType.NONE;

        int pipe = (int) getPipeline();
        if      (pipe == TAPE_RETRO_PIPE) return DetectionType.TAPE;
        else if (pipe == APRIL_TAG_PIPE)  return DetectionType.APRIL_TAG;
        else if (getEntry("tclass").getDouble(0) == CUBE_ID) return DetectionType.CUBE;
        else if (getEntry("tclass").getDouble(0) == CONE_ID) return DetectionType.CONE;

        return DetectionType.NONE;
    }

    @Override
    public boolean currentPipelineMatches(DetectionType ty)
    {
        if(ty == DetectionType.DEFAULT) return currentPipelineMatches(getDefaultInternal());

        int pipe = (int) getPipeline();

        if     (ty == DetectionType.CUBE || ty == DetectionType.CONE) return (pipe == CONE_CUBE_PIPE);
        else if(ty == DetectionType.TAPE                            ) return (pipe == TAPE_RETRO_PIPE);
        else if(ty == DetectionType.APRIL_TAG                       ) return (pipe == APRIL_TAG_PIPE);

        else return false;
    }

    @Override
    public DetectionType getTargetType() {return target;}

    @Override
    public void setTargetType(DetectionType type)
    {
        target = type;

        if(type == DetectionType.DEFAULT) return;

        setPipelineTo(type);
    }

    private void setPipelineTo(DetectionType type)
    {
        if     (type == DetectionType.CUBE || type == DetectionType.CONE) setPipeline(CONE_CUBE_PIPE);
        else if(type == DetectionType.TAPE                              ) setPipeline(TAPE_RETRO_PIPE);
        else if(type == DetectionType.APRIL_TAG                         ) setPipeline(APRIL_TAG_PIPE);
        
        if(type == DetectionType.NONE) disableProcessing();
        else                           enableProcessing();
    }

    @Override
    public double getObjectAX() 
    {
        return getEntry("tx").getDouble(0);
        // return hasObject() ? 0 : LimelightHelpers.getTX("");
    }

    @Override
    public double getObjectAY() 
    {
        return getEntry("ty").getDouble(0);
        // return hasObject() ? 0 : LimelightHelpers.getTY("");
    }
    @Override
    public double getPipeline() 
    {
        return getEntry("getpipe").getDouble(0);
        // return LimelightHelpers.getCurrentPipelineIndex("");
    }
    @Override
    public void setPipeline(double value) 
    {
        getEntry("pipeline").setDouble(value);
        // LimelightHelpers.setPipelineIndex("", (int)value);
    }

    @Override
    public void resetPose(Pose2d pose) 
    {}    
    
    private void enableProcessing()
    {
        getEntry("camMode").setDouble(1);
    }
    private void disableProcessing()
    {
        getEntry("camMode").setDouble(0);
    }
}