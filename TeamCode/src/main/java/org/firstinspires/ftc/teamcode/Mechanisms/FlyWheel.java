package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class FlyWheel {
    private DcMotor flywheelMotor1;
    private DcMotor flywheelMotor2;
    public boolean shoot = false;
    private double equation;
    public double desiredVelocity;

    public void init(HardwareMap hardwareMap, double shootEquation) {
        equation = shootEquation;
        flywheelMotor1 = hardwareMap.get(DcMotor.class, "flywheel_motor_1");
        flywheelMotor2 = hardwareMap.get(DcMotor.class, "flywheel_motor_2");
        flywheelMotor1.setDirection(DcMotor.Direction.FORWARD);
        flywheelMotor2.setDirection(DcMotor.Direction.REVERSE);
        flywheelMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheelMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    public void setVelocity(double velocity) {
        ((DcMotorEx) flywheelMotor1).setVelocity(velocity);
        ((DcMotorEx) flywheelMotor2).setVelocity(velocity);
        desiredVelocity = velocity;
    }
    public void update() {
        if (shoot) {
            if (equation == 0) {
                setVelocity(1500);
            } else {
                setVelocity(equation);
            }
        } else {
            setVelocity(0);
        }
    }

    public double getVelocity() {
        double totalVelocity = ((DcMotorEx) flywheelMotor1).getVelocity() + ((DcMotorEx) flywheelMotor2).getVelocity();
        return totalVelocity/2;
    }

}
