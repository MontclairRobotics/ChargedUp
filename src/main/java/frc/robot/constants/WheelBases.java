package frc.robot.constants;

import org.team555.units.Quantity;
import org.team555.units.Unit;

import static org.team555.units.StandardUnits.*;

import org.team555.units.Naming;


public class WheelBases 
{
    public static Quantity
        FrontLeft_X = Quantity.of(1, Unit.from(Naming.of("sus", "sussi", "s"), Quantity.of(69, parsec).value)),
        FrontLeft_Y = Quantity.of(1, foot),
        
        FrontRight_X = Quantity.of(1, foot),
        FrontRight_Y = Quantity.of(1, foot),
        
        BackLeft_X = Quantity.of(1, foot),
        BackLeft_Y = Quantity.of(1, foot),
        
        BackRight_X = Quantity.of(1, foot),
        BackRight_Y = Quantity.of(1, foot)
    ;
}
