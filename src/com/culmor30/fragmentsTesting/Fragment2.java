package com.culmor30.fragmentsTesting;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class Fragment2 extends Fragment {
	
	ViewGroup root;
	
	public static Fragment2 newInstance(int index) {
		Fragment2 f = new Fragment2();
		
		Bundle args = new Bundle();
		args.putInt("index", index);
		f.setArguments(args);
		f.setRetainInstance(true);
		
		return f;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = (ViewGroup) inflater.inflate(R.layout.test2_fragment, null);
		return root;
	}
	
	public void setFrame(final Mat frame)
	{
		
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if(frame != null && frame.width() > 0 && frame.height() > 0)
				{
					Bitmap resultBitmap = Bitmap.createBitmap(frame.width(), frame.height(), Bitmap.Config.ARGB_8888 );
					Utils.matToBitmap(frame, resultBitmap, true);
					ImageView _cameraPreview = (ImageView)root.findViewById(R.id.imageView1);
					_cameraPreview.setImageBitmap(resultBitmap);
				}
			}
		});
		
	}
	
	
	
	
	
	
	
	
	
	
}