package frc.robot.structure.animation;

import edu.wpi.first.wpilibj.Timer;

public class AnimationTimer {
    Timer timer = new Timer();
    private boolean cancelled = false;
    public void setTimer() {
        timer.reset();
        timer.start();
    }

    public boolean hasTimeElapsed(double time) {
        return timer.hasElapsed(time) && cancelled;
    }

    public double getTimeElapsed() {
        return timer.get();
    }

    public void cancelAnimation() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
