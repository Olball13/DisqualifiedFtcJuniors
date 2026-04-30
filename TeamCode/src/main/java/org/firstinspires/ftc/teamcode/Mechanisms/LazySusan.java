package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Constants.LazySusanConstants;

public class LazySusan {
    private double startAngle;
    private Telemetry telemetry;
    private DcMotor lazySusanMotor;

    public void init(HardwareMap hardwareMap, Telemetry telemetry, double startAngle) {
        //INITIALIZE the motor + telemetry and set the angle limit from its default value
        this.telemetry = telemetry;
        this.startAngle = startAngle;
        lazySusanMotor = hardwareMap.get(DcMotor.class, "lazysusan_motor");
        lazySusanMotor.setDirection(DcMotor.Direction.FORWARD);
        lazySusanMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lazySusanMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        telemetry.addLine("THE TURRET SHOULD BE FACING FORWARD RELATIVE TO THE ROBOT");
        telemetry.addLine("IF NOT THEN RESET AND FIX IT");
    }

    public void update (boolean left, boolean right, boolean centerTurret, AprilTagWebcam webcam){
        telemetry.addData("Turret Robot Relative Orientation (Degrees)", (getOrientation(AngleUnit.DEGREES)));
        if (lazySusanMotor.getMode() == DcMotor.RunMode.RUN_TO_POSITION) {
            lazySusanMotor.setPower(1);
            if (!centerTurret) {
                lazySusanMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
            return;
        }
        if (left) {
            lazySusanMotor.setPower(-0.5);
        } else if (right) {
            lazySusanMotor.setPower(0.5);
        } else if (centerTurret) {
            lazySusanMotor.setTargetPosition(0);
            lazySusanMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        } else if (webcam.degreeCorrection != 0) {
            double degCorr = webcam.degreeCorrection;
            // A function that would gradually move the turret to the target position with the amount of radians left to the target position
            lazySusanMotor.setPower(degCorr/Math.abs(degCorr) * (Math.min(1, Math.abs(degCorr / 20)))); // 20 is my guess :)
        } else {
            lazySusanMotor.setPower(0);
        }
        if(getOrientation(AngleUnit.DEGREES) < -LazySusanConstants.angleLimit / 2 && lazySusanMotor.getPower() < 0) {
            lazySusanMotor.setPower(0);
            telemetry.addLine("ANGLE LIMIT REACHED");
        }
        if(getOrientation(AngleUnit.DEGREES) > LazySusanConstants.angleLimit / 2 && lazySusanMotor.getPower() > 0) {
            lazySusanMotor.setPower(0);
            telemetry.addLine("ANGLE LIMIT REACHED");
        }
    }

    public double getOrientation(AngleUnit angleUnit) {
        if (angleUnit.equals(AngleUnit.DEGREES)) {
            return ((lazySusanMotor.getCurrentPosition() / LazySusanConstants.encoderTicksToDegrees) / LazySusanConstants.gearRatio) + startAngle;
        } else if (angleUnit.equals(AngleUnit.RADIANS)) {
            return ((lazySusanMotor.getCurrentPosition() / LazySusanConstants.encoderTicksToRadians) / LazySusanConstants.gearRatio) + (Math.toRadians(startAngle));
        } else {
            return lazySusanMotor.getCurrentPosition() / LazySusanConstants.gearRatio + startAngle * LazySusanConstants.encoderTicksToDegrees; // Out of ticks (28 = Full Rotation)
        }
    }
}
