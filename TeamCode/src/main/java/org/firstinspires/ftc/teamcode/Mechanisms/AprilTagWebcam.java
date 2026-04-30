package org.firstinspires.ftc.teamcode.Mechanisms;

import android.util.Size;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.Constants.AprilTagWebcamConstants;
import org.firstinspires.ftc.teamcode.Teleops.ErrorMainTeleop;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AprilTagWebcam {
    private AprilTagProcessor aprilTagProcessor;
    private VisionPortal visionPortal;
    private List<AprilTagDetection> detectedTags = new ArrayList<>();
    private Telemetry telemetry;
    public double degreeCorrection;
    public double rangeCorrection;
    public Pose pedroPoseCamCorrection;

    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setOutputUnits(DistanceUnit.INCH, AngleUnit.RADIANS)
                .build();
        aprilTagProcessor.setDecimation(2);
        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        builder.setCameraResolution(new Size(640, 480));
        builder.setStreamFormat(VisionPortal.StreamFormat.MJPEG);
        builder.addProcessor(aprilTagProcessor);
        visionPortal = builder.build();
        while (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            telemetry.addLine("...");
        }
        ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
        exposureControl.setMode(ExposureControl.Mode.Manual);
        exposureControl.setExposure(15, TimeUnit.MILLISECONDS);
        GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
        gainControl.setGain(20);
        this.telemetry = telemetry;
    }

    public void update(double lazySusanOrientation) {
        detectedTags = aprilTagProcessor.getDetections();
        fieldRelativeUpdate(lazySusanOrientation);
        aprilTagBasketDetection();
    }

    public List<AprilTagDetection> getDetectedTags() {
        return detectedTags;
    }

    public void displayDetectionTelemetry(AprilTagDetection detectedid) {
        if (detectedid == null) {
            return;}

        if (detectedid.metadata != null) {
            telemetry.addData("Degree Correction", degreeCorrection);
            telemetry.addData("Range Correction", rangeCorrection);
            telemetry.addLine("----Detected Tags----");
            telemetry.addLine(getDetectedTags().toString());
        } else {
            telemetry.addLine("Unknown Id");
            telemetry.addData("Info", detectedid.center.x + ", " + detectedid.center.y);
        }

    }

    public AprilTagDetection getTagBySpecificid(int id) {
        for (AprilTagDetection detection : detectedTags) {
            if (detection.id == id) {
                degreeCorrection = -detection.ftcPose.bearing;
                rangeCorrection = detection.ftcPose.range;
                return detection;
            }
        }
        degreeCorrection = 0;
        rangeCorrection = 0;
        return null;
    }

    private void fieldRelativeUpdate( double lazySusanOrientation) {
        //the robot x and y are converted from FTC to Pedro coordinates by adding 72 inches
        if (detectedTags == null || detectedTags.isEmpty()) { //Check if there are any detections
            pedroPoseCamCorrection = null;
            return;
        }
        double totalX = 0; //Sum of all x, y, sin, and cos detections
        double totalY = 0;
        double totalSin = 0;
        double totalCos = 0;
        int tags = 0; //How many tags info detected
        for (AprilTagDetection detection : detectedTags) {
            if(detection.metadata == null) {continue;}
            double tagfieldx = detection.metadata.fieldPosition.get(0); //field relative x of tag
            double tagfieldy = detection.metadata.fieldPosition.get(1); //field relative y of tag
            Orientation tagAngles = Orientation.getOrientation(
                    detection.metadata.fieldOrientation.toMatrix(),
                    AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.RADIANS); //Conversion from Quaternion to Orientation values
            double tagfieldheading = tagAngles.thirdAngle; //field relative heading of tag
            double range = detection.ftcPose.range; //range from cam to tag
            double bearing = detection.ftcPose.bearing; //degree of deflection of cam from tag
            double turretangle = lazySusanOrientation; //heading of the turret/cam
            double camfieldheading = tagfieldheading - bearing; //field relative heading of the turret/cam
            double camfieldx = tagfieldx - (range * Math.cos(camfieldheading)); //field relative x of cam
            double camfieldy = tagfieldy - (range * Math.sin(camfieldheading)); //field relative y of cam
            double robotheading = camfieldheading - turretangle; //field relative heading of robot
            double turretx = camfieldx - (AprilTagWebcamConstants.camRadius * Math.cos(camfieldheading)); //field relative x of turret
            double turrety = camfieldy - (AprilTagWebcamConstants.camRadius * Math.sin(camfieldheading)); //field relative y of turret
            double offsetx = AprilTagWebcamConstants.camXCorrection * Math.cos(robotheading) - AprilTagWebcamConstants.camYCorrection * Math.sin(robotheading); //Rotation matrix math
            double offsety = AprilTagWebcamConstants.camXCorrection * Math.sin(robotheading) + AprilTagWebcamConstants.camYCorrection * Math.cos(robotheading); //Converts robot relative offset to field relative offset
            double robotx = turretx - offsetx + 72; //Pedro field relative x of robot
            double roboty = turrety - offsety + 72; //Pedro field relative y of robot
            totalSin += Math.sin(robotheading); //Add detection data to total values
            totalCos += Math.cos(robotheading);
            totalX += robotx;
            totalY += roboty;
            tags++; //Add to the # of tags detected
        }
        double averageHeading = Math.atan2(totalSin, totalCos); //converting into radian value and automatically averaging
        double averageX = totalX / tags; //average x of all detections
        double averageY = totalY/ tags; //average y of all detections
        pedroPoseCamCorrection = new Pose (averageX, averageY, averageHeading); //updating the cam correction
    }

    private void aprilTagBasketDetection() {
        if (ErrorMainTeleop.alliance.equals("Blue")) {
            AprilTagDetection id20 = getTagBySpecificid(20);
            displayDetectionTelemetry(id20);
        } else {
            AprilTagDetection id24 = getTagBySpecificid(24);
            displayDetectionTelemetry(id24);
        }
    }

    public void stop() {
        if (visionPortal != null) {
            visionPortal.close();
        }
    }
}