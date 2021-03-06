package PSINS;

import static PSINS.PSINS.MMD2;

public class CMat {
    public int row, clm, rc;
    double dd[] = new double[MMD2];

    @Override
    public CMat clone() {
        CMat ret = new CMat(row, clm, dd);
        return ret;
    }

    CMat() {
    }

    CMat(int row0, int clm0) {
        row = row0;
        clm = clm0;
        rc = row * clm;
    }

    CMat(int row0, int clm0, double f) {
        row = row0;
        clm = clm0;
        rc = row * clm;
        for (int pd = 0; pd < rc; pd++) dd[pd] = f;
    }

    CMat(int row0, int clm0, double pf[]) {
        row = row0;
        clm = clm0;
        rc = row * clm;
//            memcpy(dd, pf, rc * sizeof(double));
        System.arraycopy(pf, 0, dd, 0, rc);
    }

    void SetDiag(double... f) {
//        va_list vl;
//        va_start(vl, f);
//        double *p = dd, *pEnd = &dd[rc];
//        for (int row1 = row + 1; p < pEnd; p += row1) {
//        *p = f;
//            f = va_arg(vl, double);
//        }
//        va_end(vl);
        for (int i = 0; i * (row + 1) < rc; i++) {
            dd[i * (row + 1)] = f[i];
        }
    }

    public void SetDiag2(double... f) {
//        va_list vl;
//        va_start(vl, f);
//        double *p = dd, *pEnd = &dd[rc];
//        for (int row1 = row + 1; p < pEnd; p += row1) {
//        *p = f * f;
//            f = va_arg(vl, double);
//        }
//        va_end(vl);
        for (int i = 0; i * (row + 1) < rc; i++) {
            dd[i * (row + 1)] = f[i] * f[i];
        }
    }

    CMat add(final CMat m0) {
        assert (row == m0.row && clm == m0.clm);
        CMat mtmp = new CMat(row, clm);
//            double *p = mtmp.dd, *pEnd = &mtmp.dd[rc];
//            final double *p1 = this.dd, *p2 = m0.dd;
//            while (p < pEnd) { *p++ = (*p1++) + (*p2++); }
        for (int i = 0; i < rc; i++) {
            mtmp.dd[i] = dd[i] + m0.dd[i];
        }
        return mtmp;
    }                // matirx addition

    CMat sub(final CMat m0) {
        assert (row == m0.row && clm == m0.clm);
        CMat mtmp = new CMat(row, clm);
//            double *p = mtmp.dd, *pEnd = &mtmp.dd[rc];
//            final double *p1 = this.dd, *p2 = m0.dd;
//            while (p < pEnd) { *p++ = (*p1++) - (*p2++); }
        for (int i = 0; i < rc; i++) {
            mtmp.dd[i] = dd[i] - m0.dd[i];
        }
        return mtmp;
    }                // matirx subtraction

    CMat multi(double f) {
        CMat mtmp = new CMat(row, clm);
//            double *p = mtmp.dd, *pEnd = &mtmp.dd[rc];
//            final double *p1 = this.dd;
//            while (p < pEnd) { *p++ = (*p1++) * f; }
        for (int i = 0; i < rc; i++) {
            mtmp.dd[i] = dd[i] * f;
        }
        return mtmp;
    }                        // matirx multiply scale

    CVect multi(final CVect v) {
        assert (this.clm == v.row);
        CVect vtmp = new CVect(this.row);
//            double *p = vtmp.dd, *pEnd = &vtmp.dd[vtmp.row];
//            final double *p1ij = this.dd, *p2End = &v.dd[v.row];
//            for (; p < pEnd; p++) {
//                double f = 0.0;
//                final double *p2j = v.dd;
//                for (; p2j < p2End; p1ij++, p2j++) f += (*p1ij) * (*p2j);
//                *p = f;
//            }
        for (int i = 0; i < vtmp.row; i++) {
            double f = 0;
            for (int j = 0; j < v.row; j++) {
                f += dd[j] * v.dd[j];
            }
            vtmp.dd[i] = f;
        }
        return vtmp;
    }                // matirx multiply vector

    CMat multi(final CMat m0) {
        assert (this.clm == m0.row);
        CMat mtmp = new CMat(this.row, m0.clm);
        int m = this.row, k = this.clm, n = m0.clm;
//            double *p = mtmp.dd;
//            final double *p1i = this.dd, *p2 = m0.dd;
//            for (int i = 0; i < m; i++, p1i += k) {
//                for (int j = 0; j < n; j++) {
//                double f = 0.0;
//                final double *p1is = p1i, *p1isEnd = &p1i[k], *p2sj = &p2[j];
//                for (; p1is < p1isEnd; p1is++, p2sj += n)
//                    f += (*p1is) * (*p2sj);
//                *p++ = f;
//                }
//            }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                double f = 0;
                for (int ii = 0; ii < k; ii++) {
                    f += dd[ii] * m0.dd[j + n * ii];
                }
                mtmp.dd[i * m + j] = f;
            }
        }
        return mtmp;
    }                // matirx multiplication

//        CMat &operator+=(final CMat &m0);                    // matirx addition

    CMat selfadd(final CVect v) {//        operator+=
        assert (row == v.row || clm == v.clm);
        int row1 = row + 1;
//        double *p = dd, *pEnd = &dd[rc];
//        for (const double *p1 = v.dd;p<pEnd ; p += row1, p1++)
//            *p += *p1;
        for (int p = 0, p1 = 0; p < rc; p += row1, p1++) {
            dd[p] += v.dd[p1];
        }
        return this;
    }                    // matirx + diag(vector)

//        CMat &operator-=(final CMat &m0);                    // matirx subtraction
//        CMat &operator*=(double f);                            // matirx multiply scale

    CMat selfadd1() {
        int row1 = row + 1;
//        for (double *p = dd, *pEnd = &dd[rc]; p < pEnd; p += row1)
//            *p += 1.0;
        for (int i = 0; i < rc; i += row1) {
            dd[i] += 1.0;
        }
        return this;
    }                                    // 1.0 + diagonal

    double getElement(int r, int c) {
        return dd[r * clm + c];
    }                    // get element m(r,c)

    void setElement(int r, int c, double n) {
        dd[r * clm + c] = n;
    }                    // set element m(r,c)

    void SetRow(int i, final CVect v) {
        assert (clm == v.clm);
//            final double *p = v.dd;
//            for (double *p1 = &dd[i * clm], *pEnd = p1 + clm; p1 < pEnd; p++, p1++)
//                *p1 = *p;
        for (int j = i * clm; j < i * clm + clm; j++) {
            dd[j] = v.dd[j];
        }
    }                    // set i-row from vector

    void SetClm(int j, final CVect v) {
        assert (row == v.row);
//            final double *p = v.dd;
//            for (double *p1 = &dd[j], *pEnd = &dd[rc]; p1 < pEnd; p++, p1 += clm)
//                *p1 = *p;
        for (int i = 0; i * clm + j < rc; i++) {
            dd[j + i * clm] = v.dd[i];
        }
    }                    // set j-column from vector

    CVect GetRow(int i) {
        CVect v = new CVect();
        v.row = 1;
        v.clm = clm;
//            final double *p1 = &dd[i * clm], *pEnd = p1 + clm;
//            for (double *p = v.dd; p1 < pEnd; p++, p1++) *p = *p1;
        for (int j = 0; j < clm; j++) {
            v.dd[j] = dd[i * clm + j];
        }
        return v;
    }                          // get i-row from matrix

    CVect GetClm(int j) {
        CVect v = new CVect();
        v.row = row;
        v.clm = 1;
//            final double *p1 = &dd[j], *pEnd = &dd[rc];
//            for (double *p = v.dd; p1 < pEnd; p++, p1 += clm) *p = *p1;
        for (int i = 0; i < row; i++) {
            v.dd[i] = dd[j + i * clm];
        }
        return v;
    }                            // get j-column from matrix

    void SetClmVect3(int i, int j, final CVect3 v) {
//            double *p = &dd[i * clm + j];
//            *p = v.i;
//            p += clm;
//            *p = v.j;
//            p += clm;
//            *p = v.k;
        dd[i * clm + j] = v.i;
        dd[++i * clm + j] = v.j;
        dd[++i * clm + j] = v.k;
    }    // set i...(i+2)-row&j-column from CVect3

    void SetRowVect3(int i, int j, final CVect3 v) {
        //*(CVect3 *) &dd[i * clm + j] = v;
        dd[i * clm + j] = v.i;
        dd[i * clm + j + 1] = v.j;
        dd[i * clm + j + 2] = v.j;
    }    // set i-row&j...(j+2)-column from CVect3

    void SetMat3(int i, int j, final CMat3 m) {
//            double *p = &dd[i * clm + j];
//            *(CVect3 *) p = *(CVect3 *) &m.e00;
//            p += clm;
//            *(CVect3 *) p = *(CVect3 *) &m.e10;
//            p += clm;
//            *(CVect3 *) p = *(CVect3 *) &m.e20;
        dd[i * clm + j] = m.e00;
        dd[i * clm + j + 1] = m.e01;
        dd[i * clm + j + 2] = m.e02;
        dd[++i * clm + j] = m.e10;
        dd[i * clm + j + 1] = m.e11;
        dd[i * clm + j + 2] = m.e12;
        dd[++i * clm + j] = m.e20;
        dd[i * clm + j + 1] = m.e21;
        dd[i * clm + j + 2] = m.e22;
    }            // set i...(i+2)-row&j...(j+2)-comumn from CMat3

    void ZeroRow(int i) {
//            for (double *p = &dd[i * clm], *pEnd = p + clm; p < pEnd; p++) *p = 0.0;
        for (int j = i * clm; j < i * clm + clm; j++) {
            dd[j] = 0;
        }
    }                                // set i-row to 0

    void ZeroClm(int j) {
//            for (double *p = &dd[j], *pEnd = &dd[rc]; p < pEnd; p += clm) *p = 0.0;
        for (int i = j; i < rc; i += clm) {
            dd[i] = 0;
        }
    }                                // set j-column to 0

    //        static CMat array2mat(final double *f, int r, int c);    // convert array to mat

    CMat trans() {
        CMat mtmp = new CMat(clm, row);
        int pos = 0;
        for (int i = 0; i < row; i++) {
            for (int j = i; j < rc; j += row)
                mtmp.dd[j] = dd[pos++];
        }
        return mtmp;
    }                // matirx transposition

    static void symmetry(CMat m) {
        assert (m.row == m.clm);
//            for (int i = 0; i < m.clm; i++) {
//                double *prow = &m.dd[i * m.clm + i + 1], *prowEnd = &m.dd[i * m.clm + m.clm], *pclm = &m.dd[i * m.clm + i +
//                        m.clm];
//                for (; prow < prowEnd; prow++, pclm += m.clm) *prow = *pclm = (*prow + *pclm) * 0.5;
//            }
        for (int i = 0; i < m.clm; i++) {
            for (int prow = i * m.clm + i + 1, prowEnd = i * m.clm + m.clm, pclm = i * m.clm + i + m.clm; prow < prowEnd; prow++, pclm += m.clm) {
                m.dd[prow] = m.dd[pclm] = (m.dd[prow] + m.dd[pclm]) * 0.5;
            }
        }
    }                        // matirx symmetrization

    //        static double norm1(CMat &m);                        // 1-norm
//        static CVect diag(final CMat &m);                    // diagonal of a matrix
//        static CMat diag(final CVect &v);                    // diagonal matrix
    static void RowMul(CMat m, final CMat m0, final CMat m1, int r) {
        assert (m0.clm == m1.row);
        int rc0 = r * m0.clm;
//        double *p = &m.dd[rc0], *pEnd = p + m0.clm;
//        const double *p0 = &m0.dd[rc0], *p0End = p0 + m0.clm, *p1j = m1.dd;
//        for (; p < pEnd; p++) {
//            double f = 0.0;
//            const double *p0j = p0, *p1jk = p1j++;
//            for (; p0j < p0End; p0j++, p1jk += m1.clm) f += ( * p0j) *( * p1jk);
//            *p = f;
//        }
        int p = rc0, pEnd = p + m0.clm;
        int p0 = rc0, p0End = p0 + m0.clm, p1j = 0;
        for (; p < pEnd; p++) {
            double f = 0.0;
            int p0j = p0, p1jk = p1j++;
            for (; p0j < p0End; p0j++, p1jk += m1.clm) f += m0.dd[p0j] * m1.dd[p1jk];
            m.dd[p] = f;
        }
    } // m(r,:)=m0(r,:)*m1

    static void RowMulT(CMat m, final CMat m0, final CMat m1, int r) {
        assert (m0.clm == m1.clm);
        int rc0 = r * m0.clm;
//        double *p = &m.dd[rc0], *pEnd = p + m0.clm;
//        const double *p0 = &m0.dd[rc0], *p0End = p0 + m0.clm, *p1jk = m1.dd;
//        for (; p < pEnd; p++) {
//            double f = 0.0;
//            const double *p0j = p0;
//            for (; p0j < p0End; p0j++, p1jk++) f += (*p0j) * (*p1jk);
//        *p = f;
//        }
        int p = rc0, pEnd = p + m0.clm;
        int p0 = rc0, p0End = p0 + m0.clm, p1jk = 0;
        for (; p < pEnd; p++) {
            double f = 0.0;
            int p0j = p0;
            for (; p0j < p0End; p0j++, p1jk++) f += m0.dd[p0j] * m1.dd[p1jk];
            m.dd[p] = f;
        }
    } // m(r,:)=m0(r,:)*m1'
//        #ifdef MAT_COUNT_STATISTIC
//                static int iCount, iMax;
//            ~CMat(void);
//        #endif
}