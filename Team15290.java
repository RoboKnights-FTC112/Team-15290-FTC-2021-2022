package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
@TeleOp(name = "Team15290")

public class Team15290 extends LinearOpMode {

    //private DistanceSensor sensorRange;
    private DcMotor fr, fl, br, bl;
    private DcMotor wheel;
    private DcMotor la;
    private Servo intakeLeft, intakeRight;
    
    @Override
    public void runOpMode() {
        
        
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        
        waitForStart();
        
        fr = hardwareMap.get(DcMotor.class,"fr");
        fl = hardwareMap.get(DcMotor.class,"fl");
        br = hardwareMap.get(DcMotor.class,"br");
        bl = hardwareMap.get(DcMotor.class,"bl");
        la = hardwareMap.get(DcMotor.class,"la");
        wheel = hardwareMap.get(DcMotor.class, "wheel");
        intakeLeft = hardwareMap.servo.get("servo");
        intakeRight = hardwareMap.servo.get("servo2");
        
        
        while (opModeIsActive()) {
        
            telemetry.update();
            
            if (gamepad1.left_stick_y < -.03){
                goForward();
            } else if (gamepad1.left_stick_y > .03){
                goBackwards();
            } else if (gamepad1.left_stick_x < -.03){
                turnLeft();
            } else if (gamepad1.left_stick_x > .03){
                turnRight();
            } else if (gamepad1.right_stick_x > .03){
                pivotRight();
            } else if (gamepad1.right_stick_x < -.03){
                pivotLeft();
            } else {
                stopBot();
            }

            if (gamepad1.a){
                rotateWheel(.3);
            } else if (gamepad1.b){
                rotateWheel(-.3);
            } else {
                stopWheel();
            }
            
            if (gamepad1.right_trigger > 0.03){
               la.setPower(.5);
            } else if (gamepad1.left_trigger > 0.03){
                la.setPower(-.5);
            } else {
                la.setPower(0.01);
            } 
        
            if (gamepad1.right_stick_y > .5){
                openServos();
            } else if (gamepad1.right_stick_y < -.5){
                closeServos();
            }
         
        }
    }

    public void goForward(){
        fr.setPower(.5);
        fl.setPower(-.5);
        br.setPower(.5);
        bl.setPower(-.5);
    }
    public void goBackwards(){
        fr.setPower(-.5);
        fl.setPower(.5);
        br.setPower(-.5);
        bl.setPower(.5);
    }
    public void turnRight(){
        fr.setPower(-.5);
        fl.setPower(-.5);
        br.setPower(-.5);
        bl.setPower(-.5);
    }
    public void turnLeft(){
        fr.setPower(.5);
        fl.setPower(.5);
        br.setPower(.5);
        bl.setPower(.5);
    }
    public void pivotRight(){
        fr.setPower(0);
        fl.setPower(-.4);
        br.setPower(0);
        bl.setPower(-.4);
    }
    public void pivotLeft(){
        fr.setPower(.4);
        fl.setPower(0);
        br.setPower(.4);
        bl.setPower(0);
    }
    public void stopBot(){
        fr.setPower(0);
        fl.setPower(0);
        br.setPower(0);
        bl.setPower(0); 
    }
    
    public void openServos(){
        intakeRight.setPosition(.8);
        intakeLeft.setPosition(.8);
    }
    
    public void closeServos(){
        intakeRight.setPosition(.33);
        intakeLeft.setPosition(.33);
    }

    public void rotateWheel(double i){
        wheel.setPower(i);
    }

    public void stopWheel(){
        wheel.setPower(0);
    }

}