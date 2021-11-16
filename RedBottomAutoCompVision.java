
package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import java.util.Locale;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

@Autonomous
public class RedBottomAutoCompVision extends LinearOpMode {
  /* Note: This sample uses the all-objects Tensor Flow model (FreightFrenzy_BCDM.tflite), which contains
   * the following 4 detectable objects
   *  0: Ball,
   *  1: Cube,
   *  2: Duck,
   *  3: Marker (duck location tape marker)

   */
    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    private static final String[] LABELS = {
      "Ball",
      "Cube",
      "Duck",
      "Marker"
    };

    private static final String VUFORIA_KEY =
            "AUVqPzz/////AAABmUqJfBGCykE9pVZxAem/d8QfKIsJY7zKmdmtHKaiLZKWTQE2HyISKH8f/ulRz6EPZQitOc/1KG4kCmtnMSZeBSwNQje8debXh8RGOLm7OAcXC2urJmIo4E39R7quRlfzFd1C0SSoI2J0jAynAho6UuM5psNS3bZi+b7FFCt4o/k5NcCjdklZ6BaPbr7mwftev9gJOWOK48rF8hAM+HRSSxlnW7Uz3V9J1OJLHSgPM71Wh6DLzQpeRRtUTwkujA97zBoB8cenCIkFjJcFmoHB49r36j+MrakccWKYmDSzANRKLWLYL3AY4Obw7qxyy+zEGPR+S/UovQsTpsR/YHkSzlR7zE+9IlCn/s+iF2NCrsO/";

    
    private VuforiaLocalizer vuforia;

    
    private TFObjectDetector tfod;
    private boolean isDetectedFlag =false;
    private String thingyPlace = "RIGHT";

    private DcMotor fr, fl, br, bl;
    private DcMotor wheel;
    private DcMotor la;
    private Servo intakeLeft, intakeRight;

    @Override
    public void runOpMode() {
        initVuforia();
        initTfod();

        if (tfod != null) {
            tfod.activate();

            tfod.setZoom(1.4, 16.0/9.0);
        }

        fr = hardwareMap.get(DcMotor.class,"fr");
        fl = hardwareMap.get(DcMotor.class,"fl");
        br = hardwareMap.get(DcMotor.class,"br");
        bl = hardwareMap.get(DcMotor.class,"bl");
        la = hardwareMap.get(DcMotor.class,"la");
        wheel = hardwareMap.get(DcMotor.class, "wheel");
        intakeLeft = hardwareMap.servo.get("servo");
        intakeRight = hardwareMap.servo.get("servo2");
        
        
        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();
        int j = 0;
        if (opModeIsActive()) {
            while (opModeIsActive()) {
                j++;
                if (tfod != null &&!isDetectedFlag&&j<3) {
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                      telemetry.addData("# Object Detected", updatedRecognitions.size());
                     int i = 0;

                      for (Recognition recognition : updatedRecognitions) {
                        
                          isDetectedFlag = true;
                          if(recognition.getLeft()+recognition.getRight()<600){
                            thingyPlace = "LEFT";
                          }else{
                            thingyPlace = "MIDDLE";  
                          }
                          
                        
                            
                        
                      
                        telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                        telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                recognition.getLeft(), recognition.getTop());
                        telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                recognition.getRight(), recognition.getBottom());
                        i++;
                        
                      }
                      telemetry.addLine(thingyPlace);
                      telemetry.update();
                    }
                }

            setMotors(0.4);
            sleep(300);
            stopMotors();
            
            if (thingyPlace == "LEFT"){
                //lowest
                telemetry.addData("Position: ", "Lowest");
                telemetry.update();
                
                la.setPower(-0.3);
                sleep(450);
                la.setPower(0);
                
            } else if (thingyPlace == "RIGHT"){
                telemetry.addData("Position: ", "Highest");
                telemetry.update();
       
                la.setPower(.3);
                sleep(1500);
                la.setPower(0);
                
            } else {
                //middle
                telemetry.addData("Position: ", "Middle");
                telemetry.update();
                
            }
            
            approach();
                
            intakeLeft.setPosition(.9);
            sleep(2000);
                
            retreat();
            

            telemetry.addLine("here");
            telemetry.update();
            
            
            break;
        }
    }
    
}

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
            "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
       tfodParameters.minResultConfidence = 0.8f;
       tfodParameters.isModelTensorFlow2 = true;
       tfodParameters.inputSize = 320;
       tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
       tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }

    public void approach(){
        setMotors(.4);
        sleep(400);
        stopMotors();
        fl.setPower(.3);
        bl.setPower(.3);
        sleep(900);
        stopMotors();
        setMotors(.2);
        sleep(1000);
        stopMotors();
    }

    public void retreat(){
        sleep(1300);
        setMotors(-.4);
        sleep(1000);
        stopMotors();
        fl.setPower(.3);
        bl.setPower(.3);
        sleep(900);
        stopMotors();
        setMotors(-.6);
        sleep(8000);
        stopMotors();
    }
    
    public void setMotors(double i){
        fr.setPower(i);
        fl.setPower(i);
        br.setPower(i);
        bl.setPower(i);
    }
    
    public void stopMotors(){
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
