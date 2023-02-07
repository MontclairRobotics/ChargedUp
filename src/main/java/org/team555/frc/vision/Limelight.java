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
    
    private final NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");;
    private final NetworkTableEntry tv = table.getEntry("tv");
    private final NetworkTableEntry tx = table.getEntry("tx");
    private final NetworkTableEntry ty = table.getEntry("ty");
    private final NetworkTableEntry ta = table.getEntry("ta");  

    // Getters //
    public boolean getDetected() {return isDetected;}
    public double getX() {return x;}
    public double getY() {return y;}
    public double getArea() {return area;}
    public int getPipelineNum()
    {
        return (int) table.getEntry("getpipe").getDouble(0.0);
    }

    public void setPipeline(int pipelineNum) {
        table.getEntry("pipeline").setNumber(pipelineNum); 
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