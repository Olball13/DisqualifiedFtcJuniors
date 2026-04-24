package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    private DcMotor intake_motor;
    private CRServo intake_servo;
    public boolean intake_On = false;

    public void init(HardwareMap hardwareMap) {
        intake_motor = hardwareMap.get(DcMotor.class, "intake_motor");
        intake_motor.setDirection(DcMotor.Direction.REVERSE);
        intake_servo = hardwareMap.get(CRServo.class, "intake_servo");
        intake_servo.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    public void update(boolean shoot, double flyWheelVelocity) {
        if (intake_On) {
            intake_motor.setPower(1);
        } else {
            intake_motor.setPower(0);
        }
        if (shoot && flyWheelVelocity >= FlyWheel.desiredVelocity *0.95) {
            intake_servo.setPower(1);
        } else {
            if (intake_On) {
                intake_servo.setPower(-1);
            } else {
                intake_servo.setPower(0);
            }
        }
    }

}
