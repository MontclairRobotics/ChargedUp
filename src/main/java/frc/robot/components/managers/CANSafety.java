package frc.robot.components.managers;

import java.util.ArrayList;
import java.util.function.Supplier;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.sensors.CANCoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.REVLibError;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import frc.robot.util.frc.commandrobot.ManagerBase;

public class CANSafety extends ManagerBase
{
    private boolean hasErrors;
    private ArrayList<CANErrorable> allMots = new ArrayList<>();

    public boolean hasErrors() {return hasErrors;}

    static interface CANErrorable
    {
        boolean hasCANError();
    }

    public CANSparkMax add(CANSparkMax motor)
    {
        allMots.add(new CANSafety.CANErrorable()
        {
            @Override
            public boolean hasCANError()
            {
                switch(motor.getLastError())
                {
                    case kCANDisconnected:
                    case kInvalidCANId:
                    case kDuplicateCANId:
                    case kInvalid:
                    case kError:
                    case kHALError:
                    case kUnknown: return true;

                    default: return false;
                }
            }
        });

        return motor;
    }

    CANErrorable getErrorable(Supplier<ErrorCode> ctreErr)
    {
        return new CANErrorable() 
        {
            @Override
            public boolean hasCANError()
            {
                switch(ctreErr.get())
                {
                    case CAN_INVALID_PARAM:
                    case CAN_MSG_NOT_FOUND:
                    case CAN_MSG_STALE:
                    case CAN_NO_MORE_TX_JOBS:
                    case CAN_NO_SESSIONS_AVAIL:
                    case CAN_OVERFLOW:
                    case CAN_TX_FULL: return true;

                    default: return false;
                }
            }
        };
    }

    public CANCoder add(CANCoder cancoder)
    {
        allMots.add(getErrorable(cancoder::getLastError));
        return cancoder;
    }
    public TalonFX add(TalonFX mot)
    {
        allMots.add(getErrorable(mot::getLastError));
        return mot;
    }
    public <T extends MotorController> T add(T mot)
    {
        if(mot instanceof CANSparkMax) add((CANSparkMax)mot);
        else if(mot instanceof TalonFX) add((TalonFX)mot);
        return mot;
    }
    public <T> T addEncoder(T enc)
    {
        if(enc instanceof CANCoder) add((CANCoder)enc);
        return enc;
    }

    @Override
    public void always() 
    {
        hasErrors = allMots.stream().anyMatch(x -> x.hasCANError());
    }
}
