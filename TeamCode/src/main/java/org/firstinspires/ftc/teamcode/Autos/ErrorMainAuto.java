package org.firstinspires.ftc.teamcode.Autos;

import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.blue_Long_Start;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.blue_Short_Start;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.follower;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Blue_Lane_1_Pickup;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Blue_Lane_1_Start;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Blue_Lane_2_Pickup;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Blue_Lane_2_Start;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Blue_Lane_3_Pickup;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Blue_Lane_3_Start;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Blue_Long_End;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Blue_Long_Shoot;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Blue_Short_Shoot_1;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Blue_Short_Shoot_2;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Red_Lane_1_Pickup;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Red_Lane_1_Start;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Red_Lane_2_Pickup;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Red_Lane_2_Start;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Red_Lane_3_Pickup;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Red_Lane_3_Start;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Red_Long_End;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Red_Long_Shoot;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Red_Short_Shoot_1;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.go_Red_Short_Shoot_2;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.red_Long_Start;
import static org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower.red_Short_Start;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Mechanisms.AprilTagWebcam;
import org.firstinspires.ftc.teamcode.Mechanisms.PedroAutoFollower;
import org.firstinspires.ftc.teamcode.Mechanisms.Robot;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.pedroPathing.MapDrawer;

@Autonomous
public class ErrorMainAuto extends OpMode {
    // Info for how to run auto
    private TelemetryManager telemetryM;
    private static boolean use_Lane = false;
    private static String alliance_Colour = "Blue", start_From = "Short", drive_Type = "Field Oriented";
    private static final float general_Shoot_Time = 5;
    Robot robot = new Robot();
    double startHeading;

    /**
     * Variable for selection placement in the interface before start
     */
    private int interface_selection = 1;
    private int path_State = 0;
    ElapsedTime autoTime = new ElapsedTime();
    ElapsedTime pathTime = new ElapsedTime();
    @Override
    public void init() {
        //PedroPathing stuff
        follower = Constants.createFollower(hardwareMap);
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        PedroAutoFollower.buildPaths();
        //Init Robot Mechanisms
        robot.init(hardwareMap,
                telemetry,
                0,
                180,
                220,
                30);
    }

    public void init_loop() {
        autoSelectUpdate();
    }

    @Override
    public void start() {
        //Get the start heading and input info to the blackboard from the prechosen opmode selected in init_loop
        startHeading = follower.getHeading();
        autoTime.reset();
        blackboard.put("ALLIANCE", alliance_Colour);
        blackboard.put("DRIVETYPE", drive_Type);
    }

    @Override
    public void loop() {
        //Apriltag Pose Correction
        if (robot.aprilTagWebcam.pedroPoseCamCorrection != null) {follower.setPose(robot.aprilTagWebcam.pedroPoseCamCorrection);}
        follower.setHeading(robot.opmodeIMU.pedroPoseHeadingCorrection(AngleUnit.RADIANS));

        //Update Robot Mechanisms
        robot.update();

        //More PedroPathing stuff
        MapDrawer.drawDebug(follower);
        telemetryM.update();
        telemetryM.debug("position", follower.getPose());
        telemetryM.debug("velocity", follower.getVelocity());
        follower.update();

        //Update Auto State
        autoUpdate();
    }
    /**
     * This updates the telemetry interface and values to be selected for running a personalized auto
     */
    private void autoSelectUpdate() {
        telemetry.clearAll();
        switch (interface_selection) {
            case 1:
                if (gamepad1.leftBumperWasPressed()) {
                    if (alliance_Colour.equals("Blue")) {
                        alliance_Colour = "Red";
                        if (start_From.equals("Short")) {
                            follower.setStartingPose(red_Short_Start);
                        } else {
                            follower.setStartingPose(red_Long_Start);
                        }
                    } else {
                        alliance_Colour = "Blue";
                        if (start_From.equals("Short")) {
                            follower.setStartingPose(blue_Short_Start);
                        } else {
                            follower.setStartingPose(blue_Long_Start);
                        }
                    }
                }
                telemetry.addData("Alliance>", alliance_Colour);
                telemetry.addData("Starting Position", start_From);
                telemetry.addData("Use Intake?", use_Lane);
                telemetry.addData("Drive Type", drive_Type);

                break;
            case 2:
                if (gamepad1.leftBumperWasPressed()) {
                    if (start_From.equals("Short")) {
                        start_From = "Long";
                        if (alliance_Colour.equals("Blue")) {
                            follower.setStartingPose(blue_Long_Start);
                        } else {
                            follower.setStartingPose(red_Long_Start);
                        }
                    } else {
                        start_From = "Short";
                        if (alliance_Colour.equals("Blue")) {
                            follower.setStartingPose(blue_Short_Start);
                        } else {
                            follower.setStartingPose(red_Short_Start);
                        }
                    }
                }
                telemetry.addData("Alliance", alliance_Colour);
                telemetry.addData("Starting Position>", start_From);
                telemetry.addData("Use Intake?", use_Lane);
                telemetry.addData("Drive Type", drive_Type);

                break;
            case 3:
                if (gamepad1.leftBumperWasPressed()) {
                    use_Lane = !use_Lane;
                }
                telemetry.addData("Alliance", alliance_Colour);
                telemetry.addData("Starting Position", start_From);
                telemetry.addData("Use Intake?>", use_Lane);
                telemetry.addData("Drive Type", drive_Type);

                break;
            case 4:
                if (gamepad1.leftBumperWasPressed()) {
                    if (drive_Type.equals("Field Oriented")) {
                        drive_Type = "Robot Oriented";
                    } else {
                        drive_Type = "Field Oriented";
                    }
                }
                telemetry.addData("Alliance", alliance_Colour);
                telemetry.addData("Starting Position", start_From);
                telemetry.addData("Use Intake?", use_Lane);
                telemetry.addData("Drive Type>", drive_Type);
                break;
        }

        if (gamepad1.dpadUpWasPressed()) {
            interface_selection -= 1;
        }
        if (gamepad1.dpadDownWasPressed()) {
            interface_selection += 1;
        }
        if (interface_selection < 1) {
            interface_selection = 4;
        }
        if (interface_selection > 4) {
            interface_selection = 1;
        }
    }

    private void autoUpdate() {
        switch (path_State) {
            case 0:
                if (use_Lane){
                    if (alliance_Colour.equals("Blue")) {
                        if (start_From.equals("Short")) {
                            follower.followPath(go_Blue_Short_Shoot_1);
                        } else {
                            follower.followPath(go_Blue_Long_Shoot);
                        }
                    } else {
                        if (start_From.equals("Short")) {
                            follower.followPath(go_Red_Short_Shoot_1);
                        } else {
                            follower.followPath(go_Red_Long_Shoot);
                        }
                    }
                } else {
                    if (alliance_Colour.equals("Blue")) {
                        if (start_From.equals("Short")) {
                            follower.followPath(go_Blue_Short_Shoot_2);
                        } else {
                            follower.followPath(go_Blue_Long_Shoot);
                        }
                    } else {
                        if (start_From.equals("Short")) {
                            follower.followPath(go_Red_Short_Shoot_2);
                        } else {
                            follower.followPath(go_Red_Long_Shoot);
                        }
                    }
                }
                pathReset();
                break;
            case 1:
                if (follower.isBusy()) {
                 telemetry.addLine("Winding up Flywheel with range correction");
                 robot.flyWheel.setDesiredVelocity(1500);
                 pathTime.reset();
                } else if (pathTime.seconds() < general_Shoot_Time) {
                    telemetry.addLine("Shooting with range correction");
                    robot.flyWheel.shoot = true;
                } else {
                    telemetry.addLine("Stopping Flywheel");
                    robot.flyWheel.shoot = false;
                    if (use_Lane){
                        telemetry.addLine("Intake On");
                        robot.intake.intakeOn = true;
                        if (alliance_Colour.equals("Blue")) {
                            if (start_From.equals("Short")) {
                                follower.followPath(go_Blue_Lane_1_Start);
                            } else {
                                follower.followPath(go_Blue_Lane_3_Start);
                            }
                        } else {
                            if (start_From.equals("Short")) {
                                follower.followPath(go_Red_Lane_1_Start);
                            } else {
                                follower.followPath(go_Red_Lane_3_Start);
                            }
                        }
                    } else {
                        if (alliance_Colour.equals("Blue")) {
                            if (start_From.equals("Short")) {
                                telemetry.addLine("Auto Finished");
                                return;
                            } else {
                                follower.followPath(go_Blue_Long_End);
                            }
                        } else {
                            if (start_From.equals("Short")) {
                                telemetry.addLine("Auto Finished");
                                return;
                            } else {
                                follower.followPath(go_Red_Long_End);
                            }
                        }
                    }
                    pathReset();
                }
                break;
            case 2:
                if (!follower.isBusy()) {
                    if (use_Lane){
                        if (alliance_Colour.equals("Blue")) {
                            if (start_From.equals("Short")) {
                                follower.followPath(go_Blue_Lane_1_Pickup);
                            } else {
                                follower.followPath(go_Blue_Lane_3_Pickup);
                            }
                        } else {
                            if (start_From.equals("Short")) {
                                follower.followPath(go_Red_Lane_1_Pickup);
                            } else {
                                follower.followPath(go_Red_Lane_3_Pickup);
                            }
                        }
                    } else {
                        telemetry.addLine("Auto Finished");
                        return;
                    }
                    pathReset();
                }
                break;
            case 3:
                if (!follower.isBusy()) {
                    telemetry.addLine("Intake Off"); //Should it be off or will it keep the last artifact in?
                    if (alliance_Colour.equals("Blue")) {
                        if (start_From.equals("Short")) {
                            follower.followPath(go_Blue_Short_Shoot_1);
                        } else {
                            follower.followPath(go_Blue_Long_Shoot);
                        }
                    } else {
                        if (start_From.equals("Short")) {
                            follower.followPath(go_Red_Short_Shoot_1);
                        } else {
                            follower.followPath(go_Red_Long_Shoot);
                        }
                    }
                    pathReset();
                }
                break;
            case 4:
                if (follower.isBusy()) {
                    telemetry.addLine("Winding up Flywheel with range correction");
                    robot.flyWheel.setDesiredVelocity(1500);
                    pathTime.reset();
                } else if (pathTime.seconds() < general_Shoot_Time) {
                    telemetry.addLine("Shooting with range correction");
                    robot.flyWheel.shoot = true;
                } else {
                    if (alliance_Colour.equals("Blue")) {
                        if (start_From.equals("Short")) {
                            follower.followPath(go_Blue_Lane_2_Start);
                        } else {
                            telemetry.addLine("Blue Long Shoot to Lazy Generation?");
                            return;
                        }
                    } else {
                        if (start_From.equals("Short")) {
                            follower.followPath(go_Red_Lane_2_Start);
                        } else {
                            telemetry.addLine("Red Long Shoot to Lazy Path Generation?");
                            return;

                        }
                    }
                }
                pathReset();
                break;
            case 5:
                telemetry.addLine("Intake Off"); //If it is not switched off before
                robot.intake.intakeOn = false;
                if (!follower.isBusy()) {
                    telemetry.addLine("Intake On");
                    if (alliance_Colour.equals("Blue")) {
                        follower.followPath(go_Blue_Lane_2_Pickup);
                    } else {
                        follower.followPath(go_Red_Lane_2_Pickup);
                    }
                    pathReset();
                }
                break;
            case 6:
                if (!follower.isBusy()) {
                    if (alliance_Colour.equals("Blue")) {
                        follower.followPath(go_Blue_Short_Shoot_2);
                    } else {
                        follower.followPath(go_Red_Short_Shoot_2);
                    }
                    pathReset();
                }
                break;
            case 7:
                if (follower.isBusy()) {
                    telemetry.addLine("Winding up Flywheel with range correction");
                    robot.flyWheel.setDesiredVelocity(1500);
                    pathTime.reset();
                } else if (pathTime.seconds() < general_Shoot_Time) {
                    telemetry.addLine("Shooting with range correction");
                    robot.flyWheel.shoot = true;
                } else {
                    telemetry.addLine("Auto Finished");
                    robot.flyWheel.shoot = false;
                }
         }
    }
    private void pathReset() {
        pathTime.reset();
        path_State += 1;
    }
    public void stop() {
        blackboard.put("STARTPOSE", follower.getPose());
        blackboard.put("SHOOTERHEADING", robot.lazySusan.getOrientation(AngleUnit.DEGREES));
    }
}
