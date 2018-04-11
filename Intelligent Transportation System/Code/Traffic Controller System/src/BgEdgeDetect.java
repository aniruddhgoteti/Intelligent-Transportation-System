



public class BgEdgeDetect {

	public static final int FC_ELLIPSE = 0;
	public static final int FC_VERT_LINE = 1;
	public static final int FC_HORIZ_LINE = 2;
	public static final int FC_LINE = 3;
	public static final int FC_SQUARE_BOX = 4;
	public static final int FC_CUSTOM = 5;

	static double PI = 3.1415926535;
	static double ZERO_TRESH = 0.0000000001;

	static int MAX_FILTS = 31;

	// loaded in CreateFilters()
	double[] smofil_ = new double[MAX_FILTS];
	double[] diffil_ = new double[MAX_FILTS];
	double[][] mN_ = new double[MAX_FILTS][MAX_FILTS];
	double[][] mQ_ = new double[MAX_FILTS][MAX_FILTS];

	// loaded in CreateLookTable()
	static final int NO_ANGLES = 361;
	double[][] lookTable_ = new double[NO_ANGLES][];

	// set in gEdgeDetect()
	int WW_;
	int WL_;
	int nhcust_ = 0;
	int nlcust_ = 0;   
	int Compcount=0;
	float[] tcustx_ = null;
	float[] tcusty_ = null;

	// set in DoEdgeDetect
	// used in Evaluators and Comparators
	float confTr_;
	float rankTr_;

	// set in DoEdgeDetect
	// used in CustomRegionEval()
	float[] custx_;
	float[] custy_;
	int ncust_;

	float[] hcustx_; // set in SetCustomHigh()
	float[] hcusty_; // set in SetCustomHigh()
	float[] lcustx_; // set in SetCustomLow()
	float[] lcusty_; // set in SetCustomLow()

	int x_;
	int y_;

	float[] tl_; // pointer into float[] low in NewHysteresisTr, used in NewEdgeFollow
	float[] tm_; // pointer into float[] mark in NewHysteresisTr, used in NewEdgeFollow
	int npt_;   // number of points in an edge? set in NewHysteresisTr, used in NewEdgeFollow
	int tc_;    // index into float[] coord in NewHysteresisTr, used in NewEdgeFollow
	float[] gCoord; // pointer into float[] coord in NewHysteresisTr, used in NewEdgeFollow

	float[] grx_; // pointer into permGx
	float[] gry_; // pointer into permGy

	// set in DoEdgeDetect
	float[] permGx_;
	float[] permGy_; 
	float[] permConf_; // the confidence map
	float[] permRank_; // the rank map
	float[] permNmxRank_;
	float[] permNmxConf_;
	boolean havePerm_ = false;

	private static double TOL_E = 2.2e-8;

	/**
       BgEdgeDetect: constructor for BgEdgeDetect
       creates filters and lookup-table
	 */
	public BgEdgeDetect (int filtDim) {
		WL_ = filtDim;
		WW_ = 2 * WL_ + 1;
		CreateFilters();
		CreateLookTable();
	}

	private static int my_sign (double val) {
		if(val > TOL_E)
			return 1;
		if(val < -TOL_E)
			return -1;
		return 0;
	}

	double factorial (double num) {
		if(num == 0 || num == 1)
			return 1;
		return (num * factorial(num - 1));
	}

	interface Evaluator {
		float eval (float x, float y);
	}

	interface Comparator {
		float comp (float x0, float y0, float x, float y);
	}

	class EllipseEval implements Evaluator {
		public float eval (float x, float y) {
			return((x * x)/(rankTr_ * rankTr_) + (y * y)/(confTr_ * confTr_) - 1);
		}
	}

	class EllipseComp implements Comparator {
		public float comp (float x0, float y0, float x, float y) {
			//   return (EllipseEval(x,y)-EllipseEval(x0,y0));
			Compcount++;
			return((x * x - x0 * x0) / (rankTr_ * rankTr_) +
					(y * y - y0 * y0) / (confTr_ * confTr_));
		}
	}

	class LineEval implements Evaluator {
		public float eval (float x, float y) {
			return(confTr_ * x + rankTr_ * y - confTr_ * rankTr_);
		}
	}

	class LineComp implements Comparator {
		public float comp (float x0, float y0, float x, float y) {
			//   return (LineEval(x,y)-LineEval(x0,y0));
			Compcount++;
			return(confTr_ * (x - x0) + rankTr_ * (y - y0));
		}
	}

	class VerticalLineEval implements Evaluator {
		public float eval (float x, float y) {
			return(x - rankTr_);
		}
	}

	class VerticalLineComp implements Comparator {
		public float comp (float x0, float y0, float x, float y) {
			//   return (VerticalLineEval(x,y)-VerticalLineEval(x0,y0));
			Compcount++;
			return(x - x0);
		}
	}

	class HorizontalLineEval implements Evaluator {
		public float eval (float x, float y){
			return(y - confTr_);
		}
	}

	class HorizontalLineComp implements Comparator {
		public float comp (float x0, float y0, float x, float y){
			//   return (HorizontalLineEval(x,y)-HorizontalLineEval(x0,y0));
			Compcount++;
			return(y - y0);
		}
	}

	class SquareEval implements Evaluator {
		public float eval (float x, float y) {
			if((x / rankTr_) > (y / confTr_))
				return(x - rankTr_);
			else
				return(y - confTr_);
		}
	}

	class SquareComp implements Comparator {
		public float comp (float x0, float y0, float x, float y){
			//   return(SquareEval(x,y)-SquareEval(x0,y0));
			Compcount++;
			float tret;
			tret = ((x / rankTr_) > (y / confTr_)) ? x - rankTr_ : y - confTr_;
			tret -= ((x0 / rankTr_) > (y0 / confTr_)) ? x0 - rankTr_ : y0 - confTr_;
			return tret;
		}
	}

	/**
       evaluate user region function
       returns -1 if inside, +1 if outside
	 */
	class CustomRegionEval implements Evaluator {
		private static final int MAX_CUSTT = 30;

		public float eval (float r, float c) {
			if((r + c) <= ZERO_TRESH)
				return -1;
			int crossings = 0;
			float x;
			if(tcustx_ == null)
				tcustx_ = new float[MAX_CUSTT];
			if(tcusty_ == null)
				tcusty_ = new float[MAX_CUSTT];
			//shift to origin
			for(int i = 0; i < ncust_; i++){
				tcustx_[i] = custx_[i] - r;
				tcusty_[i] = custy_[i] - c;
			}

			for(int i = 0; i < (ncust_ - 1); i++){
				if((tcusty_[i] > 0 && tcusty_[i + 1] <= 0) ||
						(tcusty_[i + 1] > 0 && tcusty_[i] <= 0)){
					x = (tcustx_[i] * tcusty_[i+1] - tcustx_[i+1] * tcusty_[i]) / (tcusty_[i+1]-tcusty_[i]);
					if(x > 0)
						crossings++;
				}
			}
			if((crossings % 2) == 1)
				return -1;
			else
				return 1;
		}
	}

	class CustomRegionComp implements Comparator {
		public float comp (float r0, float c0, float r, float c) {
			return 0f;
		}
	}

	/**
       called by CreateLookTable()
       first clears a[] array
	 */
	protected void GenerateMaskAngle (double[] a, double theta) {
		int sflag;
		int i, j, k;
		double cval[] = new double[4];
		double corner[][] = new double[2][4];
		double sinv, cosv;
		double intrs[][] = new double[2][4];
		int scor[] = new int[4]; 
		int nscor[] = new int[4];
		int sind, rowind, colind;
		double cordi[][] = new double[2][4];
		int lsigind, corin;
		int sigind[] = new int[4];
		double diffin[] = new double[2];
		double comcoor;

		theta = theta * PI / 180.0;
		sinv = Math.sin(theta);
		cosv = Math.cos(theta);
		int size = WW_ * WW_;

		for(i = 0; i < size; i++)
			a[i] = 0;

		for(i = WL_; i >= -WL_; i--){
			for(j = -WL_; j <= WL_; j++){
				corner[0][0] = j - 0.5;
				corner[0][1] = j + 0.5;
				corner[0][2] = j + 0.5;
				corner[0][3] = j - 0.5;

				corner[1][0] = i + 0.5;
				corner[1][1] = i + 0.5;
				corner[1][2] = i - 0.5;
				corner[1][3] = i - 0.5;

				cval[0] = -sinv * corner[0][0] + cosv * corner[1][0];
				cval[1] = -sinv * corner[0][1] + cosv * corner[1][1];
				cval[2] = -sinv * corner[0][2] + cosv * corner[1][2];
				cval[3] = -sinv * corner[0][3] + cosv * corner[1][3];

				scor[0] = my_sign(cval[0]);
				scor[1] = my_sign(cval[1]);
				scor[2] = my_sign(cval[2]);
				scor[3] = my_sign(cval[3]);

				sind = 0;
				if(scor[0] != 0)
					nscor[sind++] = scor[0];
				if(scor[1] != 0)
					nscor[sind++] = scor[1];
				if(scor[2] != 0)
					nscor[sind++] = scor[2];
				if(scor[3] != 0)
					nscor[sind++] = scor[3];

				sflag = 0;
				for(k = 1; k < sind; k++){
					if(nscor[k] != nscor[0])
						sflag++;
				}

				rowind = i + WL_;
				colind = j + WL_;

				if(sflag == 0){
					if(nscor[0] == 1)
						a[colind + rowind * WW_] = 1.0;
					else
						a[colind + rowind * WW_] = 0.0;
				}

				if(sflag != 0){
					for(k = 0; k < 4; k++)
						intrs[0][k] = intrs[1][k] = 0.0;

					if(scor[0] == 0){
						intrs[0][0] = corner[0][0];
						intrs[1][0] = corner[1][0];
					}
					if(scor[0] * scor[1]<0){
						intrs[0][0] = corner[1][0]*cosv/sinv;
						intrs[1][0] = corner[1][0];
					}
					if(scor[1] == 0){
						intrs[0][1] = corner[0][1];
						intrs[1][1] = corner[1][1];
					}
					if(scor[1] * scor[2]<0){
						intrs[0][1] = corner[0][1];
						intrs[1][1] = corner[0][1]*sinv/cosv;
					}
					if(scor[2] == 0){
						intrs[0][2] = corner[0][2];
						intrs[1][2] = corner[1][2];
					}
					if(scor[2] * scor[3] < 0){
						intrs[0][2] = corner[1][2]*cosv/sinv;
						intrs[1][2] = corner[1][2];
					}
					if(scor[3] == 0){
						intrs[0][3] = corner[0][3];
						intrs[1][3] = corner[1][3];
					}
					if(scor[3] * scor[0] < 0){
						intrs[0][3] = corner[0][3];
						intrs[1][3] = corner[0][3]*sinv/cosv;
					}

					corin = 0;
					if(Math.abs(intrs[0][0]) > TOL_E || Math.abs(intrs[1][0]) > TOL_E){
						cordi[0][corin] = intrs[0][0];
						cordi[1][corin++] = intrs[1][0];
					}
					if(Math.abs(intrs[0][1]) > TOL_E || Math.abs(intrs[1][1]) > TOL_E){
						cordi[0][corin] = intrs[0][1];
						cordi[1][corin++] = intrs[1][1];
					}
					if(Math.abs(intrs[0][2]) > TOL_E || Math.abs(intrs[1][2]) > TOL_E){
						cordi[0][corin] = intrs[0][2];
						cordi[1][corin++] = intrs[1][2];
					}
					if(Math.abs(intrs[0][3]) > TOL_E || Math.abs(intrs[1][3]) > TOL_E){
						cordi[0][corin] = intrs[0][3];
						cordi[1][corin++] = intrs[1][3];
					}

					lsigind = 0;
					if(scor[0] > 0)
						sigind[lsigind++] = 0;
					if(scor[1] > 0)
						sigind[lsigind++] = 1;
					if(scor[2] > 0)
						sigind[lsigind++] = 2;
					if(scor[3] > 0)
						sigind[lsigind++] = 3;

					if(lsigind == 1){
						a[colind + rowind * WW_] = 
							0.5 * Math.abs(cordi[0][0]-cordi[0][1]) * Math.abs(cordi[1][0]-cordi[1][1]);
					}
					if(lsigind == 2){
						diffin[0] = (int) Math.abs(cordi[0][0] - cordi[0][1]);
						diffin[1] = (int) Math.abs(cordi[1][0] - cordi[1][1]);
						if(diffin[0] == 1){
							comcoor = corner[1][sigind[0]];
							a[colind + rowind * WW_] = 
								0.5*(Math.abs(comcoor-cordi[1][0])+Math.abs(comcoor-cordi[1][1]));
						}
						if(diffin[1] == 1){
							comcoor = corner[0][sigind[0]];
							a[colind + rowind * WW_] = 
								0.5*(Math.abs(comcoor-cordi[0][0])+Math.abs(comcoor-cordi[0][1]));
						}
					}
					if(lsigind == 3){
						a[colind + rowind * WW_] = 
							1.0-0.5*Math.abs(cordi[0][0]-cordi[0][1])*Math.abs(cordi[1][0]-cordi[1][1]);
					}
				}
			}
		}

		//A=A-mean(mean(A));
		comcoor = 0;
		for(i = 0; i < size; i++)
			comcoor += a[i];
		comcoor /= size;
		for(i = 0; i < size; i++)
			a[i] -= comcoor;

		//A=A/norm(A,'fro')
		comcoor = 0;
		for(i = 0; i < size; i++)
			comcoor += a[i] * a[i];
		comcoor = Math.sqrt(comcoor);
		for(i = 0; i < size; i++)
			a[i] /= comcoor;
	}

	void CreateFilters () {
		int i, j;
		double w;
		for(i = -WL_; i <= WL_; i++){
			w = Math.pow(2,(-2 * WL_)) * factorial(2 * WL_) /
			(factorial(WL_ - i) * factorial(WL_ + i));
			smofil_[i + WL_] = w;
			diffil_[i + WL_] = (2 * i * w) / WL_;
		}

		double norms = 0;
		double normd = 0;
		for(i = 0; i < WW_; i++){
			norms += smofil_[i] * smofil_[i];
			normd += diffil_[i] * diffil_[i];
		}

		for(j = 0; j < WW_; j++){
			for(i = 0; i < WW_; i++){
				mQ_[i][j] = (smofil_[j] * smofil_[i]) / norms +
				(diffil_[j] * diffil_[i]) / normd;
				mN_[i][j] = (i==j) ? 1 - mQ_[i][j] : -mQ_[i][j];
			}
		}
	}

	void CreateLookTable () {
		//System.err.println("Creating angle lookup table");
		int size = WW_ * WW_;

		for(int i = -180; i <= 180; i++){
			lookTable_[i + 180] = new double[size];
			GenerateMaskAngle(lookTable_[i + 180], (double)i);
		}
	}

	/**
       computes confidence map and rank information of specified image
       Pre : cim is an image, space for confMap and rank are allocated
       Post: confidence map and rank have been computed for cim
         and stored into confMap and rank respectively
	 */
	void ComputeEdgeInfo (BgImage cim, float[] confMap, float[] rank){
		x_ = cim.x_;
		y_ = cim.y_;
		int size = x_ * y_;
		float[] pGx = new float[size];
		float[] pGy = new float[size];
		float[] pTemp = new float[size];

		System.err.println("Computing confidence map...\n");

		BgImage tcim = new BgImage(x_, y_, false);
		if(cim.colorIm_)
			tcim.SetImageFromRGB(cim.im_, x_, y_, false);

		System.err.println("...smooth-differentiation filtering\n");
		GaussDiffFilter(tcim, pGx, pGy, pTemp);

		// compute confidences (subspace estimate)
		System.err.println("...subspace estimate\n");
		SubspaceEstim(pTemp, pGx, pGy, confMap);

		// compute edge strength from gradient image
		System.err.println("...edge strengths\n");
		Strength(pGx, pGy, pTemp);

		// compute ranks of the strengths
		System.err.println("...computing ranks\n");
		CompRanks(pTemp, rank);

		//de-allocate memory
		pTemp = null;
		pGy = null;
		pGx = null;
	}

	/**
       DoEdgeDetect: the main function for edge detection.
       nmxType: one of FC_ELLIPSE, FC_VERT_LINE, FC_HORIZ_LINE, FC_LINE
       hystTypeHigh: one of 
         FC_ELLIPSE, FC_VERT_LINE, FC_HORIZ_LINE, FC_LINE, FC_SQUARE_BOX, FC_CUSTOM
       hystTypeLow: one of 
         FC_ELLIPSE, FC_VERT_LINE, FC_HORIZ_LINE, FC_LINE, FC_SQUARE_BOX, FC_CUSTOM
	 */
	public int
	DoEdgeDetect (BgImage cim, // input image
			BgEdgeList cel, // edge list to fill with pixels
			double nmxr, // non-max-suppresion rank threshold
			double nmxc, // non-max-suppresion confidence threshold
			double rh, // hyst High Rank threshold
			double ch, // hyst High Confidence threshold
			double rl, // hyst Low Rank threshold
			double cl, // hyst Low Confidence threshold
			int nMin, // minimum number of pixels on an edge
			int nmxType, // non-max-suppresion type
			int hystTypeHigh, 
			int hystTypeLow){
		x_ = cim.x_;
		y_ = cim.y_;
		int size = x_ * y_;
		System.err.print("Start edge detection..."); 
		permGx_ = new float[size];
		permGy_ = new float[size];
		permConf_ = new float[size];
		permRank_ = new float[size];
		permNmxRank_ = new float[size];
		permNmxConf_ = new float[size];
		havePerm_ = true;

		float[] tr = new float[size];
		float[] tc = new float[size];
		float[] tdh = new float[size];
		float[] tdl = new float[size];

		// compute and load gradient images
		// side-effect: copies cim.im_ into tr!
		// does not change cim
		System.err.print("...smooth-differentiation filtering");
		GaussDiffFilter(cim, permGx_, permGy_, tr);

		// does not change tr
		System.err.print("...subspace estimate");
		SubspaceEstim(tr, permGx_, permGy_, permConf_);

		// compute edge strength from gradient image; 
		// loads strengths into tr; does not change permGx_ or permGy_
		System.err.print("...edge strengths");
		Strength(permGx_, permGy_, tr);

		// compute ranks of the strengths
		// loads permRank_ array; does not change tr (the strengths)
		System.err.print("...computing ranks");
		CompRanks(tr, permRank_);

		// new nonmaxima supression
		System.err.print("...nonmaxima supression: ");

		// select appropriate function
		Comparator fcomp;
		Evaluator feval = null;
		switch(nmxType) {
		case FC_ELLIPSE:
			fcomp = new EllipseComp();
			System.err.print("arc. ");
			break;
		case FC_VERT_LINE:
			fcomp = new EllipseComp();
			System.err.print("vertical line. ");		
			break;
		case FC_HORIZ_LINE:
			fcomp = new HorizontalLineComp();
			System.err.print("horizontal line. ");		
			break;
		case FC_SQUARE_BOX:
			fcomp = new SquareComp();
			System.err.print("box. ");		
			break;
		case FC_LINE:
			fcomp = new LineComp();
			System.err.print("line. ");
			break;
		default:
			System.err.print("unknown type. ");
		return 0;
		}

		confTr_ = (float)nmxc;
		rankTr_ = (float)nmxr;
		NewNonMaxSupress(permRank_, permConf_, permGx_, permGy_, permNmxRank_, permNmxConf_, fcomp);

		// new hysteresis thresholding
		System.err.print("...hysteresis thresholding, high: ");

		// select function, high curve
		switch(hystTypeHigh) {
		case FC_ELLIPSE:
			feval = new EllipseEval();
			System.err.print("arc. ");		
			break;
		case FC_VERT_LINE:
			feval = new VerticalLineEval();
			System.err.print("vertical line. ");		
			break;
		case FC_HORIZ_LINE:
			feval = new HorizontalLineEval();
			System.err.print("horizontal line. ");		
			break;
		case FC_SQUARE_BOX:
			feval = new SquareEval();
			System.err.print("box. ");		
			break;
		case FC_LINE:
			feval = new LineEval();
			System.err.print("line. ");  		
			break;
		case FC_CUSTOM:
			custx_ = hcustx_;
			custy_ = hcusty_;
			ncust_ = nhcust_;
			feval = new CustomRegionEval();
			System.err.print("custom. ");
			break;
		}
		confTr_ = (float)ch;
		rankTr_ = (float)rh;
		StrConfEstim(permNmxRank_, permNmxConf_, tdh, feval);

		System.err.print("  low: ");

		// select function, low curve
		switch(hystTypeLow) {
		case FC_ELLIPSE:
			feval = new EllipseEval();
			System.err.print("arc. ");
			break;
		case FC_VERT_LINE:
			feval = new VerticalLineEval();
			System.err.print("vertical line. ");		
			break;
		case FC_HORIZ_LINE:
			feval = new HorizontalLineEval();
			System.err.print("horizontal line. ");		
			break;
		case FC_SQUARE_BOX:
			feval = new SquareEval();
			System.err.print("box. ");		
			break;
		case FC_LINE:
			feval = new LineEval();
			System.err.print("line. ");  		
			break;
		case FC_CUSTOM:
			custx_ = lcustx_;
			custy_ = lcusty_;
			ncust_ = nlcust_;
			feval = new CustomRegionEval();
			System.err.print("custom. ");
			break;	
		}
		confTr_ = (float)cl;
		rankTr_ = (float)rl;

		StrConfEstim(permNmxRank_, permNmxConf_, tdl, feval);

		grx_ = permGx_;
		gry_ = permGy_;
		NewHysteresisTr(tdh, tdl, cel, nMin, tr, tc);

		System.err.println("Done edge detection.");

		tdl = null;
		tdh = null;
		tr = null;
		tc = null;

		Compcount= Compcount-30000;
		Compcount=Compcount/12000;
		//System.out.Println("Compcount" = + Compcount);
		return Compcount; 
	}

	/**
       compute confidences (subspace estimate)
       called by DoEdgeDetect()
       im original image, set to tr in caller (clean copy of cim.im)
         Note: SubspaceEstim does not change im, only reads from it
       grx x gradient of image, set to permGx_ in caller
       gry y gradient of image, set to permGy_ in caller
       cee confidence edge estimate, set to permConf_ in caller (array of zeros)
	 */
	void SubspaceEstim (float[] im, float[] grx, float[] gry, float[] cee){
		int itim = 0; // index into im
		int itgx = 0; // index into grx
		int itgy = 0; // index into gry
		int itcee = 0; // index into cee

		double[] tae;
		double[] ti = new double[WW_ * WW_];

		int i, j, l, c;
		double v1;
		double angleEdge;
		int WW2 = WW_ * WW_;

		itim += WL_ * x_;
		itgx += WL_ * x_;
		itgy += WL_ * x_;
		itcee += WL_ * x_;

		for(j = WL_; j < (y_ - WL_); j++){
			for(i = 0; i < WL_; i++)
				cee[itcee + i] = 0;
			itim += WL_;
			itgx += WL_;
			itgy += WL_;
			itcee += WL_;

			for(i = WL_; i < (x_ - WL_); i++, itim++, itgx++, itgy++, itcee++){
				if((Math.abs(grx[itgx]) + Math.abs(gry[itgy])) > TOL_E){
					angleEdge = (-Math.atan2(grx[itgx], gry[itgy])) * 180.0 / PI;
					tae = lookTable_[(int)(angleEdge + 180.49)];

					//A=A-mean(A)
					v1 = 0;
					for(l = 0; l < WW_; l++){
						for(c = 0; c < WW_; c++){
							v1 += ti[l * WW_ + c] =
								im[itim + (l - WL_) * x_ + c - WL_];
						}
					}
					v1 /= WW2;
					for(l = 0; l < WW2; l++)
						ti[l] -= v1;

					//A/norm(A,'fro')
					v1 = 0;
					for(l = 0; l < WW2; l++)
						v1 += ti[l] * ti[l];
					v1 = Math.sqrt(v1);
					for(l = 0; l < WW2; l++)
						ti[l] /= v1;

					//global
					v1 = 0;
					for(l = 0; l < WW2; l++)
						v1 += tae[l] * ti[l];
					v1 = Math.abs(v1);
					cee[itcee] = (float)v1;
				}
				else{
					cee[itcee] = 0;
				}
			}
			for(i = 0; i < WL_; i++)
				cee[itcee + i] = 0;
			itim += WL_;
			itgx += WL_;
			itgy += WL_;
			itcee += WL_;
		}
		WW2 = x_ * y_; // why do we need to reassign this the same value?
		for(j = 0; j < (WL_ * x_); j++){
			cee[j] = 0;
			cee[WW2 - j - 1] = 0;
		}
		ti = null;
	}

	void GaussFilter (BgImage cim, float[] fim, double sigma, int width){
		double[] filter;
		char[] im;
		double[] tim;
		double sum = 0;
		double sum1 = 0;
		int i, ii, jj, j, k;
		int size = x_ * y_;

		im = cim.im_;
		if(width == -2){   
			for(i = 0; i < size; i++)
				fim[i] = im[i];
			return;
		}

		if(width < 3)
			width = (int) (1 + 2 * Math.ceil(2.5 * sigma));
		int tail = width / 2;
		width = 2 * tail + 1;

		//create kernel
		filter = new double[width];
		for(i = -tail; i <= tail; i++)
			sum += filter[i + tail] = Math.exp( -i * i / (2 * sigma * sigma));
		for(i = 0; i < width; i++)
			filter[i] /= sum;

		//filter image
		//im = cim->im_; redundant assignment
		tim = new double[size];
		for(j = 0; j < y_; j++){
			for(i = tail; i < (x_ - tail); i++){
				sum = 0;
				for(k =- tail; k <= tail; k++)
					sum += im[j * x_ + i + k] * filter[k + tail];
				tim[j * x_ + i] = sum;
			}

			for(i = 0; i < tail; i++){
				tim[j * x_ + i] = 0;
				tim[j * x_ + x_ - i - 1] = 0;
				for(k = -tail; k <= tail; k++){
					ii = (k + i) >= 0 ? k + i : 0;
					tim[j * x_ + i] += im[j * x_ + ii] * filter[k + tail];
					ii = (x_ - i - 1 + k) <  x_ ? x_ - i - 1 + k : x_ - 1;
					tim[j * x_ + x_ - i - 1] += im[j * x_ + ii] * filter[k + tail];
				}
			}
		}

		for(i = 0; i < x_; i++){
			for(j = tail; j < (y_ - tail); j++){
				sum = 0;
				for(k = -tail; k <= tail; k++)
					sum += tim[(j + k) * x_ + i] * filter[k + tail];
				fim[j * x_ + i] = (float) (sum);
			}
			for(j = 0; j < tail; j++){
				sum = 0;
				sum1 = 0;
				for(k = -tail; k <= tail; k++){
					jj = (k + j) >= 0 ? k + j : 0;
					sum += tim[jj * x_ + i] * filter[k + tail];
					jj = (y_ - j - 1 + k) < y_ ? y_ - j - 1 + k : y_ - 1;
					sum1 += tim[jj * x_ + i] * filter[k + tail];
				}
				fim[j * x_ + i] = (float)(sum);
				fim[(y_ - j - 1) * x_ + i] = (float)(sum1);
			}
		}
		filter = null;
		tim = null;
	}

	/**
       smooth differentiation filtering
       computes gradient images
       called by DoEdgeDetect()
       loads grx, grx set to permGx_ in caller
       loads gry, gry set to permGy_ in caller
       rexIm set to tr in caller >> side-effect: copies cim.im_ into rezIm!

       in the C++ version assignment was overloaded to copy, but
       in this function the image array is not changed!
       so we don't copy the image data here

       Note: this only reads from cim.im_ it does not write/change it
	 */
	void GaussDiffFilter (BgImage cim, float[] grx, float[] gry, float[] rezIm){
		char[] im = cim.im_;
		int size = x_ * y_; // set from cim.x_ and cim.y_ in caller

		double sum = 0;
		int i, j, k;

		// create kernels
		double[] sf = smofil_;  // smooth filter
		double[] df = diffil_;  // diff filter

		double[] tim = new double[size];
		for(i = 0; i < size; i++){
			//Java initializes array elements to zero
			//grx[i] = gry[i] = 0;
			//tim[i] = 0;
			rezIm[i] = im[i];
		}

		//filter image x
		//smooth on y
		for(i = 0; i < x_; i++){
			for(j = WL_; j < (y_ - WL_); j++){
				sum = 0;
				for(k = -WL_; k <= WL_; k++)
					sum += im[(j + k) * x_ + i] * sf[k + WL_];
				tim[j * x_ + i] = sum;
			}
		}
		//diff on x
		for(j = 0; j < y_; j++){
			for(i = WL_; i < (x_ - WL_); i++){
				sum = 0;
				for(k = -WL_; k <= WL_; k++)
					sum += tim[j * x_ + i + k] * df[k + WL_];
				grx[j * x_ + i] = (float)(sum);
			}
		}

		//filter image y
		for(i = 0; i < size; i++)
			tim[i] = 0;
		// in the C++ version assignment was overloaded to copy, but
		// in this function the image array is not changed!
		// so we don't copy the image data here
		// im = cim->im_;
		//smooth on x
		for(j = 0; j < y_; j++){
			for(i = WL_; i < (x_ - WL_); i++){
				sum = 0;
				for(k = -WL_; k <= WL_; k++)
					sum += im[j * x_ + i + k] * sf[k + WL_];
				tim[j * x_ + i] = sum;
			}
		}
		//diff on y
		for(i =0 ; i < x_; i++){
			for(j = WL_; j < (y_ - WL_); j++){
				sum = 0;
				for(k = -WL_; k <= WL_; k++)
					sum += tim[(j + k) * x_ + i] * df[k + WL_];
				gry[j * x_ + i] = (float)(sum);
			}
		}
		tim = null;
	}

	// called by DoEdgeDetect()
	// strength set to tr, ranks set to permRank_
	// heap sorts ranks
	void CompRanks (float[] strength, float[] ranks){

		int size = x_ * y_;
		int[] index = new int[size];
		float[] ra = new float[size];

		for(int ii = 0; ii < size; ii++){
			index[ii] = ii;
			ranks[ii] = 0; // isn't this already initialized to zero?
			ra[ii] = strength[ii]; // copy strengths into ra
		}

		// heap sort with ranks (from numerical recipies)
		//unsigned long i, ir, j, l;
		//unsigned long n = size;
		// test this!!!!
		long i, ir, j, l;
		long n = size;
		float rra;
		int irra;

		if(n < 2)
			return;
		l = (n >>> 1) + 1;
		ir = n;
		for(;;){
			if(l > 1){
				rra = ra[(int)--l - 1];
				irra = index[(int)l - 1];
			}
			else{
				rra = ra[(int)ir - 1];
				irra = index[(int)ir - 1];
				ra[(int)ir - 1] = ra[1 - 1];
				index[(int)ir - 1] = index[1 - 1];
				if((int)--ir == 1){
					ra[1 - 1] = rra;
					index[1 - 1] = irra;
					break;
				}
			}
			i = l;
			j = l + l;
			while(j <= ir){
				if(j < ir && ra[(int)j - 1] < ra[(int)j + 1 - 1]) // is this a bug?
					j++;
				if(rra < ra[(int)j - 1]){
					ra[(int)i - 1] = ra[(int)j - 1];
					index[(int)i - 1] = index[(int)j - 1];
					i = j;
					//System.err.println("j: " + j);
					j <<= 1;
					//System.err.println("j <<= 1: " + j);
				}
				else
					j = ir + 1;
			}
			ra[(int)i - 1] = rra;
			index[(int)i - 1] = irra;
		}
		// setranks
		irra = 1;
		for(int ii = 1; ii < size; ii++){
			if(ra[ii] > ZERO_TRESH){
				ranks[index[ii]] = (float)irra;
				if(ra[ii] > ra[ii - 1])
					irra++;
			}
		}
		irra--;
		for(int ii = 0; ii < size; ii++) 
			ranks[ii] /= irra;
		index = null;
		ra = null;
	}

	void StrConfEstim (float[] ranks, float[] confidence, float[] rezult,
			Evaluator eval){
		for(int i = 0; i < x_ * y_; i++)
			rezult[i] = eval.eval(ranks[i], confidence[i]);
	}

	/**
      loads strength array
	 */
	void Strength (float[] grx, float[] gry, float[] strength){
		int itgx = 0; // index into grx
		int itgy = 0; // index into gry
		int its = 0; // index into strength
		double val;

		for(int j = 0; j < y_; j++)
			for(int i = 0; i < x_; i++){
				val = Math.sqrt((double)(grx[itgx] * grx[itgx]) +
						(double)(gry[itgy] * gry[itgy]));
				strength[its] = (float)(val);
				itgx++;
				itgy++;
				its++;
			}
	}

	//	NewNonMaxSupress(permRank_, permConf_, permGx_, permGy_, permNmxRank_, permNmxConf_, fcomp);
	// changes rank[], conf[], nmxRank[], and nmxConf[], not grx, gry
	void NewNonMaxSupress (float[] rank, float[] conf, float[] grx, float[] gry, 
			float[] nmxRank, float[] nmxConf, Comparator comp){
		int itr = 0; // index into rank
		int itc = 0; // index into conf
		int itgx = 0; // index into grx
		int itgy = 0; // index into gry
		int itnmxr = 0; // index into nmxRank
		int itnmxc = 0; // index into nmxConf
		float alpha, r1, c1, r2, c2, lambda;

		//for(int i = 0; i < x_ * y_; i++)
		//    nmxRank[i] = nmxConf[i] = 0;
		for(int i = 0; i < x_; i++){
			rank[i] = conf[i] = 0;
			rank[(y_ - 1) * x_ + i] = conf[(y_ - 1) * x_ + i] = 0;
		}
		for(int j = 0; j < y_; j++){
			rank[j * x_] = conf[j * x_] = 0;
			rank[j * x_ + x_ -1] = conf[j * x_ + x_ - 1] = 0;
		}

		for(int j = 0; j < y_; j++){
			for(int i = 0; i < x_; i++, itr++, itc++, itgx++, itgy++, itnmxr++, itnmxc++){
				if(rank[itr] > 0 && conf[itc] > 0){
					alpha = (float)Math.atan2(gry[itgy], grx[itgx]);
					alpha = (alpha < 0) ? alpha + (float)PI : alpha;
					if(alpha <= PI/4){
						lambda = (float)Math.tan(alpha);
						r1 = (1 - lambda) * rank[itr + 1] + lambda * rank[itr + x_ + 1];
						c1 = (1 - lambda) * conf[itc + 1] + lambda * conf[itc + x_ + 1];
						r2 = (1 - lambda) * rank[itr - 1] + lambda * rank[itr - x_ - 1];
						c2 = (1 - lambda) * conf[itc - 1] + lambda * conf[itc - x_ - 1];
						//if((this->*feval)(*itr, *itc, r1, c1)<0 && 
						//(this->*feval)(*itr, *itc, r2, c2)<=0){
						if(comp.comp(rank[itr], conf[itc], r1, c1) < 0 && 
								comp.comp(rank[itr], conf[itc], r2, c2) <= 0){
							nmxRank[itnmxr] = rank[itr];
							nmxConf[itnmxc] = conf[itc];
						}
					}
					else if(alpha <= PI/2){
						lambda = (float)Math.tan(PI/2 - alpha);
						r1 = (1 - lambda) * rank[itr + x_] + lambda * rank[itr + x_ + 1];
						c1 = (1 - lambda) * conf[itc + x_] + lambda * conf[itc + x_ + 1];
						r2 = (1 - lambda) * rank[itr - x_] + lambda * rank[itr - x_ - 1];
						c2 = (1 - lambda) * conf[itc - x_] + lambda * conf[itc - x_ - 1];
						//if((this->*feval)(*itr, *itc, r1, c1)<0 && 
						//   (this->*feval)(*itr, *itc, r2, c2)<=0) {
						if(comp.comp(rank[itr], conf[itc], r1, c1) < 0 && 
								comp.comp(rank[itr], conf[itc], r2, c2) <= 0){
							nmxRank[itnmxr] = rank[itr];
							nmxConf[itnmxc] = conf[itc];
						}

					}
					else if(alpha <= 3 * PI/4){
						lambda = (float)Math.tan(alpha - PI/2);
						r1 = (1 - lambda) * rank[itr + x_] + lambda * rank[itr + x_ - 1];
						c1 = (1 - lambda) * conf[itc + x_] + lambda * conf[itc + x_ - 1];
						r2 = (1 - lambda) * rank[itr - x_] + lambda * rank[itr - x_ + 1];
						c2 = (1 - lambda) * conf[itc - x_] + lambda * conf[itc - x_ + 1];
						//if((this->*feval)(*itr, *itc, r1, c1)<0 && 
						//   (this->*feval)(*itr, *itc, r2, c2)<=0){
						if(comp.comp(rank[itr], conf[itc], r1, c1) < 0 && 
								comp.comp(rank[itr], conf[itc], r2, c2) <= 0){
							nmxRank[itnmxr] = rank[itr];
							nmxConf[itnmxc] = conf[itc];
						}
					}
					else{
						lambda = (float)Math.tan(PI - alpha);
						r1 = (1 - lambda) * rank[itr - 1] + lambda * rank[itr + x_ - 1];
						c1 = (1 - lambda) * conf[itc - 1] + lambda * conf[itc + x_ - 1];
						r2 = (1 - lambda) * rank[itr + 1] + lambda * rank[itr - x_ + 1];
						c2 = (1 - lambda) * conf[itc + 1] + lambda * conf[itc - x_ + 1];
						//if((this->*feval)(*itr, *itc, r1, c1)<0 && 
						//   (this->*feval)(*itr, *itc, r2, c2)<=0){
						if(comp.comp(rank[itr], conf[itc], r1, c1) < 0 && 
								comp.comp(rank[itr], conf[itc], r2, c2) <= 0){
							nmxRank[itnmxr] = rank[itr];
							nmxConf[itnmxc] = conf[itc];
						}
					}
				}
			}
		}
	}

	static final double HYST_LOW_CUT = 0.0;

	//NewHysteresisTr(tdh, tdl, cel, nMin, tr, tc);
	void NewHysteresisTr (float[] edge, float[] low, 
			BgEdgeList cel, int nMin, 
			float[] mark, float[] coord){
		int tm = 0; // index into mark
		int te = 0; // index into edge

		for(int i = 0; i < x_ * y_; i++)
			mark[i] = 0; // we need to clear the mark array (had strengths)

		tm_ = mark; // used in NewEdgeFollow
		tl_ = low;  // used in NewEdgeFollow

		for(int j = 0; j < y_; j++){
			for(int i = 0; i < x_; i++, tm++, te++){
				if((mark[tm] == 0) && (edge[te] > HYST_LOW_CUT)){ 
					// found an edge start
					npt_ = 0;
					mark[tm] = 1;
					gCoord = coord; // global pointer to coord array
					tc_ = 0; // global index into coord
					NewEdgeFollow(i, j);
					//store the edge
					if(npt_ >= nMin) 
						cel.AddEdge(coord, npt_);
				}
			}
		}
	}

	static final int gNb[][] = { {1, 0},
		{1, 1},
		{1, -1},
		{0, 1},
		{0, -1},
		{-1, 0},
		{-1, 1},
		{-1, -1} };
	/*
      uses gNb, x_, tl_, tm_, tc_, npt_
	 */
	void NewEdgeFollow (int ii, int jj){
		int iin, jjn;
		for(int i = 0; i < 8; i++){
			iin = ii + gNb[i][0];
			jjn = jj + gNb[i][1];
			if((tm_[jjn * x_ + iin] == 0) && ((tl_[jjn * x_ + iin]) > 0)){
				tm_[jjn * x_ + iin] = 1;
				NewEdgeFollow(iin, jjn);
			}
		}
		// jon added this
		gCoord[tc_++] = (float)ii;
		gCoord[tc_++] = (float)jj;
		npt_++;
	}

	void SetCustomHigh (int[] x, int[] y, int n, int sx, int sy){
		if(nhcust_ > 0) {
			hcustx_ = null;
			hcusty_ = null;
		}
		nhcust_ = n + 2;
		hcustx_ = new float[nhcust_];
		hcusty_ = new float[nhcust_];

		int idx = 0;
		//hcustx_[idx] = 0; Java arrays are initialized to zero
		//hcusty_[idx++] = 0; Java arrays are initialized to zero
		for(int i = 0; i < n; i++){
			hcustx_[idx] = ((float)x[i]) / sx;
			hcusty_[idx++] = (float)(1.0 - ((float)y[i]) / sy);
		}   
		hcustx_[idx] = 0;
		hcusty_[idx++] = 0;

		System.err.println(" hyst high custom x: ");
		for(int i = 0; i <= n; i++)
			System.err.println(hcustx_[i]);
		System.err.println();
		System.err.println(" hist high custom y: ");
		for(int i = 0; i <= n; i++)
			System.err.println(hcusty_[i]);
		System.err.println();   
	}

	void SetCustomHigh (double[] x, double[] y, int n){
		if(nhcust_ > 0){
			hcustx_ = null;
			hcusty_ = null;
		}
		//nhcust_ = 0;
		//hcustx_ = hcusty_ = 0;
		nhcust_ = n + 2;
		hcustx_ = new float[nhcust_];
		hcusty_ = new float[nhcust_];

		int idx = 0;
		//hcustx_[idx] = 0;
		//hcusty_[idx++] = 0;
		for(int i = 0; i < n; i++){
			hcustx_[idx] = (float) x[i];
			hcusty_[idx++] = (float) y[i];
		}
		hcustx_[idx] = 0;
		hcusty_[idx++] = 0;

		System.err.println(" hyst high custom x: ");
		for(int i = 0; i <= n; i++)
			System.err.println(hcustx_[i]);
		System.err.println();
		System.err.println(" hist high custom y: ");
		for(int i = 0; i <= n; i++)
			System.err.println(hcusty_[i]);
		System.err.println();   
	}

	void SetCustomLow (int[] x, int[] y, int n, int sx, int sy){
		if(nlcust_ > 0){
			lcustx_ = null;
			lcusty_ = null;
		}
		//nlcust_ = 0;
		//lcustx_ = lcusty_ = 0;   
		nlcust_ = n + 2;
		lcustx_ = new float[nlcust_];
		lcusty_ = new float[nlcust_];

		int idx = 0;
		lcustx_[idx] = 0;
		lcusty_[idx++] = 0;
		for(int i = 0; i < n; i++){
			lcustx_[idx] = ((float) x[i]) / sx;
			lcusty_[idx++] = (float)(1.0 - ((float) y[i]) / sy);
		}
		lcustx_[idx] = 0;
		lcusty_[idx++] = 0;
		System.err.println(" hyst low custom x: ");
		for(int i = 0; i <= n; i++)
			System.err.println(lcustx_[i]);
		System.err.println();
		System.err.println(" low custom y: ");
		for(int i = 0; i <= n; i++)
			System.err.println(lcusty_[i]);
		System.err.println();   
	}

	void SetCustomLow (double[] x, double[] y, int n){
		if(nlcust_ > 0){
			lcustx_ = null;
			lcusty_ = null;
		}
		//nlcust_ = 0;
		//lcustx_ = lcusty_ = 0;   
		nlcust_ = n + 2;
		lcustx_ = new float[nlcust_];
		lcusty_ = new float[nlcust_];

		int idx = 0;
		lcustx_[idx] = 0;
		lcusty_[idx++] = 0;
		for(int i = 0; i < n; i++){
			lcustx_[idx] = (float) x[i];
			lcusty_[idx++] = (float) y[i];
		}
		lcustx_[idx] = 0;
		lcusty_[idx++] = 0;
		System.err.println(" hyst low custom x: ");
		for(int i = 0; i <= n; i++)
			System.err.println(lcustx_[i]);
		System.err.println();
		System.err.println(" low custom y: ");
		for(int i = 0; i <= n; i++)
			System.err.println(lcusty_[i]);
		System.err.println();   
	}


}
