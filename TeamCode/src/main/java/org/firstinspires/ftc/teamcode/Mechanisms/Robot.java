package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Robot {
    public AprilTagWebcam aprilTagWebcam = new AprilTagWebcam();
    public FlyWheel flyWheel = new FlyWheel();
    public Hood hood = new Hood();
    public Intake intake = new Intake();
    public LazySusan lazySusan = new LazySusan();
    public MecanumDriveTrain mecanumDriveTrain = new MecanumDriveTrain();
    public OpmodeIMU opmodeIMU = new OpmodeIMU();

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
