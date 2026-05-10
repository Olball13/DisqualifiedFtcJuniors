package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Constants.LazySusanConstants;

public class LazySusan {
    private double startAngle;
    private Telemetry telemetry;
    private DcMotor lazySusanMotor;
    private final PIDFCoefficients pidfCoefficients  = new PIDFCoefficients(1,2,5, 3);

    /**
     * Description: Initializes required hardwareMaps, internal telemetry, and the starting angle of the LazySusan
     * Pre-Condition: HardwareMap and Telemetry objects must be declared (native to OpModes)
     * Post-Condition: LazySusan will be properly initialized with a set starting angle
     * @param hardwareMap The Hardware map with the motor of the LazySusan
     * @param telemetry The internal telemetry of the LazySusan
     * @param startAngle The starting orientation of the turret, helpful after auto
     */
    public void init(HardwareMap hardwareMap, Telemetry telemetry, double startAngle) {
        //INITIALIZE the motor + telemetry and set the angle limit from its default value
        this.telemetry = telemetry;
        this.startAngle = startAngle;
        lazySusanMotor = hardwareMap.get(DcMotor.class, "lazysusan_motor");
        lazySusanMotor.setDirection(DcMotor.Direction.FORWARD);
        lazySusanMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lazySusanMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lazySusanMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //((DcMotorEx) lazySusanMotor).setPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION, pidfCoefficients);
        telemetry.addLine("THE TURRET SHOULD BE FACING FORWARD RELATIVE TO THE ROBOT");
        telemetry.addLine("IF NOT THEN RESET AND FIX IT");
    }

    public void update (Gamepad gamepad2, AprilTagWebcam aprilTagWebcam){
        telemetry.addData("Turret Robot Relative Orientation (Degrees)", (getOrientation(AngleUnit.DEGREES)));
        if (lazySusanMotor.getMode() == DcMotor.RunMode.RUN_TO_POSITION) {
            lazySusanMotor.setPower(1);
        }
            if (gamepad2.left_bumper) {
                lazySusanMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                lazySusanMotor.setPower(-0.5);

            } else if (gamepad2.right_bumper) {
                lazySusanMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                lazySusanMotor.setPower(0.5);

            } else if (gamepad2.dpad_down) {
                lazySusanMotor.setTargetPosition(0);
                lazySusanMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            }else if (aprilTagWebcam.degreeCorrection != 0) {
                // A function that would gradually move the turret to the target position with the amount of radians left to the target position
                double degCorr = aprilTagWebcam.degreeCorrection;
                lazySusanMotor.setPower(Math.signum(degCorr) * Math.min(1, Math.pow(Math.abs(degCorr), 3) * 30)); // 5 is my guess :)
                //lazySusanMotor.setTargetPosition((int) (lazySusanMotor.getCurrentPosition() + aprilTagWebcam.degreeCorrection * 10));
                lazySusanMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            } else {
                lazySusanMotor.setPower(0);
            }

        if(getOrientation(AngleUnit.DEGREES) < -LazySusanConstants.angleLimit / 2 && lazySusanMotor.getPower() < 0) {
            lazySusanMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            lazySusanMotor.setPower(0);
            telemetry.addLine("ANGLE LIMIT REACHED");
        }
        if(getOrientation(AngleUnit.DEGREES) > LazySusanConstants.angleLimit / 2 && lazySusanMotor.getPower() > 0) {
            lazySusanMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            lazySusanMotor.setPower(0);
            telemetry.addLine("ANGLE LIMIT REACHED");
        }
    }

    public void update (AprilTagWebcam aprilTagWebcam){
        telemetry.addData("Turret Robot Relative Orientation (Degrees)", (getOrientation(AngleUnit.DEGREES)));
        if (aprilTagWebcam.degreeCorrection != 0) {
            double degCorr = aprilTagWebcam.degreeCorrection;
            lazySusanMotor.setPower(Math.signum(degCorr) * Math.min(1, Math.pow(Math.abs(degCorr), 3) * 30));
            lazySusanMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            lazySusanMotor.setPower(0);
        }

        if(getOrientation(AngleUnit.DEGREES) < -LazySusanConstants.angleLimit / 2 && lazySusanMotor.getPower() < 0) {
            lazySusanMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            lazySusanMotor.setPower(0);
            telemetry.addLine("ANGLE LIMIT REACHED");
        }
        if(getOrientation(AngleUnit.DEGREES) > LazySusanConstants.angleLimit / 2 && lazySusanMotor.getPower() > 0) {
            lazySusanMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            lazySusanMotor.setPower(0);
            telemetry.addLine("ANGLE LIMIT REACHED");
        }
    }

    public double getOrientation(AngleUnit angleUnit) {
        if (angleUnit.equals(AngleUnit.DEGREES)) {
            return (lazySusanMotor.getCurrentPosition() / LazySusanConstants.encoderTicksToDegrees / LazySusanConstants.gearRatio) + startAngle;
        } else if (angleUnit.equals(AngleUnit.RADIANS)) {
            return lazySusanMotor.getCurrentPosition() / LazySusanConstants.encoderTicksToRadians / LazySusanConstants.gearRatio + (Math.toRadians(startAngle));
        } else {
            return lazySusanMotor.getCurrentPosition() / LazySusanConstants.gearRatio + (startAngle * LazySusanConstants.encoderTicksToDegrees); // Out of ticks (28 = Full Rotation)
        }
    }
}
