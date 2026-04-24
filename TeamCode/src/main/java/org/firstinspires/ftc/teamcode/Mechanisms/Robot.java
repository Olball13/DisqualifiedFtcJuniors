package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Robot {


    protected static final double camXCorrection = 0;// this is the x difference of the turret to the robots center (0)
    protected static final double camYCorrection = DistanceUnit.INCH.fromCm(0.45);// this is the y difference of the turret to the robots center (+ value)
    protected static final double camRadius = DistanceUnit.INCH.fromCm(21);//This means the camera lens is 21cm from the center of the turret

    protected static final double encoderTicksToDegrees = (double) 28/360;//NOT CORRECT MAYBE
    protected static final double encoderTicksToRadians = (double) 28/360 * (Math.PI/180);
    protected static final double gearRatio =
            3 //Motor ratio attachment 1
                    *5 // Motor ratio attachment 2
                    *5; // 18 tooth gear to 90 tooth lazy susan


    public AprilTagWebcam aprilTagWebcam = new AprilTagWebcam();
    public FlyWheel flyWheel = new FlyWheel();
    public Hood hood = new Hood();
    public Intake intake = new Intake();
    public LazySusan lazySusan = new LazySusan();
    public MecanumDriveTrain mecanumDriveTrain = new MecanumDriveTrain();
    public OpmodeIMU opmodeIMU = new OpmodeIMU();

    public void init(HardwareMap hardwareMap, Telemetry telemetry, double turretStartAngle, double turretAngleLimit, double flyWheelEquation, int hoodMaxAngle, double hoodEquation, double imuStartHeading) {
        aprilTagWebcam.init(hardwareMap, telemetry);
        flyWheel.init(hardwareMap, flyWheelEquation);//<--INPUT EQUATION
        hood.init(hardwareMap, hoodMaxAngle, hoodEquation);//<--INPUT EQUATION
        intake.init(hardwareMap);
        mecanumDriveTrain.init(hardwareMap);
        opmodeIMU.init(hardwareMap, telemetry, imuStartHeading);
        lazySusan.init(hardwareMap, telemetry, turretStartAngle, turretAngleLimit);
    }
    public void update() {
        opmodeIMU.update();
        flyWheel.update();
        hood.update();
        lazySusan.update(false, false, false);
        aprilTagWebcam.update(lazySusan.getOrientation(AngleUnit.RADIANS));
        intake.update(flyWheel.shoot, flyWheel.getVelocity());
    }
    public void update(Gamepad gamepad1, Gamepad gamepad2) {
        opmodeIMU.update();
        flyWheel.update();
        hood.update();
        lazySusan.update(gamepad2.left_bumper, gamepad2.right_bumper, gamepad2.dpad_down);
        aprilTagWebcam.update(lazySusan.getOrientation(AngleUnit.RADIANS));
        intake.update(flyWheel.shoot, flyWheel.getVelocity());
        mecanumDriveTrain.update(gamepad1.dpad_down, -gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
    }
}
