package org.firstinspires.ftc.team772.implementation;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
//import org.firstinspires.ftc.team772.vision.RedSampleDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.lang.annotation.Target;
import java.util.ArrayList;
/*
public class VisionSystem {

    RedSampleDetection redSampleDetection;

    int cameraMonitorViewId;
    OpenCvCamera intakeCam;

    public VisionSystem(HardwareMap hw){

        WebcamName camName = hw.get(WebcamName.class, "CAMERANAME");

        cameraMonitorViewId = hw.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hw.appContext.getPackageName());
        intakeCam = OpenCvCameraFactory.getInstance().createWebcam(camName, cameraMonitorViewId);

        intakeCam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                // Usually this is where you'll want to start streaming from the camera (see section 4)
                intakeCam.startStreaming(1920, 1080, OpenCvCameraRotation.UPRIGHT);
                intakeCam.setPipeline(redSampleDetection);
                FtcDashboard.getInstance().startCameraStream(intakeCam, 120);
            }
            @Override
            public void onError(int errorCode)
            {
                /*
                 * This will be called if the camera could not be opened

                //Yee haw
            }
        });


    }

    public int getPos(){

        RedSampleDetection.Target biggestBox = null;
        int biggestBoxSize = biggestBox.rect.x * biggestBox.rect.y;

        ArrayList<RedSampleDetection.Target> boxes = redSampleDetection.getTargetsWithLabel("Box");


        for(RedSampleDetection.Target box: boxes){

            int boxSize = box.rect.x * box.rect.y;

            if(boxSize > biggestBoxSize){
                biggestBox = box;
            }

        }

        return biggestBoxSize;
    }


}
*/