package func;

import util.Matrix;
import util.Vector;

public class QuadraForm implements RealFunc {
    public Matrix Q;

    public QuadraForm(Matrix Q) {
        this.Q = new Matrix(Q);
    }

    @Override
    public double eval(Vector x) {
        return x.leftmul(0.5).scalar(Q.mult(x));
    }

    @Override
    public Vector grad(Vector x) {
        return Q.mult(x);
    }

    @Override
    public int dim() {
        return Q.nb_cols();
    }

}