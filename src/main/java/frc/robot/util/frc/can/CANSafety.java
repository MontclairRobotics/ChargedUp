package frc.robot.util.frc.can;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import frc.robot.util.frc.commandrobot.CommandRobot;
import frc.robot.util.frc.commandrobot.Manager;

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

    public static boolean hasErrors() {return hasErrors;}
    public static CANDevice getDevice(Object obj) {return objectMap.get(obj);}
    public static CANErrorCode getErrorCode(Object obj)
    {
        if(objectMap.containsKey(obj))
        {
            return lastCodes.get(objectMap.get(obj));
        }

        return CANErrorCode.OK;
    }
    public static Set<CANDevice> devicesWithErrors() {return erroredDevices;}

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

        for(CANDevice dev : lastCodes.keySet())
        {
            CANErrorCode errCode = dev.getErrorCode();

            if(errCode.isErr()) 
            {
                hasErrors = true;
                erroredDevices.add(dev);
            }

            lastCodes.put(dev, errCode);
        }
    }
}
