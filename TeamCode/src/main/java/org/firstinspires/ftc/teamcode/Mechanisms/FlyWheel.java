package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class FlyWheel {
    private DcMotor flywheelMotor1;
    private DcMotor flywheelMotor2;
    public boolean shoot = false;
    private double equation;
    private double desiredVelocity;
    private double currentVelocity;

    /**
     * Description: Initializes software variables and links them up to hardware variables, sets motor directions, and initializes equation.
     * Pre-Condition: Parameters are initialized and hardware map is set up with names as shown
     * Post-Condition: Software variables are initialized and equation is set
     * @param hardwareMap The hardware map containing the motors for the flywheel
     * @param shootEquation The equation the flywheel uses based of the camera detections to set its speed
     */
    public void init(HardwareMap hardwareMap, double shootEquation) {
        equation = shootEquation;
        flywheelMotor1 = hardwareMap.get(DcMotor.class, "flywheel_motor_1");
        flywheelMotor2 = hardwareMap.get(DcMotor.class, "flywheel_motor_2");
        flywheelMotor1.setDirection(DcMotor.Direction.FORWARD);
        flywheelMotor2.setDirection(DcMotor.Direction.REVERSE);
        flywheelMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheelMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Description: Has the velocity commands for the flywheel to run every iteration
     * Pre-Condition: All software and hardware is initialized
     * Post-Condition: Velocity is manipulated as required
     */
    public void update() {
        if (shoot) {
            if (equation == 0) {
                setDesiredVelocity(2200);
            } else {
                setDesiredVelocity(equation);
            }
            ((DcMotorEx) flywheelMotor1).setVelocity(desiredVelocity);
            ((DcMotorEx) flywheelMotor2).setVelocity(desiredVelocity);
        } else {
            setDesiredVelocity(0);
        }
        currentVelocity = (((DcMotorEx) flywheelMotor1).getVelocity() + ((DcMotorEx) flywheelMotor2).getVelocity()) / 2;
    }

    // Getters and Setters
    public double getDesiredVelocity() {
        return this.desiredVelocity;
    }

    public void setDesiredVelocity(double velocity) {
        desiredVelocity = velocity;
    }

    public double getCurrentVelocity() {
        return this.currentVelocity;
    }

}
