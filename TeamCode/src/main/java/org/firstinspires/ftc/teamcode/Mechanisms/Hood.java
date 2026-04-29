package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Hood {
    private int maxAngle = 30;
    private Servo ramp_angle_servo;
    private double equation;
    public void init(HardwareMap hardwareMap, int maxAngle, double angleEquation) {
        this.equation = angleEquation;
        ramp_angle_servo = hardwareMap.get(Servo.class, "ramp_angle_servo");
        this.maxAngle = Math.abs(maxAngle);
    }

    public void update() {
         setAnglePercent(equation);
    }
    public void setAnglePercent(double anglePercent) {
         double degreesToServoRotation = (double) 1 / 360; // For every 1 degree is this value to a set Servo Pos
         double anglePercentToDegrees = anglePercent * maxAngle; // Conversion from a percentage (0-1) of the max angle to degrees
         double anglePercentToServoRotation = anglePercentToDegrees * degreesToServoRotation; // The value of of the Servo Pos translated from the angle %
         double normalizedValue = Math.min(Math.max(anglePercentToServoRotation, 0), 1); // Normalize values to fit servo parameters
        ramp_angle_servo.setPosition(normalizedValue);
    }

}
