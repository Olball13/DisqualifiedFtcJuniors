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
    public final OpmodeIMU opmodeIMU = new OpmodeIMU();


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
