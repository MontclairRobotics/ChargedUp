package org.team555.util.frc;

import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * Represents a simple game controller.
 * This class is mostly a wrapper around {@link PS4Controller} and {@link XboxController}.
 * 
 * <p>
 * Axes and buttons (stored in respective enums) are named such that they 
 * are of the form XBOX_PS4 unless both controllers share the same functionality. 
 * The DPad enum wraps the angles {@code 0}, {@code 90}, {@code 180}, and {@code 270}.
 * Checking if a DPad button is pressed results in a check if the POV of the controller
 * os within a 45 degree range (includive) of that DPad button's angle.
 * 
 * @author Team 555 (Dylan Rafael)
 * @version 0.6
 * @since 0.5
 */
public abstract class GameController 
{
    /**
     * Represents one of the analog inputs on a game controller.
     * Things like joystick positions and triggers can be found here.
     */
    public static enum Axis
    {
        LEFT_X,
        LEFT_Y,

        RIGHT_X,
        RIGHT_Y,

        LEFT_TRIGGER,
        RIGHT_TRIGGER
    }
    /**
     * Represents one of the digit inputs on a game controller.
     * Things like the "start" or "a" buttons can be found here.
     */
    public static enum Button
    {
        A_CROSS,
        B_CIRCLE,
        Y_TRIANGLE,
        X_SQUARE,

        START_TOUCHPAD,

        LEFT_BUMPER,
        RIGHT_BUMPER,

        LEFT_STICK,
        RIGHT_STICK,
    }
    /**
     * Represents one of the D-Pad inputs of a controller.
     * Can either be {@code UP}, {@code DOWN}, {@code LEFT}, or {@code RIGHT}
     */
    public static enum DPad
    {
        UP(0),
        RIGHT(90),
        DOWN(180),
        LEFT(270)
        ;

        private DPad(int angle)
        {
            this.angle = angle;
        }

        public final int angle;

        public static boolean get(DPad type, int pov)
        {
            switch(type)
            {
                case UP: return (0 <= pov && pov <= 45) || (360 - 45 <= pov && pov <= 360);
                case RIGHT: return (90 - 45 <= pov && pov <= 90 + 45);
                case DOWN: return (180 - 45 <= pov && pov <= 180 + 45);
                case LEFT: return (270 - 45 <= pov && pov <= 270 + 45);
            }

            throw new RuntimeException("Unknown dpad type " + type);
        }
    }

    /**
     * Return an axis as an xbox axis.
     * @param axisType the axis to convert
     * @return the axis as an xbox axis
     */
    public static XboxController.Axis toXbox(Axis axisType)
    {
        switch(axisType)
        {
            case LEFT_X:
                return XboxController.Axis.kLeftX;
            case RIGHT_X:
                return XboxController.Axis.kRightX;
            case LEFT_Y:
                return XboxController.Axis.kLeftY;
            case RIGHT_Y: 
                return XboxController.Axis.kRightY;
            case LEFT_TRIGGER:
                return XboxController.Axis.kLeftTrigger;
            case RIGHT_TRIGGER:
                return XboxController.Axis.kRightTrigger;
        }
        return null;
    }

    /**
     * Return an axis as a ps4 axis.
     * @param axisType the axis to convert
     * @return the axis as a ps4 axis
     */
    public static PS4Controller.Axis toPS4(Axis axisType)
    {
        switch(axisType) 
        {
            case LEFT_X:
                return PS4Controller.Axis.kLeftX;
            case RIGHT_X:
                return PS4Controller.Axis.kRightX;
            case LEFT_Y:
                return PS4Controller.Axis.kLeftY;
            case RIGHT_Y:
                return PS4Controller.Axis.kRightY;
            case LEFT_TRIGGER:
                return PS4Controller.Axis.kL2;
            case RIGHT_TRIGGER:
                return PS4Controller.Axis.kR2;
        }
        return null;
    }

    /**
     * Return a button as an xbox axis.
     * @param axisType the button to convert
     * @return the button as an xbox axis
     */
    public static XboxController.Button toXbox(Button buttonType)
    {
        switch(buttonType)
        {
            case A_CROSS:
                return XboxController.Button.kA;
            case B_CIRCLE:
                return XboxController.Button.kB;
            case X_SQUARE:
                return XboxController.Button.kX;
            case Y_TRIANGLE:
                return XboxController.Button.kY;

            case START_TOUCHPAD:
                return XboxController.Button.kStart;
            
            case LEFT_BUMPER:
                return XboxController.Button.kLeftBumper;
            case RIGHT_BUMPER:
                return XboxController.Button.kRightBumper;

            case LEFT_STICK:
                return XboxController.Button.kLeftStick;
            case RIGHT_STICK:
                return XboxController.Button.kRightStick;
        }
        return null;
    }

    /**
     * Return a button as a ps4 axis.
     * @param axisType the button to convert
     * @return the button as a ps4 axis
     */
    public static PS4Controller.Button toPS4(Button buttonType)
    {
        switch(buttonType)
        {
            case A_CROSS:
                return PS4Controller.Button.kCross;
            case B_CIRCLE:
                return PS4Controller.Button.kCircle;
            case X_SQUARE:
                return PS4Controller.Button.kSquare;
            case Y_TRIANGLE:
                return PS4Controller.Button.kTriangle;

            case START_TOUCHPAD:
                return PS4Controller.Button.kTouchpad;
            
            case LEFT_BUMPER:
                return PS4Controller.Button.kL1;
            case RIGHT_BUMPER:
                return PS4Controller.Button.kR1;

            case LEFT_STICK:
                return PS4Controller.Button.kL3;
            case RIGHT_STICK:
                return PS4Controller.Button.kR3;
        }
        return null;
    }

    /**
     * Represents a type of game controller supported by this class.
     * Can either be XBOX or PS4.
     */
    public static enum Type
    {
        XBOX,
        PS4,
    }
    
    /**
     * Gets a boolean representing whether or not the given button is currently pressed.
     * @param type the button type to check
     * @return {@code true} if the button is currently pressed, {@code false} otherwise
     */
    public abstract boolean getButtonValue(Button type);
    /**
     * Gets a boolean representing whether or not the given button was just pressed.
     * @param type the button type to check
     * @return {@code true} if the button was just pressed, {@code false} otherwise
     */
    public abstract boolean getButtonPressed(Button type);
    /**
     * Gets a boolean representing whether or not the given button was just released.
     * @param type the button type to check
     * @return {@code true} if the button was just released, {@code false} otherwise
     */
    public abstract boolean getButtonReleased(Button type);
    
    /**
     * Gets a double representing the given axis.
     * @param type the axis type
     * @return the value of that axis
     */
    public abstract double getAxisValue(Axis type);
    
    /**
     * Gets a double representing the pov of this controller.
     * @return the pov of this controller, from 0 upto 360 degrees
     */
    public abstract double getPOVValue();

    /**
     * Gets a boolean representing whether or not the given dpad button is currently pressed.
     * @param type the button type to check
     * @return {@code true} if the dpad button is currently pressed, {@code false} otherwise
     */
    public abstract boolean getDPadRaw(DPad type);

    /**
     * Gets the controller type of this instance.
     * @return the value of {@link GameController.Type} which this instance represents.
     */
    public abstract Type type();

    /**
     * Gets a trigger representing the provided button.
     * @return a trigger which tracks the provided button
     */
    public final Trigger getButton(Button type) 
    {
        return new Trigger(() -> getButtonValue(type));
    }
    /**
     * Gets a trigger representing the provided dpad button.
     * @return a trigger which tracks the provided dpad button
     */
    public final Trigger getDPad(DPad type)
    {
        return new Trigger(() -> getDPadRaw(type));
    }
    /**
     * Gets an analog trigger representing the provided axis.
     * @return am analog trigger which tracks the provided axis
     */
    public final AnalogTrigger getAxis(Axis type)
    {
        return new AnalogTrigger(() -> getAxisValue(type));
    }
    /**
     * Gets an analog trigger representing this controller's POV.
     * @return an analog trigger which tracks the controller's POV
     */
    public final AnalogTrigger getPOV()
    {
        return new AnalogTrigger(() -> getPOVValue());
    }

    /**
     * Creates a new instance of {@link GameController} which wraps an 
     * xbox controller.
     * @param channel the channel on which the controller exists
     * @return a new instance which wraps an xbox controller
     */
    public static GameController xbox(int channel)
    {
        return new GameController()
        {
            private XboxController innerCont = new XboxController(channel);

            @Override
            public boolean getButtonValue(Button type) {
                return innerCont.getRawButton(toXbox(type).value);
            }

            @Override
            public boolean getDPadRaw(DPad type)
            {
                return DPad.get(type, innerCont.getPOV());
            }

            @Override
            public boolean getButtonPressed(Button type) {
                return innerCont.getRawButtonPressed(toXbox(type).value);
            }

            @Override
            public boolean getButtonReleased(Button type) {
                return innerCont.getRawButtonReleased(toXbox(type).value);
            }

            @Override
            public double getAxisValue(Axis type) {
                return innerCont.getRawAxis(toXbox(type).value);
            }

            @Override
            public Type type() {
                return Type.XBOX;
            }

            @Override
            public double getPOVValue() {
                return innerCont.getPOV();
            }
        };
    }
    /**
     * Creates a new instance of {@link GameController} which wraps a
     * ps4 controller.
     * @param channel the channel on which the controller exists
     * @return a new instance which wraps a ps4 controller
     */
    public static GameController ps4(int channel)
    {
        return new GameController()
        {
            private PS4Controller innerCont = new PS4Controller(channel);

            @Override
            public boolean getDPadRaw(DPad type)
            {
                return DPad.get(type, innerCont.getPOV());
            }

            @Override
            public boolean getButtonValue(Button type) {
                return innerCont.getRawButton(toPS4(type).value);
            }
            
            @Override
            public boolean getButtonPressed(Button type) {
                return innerCont.getRawButtonPressed(toPS4(type).value);
            }

            @Override
            public boolean getButtonReleased(Button type) {
                return innerCont.getRawButtonReleased(toPS4(type).value);
            }

            @Override
            public double getAxisValue(Axis type) {
                return innerCont.getRawAxis(toPS4(type).value);
            }

            @Override
            public Type type() {
                return Type.PS4;
            }
            
            @Override
            public double getPOVValue() {
                return innerCont.getPOV();
            }
        };
    }

    /**
     * Creates a new instance of {@link GameController} which wraps the specified
     * controller type.
     * @param type the type of controller to wrap
     * @param channel the channel on which the controller exists
     * @return a new instance which wraps the specified controller type
     */
    public static GameController from(Type type, int channel)
    {
        return type.equals(Type.XBOX) ? xbox(channel) : ps4(channel);
    }
}
