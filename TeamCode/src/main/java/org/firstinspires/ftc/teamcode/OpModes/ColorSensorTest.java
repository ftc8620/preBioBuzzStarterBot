package org.firstinspires.ftc.teamcode.OpModes;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware.Robot;

@Config
@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="ColorSensorTest")
//@Disabled
public class ColorSensorTest extends OpMode{
    Robot yoshi;

    ElapsedTime loopTimer = new ElapsedTime();

    boolean debugMode = false;

    @Override
    public void init() {
        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */

        //letting cam init fully and telling driver not to start
        telemetry.addData(">","DO NOT START YET");
        telemetry.update();

        yoshi = new Robot();

        //it is done initializing ready to start!
        telemetry.addData(">","READY TO START!");
        telemetry.update();
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {

    }

    @Override
    public void loop() {
        double loop = loopTimer.milliseconds();
        loopTimer.reset();

//        yoshi.updateColorDetectorState();
//

//        if (yoshi.colorDetector1.colorIs() == Robot.ArtifactColor.GREEN){
//            yoshi.led.setGreen();
//        } else {
//            yoshi.led.setBlack();
//        }
    }

}