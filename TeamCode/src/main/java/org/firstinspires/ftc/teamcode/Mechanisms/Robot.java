package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Robot {

    // Hardware
    public final AprilTagWebcam aprilTagWebcam = new AprilTagWebcam();
    public final FlyWheel flyWheel = new FlyWheel();
    public final Hood hood = new Hood();
    public final Intake intake = new Intake();
    public final LazySusan lazySusan = new LazySusan();
    public final MecanumDriveTrain mecanumDriveTrain = new MecanumDriveTrain();
    public final OpmodeIMU opmodeIMU = new OpmodeIMU();

    // Attributes
    private int activeTagID = 20; // Defaults to blue


    /**
     * Description:
     * Pre-Condition:
     * Post-Condition:
     * @param hardwareMap
     * @param telemetry
     * @param turretStartAngle
     * @param flyWheelEquation
     * @param hoodEquation
     * @param imuStartHeading
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
        lazySusan.update(false, false, false, aprilTagWebcam);
        aprilTagWebcam.update(lazySusan.getOrientation(AngleUnit.RADIANS));
        intake.update(flyWheel.shoot, flyWheel);
    }

    public void update(Gamepad gamepad1, Gamepad gamepad2) {
        opmodeIMU.update();
        flyWheel.update();
        hood.update();
        lazySusan.update(gamepad2.left_bumper, gamepad2.right_bumper, gamepad2.dpad_down, aprilTagWebcam);
        aprilTagWebcam.update(lazySusan.getOrientation(AngleUnit.RADIANS));
        intake.update(flyWheel.shoot, flyWheel);
        mecanumDriveTrain.update(gamepad1.dpad_down, -gamepad1.left_stick_y, -gamepad1.left_stick_x, gamepad1.right_stick_x);
    }

    // Getters and Setters
    public int getActiveTagID() {
        return activeTagID;
    }
    public void setActiveTagID(int activeTagID) {
        this.activeTagID = activeTagID;
    }
}
