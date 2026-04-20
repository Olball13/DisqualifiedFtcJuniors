package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Hood {
    private int maxAngle = 30;
    private Servo ramp_angle_servo;
    public void init(HardwareMap hardwareMap, int maxAngle) {
        ramp_angle_servo = hardwareMap.get(Servo.class, "ramp_angle_servo");
        this.maxAngle = Math.abs(maxAngle);
    }

    public void setRamp_angle(double rotationPercent) {
        double servoPose = ((double) maxAngle / 360) *rotationPercent;
        if(rotationPercent > 1) {
            ramp_angle_servo.setPosition(servoPose/rotationPercent);
        } else if(rotationPercent < 0) {
            ramp_angle_servo.setPosition(0);
        } else {
            ramp_angle_servo.setPosition(servoPose);
        }
    }

}
