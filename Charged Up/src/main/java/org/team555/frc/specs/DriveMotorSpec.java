package org.team555.frc.specs;

import org.team555.units.Quantity;
import static org.team555.units.StandardUnits.*;

public class DriveMotorSpec 
{
    private final Quantity wheelDiameter;
    private final Ratio ratio;

    public DriveMotorSpec(Quantity diameter, Ratio ratio)
    {
        this.wheelDiameter = diameter.require(length, "diameter");
        this.ratio = ratio;
    }

    public Quantity wheelDiameter() {return wheelDiameter;}
    public Quantity lengthPerRotOut() {return wheelDiameter.mul(Math.PI);}

    public Quantity lengthPerRotIn()
    {
        return lengthPerRotOut().div(ratio.inToOut);
    }
}
