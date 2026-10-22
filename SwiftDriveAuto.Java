
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name = "Swift Drive Auto")
public class SwiftDriveAuto extends LinearOpMode {

    private DcMotor frontLeft;
    private DcMotor backLeft;
    private DcMotor frontRight;
    private DcMotor backRight;

    @Override
    public void runOpMode() {

        // Connect motors using the configured hardware names
        frontLeft = hardwareMap.get(DcMotor.class, "front_left_drive");
        backLeft = hardwareMap.get(DcMotor.class, "back_left_drive");
        frontRight = hardwareMap.get(DcMotor.class, "front_right_drive");
        backRight = hardwareMap.get(DcMotor.class, "back_right_drive");

        // Match the TeleOp motor directions
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        // Stop quickly when power is removed
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Use direct motor power
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Ready");
        telemetry.addData("Autonomous", "Drive forward for 2 seconds");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            // Drive forward at 30% power
            frontLeft.setPower(-0.3);
            backLeft.setPower(-0.3);
            frontRight.setPower(-0.3);
            backRight.setPower(-0.3);
            
            sleep(2000);
            
            // Always stop the motors afterward
            frontLeft.setPower(0);
            backLeft.setPower(0);
            frontRight.setPower(0);
            backRight.setPower(0);
        }
    }
}
