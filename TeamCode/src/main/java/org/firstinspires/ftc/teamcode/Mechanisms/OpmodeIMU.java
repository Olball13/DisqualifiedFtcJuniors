package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class OpmodeIMU {//This should proof useful to keep the proper orientation for the robot, as the drivetrain's imu resets when adjusting yaw for field relative
    private IMU imu;
    private Double startingOffset;
    private Telemetry telemetry;
    public void init(HardwareMap hardwareMap, Telemetry telemetry, double startAngle) {
        this.telemetry = telemetry;
        startingOffset = startAngle;
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.FORWARD;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection =
                RevHubOrientationOnRobot.UsbFacingDirection.UP;

        RevHubOrientationOnRobot orientationOnRobot = new
                RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new com.qualcomm.robotcore.hardware.IMU.Parameters(orientationOnRobot));
    }
    public void update() {
        telemetry.addData("IMU Raw Heading", imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));
    }
    public double pedroPoseHeadingCorrection(AngleUnit angleUnit) {
        double recordedValue = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        double unnormalizedOffsetCorrect = recordedValue + startingOffset;
        double normalizedPedroCorrect = (((unnormalizedOffsetCorrect + 90) % 360) + 360) % 360; // Does this makes sense?? I will explain
        /* The pedroCorrect is the correction from imu readings to Pedro's angle coordinates
        90 is moving the context of the robot 90 degrees left
        the first module normalizes the value to fit within the range of -+360
        Theres is still a possibility of a negative value (-1 instead of 359)
        That's why another 360 is added, then moduled to account for previously possible positive values
         */
        if (angleUnit != AngleUnit.DEGREES) {
            normalizedPedroCorrect = Math.toRadians(normalizedPedroCorrect);
        }
        return normalizedPedroCorrect;
    }
}
