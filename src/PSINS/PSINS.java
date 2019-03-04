package PSINS;

public final class PSINS {
    private static final double PI = 3.141592653589793238;
    private static final double PI_2 = PI/2.0;
    private static final double PI_4 = PI/4.0;
    private static final double EPS = 2.220446049e-16F;
    private static final double INF = 3.402823466e+30F;
    private static final double _2PI = PI*2;

    // determine the sign of 'val' with the sensitivity of 'eps'
    public static int sign(double val, double eps) {
        int s;

        if (val < -eps) {
            s = -1;
        } else if (val > eps) {
            s = 1;
        } else {
            s = 0;
        }
        return s;
    }
    public static int sign(double val){
        return sign(val,EPS);
    }

    // set double value 'val' between range 'minVal' and 'maxVal'
    public static double range(double val, double minVal, double maxVal) {
        double res;

        if (val < minVal) {
            res = minVal;
        } else if (val > maxVal) {
            res = maxVal;
        } else {
            res = val;
        }
        return res;
    }

    public static double atan2Ex(double y, double x) {
        double res;

        if ((sign(y) == 0) && (sign(x) == 0)) {
            res = 0.0;
        } else {
            res = Math.atan2(y, x);
        }
        return res;
    }

    public static double diffYaw(double yaw, double yaw0) {
        double dyaw = yaw - yaw0;
        if (dyaw >= PI) dyaw -= _2PI;
        else if (dyaw <= -PI) dyaw += _2PI;
        return dyaw;
    }
    public static double asinEx(double x){
        return Math.asin(range(x, -1.0, 1.0));
    }
    public static double acosEx(double x){
        return Math.acos(range(x, -1.0, 1.0));
    }
    public static double CC180toC360(double yaw){
        return ( (yaw)>0.0 ? (_2PI-(yaw)) : -(yaw) );   // counter-clockwise +-180deg -> clockwise 0~360deg for yaw
    }
    public static double C360toCC180(double yaw){
        return ( (yaw)>=PI ? (_2PI-(yaw)) : -(yaw) );   // clockwise 0~360deg -> counter-clockwise +-180deg for yaw
    }


    // Max Matrix Dimension define
    public static int MMD =15;
    public static int MMD2 =MMD*MMD;

    // global variables and functions, can not be changed in any way
    
    final CVect3 I31=new CVect3(1, 1, 1), O31=new CVect3(0, 0, 0);
    final CQuat qI=new CQuat(1.0, 0, 0, 0);
    final CMat3 I33=new CMat3(1, 0, 0, 0, 1, 0, 0, 0, 1), O33=new CMat3(0, 0, 0, 0, 0, 0, 0, 0, 0);
    final CVect On1=new CVect(MMD, 0.0);
    final CGLV glv=new CGLV();

    public class CGLV {
        public double Re, f, g0, wie;                                            // the Earth's parameters
        public double e, e2;
        public double mg, ug, deg, min, sec, hur, ppm, ppmpsh;                    // commonly used units
        public double dps, dph, dpsh, dphpsh, ugpsh, ugpsHz, mpsh, mpspsh, secpsh;

        private double sqrt(double d){
            return Math.sqrt(d);
        }

        CGLV(){
            this(6378137.0);
        }

        CGLV(double Re){
            this(Re,(1.0 / 298.257));
        }

        CGLV(double Re , double f){
            this(Re,f,7.2921151467e-5);
        }

        CGLV(double Re , double f , double wie0){
            this(Re,f,wie0,9.7803267714);
        }

        CGLV(double Re , double f , double wie0, double g0) {
            this.Re = Re;
            this.f = f;
            this.wie = wie0;
            this.g0 = g0;
            e = sqrt(2 * f - f * f);
            e2 = e * e;
            mg = 1.0e-3 * g0;
            ug = 1.0e-6 * glv.g0;
            deg = PI / 180.0;
            min = deg / 60.0;
            sec = min / 60.0;
            ppm = 1.0e-6;
            hur = 3600.0;
            dps = deg / 1.0;
            dph = deg / hur;
            dpsh = deg / sqrt(hur);
            dphpsh = dph / sqrt(hur);
            ugpsHz = ug / sqrt(1.0);
            ugpsh = ug / sqrt(hur);
            mpsh = 1 / sqrt(hur);
            mpspsh = 1 / 1 / sqrt(hur);
            ppmpsh = ppm / sqrt(hur);
            secpsh = sec / sqrt(hur);
        }
    }

    public class CVect3 {
        public double i, j, k;

        public CVect3(double xx) {
            this(xx,0.0);
        }
        public CVect3(double xx,double yy) {
            this(xx,yy,0.0);
        }
        public CVect3(double xx, double yy, double zz){
            i=xx;
            j=yy;
            k=zz;
        }

        public CVect3(Double pdata){
            i = pdata++;
            j = pdata++;
            k = pdata++;
        }

        CVect3 add(final CVect3 v){
            return new CVect3(this.i + v.i, this.j + v.j, this.k + v.k);
        }                 // vector addition
        CVect3 sub(final CVect3 v){
            return new CVect3(this.i - v.i, this.j - v.j, this.k - v.k);
        }                 // vector subtraction
        CVect3 multi(final CVect3 v) {
            return new CVect3(this.j * v.k - this.k * v.j, this.k * v.i - this.i * v.k, this.i * v.j - this.j * v.i);
        };                // vector cross multiplication
        CVect3 multi(double f) {
            return new CVect3(i * f, j * f, k * f);
        }                 // vector multiply scale
        CVect3 div(double f) {
            return new CVect3(i * f, j * f, k * f);
        }                 // vector divide scale
//        CVect3 operator+=(final CVect3 v);                    // vector addition
//        CVect3 operator-=(final CVect3 v);                    // vector subtraction
//        CVect3 operator*=(double f);                            // vector multiply scale
//        CVect3 operator/=(double f);                            // vector divide scale
        boolean IsZero() {
            return IsZero(EPS);
        }
        boolean IsZero(double eps) {
            return (i < eps && i > -eps && j < eps && j > -eps && k < eps && k > -eps);
        }                   // assert if all elements are zeros
        boolean IsZeroXY(){
            return IsZeroXY(EPS);
        }
        boolean IsZeroXY(double eps) {
            return (i < eps && i > -eps && j < eps && j > -eps);
        }                  // assert if xy-elements are zeros
        boolean IsNaN() {
            return false; //(_isnan(i) || _isnan(j) || _isnan(k));
        }                   // assert if any element is NaN
//        friend CVect3 operator*(double f, final CVect3 v);        // scale multiply vector
//        friend CVect3 operator-(final CVect3 v);                // minus
//        friend double norm(final CVect3 v);                    // vector norm
//        friend double normXY(final CVect3 v);                    // vector norm or X  Y components
//        friend CVect3 sqrt(final CVect3 v);                    // sqrt
//        friend double dot(final CVect3 v1, final CVect3 v2);    // vector dot multiplication
//        friend CMat3 a2mat(final CVect3 att);                    // Euler angles to DCM
//        friend CQuat a2qua(double pitch, double roll, double yaw);    // Euler angles to quaternion
//        friend CQuat a2qua(final CVect3 att);                    // Euler angles to quaternion
//        friend CQuat rv2q(final CVect3 rv);                    // rotation vector to quaternion
//        friend CMat3 askew(final CVect3 v);                    // askew matrix;
//        friend CMat3 pos2Cen(final CVect3 pos);                // to geographical position matrix
//        friend CVect3 pp2vn(final CVect3 pos1, final CVect3 pos0, double ts, CEarth *pEth);  // position difference to velocity
    }

    public class CQuat {
        public double q0, q1, q2, q3;
        public CQuat(double qq0){
            this(qq0,0.0);
        }
        public CQuat(double qq0,double qq1){
            this(qq0,qq1,0.0);
        }
        public CQuat(double qq0,double qq1,double qq2) {
            this(qq0, qq1, qq2, 0.0);
        }
        public CQuat(double qq0, double qq1, double qq2, double qq3){
            q0 = qq0; q1 = qq1; q2 = qq2; q3 = qq3;
        }

        public CQuat(Double pdata){
            q0=pdata++;
            q1=pdata++;
            q2=pdata++;
            q3=pdata++;
        }

//        CQuat operator+(final CVect3 &phi) final;    // true quaternion add misalign angles
//        CQuat operator-(final CVect3 &phi) final;    // calculated quaternion delete misalign angles
//        CVect3 operator-(CQuat &quat) final;        // get misalign angles from calculated quaternion & true quaternion
//        CQuat operator*(final CQuat &q) final;        // quaternion multiplication
//        CVect3 operator*(final CVect3 &v) final;    // quaternion multiply vector
//        CQuat &operator*=(final CQuat &q);            // quaternion multiplication
//        CQuat &operator-=(final CVect3 &phi);        // calculated quaternion delete misalign angles
//        void normlize(CQuat *q);                    // quaternion norm
//        friend CQuat operator~(final CQuat &q);        // quaternion conjugate
//        friend CVect3 q2att(final CQuat &qnb);        // quaternion to Euler angles
//        friend CMat3 q2mat(final CQuat &qnb);        // quaternion to DCM
//        friend CVect3 q2rv(final CQuat &q);            // quaternion to rotation vector
    }

    class CMat3 {
        public double e00, e01, e02, e10, e11, e12, e20, e21, e22;

        CMat3(){
        }

        CMat3(double xx, double xy, double xz,
              double yx, double yy, double yz,
              double zx, double zy, double zz){
            e00 = xx; e01 = xy; e02 = xz;
            e10 = yx; e11 = yy; e12 = yz;
            e20 = zx; e21 = zy; e22 = zz;
        }

        CMat3(final CVect3 v0, final CVect3 v1, final CVect3 v2){
            e00 = v0.i; e01 = v0.j; e02 = v0.k;
            e10 = v1.i; e11 = v1.j; e12 = v1.k;
            e20 = v2.i; e21 = v2.j; e22 = v2.k;
        }  // M = [v0; v1; v2]

        CMat3 add(final CMat3 mat) {
            CMat3 mtmp=new CMat3();
            mtmp.e00 = e00 + mat.e00;
            mtmp.e01 = e01 + mat.e01;
            mtmp.e02 = e02 + mat.e02;
            mtmp.e10 = e10 + mat.e10;
            mtmp.e11 = e11 + mat.e11;
            mtmp.e12 = e12 + mat.e12;
            mtmp.e20 = e20 + mat.e20;
            mtmp.e21 = e21 + mat.e21;
            mtmp.e22 = e22 + mat.e22;
            return mtmp;
        }                    // matirx addition

        CMat3 sub(final CMat3 mat) {
            CMat3 mtmp=new CMat3();
            mtmp.e00 = e00 - mat.e00;
            mtmp.e01 = e01 - mat.e01;
            mtmp.e02 = e02 - mat.e02;
            mtmp.e10 = e10 - mat.e10;
            mtmp.e11 = e11 - mat.e11;
            mtmp.e12 = e12 - mat.e12;
            mtmp.e20 = e20 - mat.e20;
            mtmp.e21 = e21 - mat.e21;
            mtmp.e22 = e22 - mat.e22;
            return mtmp;
        }                    // matirx subtraction

        CMat3 multi(final CMat3 mat) {
            CMat3 mtmp=new CMat3();
            mtmp.e00 = e00 * mat.e00 + e01 * mat.e10 + e02 * mat.e20;
            mtmp.e01 = e00 * mat.e01 + e01 * mat.e11 + e02 * mat.e21;
            mtmp.e02 = e00 * mat.e02 + e01 * mat.e12 + e02 * mat.e22;
            mtmp.e10 = e10 * mat.e00 + e11 * mat.e10 + e12 * mat.e20;
            mtmp.e11 = e10 * mat.e01 + e11 * mat.e11 + e12 * mat.e21;
            mtmp.e12 = e10 * mat.e02 + e11 * mat.e12 + e12 * mat.e22;
            mtmp.e20 = e20 * mat.e00 + e21 * mat.e10 + e22 * mat.e20;
            mtmp.e21 = e20 * mat.e01 + e21 * mat.e11 + e22 * mat.e21;
            mtmp.e22 = e20 * mat.e02 + e21 * mat.e12 + e22 * mat.e22;
            return mtmp;
        }                  // matirx multiplication

        CMat3 multi(double f) {
            return new CMat3(e00 * f, e01 * f, e02 * f, e10 * f, e11 * f, e12 * f, e21 * f, e20 * f, e22 * f);
        }                        // matirx multiply scale

        CVect3 multi(final CVect3 v) {
            return new CVect3(e00 * v.i + e01 * v.j + e02 * v.k, e10 * v.i + e11 * v.j + e12 * v.k,
                    e20 * v.i + e21 * v.j + e22 * v.k);
        }                // matirx multiply vector

//        friend CMat3 operator-(const CMat3 &m);                    // minus
//        friend CMat3 operator~(const CMat3 &m);                    // matirx transposition
//        friend CMat3 operator*(double f, const CMat3 &m);        // scale multiply matirx
//        friend double det(const CMat3 &m);                        // matirx determination
//        friend CMat3 inv(const CMat3 &m);                        // matirx inverse
//        friend CVect3 diag(const CMat3 &m);                        // diagonal of a matrix
//        friend CMat3 diag(const CVect3 &v);                        // diagonal matrix
//        friend CMat3 dv2att(CVect3 &va1, const CVect3 &va2, CVect3 &vb1,
//                        const CVect3 &vb2);  // attitude determination using double-vector
//        friend CVect3 m2att(const CMat3 &Cnb);                    // DCM to Euler angles
//        friend CQuat m2qua(const CMat3 &Cnb);                    // DCM to quaternion
    }

    class CVect {
        public int row, clm;
        double dd[]=new double[MMD];

        CVect(){}

        CVect(int row0){
            this(row0,1);
        }

        CVect(int row0, int clm0){
            if (clm0 == 1) {
                row = row0;
                clm = 1;
            }
            else {
                row = 1;
                clm = clm0;
            }
        }

        CVect(int row0, double f){
            row = row0;
            clm = 1;
            for (int i = 0; i < row; i++) dd[i] = f;
        }

        CVect(int row0, final double pf[]){
            row = row0;
            clm = 1;
//            memcpy(dd, pf, row * sizeof(double));
            System.arraycopy(pf, 0, dd, 0, row);
        }

//        CVect(int row0, double f, double f1, ...);

        CVect(final CVect3 v){
            row = 3;
            clm = 1;
            dd[0] = v.i;
            dd[1] = v.j;
            dd[2] = v.k;
        }

        CVect(final CVect3 v1, final CVect3 v2){
            row = 6;
            clm = 1;
            dd[0] = v1.i;
            dd[1] = v1.j;
            dd[2] = v1.k;
            dd[3] = v2.i;
            dd[4] = v2.j;
            dd[5] = v2.k;
        }
//
//        void Set(double f, ...);
//
//        void Set2(double f, ...);
//
//        CVect operator+(const CVect &v) const;        // vector addition
//        CVect operator-(const CVect &v) const;        // vector subtraction
//        CVect operator*(double f) const;            // vector multiply scale
//        CVect &operator+=(const CVect &v);            // vector addition
//        CVect &operator-=(const CVect &v);            // vector subtraction
//        CVect &operator*=(double f);                // vector multiply scale
//        CVect operator*(const CMat &m) const;        // row-vector multiply matrix
//        CMat operator*(const CVect &v) const;        // 1xn vector multiply nx1 vector, or nx1 vector multiply 1xn vector
//        double &operator()(int r);                    // vector element
//        friend CVect operator~(const CVect &v);        // vector transposition
//        friend double norm(const CVect &v);            // vector norm
    }

    class CMat {
        public int row, clm, rc;
        double dd[]=new double[MMD2];

        CMat(){
        }

        CMat(int row0, int clm0){
            row = row0;
            clm = clm0;
            rc = row * clm;
        }

        CMat(int row0, int clm0, double f){
            row = row0;
            clm = clm0;
            rc = row * clm;
            for (int pd = 0; pd < rc; pd++) dd[pd] = f;
        }

        CMat(int row0, int clm0, double pf[]){
            row = row0;
            clm = clm0;
            rc = row * clm;
//            memcpy(dd, pf, rc * sizeof(double));
            System.arraycopy(pf, 0, dd, 0, rc);
        }

//        void SetDiag(double f, ...);
//
//        void SetDiag2(double f, ...);
//
        CMat add(final CMat m0){
            assert(row == m0.row && clm == m0.clm);
            CMat mtmp = new CMat(row, clm);
//            double *p = mtmp.dd, *pEnd = &mtmp.dd[rc];
//            const double *p1 = this->dd, *p2 = m0.dd;
//            while (p < pEnd) { *p++ = (*p1++) + (*p2++); }
            for (int i=0;i<rc;i++){
                mtmp.dd[i]=dd[i]+m0.dd[i];
            }
            return mtmp;
        }                // matirx addition

        CMat sub(final CMat m0){
            assert(row == m0.row && clm == m0.clm);
            CMat mtmp=new CMat(row, clm);
//            double *p = mtmp.dd, *pEnd = &mtmp.dd[rc];
//            const double *p1 = this->dd, *p2 = m0.dd;
//            while (p < pEnd) { *p++ = (*p1++) - (*p2++); }
            for (int i=0;i<rc;i++){
                mtmp.dd[i]=dd[i]-m0.dd[i];
            }
            return mtmp;
        }                // matirx subtraction

        CMat multi(double f) {
            CMat mtmp=new CMat(row, clm);
//            double *p = mtmp.dd, *pEnd = &mtmp.dd[rc];
//            const double *p1 = this->dd;
//            while (p < pEnd) { *p++ = (*p1++) * f; }
            for (int i=0;i<rc;i++){
                mtmp.dd[i]=dd[i]*f;
            }
            return mtmp;
        }                        // matirx multiply scale

        CVect multi(final CVect v) {
            assert(this.clm == v.row);
            CVect vtmp=new CVect(this.row);
//            double *p = vtmp.dd, *pEnd = &vtmp.dd[vtmp.row];
//            const double *p1ij = this->dd, *p2End = &v.dd[v.row];
//            for (; p < pEnd; p++) {
//                double f = 0.0;
//                const double *p2j = v.dd;
//                for (; p2j < p2End; p1ij++, p2j++) f += (*p1ij) * (*p2j);
//                *p = f;
//            }
            for (int i=0;i<vtmp.row;i++){
                double f=0;
                for (int j=0;j<v.row;j++){
                    f+=dd[j]*v.dd[j];
                }
                vtmp.dd[i]=f;
            }
            return vtmp;
        }                // matirx multiply vector

//        CMat operator*(const CMat &m) const;                // matirx multiplication
//        CMat &operator+=(const CMat &m0);                    // matirx addition
//        CMat &operator+=(const CVect &v);                    // matirx + diag(vector)
//        CMat &operator-=(const CMat &m0);                    // matirx subtraction
//        CMat &operator*=(double f);                            // matirx multiply scale
//        CMat &operator++();                                    // 1.0 + diagonal
//        double &operator()(int r, int c);                    // get element m(r,c)
//        void SetRow(int i, const CVect &v);                    // set i-row from vector
//        void SetClm(int j, const CVect &v);                    // set j-column from vector
//        CVect GetRow(int i) const;                            // get i-row from matrix
//        CVect GetClm(int j) const;                            // get j-column from matrix
//        void SetClmVect3(int i, int j, const CVect3 &v);    // set i...(i+2)-row&j-column from CVect3
//        void SetRowVect3(int i, int j, const CVect3 &v);    // set i-row&j...(j+2)-column from CVect3
//        void SetMat3(int i, int j, const CMat3 &m);            // set i...(i+2)-row&j...(j+2)-comumn from CMat3
//        void ZeroRow(int i);                                // set i-row to 0
//        void ZeroClm(int j);                                // set j-column to 0
//        friend CMat array2mat(const double *f, int r, int c);    // convert array to mat
//        friend CMat operator~(const CMat &m);                // matirx transposition
//        friend void symmetry(CMat &m);                        // matirx symmetrization
//        friend double norm1(CMat &m);                        // 1-norm
//        friend CVect diag(const CMat &m);                    // diagonal of a matrix
//        friend CMat diag(const CVect &v);                    // diagonal matrix
//        friend void RowMul(CMat &m, const CMat &m0, const CMat &m1, int r); // m(r,:)=m0(r,:)*m1
//        friend void RowMulT(CMat &m, const CMat &m0, const CMat &m1, int r); // m(r,:)=m0(r,:)*m1'
//        #ifdef MAT_COUNT_STATISTIC
//                static int iCount, iMax;
//            ~CMat(void);
//        #endif
    }



}
