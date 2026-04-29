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
import org.firstinspires.ftc.teamcode.Mechanisms.Robot;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.pedroPathing.MapDrawer;

@Configurable
@TeleOp
public class ErrorMainTeleop extends OpMode {
    private static Follower teleopfollower;
    private TelemetryManager telemetryM;
    public static Pose startingPose = (Pose) blackboard.getOrDefault("STARTPOSE", new Pose (60, 60, Math.toRadians(90)));
    Robot robot = new Robot();
    public static String alliance = String.valueOf(blackboard.get("ALLIANCE")), drive_type = String.valueOf(blackboard.get("DRIVETYPE"));
    private int interfaceSelection = 1;
    @Override
    public void init() {
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();

        robot.init(hardwareMap, telemetry,
                (double) blackboard.getOrDefault("SHOOTERHEADING", 0),
                180,
                2200,
                30,
                (AprilTagWebcam.rangeCorrection % 125)/125,
                startingPose.getHeading());

        teleopfollower = Constants.createFollower(hardwareMap);
            teleopfollower.setStartingPose(startingPose);
    }

    @Override
    public void init_loop() {
        switch (interfaceSelection) {
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
            interfaceSelection -= 1;
        }
        if (gamepad1.dpadDownWasPressed()) {
            interfaceSelection += 1;
        }
        if (interfaceSelection < 1) {
            interfaceSelection = 2;
        }
        if (interfaceSelection > 2) {
            interfaceSelection = 1;
        }
    }
    public void start() {teleopfollower.startTeleopDrive();}

    @Override
    public void loop() {
        teleopfollower.update();
        // if (robot.aprilTagWebcam.pedroPoseCamCorrection != null) {teleopfollower.setPose(robot.aprilTagWebcam.pedroPoseCamCorrection);}
        // teleopfollower.setHeading(robot.opmodeIMU.pedroPoseHeadingCorrection(AngleUnit.RADIANS));
        MapDrawer.drawDebug(teleopfollower);
        MapDrawer.drawRobot(robot.aprilTagWebcam.pedroPoseCamCorrection);
        telemetryM.update();
        telemetryM.debug("position", teleopfollower.getPose());
        telemetryM.debug("velocity", teleopfollower.getVelocity());
        telemetryM.debug("HELLO");
        /*
        telemetry.addLine("----Gmpd 1----");
        telemetry.addLine("dpad down to reset Yaw");
        telemetry.addLine("Press Y for shoot toggle");
        telemetry.addLine("Press A for intake toggle");
        telemetry.addLine("----Gmpd 2----");
        telemetry.addLine("L1 for turret left");
        telemetry.addLine("R1 for turret Right");
        telemetry.addLine("dpad down to move turret to 0 (robot relative)");
        */
        telemetry.addData("Pedro Pose", Math.round(teleopfollower.getPose().getX()) + ", " + Math.round(teleopfollower.getPose().getY()) + ", " + Math.round(teleopfollower.getHeading()));
        telemetry.addData("Camera Pose", robot.aprilTagWebcam.pedroPoseCamCorrection);
        telemetry.addData("IMU Pedro Heading", robot.opmodeIMU.pedroPoseHeadingCorrection(AngleUnit.DEGREES));
        telemetry.addData("Flywheel Velocity", robot.flyWheel.getVelocity());
        telemetry.addData("Intake?", robot.intake.intakeOn);
        telemetry.addData("Shoot?", robot.flyWheel.shoot);

        robot.update(gamepad1, gamepad2);

        if(gamepad1.yWasReleased()) {
            robot.flyWheel.shoot = !robot.flyWheel.shoot;
        }
        if (gamepad1.aWasReleased()) {
            robot.intake.intakeOn = !robot.intake.intakeOn;
        }
    }

}
