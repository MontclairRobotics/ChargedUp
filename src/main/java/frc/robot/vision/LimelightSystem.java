package frc.robot.vision;

import frc.robot.util.Unimplemented;
import frc.robot.util.frc.commandrobot.ManagerBase;
import frc.robot.vision.VisionSystem;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class LimelightSystem extends ManagerBase implements VisionSystem
{
    private final LimelightWrapper limelight = new LimelightWrapper();
    private DetectionType target = DetectionType.NONE;

    public static final int CONE_CUBE_PIPE  = 0;
    public static final int TAPE_RETRO_PIPE = 1;
    public static final int APRIL_TAG_PIPE  = 2;
    
    public static final int NONE_ID = 0;
    public static final int CONE_ID = 1;
    public static final int CUBE_ID = 2;

    @Override
    public void always() 
    {
        limelight.update();
    }

    @Override
    public void updateEstimatedPose(Pose2d prev) 
    {
        Unimplemented.here();
    }

    @Override
    public Pose2d getEstimatedPose() {return limelight.getBotpose().toPose2d();}

    @Override
    public double getTimestampSeconds() {return limelight.getTimestamp() / 1000.0;}

    @Override
    public boolean hasObject() {return limelight.getDetected();}

    @Override
    public DetectionType getCurrentType() 
    {
        int pipe = (int) limelight.getPipeline();
        if      (pipe == TAPE_RETRO_PIPE)           return DetectionType.TAPE;
        else if (pipe == APRIL_TAG_PIPE)            return DetectionType.APRIL_TAG;
        
        else if (limelight.getClassID() == CUBE_ID) return DetectionType.CUBE;
        else if (limelight.getClassID() == CONE_ID) return DetectionType.CONE;
        else                                        return DetectionType.NONE;
    }

    @Override
    public DetectionType getTargetType() {return target;}

    @Override
    public void setTargetType(DetectionType type)
    {
        target = type;

        if     (type == DetectionType.CUBE || type == DetectionType.CONE) limelight.setPipeline(CONE_CUBE_PIPE);
        else if(type == DetectionType.TAPE                              ) limelight.setPipeline(TAPE_RETRO_PIPE);
        else if(type == DetectionType.APRIL_TAG                         ) limelight.setPipeline(APRIL_TAG_PIPE);
    }

    @Override
    public double getObjectAX() {return limelight.getX();}
    @Override
    public double getObjectAY() {return limelight.getX();}
    @Override
    public double getPipeline() {return limelight.getPipeline();}
    @Override
    public void setPipeline(double value) {limelight.setPipeline(value);}

    @Override
    public void resetPose(Pose2d pose) {
        limelight.
    }

    
}