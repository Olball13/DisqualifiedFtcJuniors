package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Constants.HoodConstants;

public class Hood {

    private Servo hoodServo;
    private double equation;

    public void init(HardwareMap hardwareMap, int maxAngle, double angleEquation) {
        this.equation = angleEquation;
        hoodServo = hardwareMap.get(Servo.class, "ramp_angle_servo");
    }

    public void update() {
         setAnglePercent(equation);
    }
    public void setAnglePercent(double anglePercent) {
         double servoPose =((double) HoodConstants.maxAngle / 360) *Math.min(Math.max(anglePercent, 0), 1);
        hoodServo.setPosition(servoPose);
    }

}
