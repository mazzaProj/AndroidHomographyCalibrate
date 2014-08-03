package com.culmor30.fragmentsTesting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import android.app.Activity;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Toast;

public class Fragment1 extends Fragment implements CvCameraViewListener2, OnTouchListener{

    private  JavaCamHom mOpenCvCameraView;
    private Mat mRgba;
    private Mat mRgbaR;
	frameSender sender;
    private homography mhomography;
    
    private List<Size> mResolutionList;
    private MenuItem[] mResolutionMenuItems;
    private SubMenu mResolutionMenu;
    
    

	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		sender = (frameSender) getActivity();
	}
	
	
	public static Fragment1 newInstance(int index) {
		Fragment1 f = new Fragment1();
		
		Bundle args = new Bundle();
		args.putInt("index", index);
		f.setArguments(args);
		
		return f;
	}
	
	
    public boolean onOptionsItemSelected(MenuItem item) {
      
            int id = item.getItemId();
            Size resolution = mResolutionList.get(id);
            mOpenCvCameraView.setResolution(resolution);
            resolution = mOpenCvCameraView.getResolution();
            final String caption = Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString();
            
            getActivity().runOnUiThread(new Runnable() {
    			public void run() {
    				Toast.makeText(getActivity(), caption, Toast.LENGTH_SHORT).show();
    			}
    		});
            
            

        return true;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		//mOpenCvCameraView.enableView();
		
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this.getActivity(), new BaseLoaderCallback(this.getActivity())
        {
        	 @Override
             public void onManagerConnected(int status) {
                 switch (status) {
                     case LoaderCallbackInterface.SUCCESS:
                     {
                         mOpenCvCameraView.enableView();
                         mOpenCvCameraView.setOnTouchListener(Fragment1.this);
                     } 
                 }
        	 }
             
        });
        
		 mOpenCvCameraView = (JavaCamHom) getView().findViewById(R.id.javaCamHom1);
	     mOpenCvCameraView.setCvCameraViewListener(this);
	     setHasOptionsMenu(true);
	   
	}

	

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.test1_fragment, container, false);
		return layout;
	}

	// OPENCV BELOW
	
	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
   	 	mRgba = new Mat(height, width, CvType.CV_8UC4);
        mhomography = new homography();
        mRgbaR =  new Mat(height, width, CvType.CV_8UC4);
		
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		mRgba = inputFrame.rgba();
		sender.sendFrame(mRgba);
		mRgbaR = mhomography.process(mRgba);
		
		/*
		if(mRgbaR != null)
		{
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					Toast FOUND = Toast.makeText(getActivity(), "Corners Found!", Toast.LENGTH_SHORT);
					FOUND.setGravity(Gravity.BOTTOM|Gravity.RIGHT, 0, 0);
					FOUND.show();
				}
			});
			
		}
		*/
		return mRgbaR;
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		 int idx = 0;
		
		 	mResolutionMenu = menu.addSubMenu("Resolution");
	        mResolutionList = mOpenCvCameraView.getResolutionList();
	        mResolutionMenuItems = new MenuItem[mResolutionList.size()];

	        ListIterator<Size> resolutionItr = mResolutionList.listIterator();
	        idx = 0;
	        while(resolutionItr.hasNext()) {
	            Size element = resolutionItr.next();
	            mResolutionMenuItems[idx] = mResolutionMenu.add(2, idx, Menu.NONE,
	                    Integer.valueOf(element.width).toString() + "x" + Integer.valueOf(element.height).toString());
	            idx++;
	         }

	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		
		if(mhomography.getLastMatrix().size() > 0){
			writeStringAsFile(mhomography.getLastMatrix(),"homographyT.txt");
			
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					Toast FOUND = Toast.makeText(getActivity(), "MATRIX SAVED !", Toast.LENGTH_SHORT);
					FOUND.show();
				}
			});
			
		}
		else
		{
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					Toast FOUND = Toast.makeText(getActivity(), "MATRIX NOT FOUND !", Toast.LENGTH_SHORT);
					FOUND.show();
				}
			});
		}
		
		
		return false;
	}
	
	
	public void writeStringAsFile(final ArrayList<double[]> incMat, String fileName) {
        
		String Buff = "";
		//Buff = incMat.toString();
		
		
		for(int w = 0; w < incMat.size(); w++)
		{
			
			for(int q = 0; q < incMat.get(w).length; q++)
			{
				
				Buff = Buff + incMat.get(w)[q] + " ";
			}
			
			Buff = Buff + "\n";
		}
		
		
        try {
            FileWriter out = new FileWriter(new File(Environment.getExternalStorageDirectory(), fileName));
            out.write(Buff);
            out.close();
        } catch (IOException e) {
        }
    }
	

}