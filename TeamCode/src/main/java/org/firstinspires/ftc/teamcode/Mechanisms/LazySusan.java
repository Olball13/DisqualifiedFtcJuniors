package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class LazySusan {
    private static final double encoderTicksToDegrees = (double) 28/360;//NOT CORRECT MAYBE
    private static final double encoderTicksToRadians = (double) 28/360 * (Math.PI/180);
    private static final double gearRatio =
            3 //Motor ratio attachment 1
            *5 // Motor ratio attachment 2
            *5; // 18 tooth gear to 90 tooth lazy susan
    private double angleLimit = 180;// This would only allow the turret to move 90 degrees left and right
    private Telemetry telemetry;
    private static DcMotor lazysusan_motor;

    public void init(HardwareMap hardwareMap, Telemetry telemetry, double angleLimit) {
        //INITIALIZE the motor + telemetry and set the angle limit from its default value
        this.telemetry = telemetry;
        lazysusan_motor = hardwareMap.get(DcMotor.class, "lazysusan_motor");
        lazysusan_motor.setDirection(DcMotor.Direction.FORWARD);
        lazysusan_motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lazysusan_motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.angleLimit = angleLimit;
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
            double deg_corr = AprilTagWebcam.degreeCorrection;
            // A function that would gradually move the turret to the target position with the amount of radians left to the target position
            lazysusan_motor.setPower(deg_corr/Math.abs(deg_corr) * (Math.min(1, Math.abs(deg_corr / 20)))); // 20 is my guess :)
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
    public static double getOrientation(AngleUnit angleUnit) {
        if (angleUnit.equals(AngleUnit.DEGREES)) {
            return lazysusan_motor.getCurrentPosition()*encoderTicksToDegrees/gearRatio;
        } else if (angleUnit.equals(AngleUnit.RADIANS)) {
            return Math.toRadians(lazysusan_motor.getCurrentPosition()*encoderTicksToRadians/gearRatio);
        } else {
            return lazysusan_motor.getCurrentPosition()/gearRatio; // Out of ticks (28 = Full Rotation)
        }
    }
}
