package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class LazySusan {
    private double angleLimit = 180;// This would only allow the turret to move 90 degrees left and right
    private double startAngle;
    private Telemetry telemetry;
    private static DcMotor lazysusan_motor;

    public void init(HardwareMap hardwareMap, Telemetry telemetry, double startAngle, double angleLimit) {
        //INITIALIZE the motor + telemetry and set the angle limit from its default value
        this.telemetry = telemetry;
        this.angleLimit = angleLimit;
        this.startAngle = startAngle;
        lazysusan_motor = hardwareMap.get(DcMotor.class, "lazysusan_motor");
        lazysusan_motor.setDirection(DcMotor.Direction.FORWARD);
        lazysusan_motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lazysusan_motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        telemetry.addLine("THE TURRET SHOULD BE FACING FORWARD RELATIVE TO THE ROBOT");
        telemetry.addLine("IF NOT THEN RESET AND FIX IT");
    }
    public void update (boolean left, boolean right, boolean centerTurret){
        telemetry.addData("Turret Robot Relative Orientation (Degrees)", (getOrientation(AngleUnit.DEGREES)));
        if (lazysusan_motor.getMode() == DcMotor.RunMode.RUN_TO_POSITION) {
            lazysusan_motor.setPower(1);
            if (!centerTurret) {
                lazysusan_motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
            return;
        }
        if (left) {
            lazysusan_motor.setPower(-0.5);
        } else if (right) {
            lazysusan_motor.setPower(0.5);
        } else if (centerTurret) {
            lazysusan_motor.setTargetPosition(0);
            lazysusan_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }else if (AprilTagWebcam.degreeCorrection != 0) {
            double degCorr = AprilTagWebcam.degreeCorrection;
            // A function that would gradually move the turret to the target position with the amount of radians left to the target position
            lazysusan_motor.setPower(degCorr/Math.abs(degCorr) * (Math.min(1, Math.abs(degCorr / 20)))); // 20 is my guess :)
        } else {
            lazysusan_motor.setPower(0);
        }
        if(getOrientation(AngleUnit.DEGREES) < -angleLimit/2 && lazysusan_motor.getPower() < 0) {
            lazysusan_motor.setPower(0);
            telemetry.addLine("ANGLE LIMIT REACHED");
        }
        if(getOrientation(AngleUnit.DEGREES) > angleLimit/2 && lazysusan_motor.getPower() > 0) {
            lazysusan_motor.setPower(0);
            telemetry.addLine("ANGLE LIMIT REACHED");
        }
    }
    public double getOrientation(AngleUnit angleUnit) {
        if (angleUnit.equals(AngleUnit.DEGREES)) {
            return (lazysusan_motor.getCurrentPosition() *Robot.encoderTicksToDegrees/Robot.gearRatio) + startAngle;
        } else if (angleUnit.equals(AngleUnit.RADIANS)) {
            return lazysusan_motor.getCurrentPosition()*Robot.encoderTicksToRadians/Robot.gearRatio + (Math.toRadians(startAngle));
        } else {
            return lazysusan_motor.getCurrentPosition()/Robot.gearRatio + startAngle/Robot.encoderTicksToDegrees; // Out of ticks (28 = Full Rotation)
        }
    }
}
