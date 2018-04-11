/**
  BGEdge class
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

/*
    each pair of ints in edge_ corresponds to the x and y values of a point
 */


public class BgEdge {
	public int[] edge_;    // the edge array
	public int nPoints_;   // half the number of ints in the edge_ array
	double[] grad_; // the gradient array
	char[] mark_;   // was unsigned char, how is it used?
	boolean isGradSet_;
	boolean isMarkSet_;
	static double PI = 3.1415926535;

	// 8-connected neighbour
	static int gNb8[][] = { {1, 1},
		{1,-1},
		{1, 0},
		{0, 1},
		{0,-1},
		{-1,-1},
		{-1, 0},
		{-1, 1} };

	public BgEdge () {
		nPoints_ = 0;
		edge_ = null;
		grad_ = null;
		mark_ = null;
		isGradSet_ = false;
		isMarkSet_ = false;
	}

	/**
       copy the float array into the edge_ array casting each float to an int.
	 */
	public void SetPoints (float[] points, int npoints) {
		if(nPoints_ > 0)
			edge_ = null;
		nPoints_ = npoints;
		edge_ = new int[npoints * 2];
		for (int i=0; i < 2 * npoints; i++)
			edge_[i] = (int)points[i];
	}

	/**
       copy the points array into the edge_ array
	 */
	public void SetPoints (int[] points, int npoints) {
		if(nPoints_ > 0)
			edge_ = null;
		nPoints_ = npoints;
		edge_ = new int[npoints * 2];
		for(int i = 0; i < 2 * npoints; i++)
			edge_[i] = points[i];
	}

	/**
       expects nPoints to be set
	 */
	public void SetGradient (float[] grx, float[] gry, float[] mark, int ncol){
		if(isGradSet_ && nPoints_ > 0)
			grad_ = null;

		grad_ = new double[nPoints_];
		isGradSet_ = true;

		double alpha, gx, gy;
		int x, y;
		for(int i = 0, k = 0; i < nPoints_; i++, k += 2){
			x = edge_[k];
			y = edge_[k + 1];
			gx = grx[y * ncol + x];
			gy = gry[y * ncol + x];
			for(int j = 0; j < 8; j++){
				if(mark[(y + gNb8[j][1]) * ncol + gNb8[j][0] + x] == 1){
					gx += grx[(y + gNb8[j][1]) * ncol + gNb8[j][0] + x];
					gy += gry[(y + gNb8[j][1]) * ncol + gNb8[j][0] + x];
				}
			}
			alpha = Math.atan2(gy, gx);
			alpha = (alpha < 0) ? alpha + PI : alpha;
			grad_[i] = alpha;
		}
	}

	/**
      added by jon so BgEdge can clear its own mark data
	 */
	void clearMark () {
		mark_ = new char[nPoints_];
		isMarkSet_ = true;
		//for(int j = 0; j < nPoints_; j++)
		//    mark_[j] = 0;
	}
}
