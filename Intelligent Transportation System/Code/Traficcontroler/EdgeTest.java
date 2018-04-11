
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 *  basic test of jedison edge detection creates a black and white png image.
 */
public class EdgeTest {

	public int getComponentCount(File img) {
		int gradWindRad     = 2;
		int minLength       = 5;
		float nmxRank       = 0.5f;
		float nmxConf       = 0.5f;
		int   nmxType       = BgEdgeDetect.FC_ELLIPSE; // Arc
		float hystHighRank  = 0.93f;
		float hystHighConf  = 0.96f;
		int   hystHighType  = BgEdgeDetect.FC_SQUARE_BOX; // Box
		float hystLowRank   = 0.99f;
		float hystLowConf   = 0.91f;
		int   hystLowType   = BgEdgeDetect.FC_ELLIPSE; // Arc
		int Compcnt=0;
		ComponentColorModel colorModel = null;
		ComponentSampleModel csm = null;

		String outputFile = "edgeDetect.png";
		BufferedImage inputImage = null;

		try{
			inputImage = ImageIO.read(img);//new File(inputFile));
		}
		catch(IOException e){
			System.err.println("EdgeTest:" + e);
			System.exit(2);
		}
		int width  = inputImage.getWidth();
		int height = inputImage.getHeight();

		System.err.println("image width: " + width);
		System.err.println("image height: " + height);
		colorModel =
			new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY),
					new int[] {1}, false, false, 
					Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		csm = new ComponentSampleModel(DataBuffer.TYPE_BYTE,
				width, height, 1, width,
				new int[] {0});
		int numComponents = inputImage.getColorModel().getNumComponents();
		if(numComponents > 1){
			System.err.println("converting input image to greyscale");
			System.err.println("inputImage.getColorModel(): " + inputImage.getColorModel());
			//values from Building Imaging Applications with Java Technology, L. Rodrigues, p.379
			//float[][] grayBandCombine = {{0.121671f, 0.715160f, 0.071169f}};       
			//values from Bogdan Georgescu's BgImage C++ code and prompt/globalFnc.cpp
			float[][] grayBandCombine = {{0.299f, 0.587f, 0.114f}};
			BandCombineOp bandCombineOp = new BandCombineOp(grayBandCombine, null);
			Raster src = inputImage.getRaster();

			DataBuffer dataBuf = new DataBufferByte(width * height);
			WritableRaster dst = Raster.createWritableRaster(csm, dataBuf, new Point(0, 0));
			try{
				bandCombineOp.filter(src, dst);
				inputImage = new BufferedImage(colorModel, dst, false, null);
			}
			catch(IllegalArgumentException e){
				System.err.println("EdgeTest:" + e);
				System.exit(2);		
			}
		}

		DataBufferByte dbb = (DataBufferByte)(inputImage.getRaster().getDataBuffer());
		byte[] data = dbb.getData();
		//for(int i = 0; i < 100; i++)
		//    System.err.print(data[i] + ", ");
		System.err.println();
		BgImage bgImage = new BgImage();                                  
		bgImage.SetImage(data, width, height, false);
		BgEdgeDetect bgCompDetect = new BgEdgeDetect(gradWindRad);
		BgEdgeList edgeList = new BgEdgeList();

		Compcnt=bgCompDetect.DoEdgeDetect(bgImage, edgeList, 
				nmxRank, nmxConf, hystHighRank, hystHighConf,
				hystLowRank, hystLowConf, minLength, 
				nmxType, hystHighType, hystLowType);
		//System.out.println("Count = " +Compcnt);	  
		// load edge pixels into BgImage data as black lines on white background
//		System.out.println("count = " + Compcnt);

		bgImage.fillImage(255);
		edgeList.SetBinImage(bgImage, (char)0);

		// extract BgImage data and build greyscale BufferedImage
		byte[] byteData = new byte[width * height];                                    
		bgImage.GetImage(byteData);
		dbb = new DataBufferByte(byteData, width);
		WritableRaster dst = Raster.createWritableRaster(csm, dbb, new Point(0, 0));
		BufferedImage dstImage = new BufferedImage(colorModel, dst, true, null);

		// write out greyscale BufferedImage
		try{
			ImageIO.write(dstImage, "png", new File(outputFile));
		}
		catch(IOException e){
			System.err.println("EdgeTest:" + e);
		}

		return Compcnt; 	
	}

}
