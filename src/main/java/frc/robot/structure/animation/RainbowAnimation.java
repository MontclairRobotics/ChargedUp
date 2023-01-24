package frc.robot.structure.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import frc.robot.ChargedUp;

public class RainbowAnimation implements Animation {
    public void perform(AddressableLEDBuffer ledBuffer, double time) {
        ChargedUp.animationTimer.setTimer();
        int rainbowFirstPixelHue = 0;
        // For every pixel
        while (!ChargedUp.animationTimer.hasTimeElapsed(time)) {
            for (var i = 0; i < ledBuffer.getLength(); i++) {
                // Calculate the hue - hue is easier for rainbows because the color
                // shape is a circle so only one value needs to precess
                final var hue = (rainbowFirstPixelHue + (i * 180 / ledBuffer.getLength())) % 180;
                // Set the value
                ledBuffer.setHSV(i, hue, 255, 128);
            }
            // Increase by to make the rainbow "move"
            rainbowFirstPixelHue += 3;
            // Check bounds
            rainbowFirstPixelHue %= 180;
        }
    }
}