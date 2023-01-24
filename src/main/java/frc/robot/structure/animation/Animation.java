package frc.robot.structure.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;

public interface Animation {
    public void perform(AddressableLEDBuffer ledBuffer, double time);

}
