package frc.robot.vision;

public enum DetectionType 
{
    NONE(-1),
    CONE(0),
    CUBE(1),
    TAPE(2),
    APRIL_TAG(69420666)
    ;

    public final int ID;

    DetectionType(int id)
    {
        ID = id;
    }
}
