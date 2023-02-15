package frc.robot.structure;

import java.util.ArrayList;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import pabeles.concurrency.ConcurrencyOps.Reset;

public class RebootRequire 
{
    private static ArrayList<RebootRequire> values = new ArrayList<>();
    
    private final String name;
    private final double value;

    private RebootRequire(String name, double value)
    {
        this.name = name;
        this.value = value;
        
        values.add(this);
    }

    private void check()
    {
        NetworkTableInstance nt = NetworkTableInstance.getDefault();
        NetworkTableEntry entry = nt.getTable("__reset_vals").getEntry(name);

        double val = entry.getDouble(value);

        if(val != value)
        {
            throw new Error("Value " + name + " was changed but the robot was not rebooted! Please reboot the robot.");
        }

        entry.setDouble(value);
    }

    public static void checkAll()
    {
        for(RebootRequire k : values) 
        {
            k.check();
        }
    }

    public static double of(String name, double n) 
    {
        return new RebootRequire(name, n).value;
    }
}
