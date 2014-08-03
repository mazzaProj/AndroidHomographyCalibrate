/*
 * 	This is a test of the fragment support libraries for Android 1.6+
 * 	And also AsyncTask rotation + DialogFragments
 * 	
 * 	By Cullin Moran
 * 
 * 	Using reference: http://developer.android.com/guide/topics/fundamentals/fragments.html
 * 	And also: http://stackoverflow.com/questions/8417885/android-fragments-retaining-an-asynctask-during-screen-rotation-or-configuratio
 */

package com.culmor30.fragmentsTesting;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class FragmentsTestingActivity extends FragmentActivity implements frameSender{
	
	private final int FRAGMENT_1 = 0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //Add fragment 1 without XML
        
        /*
        FragmentManager fragmentManager = getSupportFragmentManager();	//"support" because we're using the support libraries
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        
        Fragment1 fragment1 = Fragment1.newInstance(FRAGMENT_1);
        fragment1.setRetainInstance(true);
        fragmentTransaction.add(R.id.main_viewgroup, fragment1);
        fragmentTransaction.commit();
        */
       

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}
	

	
	public void sendFrame(Mat frame)
	{
		Fragment2 Obj = (Fragment2) getSupportFragmentManager().findFragmentById(R.id.fragment_2);

		if(Obj!=null)
			Obj.setFrame(frame);
	}
	
	
	
}