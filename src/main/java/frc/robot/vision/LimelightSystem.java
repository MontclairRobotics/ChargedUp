package frc.robot.vision;

import static frc.robot.constants.LimelightConstants.*;
import frc.robot.structure.DetectionType;
import frc.robot.util.Unimplemented;
import frc.robot.util.frc.commandrobot.ManagerBase;
import frc.robot.vision.VisionSystem;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.RobotBase;

public class LimelightSystem extends VisionSystem
{
    private LimelightHelpers.Results results;
    private DetectionType target = DetectionType.NONE;
    
    public static final int CONE_CUBE_PIPE  = 0;
    public static final int TAPE_RETRO_PIPE = 1;
    public static final int APRIL_TAG_PIPE  = 2;
    
    public static final int NONE_ID = 0;
    public static final int CONE_ID = 1;
    public static final int CUBE_ID = 2;


    public LimelightSystem()
    {
        LimelightHelpers.setCameraPose_RobotSpace("", FORWARD_OFFSET, SIDE_OFFSET, UP_OFFSET, 0.0, 0.0, 0.0);
    }

    public String getCameraStreamURL()
    {
        return LIMELIGHT_URL;
    }

    @Override
    public void always() 
    {
        if(RobotBase.isReal())
        {
            results = LimelightHelpers.getLatestResults("");
        }
    }

    @Override
    public void updateEstimatedPose(Pose2d prev) {}

    @Override
    public Pose2d getEstimatedPose() {return hasObject() ? LimelightHelpers.getBotPose2d("") : new Pose2d() /*TODO: this is very stupid*/;}

    @Override
    public double getTimestampSeconds() {return results == null ? -1 : results.targetingResults.timestamp_LIMELIGHT_publish;}

    @Override
    public boolean hasObject() {return LimelightHelpers.getTV("");}

    @Override
    public DetectionType getCurrentType() 
    {
        int pipe = (int) LimelightHelpers.getCurrentPipelineIndex("");
        if      (pipe == TAPE_RETRO_PIPE) return DetectionType.TAPE;
        else if (pipe == APRIL_TAG_PIPE)  return DetectionType.APRIL_TAG;
        else if (LimelightHelpers.getNeuralClassID("") == CUBE_ID) return DetectionType.CUBE;
        else if (LimelightHelpers.getNeuralClassID("") == CONE_ID) return DetectionType.CONE;
        else                                                       return DetectionType.NONE;
    }

    @Override
    public DetectionType getTargetType() {return target;}

    @Override
    public void setTargetType(DetectionType type)
    {
        target = type;

        if     (type == DetectionType.CUBE || type == DetectionType.CONE) setPipeline(CONE_CUBE_PIPE);
        else if(type == DetectionType.TAPE                              ) setPipeline(TAPE_RETRO_PIPE);
        else if(type == DetectionType.APRIL_TAG                         ) setPipeline(APRIL_TAG_PIPE);
    }

    @Override
    public double getObjectAX() {return hasObject() ? 0 : LimelightHelpers.getTX("");}
    @Override
    public double getObjectAY() {return hasObject() ? 0 : LimelightHelpers.getTY("");}
    @Override
    public double getPipeline() {return LimelightHelpers.getCurrentPipelineIndex("");}
    @Override
    public void setPipeline(double value) {LimelightHelpers.setPipelineIndex("", (int)value);}

    @Override
    public void resetPose(Pose2d pose) 
    {}    
}