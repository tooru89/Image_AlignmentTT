import ij.*;
import ij.IJ;
import ij.io.*;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import ij.plugin.*;
import ij.gui.*;

import java.awt.*;
import java.util.Vector;

/*
 * The MIT License
 *
 * Copyright 2018 Tooru Takahashi.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

public class Image_AligmentTT implements PlugIn {

  public void run(String arg){
    ImagePlus imp = WindowManager.getCurrentImage();
    int channelNum = imp.getNChannels();
    int sliceNum = imp.getNSlices();

    if(channelNum == 1 && sliceNum == 1){
      IJ.error("error: Unsupported image format.");
    } else {
      double bgFactor = 3;
      String[] modeItems = {"Centroid", "Center"};

      GenericDialog gd = new GenericDialog("Image Aligment");
      gd.addNumericField("Background factor: ", bgFactor, 1);
      gd.addChoice("mode", modeItems, "Centroid"); //(label, items, default)

      gd.showDialog();
      if(gd.wasCanceled()) return;

      bgFactor = gd.getNextNumber();
      Vector modeOption = new Vector();
      modeOption = gd.getChoices();
      String mode = "Centroid";
      if(modeOption.toString().contains("Center")){
        mode = "Center";
      }

      ImageStack stack = imp.getStack();
      int frames = imp.getStackSize()/2;
      Roi roi = imp.getRoi();
      //when there is no selected Roi, the entire image is regarded as Roi.
      if(roi == null){
        int width = imp.getWidth();
        int height = imp.getHeight();
        imp.setRoi(0, 0, width, height);
        roi = imp.getRoi();
        imp.deleteRoi();
      }

      ImageProcessor OriginIP = stack.getProcessor(2);
      int[] Origin = new int[2];
      if(mode == "Centroid"){
        Origin = Centroid(OriginIP, roi, bgFactor);
      } else {
        Origin = Center(OriginIP, roi, bgFactor);
      }
      for(int t=2; t<=frames; t++){
        imp.setT(t);

        ImageProcessor cfp = stack.getProcessor(1+2*(t-1));
        ImageProcessor yfp = stack.getProcessor(2+2*(t-1));
        int x=0;
        int y=0;
        if(mode == "Centroid"){
          int[] centroid = new int[2];
          centroid = Centroid(yfp, roi, bgFactor);
          x = centroid[0]-Origin[0];
          y = centroid[1]-Origin[1];
        } else {
          int[] center = new int[2];
          center = Center(yfp, roi, bgFactor);
          x = center[0]-Origin[0];
          y = center[1]-Origin[1];
        }
        cfp.translate(-x, -y);
        yfp.translate(-x, -y);
      }
    }
  }

  public int[] Centroid(ImageProcessor ip, Roi roi, double f){
    ImageProcessor mask = roi.getMask();
    Rectangle r = roi.getBounds();

    //The calculation of the background
    ImageStatistics stats = ip.getStatistics();
    float BG = (float)(stats.mean+f*stats.stdDev);

    double totalIntensity = 0; //total intensity
    double xg = 0; //the center of gravity position on the axes X
    double yg = 0; //the center of gravity position on the axes Y

    for(int x=0; x<r.width; x++){
      for(int y=0; y<r.height; y++){
        if (mask==null || mask.getPixel(x,y)!=0) {
          if(ip.getPixelValue(r.x+x, r.y+y) > BG){
            xg += (double)r.x+x * (ip.getPixelValue(r.x+x, r.y+y));
            yg += (double)r.y+y * (ip.getPixelValue(r.x+x, r.y+y));
            totalIntensity += (ip.getPixelValue(r.x+x, r.y+y));
          }
        }
      }
    }
    int centroid[] = new int[2];
    centroid[0] = (int)(xg/totalIntensity);
    centroid[1] = (int)(yg/totalIntensity);

    return centroid;
  }

  public int[] Center(ImageProcessor ip, Roi roi, double f){
    ImageProcessor mask = roi.getMask();
    Rectangle r = roi.getBounds();

    //The calculation of the background
    ImageStatistics stats = ip.getStatistics();
    float BG = (float)(stats.mean+f*stats.stdDev);


    double count = 0; //the number of pixels
    double xg = 0; //the center of position on the axes X
    double yg = 0; //the center of position on the axes Y

    for(int x=0; x<r.width; x++){
      for(int y=0; y<r.height; y++){
        if (mask==null || mask.getPixel(x,y)!=0) {
          if(ip.getPixelValue(r.x+x, r.y+y) > BG){
            xg += (double)r.x+x;
            yg += (double)r.y+y;
            count++;
          }
        }
      }
    }
    int center[] = new int[2];
    center[0] = (int)(xg/count);
    center[1] = (int)(yg/count);

    return center;
  }
}
