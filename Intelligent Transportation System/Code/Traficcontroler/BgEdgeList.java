/**
 BgEdgeList class.
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



import java.util.Vector;
import java.io.*;

/**
   a Vector of BgEdge elements.
   the Java version of BgEdgeList is different from the C++ version:
   1) by extending java.util.Vector, 
      BgEdgeList does not need "edgelist_" the list member
   2) because Java Vectors know their size, 
      BgEdgeList does not need to maintain "nEdges_" the number of edges
   3) the "crtedge_" next pointer is also not needed.
 */
public class BgEdgeList extends Vector {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1839994904410594886L;

	void AddEdge (float[] edge, int nPoints) {
		BgEdge tedge = new BgEdge();
		tedge.SetPoints(edge, nPoints);
		add(tedge);	
	}

	void AddEdge (int[] edge, int nPoints) {
		BgEdge tedge = new BgEdge();
		tedge.SetPoints(edge, nPoints);
		add(tedge);	
	}

	/**
      calls bgEdge.SetGradient() for each bgEdge in the BgEdgeList
	 */
	void SetGradient (float[] grx, float[] gry, float[] mark, int ncol) {
		int size = size();
		BgEdge bgEdge = null;
		for(int i = 0; i < size; i++){
			try{
				bgEdge = (BgEdge)elementAt(i);
				bgEdge.SetGradient(grx, gry, mark, ncol);
			}
			catch(ArrayIndexOutOfBoundsException e){
				System.err.println("BgEdgeList.SetGradient: " + e);
			}
		}
	}

	/**
       splits edges with discontinuities greater than the gap
	 */
	void processEdges (double gap) {
		int size = size();
		for(int i = 0; i < size; i++) {
			BgEdge bgEdge = null;
			try{
				bgEdge = (BgEdge)elementAt(i);
			}
			catch(ArrayIndexOutOfBoundsException e){
				System.err.println("BgEdgeList.processEdges: " + e);
				return;
			}
			for(int j = 0; j < bgEdge.nPoints_; j++){
			}
		}
	}

	void RemoveShortEdges (int minp) {
		int size = size();
		for(int i = 0; i < size; /* don't increment here */) {
			BgEdge bgEdge = null;
			try{
				bgEdge = (BgEdge)elementAt(i);
			}
			catch(ArrayIndexOutOfBoundsException e){
				System.err.println("BgEdgeList.RemoveShortEdges: " + e);
				return;
			}
			if(bgEdge.nPoints_ < minp){
				try{
					removeElementAt(i);
				}
				catch(ArrayIndexOutOfBoundsException e){
					System.err.println("BgEdgeList.RemoveShortEdges: " + e);
				}
			}
			else
				i++; // only increment if we don't remove an element
		}
	}

	/**
       set edge pixels in image to white
       first clears image to black
	 */
	public void SetBinImage (BgImage image) {
		int ix, iy;
		BgEdge crtedge = null;
		//int width = image.x_;   
		image.zeroImage(); // set all image pixels to black
		int size = size();
		for(int i = 0; i < size; i++) {
			try{
				crtedge = (BgEdge)elementAt(i);
			}
			catch(ArrayIndexOutOfBoundsException e){
				System.err.println("BgEdgeList.SetBinImage: " + e);
				return;
			}
			int edgeSize = crtedge.nPoints_ * 2;
			for(int j = 0; j < edgeSize; j += 2) {
				ix = crtedge.edge_[j];
				iy = crtedge.edge_[j + 1];
				image.im_[iy * image.x_ + ix] = 255;
			}
		}
	}

	/**
       set edge pixels in image to value
       does not clear image first
       new to Java version
	 */
	public void SetBinImage (BgImage image, char value) {
		int ix, iy;
		BgEdge crtedge = null;
		//int width = image.x_;   
		//image.zeroImage(); // set all image pixels to black
		int size = size();
		for(int i = 0; i < size; i++) {
			try{
				crtedge = (BgEdge)elementAt(i);
			}
			catch(ArrayIndexOutOfBoundsException e){
				System.err.println("BgEdgeList.SetBinImage: " + e);
				return;
			}
			int edgeSize = crtedge.nPoints_ * 2;
			for(int j = 0; j < edgeSize; j += 2) {
				ix = crtedge.edge_[j];
				iy = crtedge.edge_[j + 1];
				image.im_[iy * image.x_ + ix] = value;
			}
		}
	}

	public boolean SaveEdgeList (String edgeFileName) {
		int length;
		int i,j;
		BgEdge crtedge;
		int size = size();
		String s;

		FileOutputStream fp = null;
		try{
			fp = new FileOutputStream(edgeFileName);
		}
		catch(FileNotFoundException e){
			System.err.println("BgEdgeList.SaveEdgeList: " + e);
			return false;
		}
		for(i = 0; i < size; i++){
			try{
				crtedge = (BgEdge)elementAt(i);
				length = crtedge.nPoints_;
				for(j = 0; j < length; j++){
					//fprintf(fp, "%d %d %d\n", *((crtedge->edge_)+2*j), *((crtedge->edge_)+2*j+1), i);
					s = crtedge.edge_[2 * j] + " " + crtedge.edge_[2 * j + 1] + " " + i + "\n";
					fp.write(s.getBytes());
				}
			}
			catch(ArrayIndexOutOfBoundsException e){
				System.err.println("BgEdgeList.SaveEdgeList: " + e);
				return false;
			}
			catch(IOException e){
				System.err.println("BgEdgeList.SaveEdgeList: " + e);
				return false;
			}
		}
		try{   
			fp.close();
		}
		catch(IOException e){
			System.err.println("BgEdgeList.SaveEdgeList: " + e);
			return false;
		}
		return true;
	}

	/*
    void GetAllEdgePoints(int* x, int* y, int* n) {
	int length;
	int i,j;
	BgEdge *crtedge;
	int *edgep;

	crtedge = edgelist_;
	 *n = 0;
	for(i=0; i<nEdges_; i++) {
	    length = crtedge->nPoints_;
	    edgep = crtedge->edge_;
	    for(j=0; j<length; j++) {
		x[*n] = edgep[2*j];
		y[*n] = edgep[2*j+1];
		(*n)++;
	    }
	    crtedge = crtedge->next_;
	}
    }
	 */

	/**
       load array arguments with x and y coords of edge pels
       loses information about individual edges
       Java version changed to return n, the total number of edge pels
	 */
	public int GetAllEdgePoints(int[] x, int[] y) {
		int length;
		BgEdge crtedge;
		int size = size(); // the number of edges in edgeList Vector
		int n = 0;
		for(int i = 0; i < size; i++) {
			try{
				crtedge = (BgEdge)elementAt(i);
			}
			catch(ArrayIndexOutOfBoundsException e){
				System.err.println("BgEdgeList.GetAllEdgePoints: " + e);
				return 0;
			}
			length = crtedge.nPoints_;
			for(int j = 0; j < length; j++) {
				x[n] = crtedge.edge_[2 * j];
				y[n] = crtedge.edge_[2 * j + 1];
				n++;
			}
		}
		return n;
	}

	/**
       clears the mark array of each BgEdge in the BgEdgeList
       jon added BgEdge.clearMark() to let each BgEdge clear its own mark data
	 */
	void SetNoMark () {
		BgEdge crtedge;
		int size = size();
		for(int i = 0; i < size; i++) {
			try{
				crtedge = (BgEdge)elementAt(i);
			}
			catch(ArrayIndexOutOfBoundsException e){
				System.err.println("BgEdgeList.SetNoMark: " + e);
				return;
			}
			crtedge.clearMark();
		}
	}
}
