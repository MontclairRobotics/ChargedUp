// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.net.PortForwarder;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.animation.FadeTransition;
import frc.robot.components.managers.Auto;
import frc.robot.components.managers.GyroscopeNavX;
import frc.robot.components.managers.LED;
import frc.robot.components.managers.MiscData;
import frc.robot.components.subsystems.Drivetrain;
import frc.robot.components.subsystems.Elevator;
import frc.robot.components.subsystems.Grabber;
import frc.robot.components.subsystems.Shwooper;
import frc.robot.components.subsystems.Stinger;
import frc.robot.inputs.JoystickInput;
import frc.robot.structure.DetectionType;
import frc.robot.util.frc.GameController;
import frc.robot.util.frc.Logging;
import frc.robot.util.frc.GameController.Axis;
import frc.robot.util.frc.GameController.Button;
import frc.robot.util.frc.GameController.DPad;
import frc.robot.util.frc.can.CANSafety;
import frc.robot.util.frc.commandrobot.RobotContainer;
import frc.robot.vision.LimelightSystem;
import frc.robot.vision.VisionSystem;

import frc.robot.constants.*;

import java.util.Map;

public class ChargedUp extends RobotContainer 
{
    public static final boolean useDebugController = true;

    // SIMULATION //
    public static final Field2d field = new Field2d();
    public static Field2d getField() {return field;}
    public static final Mechanism2d mainMechanism = new Mechanism2d(5, 5);

    // CONTROLLERS //
    public static final GameController driverController = GameController.from(
            ControlScheme.DRIVER_CONTROLLER_TYPE,
            ControlScheme.DRIVER_CONTROLLER_PORT);
    public static final GameController operatorController = GameController.from(
        ControlScheme.OPERATOR_CONTROLLER_TYPE,
        ControlScheme.OPERATOR_CONTROLLER_PORT);
    public static final GameController debugController = useDebugController 
        ? GameController.from(
        ControlScheme.DEBUG_CONTROLLER_TYPE,
        ControlScheme.DEBUG_CONTROLLER_PORT)
        : null;

    // SHUFFLEBOARD //
    private static final ShuffleboardTab debugTab = Shuffleboard.getTab("Debug");

    public static ShuffleboardTab getDebugTab() {
        return debugTab;
    }

    private static final ShuffleboardTab mainTab = Shuffleboard.getTab("Main Tab");

    public static ShuffleboardTab getMainTab() 
    {
        return mainTab;
    }

    private static final ShuffleboardTab PIDTab = Shuffleboard.getTab("PID");

    public static ShuffleboardTab getPIDTab() 
    {
        return PIDTab;
    }

    // COMPONENTS //
    public static final GyroscopeNavX gyroscope = new GyroscopeNavX();
    public static final LED led = new LED();

    public static final PneumaticHub pneu = new PneumaticHub(PneuConstants.PH_PORT);

    public static final VisionSystem vision = new LimelightSystem();

    public static final Drivetrain drivetrain = new Drivetrain();
    public static final Elevator elevator = new Elevator();
    public static final Shwooper shwooper = new Shwooper();
    public static final Grabber grabber = new Grabber();
    public static final Stinger stinger = new Stinger();

    // TODO: needing to create an object for this is kinda dumb
    public static final MiscData misc = new MiscData();

    // INITIALIZER //
    @Override 
    public void initialize() 
    {
        field.setRobotPose(2, 2, Rotation2d.fromDegrees(0));
        Shuffleboard.getTab("Auto").add(field).withSize(7, 4).withPosition(2, 0);

        pneu.enableCompressorDigital();
        CANSafety.monitor(pneu);

        CameraServer.startAutomaticCapture();

        for(int port = 5800; port <= 5805; port++)
        {
            PortForwarder.add(port, "limelight.local", port);
        }

        led.setTransition(FadeTransition::new);

        setupDebugTab();
        setupMainTab();
        setupPIDTab();

        vision.setTargetType(DetectionType.APRIL_TAG);

        // HANDLE DRIVING //
        drivetrain.setDefaultCommand(Commands.run(() -> {
                if (!DriverStation.isTeleop()) {
                        drivetrain.set(0, 0, 0);
                        return;
                }

                drivetrain.setInput(
                        JoystickInput.getRight(driverController, true, true),
                        JoystickInput.getLeft(driverController, true, true));
                },
                drivetrain));

        // driverController.getButton(Button.A_CROSS)
        // .onTrue(Commands.runOnce(() -> DefaultAnimation.setViolet()));
        // driverController.getButton(Button.B_CIRCLE)
        // .onTrue(Commands.runOnce(() -> led.add(MagicAnimation.fire(4))));
        // driverController.getButton(Button.A_CROSS)
        // .onTrue(Commands.runOnce( () -> led.add(new CircusAnimation(7))));
        // driverController.getButton(Button.Y_TRIANGLE)
        // .onTrue(Commands.runOnce(() -> led.add(MagicAnimation.galaxy(5))));
        // driverController.getButton(Button.X_SQUARE)
        // .onTrue(Commands.runOnce(() -> led.add(new QuickSlowFlash(Color.kYellow))));

        // Button for Field Relative
        // driverController.getButton(Button.A_CROSS)
                // .and(driverController.getButton(Button.START_TOUCHPAD))
                // .onTrue(drivetrain.commands.toggleFieldRelative());
                // .onTrue(Commands.runOnce(() -> led.add(new ASCIImation(5, "Hello",
                // Color.kBlack, Color.kWhite, Color.kGreen, Color.kOrange))));
                // .onTrue(Commands.runOnce(() -> led.add(new RaceAnimation(5))));

        // driverController.getAxis(Axis.LEFT_TRIGGER)
        //         .whenGreaterThan(0.5)
        //         .onTrue(drivetrain.commands.enableStraightPidding())
        //         .onFalse(drivetrain.commands.disableStraightPidding());

        // Increase/Decrease Max Speed
        driverController.getButton(Button.RIGHT_BUMPER)
                .onTrue(drivetrain.commands.increaseSpeed());
        driverController.getButton(Button.LEFT_BUMPER)
                .onTrue(drivetrain.commands.decreaseSpeed());

        // VISION MOVEMENT //
        driverController.getButton(Button.A_CROSS) // switch cone or cube
                .onTrue(Commands.runOnce(() -> vision.cycleDesiredDriveTarget()));

        driverController.getButton(Button.X_SQUARE)
                .onTrue(Commands2023.moveToObjectSideways(vision::getDesiredDriveTarget));

        driverController.getButton(Button.B_CIRCLE)
                .onTrue(Commands2023.turnToObject(vision::getDesiredDriveTarget));

        driverController.getButton(Button.LEFT_STICK)
                .onTrue(drivetrain.commands.enableFieldRelative());

        // BALANCE //
        driverController.getButton(Button.Y_TRIANGLE)
                .onTrue(Commands2023.balance());

        // Button to Zero NavX
        driverController.getButton(Button.START_TOUCHPAD)
            .onTrue(Commands.runOnce(() -> {
                Logging.info("Diego hit the button; current rotation is " + gyroscope.getRotation2d().getDegrees() + " degrees");
                gyroscope.setNorth();
            }).ignoringDisable(true));

        // driverController.getDPad(DPad.UP).onTrue(drivetrain.commands.goToAngle(Math.PI / 2));
        // driverController.getDPad(DPad.RIGHT).onTrue(drivetrain.commands.goToAngle(0));
        // driverController.getDPad(DPad.DOWN).onTrue(drivetrain.commands.goToAngle((3 * Math.PI) / 2));
        // driverController.getDPad(DPad.LEFT).onTrue(drivetrain.commands.goToAngle(Math.PI));

        // OPERATOR CONTROLS //

        // Cancel PID
        Trigger pidActive = operatorController.getButton(Button.START_TOUCHPAD).negate();

        if(useDebugController)
        {
            debugController.getButton(Button.X_SQUARE).onTrue(drivetrain.yPID.goToSetpoint(2, drivetrain));

            debugController.getDPad(DPad.UP)   .onTrue(drivetrain.commands.goToAngle(0));
            debugController.getDPad(DPad.LEFT) .onTrue(drivetrain.commands.goToAngle(90));
            debugController.getDPad(DPad.DOWN) .onTrue(drivetrain.commands.goToAngle(180));
            debugController.getDPad(DPad.RIGHT).onTrue(drivetrain.commands.goToAngle(270));

            debugController.getButton(Button.B_CIRCLE).onTrue(Commands.runOnce(() -> vision.setTargetType(DetectionType.TAPE)).ignoringDisable(true));
            debugController.getButton(Button.A_CROSS).onTrue(Commands.runOnce(() -> vision.setTargetType(DetectionType.APRIL_TAG)).ignoringDisable(true));
        }

        // D-Pad Controls
        // operatorController.getDPad(DPad.UP).and(pidActive)
        //     .toggleOnTrue(Commands2023.scoreHigh());
        operatorController.getDPad(DPad.LEFT).and(pidActive)
            .toggleOnTrue(Commands2023.scoreMid());
        // operatorController.getDPad(DPad.DOWN).and(pidActive)
        //     .toggleOnTrue(Commands2023.scoreLow());
        operatorController.getDPad(DPad.RIGHT).and(pidActive)
                .toggleOnTrue(Commands2023.elevatorStingerReturn());

        // Grabber
        operatorController.getButton(Button.A_CROSS)
            .onTrue(Commands2023.toggleGrabber());
        operatorController.getButton(Button.Y_TRIANGLE)
            .onTrue(Commands2023.setGrabberHasCone());
        operatorController.getButton(Button.X_SQUARE)
            .onTrue(Commands2023.setGrabberHasCube());
        operatorController.getButton(Button.B_CIRCLE)
            .onTrue(Commands2023.toggleStinger());

        // Schwooper
        // suck button
        operatorController.getAxis(Axis.LEFT_TRIGGER)
                .whenGreaterThan(0.5)
                .onTrue(Commands2023.shwooperSuck())
                .onFalse(Commands2023.stopShwooper());
        // button to spit schwooper
        operatorController.getAxis(Axis.RIGHT_TRIGGER)
                .whenGreaterThan(0.5)
                .onTrue(Commands2023.shwooperSpit())
                .onFalse(Commands2023.stopShwooper());

        // Elevator
        elevator.setDefaultCommand(Commands.run(() -> {
            JoystickInput left = JoystickInput.getLeft(
                operatorController, false, true);

            ElevatorConstants.JOY_ADJUSTER.adjustY(left);

            elevator.setSpeed(left.getY());
        }, elevator));
        
        //LEDs
        operatorController.getButton(Button.RIGHT_BUMPER)
            .onTrue(Commands2023.quickSlowFlashPurple());
        operatorController.getButton(Button.LEFT_BUMPER)
                .onTrue(Commands2023.quickSlowFlashYellow());

        
        // Quick calibrate + zero
        // gyroscope.calibrate();
        // Timer.delay(2);
        // gyroscope.zeroYaw();
    }

    public static boolean skipDriveAuto()
    {
        return false; // TODO; this
    }

    // AUTO //
    public static final Auto auto = new Auto();

    @Override
    public Command getAuto() 
    {
        grabber.setHoldingCone(false);
        return Commands2023.backupAuto();//auto.get();
    }

    public void setupDebugTab() 
    {
        debugTab.addStringArray("All Logs", Logging::allLogsArr)
            .withPosition(0 + 2 + 2 + 2, 3)
            .withSize(2, 2);

        debugTab.addDouble("Elevator Extension", elevator::getHeight)
            .withPosition(0 + 2, 3)
            .withSize(2, 1);

        debugTab.addDouble("FPS", misc::fps)
            .withPosition(0 + 2 + 2, 3)
            .withSize(2, 1);

        debugTab.add("Mechanism", mainMechanism);
    }

    public void setupPIDTab() {
        final ShuffleboardLayout xPID = PIDTab.getLayout("X-PID", BuiltInLayouts.kGrid)
            .withPosition(0, 0)
            .withSize(1, 5)
            .withProperties(Map.of("Number of columns", 1, "Number of rows", 6));
        xPID.add("X-PID Controller", drivetrain.xPID.getPIDController()).withPosition(0, 0);
        xPID.addBoolean("At SetPoint?", drivetrain.xPID::free).withPosition(0, 1);
        xPID.addDouble("X-Speed", drivetrain.xPID::getSpeed).withPosition(0, 2);
        xPID.addDouble("X-Measurement", drivetrain.xPID::getMeasurement).withPosition(0, 3);

        final ShuffleboardLayout yPID = PIDTab.getLayout("Y-PID", BuiltInLayouts.kGrid)
            .withPosition(1, 0)
            .withSize(1, 5)
            .withProperties(Map.of("Number of columns", 1, "Number of rows", 6));
        yPID.add("Y-PID Controller", drivetrain.yPID.getPIDController()).withPosition(0, 0);
        yPID.addBoolean("At SetPoint?", drivetrain.yPID::free).withPosition(0, 1);
        yPID.addDouble("Speed", drivetrain.yPID::getSpeed).withPosition(0, 2);
        yPID.addDouble("Measurement", drivetrain.yPID::getMeasurement).withPosition(0, 3);

        final ShuffleboardLayout thetaPID = PIDTab.getLayout("THETA-PID", BuiltInLayouts.kGrid)
            .withPosition(2, 0)
            .withSize(1, 5)
            .withProperties(Map.of("Number of columns", 1, "Number of rows", 6));
        thetaPID.add("θ-PID Controller", drivetrain.thetaPID.getPIDController()).withPosition(0, 0);
        thetaPID.addBoolean("At SetPoint?", drivetrain.thetaPID::free).withPosition(0, 1);
        thetaPID.addDouble("Speed", drivetrain.thetaPID::getSpeed).withPosition(0, 2);
        thetaPID.addDouble("Measurement", drivetrain.thetaPID::getMeasurement).withPosition(0, 3);

        final ShuffleboardLayout elevatorPID = PIDTab.getLayout("ELEVATOR-PID", BuiltInLayouts.kGrid)
            .withPosition(3, 0)
            .withSize(1, 5)
            .withProperties(Map.of("Number of columns", 1, "Number of rows", 6));
        elevatorPID.add("Elevator PID", elevator.PID.getPIDController()).withPosition(0, 0);
        elevatorPID.addBoolean("At SetPoint?", elevator.PID::free).withPosition(0, 1);
        elevatorPID.addDouble("Speed", elevator.PID::getSpeed).withPosition(0, 2);
        elevatorPID.addDouble("Measurement", elevator.PID::getMeasurement).withPosition(0, 3);
    }

    // SHUFFLEBOARD //
    public void setupMainTab() 
    {
        // IS USING FIELD RELATIVE //
        final ShuffleboardLayout info = mainTab.getLayout("Info", BuiltInLayouts.kList)
            .withSize(2, 4)
            .withPosition(9, 0);

        info.addBoolean("Field Relative", drivetrain::usingFieldRelative)
            .withWidget(BuiltInWidgets.kBooleanBox);

        // LOGGING LOG RECENT //
        mainTab
            .addString("Recent Log", Logging::mostRecentLog)
            .withWidget(BuiltInWidgets.kTextView)
            .withSize(3, 1)
            .withPosition(2, 3);

        mainTab
            .addBoolean("Pressure Maxxed?", () -> !pneu.getPressureSwitch())
            .withSize(2, 1)
            .withPosition(7, 3);

        
        mainTab
            .addString("Suck Mode", shwooper::currentMode)
            .withSize(2, 1)
            .withPosition(7, 2);
            

        // CAMERAS //
        // mainTab
        // .addCamera("Cameras", "Vision Camera", vision.getCameraStreamURL())
        // .withSize(3, 2)
        // .withPosition(0, 3);

        // GYROSCOPE VALUE //
        mainTab
            .addNumber("Gyroscope", () -> gyroscope.getRotationInCircle().getDegrees())
            .withWidget(BuiltInWidgets.kGyro)
            .withSize(2, 2)
            .withPosition(0, 0);

        // MAX LINEAR SPEED //
        info.addNumber("Max Linear Speed (mps)",
            () -> drivetrain.getCurrentSpeedLimits()[0] * DriveConstants.MAX_SPEED_MPS)
            .withWidget(BuiltInWidgets.kTextView);

        // MAX ANGULAR SPEED //
        info.addNumber("Max Angular Speed (rps)",
            () -> drivetrain.getCurrentSpeedLimits()[1] * DriveConstants.MAX_TURN_SPEED_RAD_PER_S)
            .withWidget(BuiltInWidgets.kTextView);

        // HELD OBJECT //
        // mainTab
        //     .addString("Held Object", grabber::getHeldObjectName)
        //     .withWidget(BuiltInWidgets.kTextView)
        //     .withSize(2, 1)
        //     .withPosition(0, 2);
        mainTab
            .addBoolean("Current Held Object", ChargedUp.grabber::getHoldingCone)
            .withWidget(BuiltInWidgets.kBooleanBox)
            .withSize(2, 1)
            .withPosition(0, 2)
            .withProperties(Map.of(
                "Color when true",  Color.kGold.toHexString(),
                "Color when false", Color.kDarkViolet.toHexString()
            ));
        
        mainTab
            .addString("Current Target Object", ChargedUp.vision::getDesiredDriveTargetAsString)
            .withSize(2, 1)
            .withPosition(5, 3);
    }
}