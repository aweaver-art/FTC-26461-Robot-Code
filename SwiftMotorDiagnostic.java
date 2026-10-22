
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Swift Motor Diagnostic")
public class SwiftMotorDiagnostic extends LinearOpMode {

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    private static final double TEST_POWER = 0.25;

    @Override
    public void runOpMode() {

        // Match these names to the Robot Configuration
        frontLeft = hardwareMap.get(
            DcMotor.class, "front_left_drive");
        frontRight = hardwareMap.get(
            DcMotor.class, "front_right_drive");
        backLeft = hardwareMap.get(
            DcMotor.class, "back_left_drive");
        backRight = hardwareMap.get(
            DcMotor.class, "back_right_drive");

        // Match the directions used by the driving OpMode
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        // Stop promptly when a test button is released
        frontLeft.setZeroPowerBehavior(
            DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(
            DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(
            DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(
            DcMotor.ZeroPowerBehavior.BRAKE);

        // Direct power control
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        stopAllMotors();

        telemetry.addLine("SWIFT MOTOR DIAGNOSTIC");
        telemetry.addLine("A = Front Left");
        telemetry.addLine("B = Front Right");
        telemetry.addLine("X = Back Left");
        telemetry.addLine("Y = Back Right");
        telemetry.addLine("Hold a button to test its motor.");
        telemetry.addLine("Release to stop. Only one motor at a time.");
        telemetry.addData("Test Power", "%.0f%%", TEST_POWER * 100);
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Read button states
            boolean a = gamepad1.a;
            boolean b = gamepad1.b;
            boolean x = gamepad1.x;
            boolean y = gamepad1.y;

            int buttonsPressed =
                (a ? 1 : 0) +
                (b ? 1 : 0) +
                (x ? 1 : 0) +
                (y ? 1 : 0);

            // Never run multiple motors during a test
            if (buttonsPressed > 1) {
                stopAllMotors();
                telemetry.addData("Status", "STOPPED");
                telemetry.addLine(
                    "Press only ONE button at a time.");
            } else if (a) {
                testMotor(frontLeft, "FRONT LEFT",
                    "front_left_drive");
            } else if (b) {
                testMotor(frontRight, "FRONT RIGHT",
                    "front_right_drive");
            } else if (x) {
                testMotor(backLeft, "BACK LEFT",
                    "back_left_drive");
            } else if (y) {
                testMotor(backRight, "BACK RIGHT",
                    "back_right_drive");
            } else {
                stopAllMotors();
                telemetry.addData("Status", "READY");
                telemetry.addLine(
                    "Hold A, B, X, or Y to test a motor.");
            }

            telemetry.update();
            idle();
        }

        stopAllMotors();
    }

    private void testMotor(
        DcMotor motor, String position, String configName) {

        stopAllMotors();
        motor.setPower(TEST_POWER);

        telemetry.addData("Status", "TESTING");
        telemetry.addData("Expected Position", position);
        telemetry.addData("Configured Port Name", configName);
        telemetry.addData("Power", "%.0f%%", TEST_POWER * 100);
        telemetry.addLine(
            "Check that the correct wheel is moving.");
    }

    private void stopAllMotors() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
}
