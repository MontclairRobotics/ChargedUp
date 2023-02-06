package frc.robot.structure;

import edu.wpi.first.networktables.NetworkTable;
import frc.robot.ChargedUp;

/**
 * An enum containing the thing the robot is currently tracking
 */
public enum Tracking {

    //TODO: Get actual pipeline values

    NONE(-1),
    CUBE(1),
    CONE(2),
    SERENA(3);

    private int pipelineNum;

    private Tracking(int pipelineNum) {
        this.pipelineNum = pipelineNum;
    }

    public int getPipelineNum() {
        return pipelineNum;
    }
}