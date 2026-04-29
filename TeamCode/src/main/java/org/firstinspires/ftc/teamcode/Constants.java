package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.DriveEncoderConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static final class AprilTagWebcamConstants {
        public static final double camXCorrection = 0;// this is the x difference of the turret to the robots center (0)
        public static final double camYCorrection = DistanceUnit.INCH.fromCm(0.45);// this is the y difference of the turret to the robots center (+ value)
        public static final double camRadius = DistanceUnit.INCH.fromCm(21);//This means the camera lens is 21cm from the center of the turret
    }

    public static final class LazySusanConstants {
        public static final double encoderTicksToDegrees = (double) 28/360;//NOT CORRECT MAYBE
        public static final double encoderTicksToRadians = (double) 28/360 * (Math.PI/180);
        public static final double gearRatio =
            3 //Motor ratio attachment 1
                    *5 // Motor ratio attachment 2
                    *5; // 18 tooth gear to 90 tooth lazy susan
        public static final double angleLimit = 180;// This would only allow the turret to move 90 degrees left and right

    }

    public static final class HoodConstants {
        public static final int maxAngle = 30;
    }

}
