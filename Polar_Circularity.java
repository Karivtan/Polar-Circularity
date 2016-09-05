package HomeMade.Tools;
import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;
import ij.measure.*;

public class Polar_Circularity implements PlugIn {

	public void run(String arg) {
		ResultsTable rt = ResultsTable.getResultsTable();
		rt.reset();
		//IJ.log("\\Clear");
		ImagePlus imp = IJ.getImage();
		Roi cRoi=imp.getRoi();
		if (cRoi==null){
			IJ.showMessage("ROI needed");	
		} else if (!cRoi.isArea()){
			IJ.showMessage("Area ROI needed");	
		} else {
			rt.incrementCounter();
			rt.addValue("Polar Circularity", getPolarCircularity(cRoi, imp));
			rt.show("Results");
		}
		
	}

	public double getPolarCircularity(Roi cRoi, ImagePlus imp){
		double standarddeviation=0;
		double av=0;
		if (cRoi.getType()<5){
			/*
			 * rect = 0
			 * rounded rect =0
			 * oval =1
			 * ellipse =3
			 * changed by selection brush becomes type 9
			 * polygon =2
			 * freehand =3
			 * magic wand =4
			 */
			 imp.setRoi(cRoi);
			 ImageStatistics is = imp.getStatistics(32);
			double XM=is.xCentroid;
			double YM=is.yCentroid;
			FloatPolygon fp = cRoi.getInterpolatedPolygon();
			float[] xs= fp.xpoints;
			float[] ys= fp.ypoints;
			// need to get center
			double[] Radii = new double[xs.length];
			double totaldist=0;
			for (int k=0;k<xs.length;k++){
				Radii[k]=Math.sqrt(Math.pow((xs[k]-XM),2)+Math.pow((ys[k]-YM),2));
				totaldist+=Radii[k];
			}
			av=totaldist/xs.length;
			double standarddevationsum=0;
			for (int k=0;k<xs.length;k++){
				standarddevationsum+=Math.pow((Radii[k]-av),2);
			}
			standarddeviation=Math.sqrt(standarddevationsum/xs.length);
			return standarddeviation/av;
		} else {
			IJ.showMessage("This type of ROI is not supported: type "+cRoi.getType());
			return standarddeviation/av;
		}
	}

}
