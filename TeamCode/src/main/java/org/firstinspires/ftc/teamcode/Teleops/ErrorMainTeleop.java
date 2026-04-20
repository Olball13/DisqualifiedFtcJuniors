package org.firstinspires.ftc.teamcode.Teleops;


import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Mechanisms.AprilTagWebcam;
import org.firstinspires.ftc.teamcode.Mechanisms.Hood;
import org.firstinspires.ftc.teamcode.Mechanisms.Intake;
import org.firstinspires.ftc.teamcode.Mechanisms.LazySusan;
import org.firstinspires.ftc.teamcode.Mechanisms.MecanumDriveTrain;
import org.firstinspires.ftc.teamcode.Mechanisms.FlyWheel;
import org.firstinspires.ftc.teamcode.Mechanisms.OpmodeIMU;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.pedroPathing.MapDrawer;

@Configurable
@TeleOp
public class ErrorMainTeleop extends OpMode {
    private static  Follower teleopfollower;
    private TelemetryManager telemetryM;
    public static Pose startingPose = new Pose(60, 60, Math.toRadians(90)); // USE BLACKBOARD INSTEAD
    FlyWheel flyWheel = new FlyWheel();
    LazySusan lazySusan = new LazySusan();
    Hood hood = new Hood();
    AprilTagWebcam aprilTagWebcam = new AprilTagWebcam();
    Intake intake = new Intake();
    MecanumDriveTrain mecanumDriveTrain = new MecanumDriveTrain();
    OpmodeIMU opmodeIMU = new OpmodeIMU();
    public static String alliance = String.valueOf(blackboard.get("ALLIANCE")), drive_type = String.valueOf(blackboard.get("DRIVETYPE"));
    private int interface_selection = 1;
    @Override
    public void init() {
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        flyWheel.init(hardwareMap);
        aprilTagWebcam.init(hardwareMap, telemetry);
        hood.init(hardwareMap, 30);
        intake.init(hardwareMap);
        mecanumDriveTrain.init(hardwareMap);
        opmodeIMU.init(hardwareMap, telemetry, Math.toDegrees(startingPose.getHeading()));
        lazySusan.init(hardwareMap, telemetry, 180);
        teleopfollower = Constants.createFollower(hardwareMap);
            teleopfollower.setStartingPose(startingPose);
    }

    @Override
    public void init_loop() {
        switch (interface_selection) {
            case 1:
                if (gamepad1.leftBumperWasPressed()) {
                    if (alliance.equals("Blue")) {
                        alliance = "Red";
                    } else {
                        alliance = "Blue";
                    }
                }
                telemetry.addData("Alliance>", alliance);
                telemetry.addData("Drive Type", drive_type);

                break;
            case 2:
                if (gamepad1.leftBumperWasPressed()) {
                    if (drive_type.equals("Field Oriented")) {
                        drive_type = "Robot Oriented";
                    } else {
                        drive_type = "Field Oriented";
                    }
                }
                telemetry.addData("Alliance", alliance);
                telemetry.addData("Drive Type>", drive_type);
                break;
        }

        if (gamepad1.dpadUpWasPressed()) {
            interface_selection -= 1;
        }
        if (gamepad1.dpadDownWasPressed()) {
            interface_selection += 1;
        }
        if (interface_selection < 1) {
            interface_selection = 2;
        }
        if (interface_selection > 2) {
            interface_selection = 1;
        }
    }
    public void start() {teleopfollower.startTeleopDrive();
    }

    @Override
    public void loop() {
        teleopfollower.update();
        if (AprilTagWebcam.pedroPoseCamCorrection != null) {teleopfollower.setPose(AprilTagWebcam.pedroPoseCamCorrection);}
        teleopfollower.setHeading(opmodeIMU.pedroPoseHeadingCorrection(AngleUnit.RADIANS));
        MapDrawer.drawDebug(teleopfollower);
        telemetryM.update();
        telemetryM.debug("position", teleopfollower.getPose());
        telemetryM.debug("velocity", teleopfollower.getVelocity());
        telemetry.addLine("----Gmpd 1----");
        telemetry.addLine("dpad down to reset Yaw");
        telemetry.addLine("Press Y for shoot toggle");
        telemetry.addLine("Press A for intake toggle");
        telemetry.addLine("----Gmpd 2----");
        telemetry.addLine("L1 for turret left");
        telemetry.addLine("R1 for turret Right");
        telemetry.addLine("dpad down to move turret to 0 (robot relative)");
        telemetry.addLine(" ");
        telemetry.addData("Pedro Pose", Math.round(teleopfollower.getPose().getX()) + ", " + Math.round(teleopfollower.getPose().getY()) + ", " + Math.round(teleopfollower.getHeading()));
        telemetry.addData("Camera Pose", AprilTagWebcam.pedroPoseCamCorrection);
        telemetry.addData("IMU Pedro Heading", opmodeIMU.pedroPoseHeadingCorrection(AngleUnit.DEGREES));
        telemetry.addLine(" ");
        telemetry.addData("Flywheel Velocity", FlyWheel.getVelocity());
        telemetry.addData("Intake?", Intake.intake_On);
        telemetry.addData("Shoot?", FlyWheel.shoot);

        opmodeIMU.update();
        flyWheel.update(0); //<--INPUT EQUATION
        hood.setRamp_angle(0); //<--INPUT EQUATION
        lazySusan.update(gamepad2.left_bumper, gamepad2.right_bumper, gamepad2.dpad_down);
        telemetry.addLine(" ");
        aprilTagWebcam.update();
        intake.update();
        mecanumDriveTrain.update(gamepad1.dpadDownWasPressed(), gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        if(gamepad1.yWasPressed()) {
            FlyWheel.shoot = !FlyWheel.shoot;
        }
        if (gamepad2.aWasPressed()) {
            Intake.intake_On = !Intake.intake_On;
        }
    }

}
