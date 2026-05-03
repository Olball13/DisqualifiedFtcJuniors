package org.firstinspires.ftc.teamcode.Teleops;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Mechanisms.AprilTagWebcam;
import org.firstinspires.ftc.teamcode.Mechanisms.FlyWheel;
import org.firstinspires.ftc.teamcode.Mechanisms.Hood;
import org.firstinspires.ftc.teamcode.Mechanisms.LazySusan;

@Configurable
@TeleOp
public class ShootingInfoTest extends OpMode {
    FlyWheel flyWheel = new FlyWheel();
    LazySusan lazySusan = new LazySusan();
    Hood hood = new Hood();
    AprilTagWebcam aprilTagWebcam = new AprilTagWebcam();
    private TelemetryManager telemetryM;
    public static double flywheelSpeed = 0.2;
    public static double turretAngleLimit = 180;
    public static double rampPercent = 0;

    @Override
    public void init() {
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        lazySusan.init(hardwareMap, telemetry, turretAngleLimit);
        aprilTagWebcam.init(hardwareMap, telemetry);
        flyWheel.init(hardwareMap, 0);
        hood.init(hardwareMap, 0);
    }


    @Override
    public void loop() {
        telemetryM.debug("Distance From Basket", aprilTagWebcam.rangeCorrection);
        telemetryM.debug("Flywheel Velocity", flyWheel.getCurrentVelocity());
        telemetryM.debug("Ramp %", rampPercent);
        lazySusan.update(gamepad1, aprilTagWebcam);
        if (gamepad1.yWasPressed()) {
            flyWheel.setDesiredVelocity(flywheelSpeed);
        }
        hood.update();
        aprilTagWebcam.update(lazySusan.getOrientation(AngleUnit.RADIANS));


    }
}
