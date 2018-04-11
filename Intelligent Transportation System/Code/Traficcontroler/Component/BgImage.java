/**
 BgImage class.
 @author Bogdan Georgescu (C++ version, Copyright (c) Bogdan Georgescu, 2000)
 @author Jonathan Helfman (Java version, Copyright (c) FXPAL, 2003)
 @version v0.1

Copyright (c) 2003, FXPAL, All rights reserved.

Redistribution of the source code in electronic form, with or without
modification, is permitted provided the following conditions are met:

LIST OF CONDITIONS
Redistribution of the source code must contain:
1) the above Copyright notice
2) this list of conditions
3) the following Disclaimer
4) the following Reference Information

Redistributions in source code must contain the above Copyright
notice, this list of conditions, the following Disclaimer and
Reference Information in the source code.  Redistributions in binary
form must contain the above Copyright notice, this list of conditions,
the following Disclaimer and Reference Information in documentation
and/or other materials provided with the distribution.  FXPAL nor the
names of the contributors may not be used to endorse or promote
products derived from this software without specific prior written
permission from someone authorized to act on the behalf of FXPAL.

DISCLAIMER: THE SOFTWARE IS PROVIDED BY FXPAL AND THE CONTRIBUTORS
"AS-IS," AND ALL WARRANTIES, EXPRESS OR IMPLIED, ARE DISCLAIMED
(INCLUDING BUT NOT LIMITED TO THE DISCLAIMER OF ANY IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE).  THE
INFORMATION AND SERVICES MAY CONTAIN BUGS, ERRORS, PROBLEMS OR OTHER
LIMITATIONS.  FXPAL, THE CONTRIBUTORS, AND OUR AFFILIATED PARTIES HAVE
NO LIABILITY WHATSOEVER FOR ANY USE OF THIS SOFTWARE.  IN PARTICULAR,
BUT NOT AS A LIMITATION THEREOF, FXPAL, THE CONTRIBUTORS AND OUR
AFFILIATED PARTIES ARE NOT LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL,
EXAMPLARY, INCIDENTAL OR CONSEQUENTIAL DAMAGES (INCLUDING DAMAGES FOR
LOSS OF BUSINESS, LOSS OF PROFITS, LOSS OF USE, LOSS OF DATA,
LITIGATION, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES, OR THE LIKE),
WHETHER BASED ON BREACH OF CONTRACT, BREACH OF WARRANTY, TORT
(INCLUDING NEGLIGENCE OR OTHERWISE), PRODUCT LIABILITY OR OTHERWISE,
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
THE POSSIBILITY OF SUCH DAMAGES.

REFERENCE INFORMATION

This software is a partial port of the code for Edge Detection and
Image Segmentation (EDISON) system.  The EDISON system code is
available at www.caip.rutgers.edu/riul/research/code/EDISON.

The following Publications are relevant to the code and are available
at: www.caip.rutgers.edu/riul/research/robust.html

C. M. Christoudias, B. Georgescu, P. Meer: Synergism in low level
vision. 16th International Conference of Pattern Recognition, Track 1
- Computer Vision and Robotics, Quebec City, Canada, August 2001.

P. Meer, B. Georgescu: Edge detection with embedded confidence. IEEE
Transactions on Pattern Analysis and Machine Intelligence, Vol. 23,
No. 12, December 2001.

D. Comaniciu, P. Meer: Mean shift: A robust approach toward feature
space analysis. IEEE Transactions of Pattern Analysis and Machine
Intelligence, Vol. 24, No. 5, May 2002.
 */

package Component;

/**
   class BgImage: holds an image array for edge detection.
   changes in the Java version:
   im_ has been changed from unsigned char to char
   hasIm_ has been eliminated and 
   references to if(hasIm_) are replaced with if(im_ != null)
 */
public class BgImage {

	int x_; // width
	int y_; // height
	char[] im_ = null; // had been unsigned char in C++ version
	boolean colorIm_; // false - bw image; true - color RGB image

	// weights for converting from RGB to greyscale
	static final double RED_WEIGHT = 0.299;
	static final double GREEN_WEIGHT = 0.587;
	static final double BLUE_WEIGHT = 0.114;

	public BgImage () {
		colorIm_ = false;
		im_ = null;
	}

	public BgImage (int x, int y, boolean colorIm) {
		colorIm_ = colorIm;
		if(colorIm_ == false)
			im_ = new char[x * y];
		else
			im_ = new char[x * y * 3];
		x_ = x;
		y_ = y;
	}

	void CleanData () {
		if(im_ != null){
			im_ = null;
			x_ = y_ = 0;
			colorIm_ = false;
		}
	}

	void zeroImage () {
		for(int i = 0; i < im_.length; i++)
			im_[i] = 0;
	}

	/**
       fills image data with an int value (cast first to char)
       new to Java version
	 */
	public void fillImage (int value) {
		for(int i = 0; i < im_.length; i++)
			im_[i] = (char)value;
	}

	// copies im array to im_ array
	void SetImage (char[] im, int x, int y, boolean colorIm) {
		CleanData();
		colorIm_ = colorIm;
		int dataSize;
		if(colorIm_ == false)
			dataSize = x * y;
		else
			dataSize = x * y * 3;
		im_ = new char[dataSize];
		x_ = x;
		y_ = y;
		/*
	int i;
	unsigned char *its, *itd;
	if(colorIm_ == false){
	    for (i=0, its=im, itd=im_; i < x*y; i++)
		 *(itd++) = *(its++);
	}
	else{
	    for(i=0, its=im, itd=im_; i < x*y*3; i++)
		 *(itd++) = *(its++);
	}
		 */
		for(int i = 0; i < dataSize; i++)
			im_[i] = im[i];
	}

	public void SetImage (byte[] im, int x, int y, boolean colorIm) {
		CleanData();
		colorIm_ = colorIm;
		int dataSize;
		if(colorIm_ == false)
			dataSize = x * y;
		else
			dataSize = x * y * 3;
		im_ = new char[dataSize];
		x_ = x;
		y_ = y;
		for(int i = 0; i < dataSize; i++)
			im_[i] = (char)im[i];
	}

	public void SetImage (short[] im, int x, int y, boolean colorIm) {
		CleanData();
		colorIm_ = colorIm;
		int dataSize;
		if(colorIm_ == false)
			dataSize = x * y;
		else
			dataSize = x * y * 3;
		im_ = new char[dataSize];
		x_ = x;
		y_ = y;
		for(int i = 0; i < dataSize; i++)
			im_[i] = (char)im[i];
	}

	void SetImageFromRGB (char[] im, int x, int y, boolean colorIm) {
		PrivateResize(x, y, colorIm);
		//unsigned char *its, *itd;
		if(colorIm_ == false){
			// j ranges from 0 to (x * y * 3)
			/*
	    for(int i = 0, its=im, itd=im_; i < x * y; i++, itd++, its+=3)
			 *itd = (int) (its[0]*RED_WEIGHT + its[1]*GREEN_WEIGHT + its[2]*BLUE_WEIGHT);
			 */
			for(int i = 0, j = 0; i < x * y; i++, j += 3)
				im_[i] = (char)(im[j] * RED_WEIGHT + im[j + 1] * GREEN_WEIGHT + im[j + 2] * BLUE_WEIGHT);

		}
		else{
			for(int i=0; i < x * y * 3; i++)
				//*(itd++) = *(its++);
				im_[i] = im[i];
		}
	}

	void SetSameImageFromRGB (char[] im) {
		//unsigned char *its, *itd;
		if(colorIm_ == false){
			// j ranges from 0 to (x * y * 3)
			/*
	    for(i=0, its=im, itd=im_; i<x_*y_; i++, itd++, its+=3)
			 *itd = (int) (its[0]*RED_WEIGHT + its[1]*GREEN_WEIGHT + its[2]*BLUE_WEIGHT);
			 */
			for(int i = 0, j = 0; i < x_ * y_; i++, j += 3)
				im_[i] = (char)(im[j] * RED_WEIGHT + im[j + 1] * GREEN_WEIGHT + im[j + 2] * BLUE_WEIGHT);
		}
		else{
			for(int i=0; i < x_ * y_ * 3; i++)
				//*(itd++) = *(its++);
				im_[i] = im[i];
		}
	}

	public void GetImage (byte[] im) {
		if(colorIm_ == false){
			for(int i = 0; i < x_ * y_; i++)
				im[i] = (byte)im_[i];
		}
		else{
			for(int i = 0; i < x_ * y_ * 3; i++)
				im[i] = (byte)im_[i];
		}
	}

	public void GetImage (short[] im) {
		if(colorIm_ == false){
			for(int i = 0; i < x_ * y_; i++)
				im[i] = (short)im_[i];
		}
		else{
			for(int i = 0; i < x_ * y_ * 3; i++)
				im[i] = (short)im_[i];
		}
	}

	// copies image data into the array argument
	void GetImage (char[] im) {
		if(colorIm_ == false){
			for(int i = 0; i < x_ * y_; i++)
				im[i] = im_[i];
		}
		else{
			for(int i = 0; i < x_ * y_ * 3; i++)
				im[i] = im_[i];
		}
	}

	// copies image data into the array argument
	// if the image is B&W the same char is copied out 3 times
	void GetImageColor (char[] im) {
		if(colorIm_ == false){
			// j ranges from 0 to x_ * y_ * 3
			for(int i = 0, j = 0; i < x_ * y_; i++, j += 3){
				im[j] = im_[i];
				im[j + 1] = im_[i];
				im[j + 2] = im_[i];
			}
		}
		else{
			for(int i = 0; i < (x_ * y_ * 3); i++)
				im[i] = im_[i];
		}
	}

	public void GetImageColor (byte[] im) {
		if(colorIm_ == false){
			// j ranges from 0 to x_ * y_ * 3
			for(int i = 0, j = 0; i < x_ * y_; i++, j += 3){
				im[j] = (byte)im_[i];
				im[j + 1] = (byte)im_[i];
				im[j + 2] = (byte)im_[i];
			}
		}
		else{
			for(int i = 0; i < (x_ * y_ * 3); i++)
				im[i] = (byte)im_[i];
		}
	}

	// copies image data into the array argument
	void GetImageBW (char[] im) {
		if(colorIm_ == false){
			for(int i = 0; i < x_ * y_; i++)
				im[i] = im_[i];
		}
		else{
			// j ranges from 0 to x_ * y_ * 3
			for(int i = 0, j = 0; i < x_ * y_; i++, j += 3)
				im[i] = (char)(im_[j] * RED_WEIGHT + im_[j + 1] * GREEN_WEIGHT + im_[j + 2] * BLUE_WEIGHT);
		}
	}

	// copies red image data into the array argument
	void GetImageR (char[] im) {
		if(colorIm_ == false){
			for(int i = 0; i < x_ * y_; i++)
				im[i] = im_[i];
		}
		else{
			for(int i = 0, j = 0; i < x_ * y_; i++, j += 3){
				im[i] = im_[j];
			}
		}
	}

	// copies green image data into the array argument
	void GetImageG (char[] im) {
		if(colorIm_ == false){
			for(int i = 0; i < x_ * y_; i++)
				im[i] = im_[i];
		}
		else{
			for(int i = 0, j = 1; i < x_ * y_; i++, j += 3){
				im[i] = im_[j];
			}
		}
	}

	// copies blue image data into the array argument
	void GetImageB (char[] im){
		if(colorIm_ == false){
			for(int i = 0; i < x_ * y_; i++)
				im[i] = im_[i];
		}
		else{
			for(int i = 0, j = 2; i < x_ * y_; i++, j += 3){
				im[i] = im_[j];
			}
		}
	}

	/*
    unused
    char inline BgImage::operator()(int r, int c) const
    {
	assert(hasIm_ && (r >= 0) && (r < y_) && (c >= 0) && (c < x_));
	return im_[c+r*x_];
    }

    inline unsigned char& BgImage::operator()(int r, int c)
    {
	assert(hasIm_ && (r >= 0) && (r < y_) && (c >= 0) && (c < x_));
	return im_[c+r*x_];
    }

    unsigned char BgImage::PixelValue(int r, int c)
    {
	assert(hasIm_ && (r >= 0) && (r < y_) && (c >= 0) && (c < x_));
	return im_[c+r*x_];
    }

    inline unsigned char gBgImPt(BgImage* in_im, int in_r, int in_c) {
	assert(in_im->hasIm_ && (in_r >= 0) && (in_r < in_im->y_) && (in_c >= 0) && (in_c < in_im->x_));
	return in_im->im_[in_c+in_r*in_im->x_];
    }

    bool ValidCoord (int in_x, int in_y) {
	return ((in_x>=0) && (in_x<x_) && (in_y>=0) && (in_y<y_));
    }

    int ValidReturnBW (int in_x, int in_y, int& cval) {
	if((in_x>=0) && (in_x<x_) && (in_y>=0) && (in_y<y_)) {
	    cval = im_[in_x+in_y*x_];
	    return 1;
	} 
	else{
	    cval = -1;
	    return 0;
	}
    }

    int ValidReturnCol (int in_x, int in_y, int& rval, int& gval, int& bval) {
	if ((in_x>=0) && (in_x<x_) && (in_y>=0) && (in_y<y_)) {
	    rval = im_[in_x*3+0+in_y*(x_*3)];
	    gval = im_[in_x*3+1+in_y*(x_*3)];
	    bval = im_[in_x*3+2+in_y*(x_*3)];

	    return 1;
	} 
	else{
	    rval = gval = bval = -1;
	    return 0;
	}
    }

    int ReturnCol (int in_x, int in_y, int& rval, int& gval, int& bval) {
	rval = im_[in_x*3+0+in_y*(x_*3)];
	gval = im_[in_x*3+1+in_y*(x_*3)];
	bval = im_[in_x*3+2+in_y*(x_*3)];
	return 1;
    }

     boolean IsAllocated () {
       return hasIm_;
     }
	 */
	/*
      you can't override assignment in Java!
      we could (should) implement clone()
      but maybe this is never used . . .

    const BgImage& BgImage::operator=(const BgImage& im) {
	if (this == &im)
	    return *this;

	if (!im.IsAllocated())
	    {
		CleanData();
		return *this;
	    }

	PrivateCopyToThis(im);
	return *this;
    }

    void PrivateCopyToThis (const BgImage& im) {
	PrivateResize(im.x_, im.y_, im.colorIm_);
	int ncopy, i;
	ncopy = x_*y_;
	if (colorIm_ == true)
	    ncopy *= 3;

	char *src;
	src = im.im_;
	for (i=0; i<ncopy; i++)
	    im_[i] = src[i];
    }
	 */

	void PrivateResize (int width, int height, boolean color) {
		if((im_ == null) || (width != x_) || (height != y_) || (color != colorIm_)) {
			CleanData();
			x_ = width;
			y_ = height;
			colorIm_ = color;
			if(color == false)
				im_ = new char[x_ * y_];
			else
				im_ = new char[x_ * y_ * 3];
		}
	}

	void Resize (int width, int height, boolean color) {
		PrivateResize(width, height, color);
	}

}
