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

    // Attributes
    private static Follower teleopfollower;
    private TelemetryManager telemetryM;
    public static Pose startingPose = (Pose) blackboard.getOrDefault("STARTPOSE", new Pose (60, 60, Math.toRadians(90)));
    Robot robot = new Robot();
    public static String alliance = String.valueOf(blackboard.getOrDefault("ALLIANCE", "Blue")), drive_type = String.valueOf(blackboard.getOrDefault("DRIVETYPE", "Robot Oriented"));
    private int interfaceSelection = 1;

    /**
     * Description: Performs the tasks for the robot when the init button is pressed (init phase)
     * Pre-Condition:
     * Post-Condition: Init is performed
     */
    @Override
    public void init() {
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        Object shooterHeading = blackboard.get("SHOOTERHEADING");
        if (shooterHeading == null) {
            shooterHeading = 0;
        }

        robot.init(hardwareMap,
                telemetry,
                (double) shooterHeading,
                180,
                2200,
                30);

        teleopfollower = Constants.createFollower(hardwareMap);
            teleopfollower.setStartingPose(startingPose);
    }

    /**
     * Description: Performs the tasks during the init loop including alliance selection and drive modes
     */
    @Override
    public void init_loop() {
        // Selection for robot values
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

        // Selection for drive type and alliance as an interface
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


    @Override
    public void start() {teleopfollower.startTeleopDrive();}

    /**
     * Description: Performs the required loop for the play phase of the OpMode after the play button is pressed
     * Pre-Condition:
     * Post-Condition:
     */
    @Override
    public void loop() {
        teleopfollower.update();
        // if (robot.aprilTagWebcam.pedroPoseCamCorrection != null) {teleopfollower.setPose(robot.aprilTagWebcam.pedroPoseCamCorrection);}
        // teleopfollower.setHeading(robot.opmodeIMU.pedroPoseHeadingCorrection(AngleUnit.RADIANS));
        MapDrawer.drawDebug(teleopfollower);
        MapDrawer.drawRobot(robot.aprilTagWebcam.pedroPoseCamCorrection); // Will this negate the robot previously drawn?
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
        telemetry.addData("Flywheel Velocity", robot.flyWheel.getCurrentVelocity());
        telemetry.addData("Intake?", robot.intake.intakeOn);
        telemetry.addData("Shoot?", robot.flyWheel.shoot);

        // Update for robot and all mechanisms
        robot.update(gamepad1, gamepad2);

        if(gamepad1.yWasReleased()) {
            robot.flyWheel.shoot = !robot.flyWheel.shoot;
        }
        if (gamepad1.aWasReleased()) {
            robot.intake.intakeOn = !robot.intake.intakeOn;
        }
    }

}
