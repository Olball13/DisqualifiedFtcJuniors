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
         double servoPose =((double) maxAngle / 360) *Math.min(Math.max(anglePercent, 0), 1);
        ramp_angle_servo.setPosition(servoPose);
    }

}
