package com.culmor30.fragmentsTesting;

import java.util.ArrayList;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

public class homography {

	private Mat original;
	private Mat imgCopy;
	private Mat imgWarp;
    private Mat grayImg;
    private volatile ArrayList<double[]> hMatrix = new ArrayList<double[]>();
    private Mat hMatrixhold;
    private MatOfPoint2f corners;
    
	public ArrayList<double[]> getLastMatrix ()
	{
		return hMatrix;
	}

	public Mat process(Mat rgbaImage) {
	

		//original = new Mat(rgbaImage.rows(), rgbaImage.cols(), CvType.CV_8UC4);
		corners = new MatOfPoint2f();
		//imgCopy = new Mat(rgbaImage.rows(), rgbaImage.cols(), CvType.CV_8UC4);
		imgWarp = new Mat(rgbaImage.rows(), rgbaImage.cols(), CvType.CV_8UC4);
		grayImg = new Mat(rgbaImage.rows(), rgbaImage.cols(), CvType.CV_8UC4);
		
		int mChessRows = 6;
		int mChessCols = 9;
		
		
		imgCopy = rgbaImage.clone();
		
		Size patternSize = new Size(mChessRows, mChessCols);
		
		// First find image and object points using chessboard....
		
		
		Imgproc.cvtColor(imgCopy, grayImg, Imgproc.COLOR_BGR2GRAY);
		
		//Boolean foundCorners = false;
		Boolean foundCorners = Calib3d.findChessboardCorners(grayImg, patternSize, corners);
		
		if(foundCorners)
		{	
			// Get the subpixel accuracy on these corners:
			Imgproc.cornerSubPix(grayImg,
					corners,
					new Size(11, 11),
					new Size(-1, -1),
					new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, .1));

			// Get the image and object points
			// We will choose chessboard object points as (r, c):
			// (0, 0), (board_w - 1, 0), (0, board_h - 1),
			// (board_w - 1, board_h - 1).
			Mat objPts = new Mat();
			Mat imgPts = new Mat();
			ArrayList<Point> src_pts = new ArrayList<Point>(4);
			ArrayList<Point> dst_pts = new ArrayList<Point>(4);
			double widthCm = 2.54*(patternSize.width + 1);
			double heightCm = 2.54*(patternSize.height + 1);
			// 4 Corners of physical chessboard image.
			src_pts.add(0, new Point(imgCopy.width()/2 - widthCm/2, imgCopy.rows() - heightCm));
			src_pts.add(1, new Point(imgCopy.width()/2 + widthCm/2, imgCopy.rows() - heightCm));
			src_pts.add(2, new Point(imgCopy.width()/2 - widthCm/2, imgCopy.rows()));
			src_pts.add(3, new Point(imgCopy.width()/2 + widthCm/2, imgCopy.rows()));
			// 4 Corners of chessboard found in image.
			dst_pts.add(0, new Point(corners.get(0, 0)));
			dst_pts.add(1, new Point(corners.get((int)(patternSize.width) - 1, 0)));
			dst_pts.add(2, new Point(corners.get((int)(patternSize.width) * ((int)(patternSize.height) - 1), 0)));
			dst_pts.add(3, new Point(corners.get((int)(corners.height() * corners.width()) - 1, 0)));
			
			// Convert list to matrix for transform function
			objPts = Converters.vector_Point_to_Mat(src_pts);
			imgPts = Converters.vector_Point_to_Mat(dst_pts);
			objPts.convertTo(objPts, CvType.CV_32F);
			imgPts.convertTo(imgPts, CvType.CV_32F);
			// FIND THE HOMOGRAPHY
			Mat h = Imgproc.getPerspectiveTransform(objPts, imgPts);
			
			hMatrixhold = new Mat();
			h.copyTo(hMatrixhold);
			
			hMatrix.clear();
			
			for(int w = 0; w < hMatrixhold.rows(); w++)
			{
				for(int z = 0; z < hMatrixhold.cols(); z++)
				{
					hMatrix.add( hMatrixhold.get(w, z).clone());
					
					//Log.e("ROWS", String.valueOf(hMatrixhold.rows()));
					//Log.e("COLS", String.valueOf(hMatrixhold.cols()));
					//Log.e("HI", String.valueOf(hMatrixhold.get(w, z).length));
					//hMatrixhold.get
				}
			}
			
			
			
			
			
			
			//byte buffe[] = new byte[(int) (h.total() * h.channels())];

			
			// DRAW THE POINTS in order: B,G,R,YELLOW
			Core.circle( imgCopy, dst_pts.get(0), 9, new Scalar(0,0,255), 3);
			Core.circle( imgCopy, dst_pts.get(1), 9, new Scalar(0,255,0), 3);
			Core.circle( imgCopy, dst_pts.get(2), 9, new Scalar(255,0,0), 3);
			Core.circle( imgCopy, dst_pts.get(3), 9, new Scalar(255,255,0), 3);
			imgWarp = Mat.zeros(imgCopy.size(), CvType.CV_8UC4);
			Imgproc.warpPerspective(imgCopy,
					imgWarp,
					h,
					imgCopy.size(),
					Imgproc.INTER_LINEAR | Imgproc.CV_WARP_FILL_OUTLIERS | Imgproc.CV_WARP_INVERSE_MAP);
			
			// Save the corners for the camera.
			// save h out, transform matrix
			
			
			// Other homography class, Helper functions
			// Active IST - src
			/*
			Video::Homography hom(h);
			Point2d center =
					hom.FromImageToPlane(imgCopy.rows - 1,
							imgCopy.cols/2,
							imgCopy.size());
			Point pix =
					hom.FromPlaneToImage(center,
							imgCopy.size());
			*/
			
		}
		
		
			//return grayImg;
			
			if(foundCorners)
			{
				//Log.e("frag2Dim", "height: " + Integer.toString(imgWarp.height()) + ", width: " + Integer.toString(imgWarp.width()));
				return imgWarp;
			}
			else
				return null;
			
		
	}



}
