package org.team555.frc.vision;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.team555.frc.command.commandrobot.ManagerBase;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight extends ManagerBase 
{
    boolean isDetected;
    double x;
    double y;
    double area;
    
    private final NetworkTable table;
    private final NetworkTableEntry tv;
    private final NetworkTableEntry tx;
    private final NetworkTableEntry ty;
    private final NetworkTableEntry ta;    
    
    public Limelight() 
    {
        table = NetworkTableInstance.getDefault().getTable("limelight");
        tv = table.getEntry("tv");
        tx = table.getEntry("tx");
        ty = table.getEntry("ty");
        ta  = table.getEntry("ta");
    }

    // Getters //
    public boolean getDetected() {return isDetected;}
    public double getX() {return x;}
    public double getY() {return y;}
    public double getArea() {return area;}

    //TODO: set the pipeline of the limelight
    public void setPipeline(int pipelineNum) {

    }

    @Override
    public void always() 
    {
        //Read values periodically
        isDetected = tv.getDouble(0.0) == 1 ? true : false;
        x = tx.getDouble(0.0);
        y = ty.getDouble(0.0);
        area = ta.getDouble(0.0);
    }
}