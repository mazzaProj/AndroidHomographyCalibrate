package com.culmor30.fragmentsTesting;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;

public interface frameSender {
	public void sendFrame(Mat frame);
}
