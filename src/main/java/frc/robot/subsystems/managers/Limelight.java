package frc.robot.subsystems.managers;

import frc.robot.framework.commandrobot.ManagerBase;
import frc.robot.structure.Unimplemented;
import frc.robot.structure.VisionProvider;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight extends ManagerBase implements VisionProvider
{
    boolean isDetected;
    double x;
    double y;
    double area;
    double timestamp;
    private double pipelineNum = 0;

    private final NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");;
    private final NetworkTableEntry tv = table.getEntry("tv");
    private final NetworkTableEntry tx = table.getEntry("tx");
    private final NetworkTableEntry ty = table.getEntry("ty");
    private final NetworkTableEntry ta = table.getEntry("ta");  
    private final NetworkTableEntry ts = table.getEntry("ts");  
    private final NetworkTableEntry pipeline = table.getEntry("pipeline");
    private final NetworkTableEntry latency = table.getEntry("tl"); // pipeine's latency contribution
    private final NetworkTableEntry tshort = table.getEntry("tshort");
    private final NetworkTableEntry tlong = table.getEntry("tlong");
    private final NetworkTableEntry thor = table.getEntry("thor");
    private final NetworkTableEntry tvert = table.getEntry("tvert");
    private final NetworkTableEntry json = table.getEntry("json");
    private final NetworkTableEntry tclass = table.getEntry("tclass");
    private final NetworkTableEntry LEDMode = table.getEntry("ledMode");
    private final NetworkTableEntry camMode = table.getEntry("camMode");
    private final NetworkTableEntry stream = table.getEntry("stream");
    private final NetworkTableEntry snapshot = table.getEntry("snapshot");
    private final NetworkTableEntry crop = table.getEntry("crop");
    private final NetworkTableEntry botpose = table.getEntry("botpose");
    

    // Getters //
    /**
     * if its detected, return true
     */    
    public boolean getDetected()    {return isDetected;}
    /**
     * Get Horizontal Offset From Crosshair To Target (LL1: -27 degrees to 27 degrees | LL2: -29.8 to 29.8 degrees)
     */
    public double getX()            {return x;}
    /**
     * Get Vertical Offset From Crosshair To Target (LL1: -20.5 degrees to 20.5 degrees | LL2: -24.85 to 24.85 degrees)
     */
    public double getY()            {return y;}
    /**
     * Get Target Area (0% of image to 100% of image)
     */
    public double getArea()         {return area;}
    /**
     * Get the timestamp of the limelight's usage
     */
    public double getTimestamp()    {return timestamp;}
    /**
     * Get True active pipeline index of the camera (0 .. 9)
     */
    public double getPipeline()     {return (int) pipeline.getInteger(0);}
    /**
     * Get The pipeline’s latency contribution (ms) Add at least 11ms for image capture latency.
     */
    public double getLatency()      {return latency .getDouble(0);}
    /**
     * Get Sidelength of shortest side of the fitted bounding box (pixels)
     */
    public double getShort()        {return tshort  .getDouble(0);}
    /**
     * Get Sidelength of longest side of the fitted bounding box (pixels)
     */
    public double getLong()         {return tlong   .getDouble(0);}
    /**
     * Get Horizontal sidelength of the rough bounding box (0 - 320 pixels)
     */
    public double getTHOR()         {return thor    .getDouble(0);}
    /**
     * Get Vertical sidelength of the rough bounding box (0 - 320 pixels)
     */
    public double getVert()         {return tvert   .getDouble(0);}
    /**
     * Get Full JSON dump of targeting results
     */
    public String getJson()         {return json    .getString("");}
    /**
     * Get Class ID of primary neural detector result or neural classifier result
     */
    public double getClassID()      {return tclass  .getDouble(-1.0);}

    /**
    * Robot transform in field-space. Translation (X,Y,Z) Rotation(Roll,Pitch,Yaw)
    */
    public final double[] getBotposeRaw() {return botpose.getDoubleArray((double[])null);}

    /**
    * Gets the pose3d encoded by {@link Limelight#getBotposeRaw}
    */
    public final Pose3d getBotpose()
    {
        double[] items = getBotposeRaw();
        return new Pose3d(items[0], items[1], items[2], new Rotation3d(items[3], items[4], items[5]));
    }

    private final NetworkTableEntry botpose_wpiblue = table.getEntry("botpose_wpiblue");

    /**
    * Robot transform in field-space (blue driverstation WPILIB origin). Translation (X,Y,Z) Rotation(Roll,Pitch,Yaw)
    */
    public final double[] getBotposeWpiblueRaw() {return botpose_wpiblue.getDoubleArray((double[])null);}

    /**
    * Gets the pose3d encoded by {@link Limelight#getBotposeWpiblueRaw}
    */
    public final Pose3d getBotposeWpiblue()
    {
        double[] items = getBotposeWpiblueRaw();
        return new Pose3d(items[0], items[1], items[2], new Rotation3d(items[3], items[4], items[5]));
    }

    private final NetworkTableEntry botpose_wpired = table.getEntry("botpose_wpired");

    /**
    * Robot transform in field-space (red driverstation WPILIB origin). Translation (X,Y,Z) Rotation(Roll,Pitch,Yaw)
    */
    public final double[] getBotposeWpiredRaw() {return botpose_wpired.getDoubleArray((double[])null);}

    /**
    * Gets the pose3d encoded by {@link Limelight#getBotposeWpiredRaw}
    */
    public final Pose3d getBotposeWpired()
    {
        double[] items = getBotposeWpiredRaw();
        return new Pose3d(items[0], items[1], items[2], new Rotation3d(items[3], items[4], items[5]));
    }

    private final NetworkTableEntry camerapose_targetspace = table.getEntry("camerapose_targetspace");

    /**
    * 3D transform of the camera in the coordinate system of the primary in-view AprilTag (array (6))
    */
    public final double[] getCameraposeTargetspaceRaw() {return camerapose_targetspace.getDoubleArray((double[])null);}

    /**
    * Gets the pose3d encoded by {@link Limelight#getCameraposeTargetspaceRaw}
    */
    public final Pose3d getCameraposeTargetspace()
    {
        double[] items = getCameraposeTargetspaceRaw();
        return new Pose3d(items[0], items[1], items[2], new Rotation3d(items[3], items[4], items[5]));
    }

    private final NetworkTableEntry targetpose_cameraspace = table.getEntry("targetpose_cameraspace");

    /**
    * 3D transform of the primary in-view AprilTag in the coordinate system of the Camera (array (6))
    */
    public final double[] getTargetposeCameraspaceRaw() {return targetpose_cameraspace.getDoubleArray((double[])null);}

    /**
    * Gets the pose3d encoded by {@link Limelight#getTargetposeCameraspaceRaw}
    */
    public final Pose3d getTargetposeCameraspace()
    {
        double[] items = getTargetposeCameraspaceRaw();
        return new Pose3d(items[0], items[1], items[2], new Rotation3d(items[3], items[4], items[5]));
    }

    private final NetworkTableEntry targetpose_robotspace = table.getEntry("targetpose_robotspace");

    /**
    * 3D transform of the primary in-view AprilTag in the coordinate system of the Robot (array (6))
    */
    public final double[] getTargetposeRobotspaceRaw() {return targetpose_robotspace.getDoubleArray((double[])null);}

    /**
    * Gets the pose3d encoded by {@link Limelight#getTargetposeRobotspaceRaw}
    */
    public final Pose3d getTargetposeRobotspace()
    {
        double[] items = getTargetposeRobotspaceRaw();
        return new Pose3d(items[0], items[1], items[2], new Rotation3d(items[3], items[4], items[5]));
    }

    private final NetworkTableEntry botpose_targetspace = table.getEntry("botpose_targetspace");

    /**
    * 3D transform of the robot in the coordinate system of the primary in-view AprilTag (array (6))
    */
    public final double[] getBotposeTargetspaceRaw() {return botpose_targetspace.getDoubleArray((double[])null);}

    /**
    * Gets the pose3d encoded by {@link Limelight#getBotposeTargetspaceRaw}
    */
    public final Pose3d getBotposeTargetspace()
    {
        double[] items = getBotposeTargetspaceRaw();
        return new Pose3d(items[0], items[1], items[2], new Rotation3d(items[3], items[4], items[5]));
    }

    private final NetworkTableEntry tid = table.getEntry("tid");
    /**
    * ID of the primary in-view AprilTag
    */
    public final double getTid() {return tid.getDouble(0);}
    
    // Setters //
    public void setCamMode(int mode)
    {
        camMode.setInteger(mode);
    }

    public void setLEDMode(int mode) 
    {
        LEDMode.setNumber(mode);
    }

    public void setStreamMode(int mode) 
    {
        stream.setNumber(mode);
    }

    public void setSnapshotMode(int mode) 
    {
        snapshot.setNumber(mode);
    }

    /**
    * Takes a double array of length 4, with the crop values from -1 to 1.<p>
    * [0] Min X crop value of rectangle<p>
    * [1] Max X value of crop rectangle<p>
    * [2] Min value of crop rectangle<p>
    * [3] Max Y value of crop rectangle
    */
    public void setCrop(double[] arr)
    {
        crop.setDoubleArray(arr);
    }

    /**
     * Change the current pipeline
     * @param pipelineNum pipeline to change to
     */
    public void setPipeline(double pipelineNumInput) 
    {
        pipelineNum = pipelineNumInput;
    }

    @Override
    public void always() 
    {
        //Read values periodically
        pipeline.setDouble(pipelineNum);
        isDetected = tv.getDouble(0.0) == 1 ? true : false;
        x = tx.getDouble(0.0);
        y = ty.getDouble(0.0);
        area = ta.getDouble(0.0);
        timestamp = ts.getDouble(0.0);
    }

    public void updateEstimatedPose(Pose2d previous)
    {
        Unimplemented.here();
    }

    public Pose2d getEstimatedPose()
    {
        return getBotpose().toPose2d();
    }
    public double getTimestampSeconds()
    {
        return getTimestamp() / 1000;
    }
    public double getObjectAX() {return getX();}
    public double getObjectAY() {return getY();}
    public boolean hasObject() {return getDetected();}
}