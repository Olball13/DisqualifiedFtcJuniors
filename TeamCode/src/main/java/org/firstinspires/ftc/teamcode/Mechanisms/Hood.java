package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Constants.HoodConstants;

public class Hood {

    private Servo hoodServo;
    private double equation;

    /**
     * Description: Initializes software variables and links them up to hardware variables and initializes equation
     * Pre-Condition: Parameters are initialized and hardware map is set up with names as shown
     * Post-Condition: Software variables are initialized and equation is set
     * @param hardwareMap The hardware map containing the hardware for the hood
     * @param angleEquation The equation the hood uses to set its angle
     */
    public void init(HardwareMap hardwareMap, double angleEquation) {
        this.equation = angleEquation;
        hoodServo = hardwareMap.get(Servo.class, "ramp_angle_servo");
    }

    public void update() {
         setAnglePercent(equation);
    }
    public void setAnglePercent(double anglePercent) {
         double degreesToServoRotation = (double) 1 / 360; // For every 1 degree is this value to a set Servo Pos
         double anglePercentToDegrees = anglePercent * HoodConstants.maxAngle; // Conversion from a percentage (0-1) of the max angle to degrees
         double anglePercentToServoRotation = anglePercentToDegrees * degreesToServoRotation; // The value of of the Servo Pos translated from the angle %
         double normalizedValue = Math.min(Math.max(anglePercentToServoRotation, 0), 1); // Normalize values to fit servo parameters
         hoodServo.setPosition(normalizedValue);
    }

}
