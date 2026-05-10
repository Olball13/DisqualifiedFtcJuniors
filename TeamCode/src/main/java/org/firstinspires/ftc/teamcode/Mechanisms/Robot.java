package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Robot {

    // Hardware
    public final AprilTagWebcam aprilTagWebcam = new AprilTagWebcam();
    public final FlyWheel flyWheel = new FlyWheel();
    public final Hood hood = new Hood();
    public final Intake intake = new Intake();
    public final LazySusan lazySusan = new LazySusan();
    public final MecanumDriveTrain mecanumDriveTrain = new MecanumDriveTrain();
    public final OpModeIMU opmodeIMU = new OpModeIMU();


    /**
     * Description: The init method for the all the subclasses within the robot object
     * Pre-Condition: hardwareMap and telemetry must be declared (already from extended OpMode)
     * Post-Condition: Initialises all mechanisms with set parameters
     * @param hardwareMap the hardwareMap passing through all the motors and sensors of the mechanisms
     * @param telemetry telemetry passed that the mechanisms display
     * @param turretStartAngle The starting orientation of the turret, helpful after auto
     * @param flyWheelEquation The equation determining the average flywheel velocity based off of distance readings
     * @param hoodEquation The equation determining the average hood percentage based off of distance readings
     * @param imuStartHeading The starting orientation of the robot, helpful for preset autos and to the start of teleop
     */
    public void init(HardwareMap hardwareMap, Telemetry telemetry, double turretStartAngle, double flyWheelEquation, double hoodEquation, double imuStartHeading) {
        aprilTagWebcam.init(hardwareMap, telemetry);
        flyWheel.init(hardwareMap, flyWheelEquation);//<--INPUT EQUATION
        hood.init(hardwareMap, hoodEquation);//<--INPUT EQUATION
        intake.init(hardwareMap);
        mecanumDriveTrain.init(hardwareMap);
        opmodeIMU.init(hardwareMap, telemetry, imuStartHeading);
        lazySusan.init(hardwareMap, telemetry, turretStartAngle);
    }

    public void update() {
        opmodeIMU.update();
        flyWheel.update();
        hood.update();
        lazySusan.update(aprilTagWebcam);
        aprilTagWebcam.update(lazySusan.getOrientation(AngleUnit.RADIANS));
        intake.update(flyWheel);
    }

    public void update(Gamepad gamepad1, Gamepad gamepad2) {
        opmodeIMU.update();
        flyWheel.update();
        hood.update();
        lazySusan.update(gamepad2, aprilTagWebcam);
        aprilTagWebcam.update(lazySusan.getOrientation(AngleUnit.RADIANS));
        intake.update(flyWheel);
        mecanumDriveTrain.update(gamepad1);
    }

}
