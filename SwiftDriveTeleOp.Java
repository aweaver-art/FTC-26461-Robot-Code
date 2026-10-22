
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "Swift Drive TeleOp")
public class SwiftDriveTeleOp extends LinearOpMode {

    private DcMotor frontLeft;
    private DcMotor backLeft;
    private DcMotor frontRight;
    private DcMotor backRight;

    private IMU imu;

    // Ignore tiny joystick movements
    private double deadZone(double value) {
        return Math.abs(value) < 0.05 ? 0 : value;
    }

    // Keep angle error between -180 and 180 degrees
    private double angleError(double target, double current) {
        double error = target - current;

        while (error > 180) error -= 360;
        while (error < -180) error += 360;

        return error;
    }

    @Override
    public void runOpMode() {

        // Connect motors
        frontLeft = hardwareMap.get(DcMotor.class, "front_left_drive");
        backLeft = hardwareMap.get(DcMotor.class, "back_left_drive");
        frontRight = hardwareMap.get(DcMotor.class, "front_right_drive");
        backRight = hardwareMap.get(DcMotor.class, "back_right_drive");

        // Motor directions
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        // Brake when controls are released
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Use direct motor power
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Initialize the built-in Control Hub IMU
        imu = hardwareMap.get(IMU.class, "imu");

        // Assumes the logo faces UP and USB ports face LEFT
        // Change LEFT if the physical mounting differs.
        IMU.Parameters parameters = new IMU.Parameters(
            new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT
            )
        );

        imu.initialize(parameters);

        telemetry.addData("Status", "Ready");
        telemetry.addData("Drive", "Left stick: Move");
        telemetry.addData("Drive", "Right stick: Turn");
        telemetry.addData("Slow Mode", "Hold Left Bumper");
        telemetry.addData("IMU", "Initialized");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) {
            return;
        }

        // Set the starting heading to zero
        imu.resetYaw();

        double targetHeading = 0;
        boolean wasDrivingStraight = false;

        // Heading correction settings
        double correctionGain = 0.02;
        double maxCorrection = 0.25;

        while (opModeIsActive()) {

            // Read controller sticks
            double forward = deadZone(gamepad1.left_stick_y);
            double strafe = deadZone(gamepad1.left_stick_x);
            double turn = deadZone(gamepad1.right_stick_x);

            // Read current robot heading
            double heading = imu.getRobotYawPitchRollAngles()
                    .getYaw(AngleUnit.DEGREES);

            boolean translating =
                    Math.abs(forward) > 0.05 ||
                    Math.abs(strafe) > 0.05;

            boolean manualTurning = Math.abs(turn) > 0.05;

            // Hold heading while moving, unless the driver turns
            if (translating && !manualTurning) {

                if (!wasDrivingStraight) {
                    targetHeading = heading;
                }

                double error = angleError(targetHeading, heading);

                double correction = error * correctionGain;

                // Limit correction strength
                correction = Math.max(
                    -maxCorrection,
                    Math.min(maxCorrection, correction)
                );

                turn = correction;
                wasDrivingStraight = true;

            } else {
                wasDrivingStraight = false;
            }

            // X-drive motor mixing
            double frontLeftPower = forward + strafe + turn;
            double backLeftPower = forward - strafe + turn;
            double frontRightPower = forward - strafe - turn;
            double backRightPower = forward + strafe - turn;

            // Normalize powers to stay within -1 to 1
            double maxPower = Math.max(1.0, Math.max(
                Math.max(Math.abs(frontLeftPower),
                         Math.abs(backLeftPower)),
                Math.max(Math.abs(frontRightPower),
                         Math.abs(backRightPower))
            ));

            // Slow mode: hold left bumper for 40% power
            double speedMultiplier =
                    gamepad1.left_bumper ? 0.4 : 1.0;

            // Apply motor power
            frontLeft.setPower(
                frontLeftPower / maxPower * speedMultiplier
            );
            backLeft.setPower(
                backLeftPower / maxPower * speedMultiplier
            );
            frontRight.setPower(
                frontRightPower / maxPower * speedMultiplier
            );
            backRight.setPower(
                backRightPower / maxPower * speedMultiplier
            );

            // Telemetry
            telemetry.addData("Forward", "%.2f", forward);
            telemetry.addData("Strafe", "%.2f", strafe);
            telemetry.addData("Turn Input", "%.2f",
                    gamepad1.right_stick_x);
            telemetry.addData("Heading", "%.1f degrees", heading);
            telemetry.addData("Target Heading", "%.1f degrees",
                    targetHeading);
            telemetry.addData("Speed Mode",
                    gamepad1.left_bumper ? "SLOW (40%)" : "NORMAL");
            telemetry.addData("IMU Correction",
                    translating && !manualTurning ? "ACTIVE" : "OFF");
            telemetry.update();
        }

        // Stop motors when the OpMode ends
        frontLeft.setPower(0);
        backLeft.setPower(0);
        frontRight.setPower(0);
        backRight.setPower(0);
    }
}
