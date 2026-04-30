package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {

    // Hardware
    private DcMotor intakeMotor;
    private CRServo intakeServo;
    public boolean intakeOn = false; // Also where is this set?

    /**
     * Description: Initializes software variables and links them up to hardware variables and motor directions are set
     * Pre-Condition: Parameters are initialized and hardware map is set up with names as shown
     * Post-Condition: Software variables are initialized and motor directions are set
     * @param hardwareMap The hardware map with the hardware for the intake
     */
     public void init(HardwareMap hardwareMap) {
         intakeMotor = hardwareMap.get(DcMotor.class, "intake_motor");
         intakeMotor.setDirection(DcMotor.Direction.REVERSE);
         intakeServo = hardwareMap.get(CRServo.class, "intake_servo");
         intakeServo.setDirection(DcMotorSimple.Direction.REVERSE);
     }

    /**
     * Description: Updates the intake motor and indexer each loop
     * Pre-Condition: All hardware and objects are initialized and params are of correct types
     * Post-Condition: Power commands are sent to desired hardware
     * @param shoot
     * @param flyWheel The flywheel object for the robot
     */
     public void update(boolean shoot, FlyWheel flyWheel) {
         if (intakeOn) {
             intakeMotor.setPower(1);
         } else {
             intakeMotor.setPower(0);
         }
         if (shoot && flyWheel.getCurrentVelocity() >= flyWheel.getDesiredVelocity() *0.95) {
             intakeServo.setPower(1);
         } else {
             if (intakeOn) {
                 intakeServo.setPower(-1);
             } else {
                 intakeServo.setPower(0);
             }
         }
     }
}
