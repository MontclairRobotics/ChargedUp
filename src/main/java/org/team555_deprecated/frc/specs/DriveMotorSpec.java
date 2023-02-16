package org.team555_deprecated.frc.specs;

import static org.team555_deprecated.units.StandardUnits.*;

import org.team555_deprecated.units.Quantity;

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
