Image AlignmentTT

##Overview
This plugin is designed for ImageJ. 
It corrects for positional misalignments between stack images.


##Description
ImageJ Plugin "Image_AligmentTT.java" corrects for positional misalignments between stack images (not between 2 channels).  This plugin supports only 2 channels stack images. Calculation of the reference points for correcting the position is performed only with the images of the second channel.

##Author
Tooru Takahashi
ttakahashi@mth.biglobe.ne.jp

##Date
2018/08/04

##Source
Image_AlignmentTT.java

##Installation
Download "Image_AligmentTT.java" to the plugins folder or subfolder, and compile it with the "Compile and Run" command. Restart ImageJ to add the "Image Alignment" command to the Plugins menu. For more information see: http://imagej.net/

##Procedures
1. "Image_AlignmentTT.java" requires 2 channels stack images as an input. First, you have to open 2 channels stack images.

2. This correction uses the reference points calculated from intensity of each pixel. If there is an area that you want to refer, enclose the area as ROI. If not, you don't have to do that. The whole image is automatically referred.

3. Run "Image　AlignmentTT" by choosing it from "Plugins" menu.

4. In order to improve the reference point calculation, you need to set the "Background factor". The factor is a coefficient that determines the background threshold. The background threshold (BT) is the average of the intensity ​​of the entire image (Ave) plus the standard deviation (SD) multiplied by the "Background factor (BF)".
BT = Ave + SD x BF
Empirically, it seems that the background factor is about 3. The reference point is calculated using only pixels above this background threshold.

5. Select method of calculating reference point.
Centroid: Calculate the center of gravity of the signals in the area to be referenced (recommended).
Center: Calculate the center position of the signals in the area to be referenced.

6. Repeat steps 2-5 until the image trembling stops.

## License
This plugin is released under the MIT License , see LICENSE.txt.

