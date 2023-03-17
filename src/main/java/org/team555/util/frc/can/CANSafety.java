package org.team555.util.frc.can;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.team555.util.frc.commandrobot.CommandRobot;
import org.team555.util.frc.commandrobot.Manager;

public class CANSafety implements Manager
{
    private CANSafety() {}
    
    static
    {
        CommandRobot.registerManager(new CANSafety());
    }

    private static boolean hasErrors;
    
    private static HashMap<Object, CANDevice> objectMap = new HashMap<>();
    private static HashMap<CANDevice, CANErrorCode> lastCodes = new HashMap<>();
    private static HashSet<CANDevice> erroredDevices = new HashSet<>();
    private static HashSet<CANDevice> okDevices = new HashSet<>();

    public static boolean hasErrors() {return hasErrors;}
    public static CANErrorCode getErrorCode(CANDevice dev)
    {
        if(lastCodes.containsKey(dev))
        {
            return lastCodes.get(dev);
        }

        return CANErrorCode.OK;
    }
    public static Set<CANDevice> devices() {return lastCodes.keySet();}
    public static Set<CANDevice> erroredDevices() {return erroredDevices;}
    public static Set<CANDevice> okDevices() {return erroredDevices;}

    public static <T> T monitor(T obj)
    {
        CANDevice dev = CANDevice.get(obj);
        if(dev != null) 
        {
            lastCodes.put(dev, CANErrorCode.OK);
            objectMap.put(obj, dev);
        }

        return obj;
    }

    @Override
    public void always() 
    {
        hasErrors = false;

        erroredDevices.clear();
        okDevices.clear();

        for(CANDevice dev : lastCodes.keySet())
        {
            CANErrorCode errCode = dev.getErrorCode();

            if(errCode.isErr()) 
            {
                hasErrors = true;
                erroredDevices.add(dev);
            }
            else 
            {
                okDevices.add(dev);
            }

            lastCodes.put(dev, errCode);
        }
    }
}
