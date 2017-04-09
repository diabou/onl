package util;


import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

/**
 * Matrix (noted "M" in comments)
 *
 * @author Gilles Chabert
 */
public class Matrix {

    private int m;         // number of rows
    private int n;         // number of columns
    private Vector[] rows; // vector of rows

    /**
     * Build a m*n matrix filled with zeros.
     */
    public Matrix(int m, int n) {
        this.n = n;
        this.m = m;
        this.rows = range(0, m).mapToObj(i -> new Vector(n)).toArray(Vector[]::new);
    }

    /**
     * Build a matrix from a double array of values.
     * vals[i] represents the ith row.
     */
    public Matrix(double[][] vals) {
        this.m = vals.length;
        this.n = vals[0].length;
        this.rows = stream(vals).map(val -> {
            assert (val.length == n);
            return new Vector(val);
        }).toArray(Vector[]::new);
    }

    /**
     * Copy constructur
     *
     * @param mat - The matrix to be copied
     */
    public Matrix(Matrix mat) {
        this.n = mat.nb_cols();
        this.m = mat.nb_rows();
        //mat.rows.clone() ?
        this.rows = stream(mat.rows).toArray(Vector[]::new);
    }

    public static Matrix makeMatrix(int n, IntToDoubleBinaryOperator valueBuilder) {
        return makeMatrix(n, n, valueBuilder);
    }

    /**
     * @param m            Matrix size.
     * @param valueBuilder a function f such as f(i, j) = value of (i, j) coefficient of Q,
     * @return a Matrix whose values are the output of valueBuilder.
     */
    private static Matrix makeMatrix(int m, int n, IntToDoubleBinaryOperator valueBuilder) {
        return new Matrix(range(0, m).mapToObj(i -> makeRow(n, i, valueBuilder)).toArray(double[][]::new));
    }

    private static double[] makeRow(int n, int i, IntToDoubleBinaryOperator valueBuilder) {
        return range(0, n).mapToDouble(j -> valueBuilder.applyAsDouble(i, j)).toArray();
    }

    /**
     * Build the nxn identity matrix.
     */
    public static Matrix identity(int n) {
        return makeMatrix(n, (i, j) -> i == j ? 1 : 0);
    }

    public static void main(String[] args) {
        Matrix A = new Matrix(new double[][]{{1, 2, 3}, {2, -1, -1}});
        Matrix B = new Matrix(new double[][]{{1, 2}, {1, -1}, {1, 1}});
        Matrix C = A.mult(B);
        System.out.println("A=" + A);
        System.out.println("B=" + B);
        System.out.println("B.getCol(1)=" + B.get_col(1));
        System.out.println("C=" + C);
        try {
            Matrix iC = C.inverse();
            System.out.println("inv(C)*C=" + iC.mult(C));
        } catch (Singularity e) {
            e.printStackTrace();
        }
        System.out.println(A.transpose());

    }

    /**
     * @return the number of rows
     */
    public int nb_rows() {
        return m;
    }

    /**
     * @return the number of columns
     */
    public int nb_cols() {
        return n;
    }

    /**
     * @return M[i, j]
     */
    public double get(int i, int j) {
        return rows[i].get(j);
    }

    /**
     * @return M[r,:]
     */
    public Vector get_row(int r) {
        assert (r >= 0 && r < nb_rows());
        return new Vector(rows[r]);
    }

    /**
     * @return M[:,c]
     */
    public Vector get_col(int c) {
        assert (c >= 0 && c < nb_cols());
        Vector v = new Vector(nb_rows());
        for (int i = 0; i < nb_rows(); i++) v.set(i, get(i, c));
        return v;
    }

    /**
     * M[i,j]:=d
     */
    public void set(int i, int j, double d) {
        rows[i].set(j, d);
    }

    /**
     * M[r,:]:=v
     */
    public void set_row(int r, Vector v) {
        assert (r >= 0 && r < nb_rows());
        assert (nb_cols() == v.size());
        rows[r] = new Vector(v);
    }

    /**
     * M[:,c]:=v
     */
    public void set_col(int c, Vector v) {
        assert (c >= 0 && c < nb_cols());
        assert (nb_rows() == v.size());

        for (int i = 0; i < nb_rows(); i++)
            set(i, c, v.get(i));
    }

    /**
     * @return this * v
     */
    public Vector mult(Vector v) {
        assert (v.size() == n);
        return new Vector(stream(rows).mapToDouble(row -> row.scalar(v)).toArray());
    }

    /**
     * @return this^T
     */
    public Matrix transpose() {
        return makeMatrix(n, m, (i, j) -> get(j, i));
    }

    /**
     * @return this^{-1}
     * @throws Singularity
     */
    public Matrix inverse() throws Singularity {
        Matrix LU = new Matrix(n, n);
        Matrix invA = new Matrix(n, n);
        int[] p = new int[n];

        real_LU(LU, p);

        Vector b = new Vector(n);
        Vector x = new Vector(n);

        for (int i = 0; i < n; i++) {
            b.set(i, 1);
            real_LU_solve(LU, p, b, x);
            invA.set_col(i, x);
            b.set(i, 0);
        }
        return invA;
    }

    /**
     * @return -M
     */
    public Matrix minus() {
        return makeMatrix(m, n, (i, j) -> -1 * get(i, j));
    }

    /**
     * @return this + M
     */
    public Matrix add(Matrix M) {
        assert (M.nb_rows() == m);
        assert (M.nb_cols() == n);
        return makeMatrix(m, n, (i, j) -> get(i, j) + M.get(i, j));
    }

    /**
     * @return this - M
     */
    public Matrix sub(Matrix M) {
        assert (M.nb_rows() == m);
        assert (M.nb_cols() == n);
        return makeMatrix(m, n, (i, j) -> get(i, j) - M.get(i, j));
    }

    /**
     * @return this * M
     */
    public Matrix mult(Matrix M) {
        assert (M.nb_rows() == n);
        return makeMatrix(m, M.nb_cols(), (i, j) -> get_row(i).scalar(M.get_col(j)));
    }

    /**
     * @return M as a string
     */
    public String toString() {
        return stream(rows).map(Vector::toString).collect(joining("\n", "(", ")"));
    }

    /*
     * Build a LU decomposition of M.
     *
     * @param LU - a n*n matrix (to be filled)
     * @param p  - a n-sized permutation vector (to be filled)
     * @throws SingularMatrixException if M is not invertible
     */
    private void real_LU(Matrix LU, int p[]) throws Singularity {

        double TOO_LARGE = 1e30;

        assert (m == n);
        assert (n == (LU.nb_rows()) && n == (LU.nb_cols()));
        assert (n == p.length);

        // check the matrix has no "infinite" values
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (Math.abs(get(i, j)) >= TOO_LARGE) throw new Singularity();
                LU.set(i, j, get(i, j));
            }
        }

        for (int i = 0; i < n; i++) p[i] = i;

        // LU computation
        double pivot;

        for (int i = 0; i < n; i++) {
            // pivot search
            int swap = i;
            pivot = LU.get(p[i], i);
            for (int j = i + 1; j < n; j++) {
                double tmp = LU.get(p[j], i);
                if (Math.abs(tmp) > Math.abs(LU.get(p[swap], i))) swap = j;
            }
            int tmp = p[i];
            p[i] = p[swap];
            p[swap] = tmp;
            // --------------------------------------------------

            pivot = LU.get(p[i], i);
            if (pivot == 0.0) throw new Singularity();
            if (Math.abs(1 / pivot) >= TOO_LARGE) throw new Singularity();

            for (int j = i + 1; j < n; j++) {
                for (int k = i + 1; k < n; k++) {
                    //cout << LU.get(p[j]][k] << " " << LU.get(p[j]][i] << " " << pivot << endl;
                    LU.set(p[j], k, LU.get(p[j], k) - LU.get(p[j], i) / pivot * LU.get(p[i], k));
                }
                LU.set(p[j], i, LU.get(p[j], i) / pivot);
            }
        }
    }

    /*
     * Solve LU x = b with permutation matrix P.
     * @throws SingularMatrixException
     */
    private void real_LU_solve(Matrix LU, int[] p, Vector b, Vector x) throws Singularity {
        //cout << "LU=" << LU << " b=" << b << endl;

        double TOO_SMALL = 1e-10;

        int n = (LU.nb_rows());
        assert (n == (LU.nb_cols())); // throw NotSquareMatrixException();
        assert (n == (b.size()) && n == (x.size()));

        // solve Lx=b
        x.set(0, b.get(p[0]));
        for (int i = 1; i < n; i++) {
            x.set(i, b.get(p[i]));
            for (int j = 0; j < i; j++) {
                x.set(i, x.get(i) - LU.get(p[i], j) * x.get(j));
            }
        }
        //cout << " x1=" << x << endl;

        // solve Uy=x
        if (Math.abs(LU.get(p[n - 1], n - 1)) <= TOO_SMALL) throw new Singularity();
        x.set(n - 1, x.get(n - 1) / LU.get(p[n - 1], n - 1));

        for (int i = n - 2; i >= 0; i--) {
            for (int j = i + 1; j < n; j++) {
                x.set(i, x.get(i) - LU.get(p[i], j) * x.get(j));
            }
            if (Math.abs(LU.get(p[i], i)) <= TOO_SMALL) throw new Singularity();
            x.set(i, x.get(i) / LU.get(p[i], i));
        }
        //cout << " x2=" << x << endl;
    }
}