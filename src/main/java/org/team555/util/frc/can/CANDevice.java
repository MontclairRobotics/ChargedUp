package org.team555.util.frc.can;

import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.sensors.CANCoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.REVLibError;

import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.PneumaticsBase;
import edu.wpi.first.wpilibj.PneumaticsControlModule;

public interface CANDevice 
{
    CANErrorCode getErrorCode();
    int getID();
    Object getDeviceObject();

    public static CANDevice rev(Object dev, Supplier<REVLibError> error, IntSupplier id)
    {
        return new CANDevice() 
        {
            @Override
            public Object getDeviceObject() {return dev;}

            @Override
            public int getID() {return id.getAsInt();}

            @Override
            public CANErrorCode getErrorCode() 
            {
                switch(error.get())
                {
                    case kCANDisconnected: return CANErrorCode.NOT_FOUND;

                    case kDuplicateCANId:
                    case kInvalidCANId: return CANErrorCode.BAD_ID;

                    default: return CANErrorCode.OK;
                }
            }
        };
    }
    public static CANDevice ctre(Object dev, Supplier<ErrorCode> error, IntSupplier id)
    {
        return new CANDevice() 
        {
            
            @Override
            public Object getDeviceObject() {return dev;}

            @Override
            public int getID() {return id.getAsInt();}

            @Override
            public CANErrorCode getErrorCode() 
            {
                switch(error.get())
                {
                    case CAN_MSG_STALE:
                    case CAN_MSG_NOT_FOUND: return CANErrorCode.NOT_FOUND;

                    case CAN_NO_MORE_TX_JOBS:
                    case CAN_OVERFLOW:
                    case CAN_INVALID_PARAM:
                    case CAN_TX_FULL:
                    case CAN_NO_SESSIONS_AVAIL: return CANErrorCode.BAD_ID;

                    default: return CANErrorCode.OK;
                }
            }
        };
    }
    public static CANDevice pneu(PneumaticsBase pneu, Supplier<CANErrorCode> err)
    {
        return new CANDevice() 
        {
            
            @Override
            public Object getDeviceObject() {return pneu;}

            @Override
            public int getID() {return pneu.getModuleNumber();}

            @Override
            public CANErrorCode getErrorCode() {return err.get();}
        };
    }

    public static CANDevice sparkMax(CANSparkMax mot)
    {
        return rev(mot, mot::getLastError, mot::getDeviceId);
    }
    public static CANDevice talon(TalonFX mot)
    {
        return ctre(mot, mot::getLastError, mot::getDeviceID);
    }
    public static CANDevice cancoder(CANCoder cdr)
    {
        return ctre(cdr, cdr::getLastError, cdr::getDeviceID);
    }
    public static CANDevice revph(PneumaticHub ph)
    {
        return pneu(ph, () -> 
        {
            if(ph.getFaults().CanWarning) return CANErrorCode.NOT_FOUND;

            return CANErrorCode.OK;
        });
    }

    public static CANDevice get(Object obj)
    {
        if(obj instanceof CANSparkMax)  return sparkMax((CANSparkMax)obj);
        if(obj instanceof TalonFX)      return talon((TalonFX)obj);
        if(obj instanceof CANCoder)     return cancoder((CANCoder)obj);
        if(obj instanceof PneumaticHub) return revph((PneumaticHub)obj);

        return null;
    }
}
