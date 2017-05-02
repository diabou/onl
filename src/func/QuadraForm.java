package func;

import util.Matrix;
import util.Vector;

public class QuadraForm implements RealFunc {
    public Matrix Q;
    private Vector b;

    public QuadraForm(Matrix Q) {
        this(Q, new Vector(Q.nb_cols()));
    }

    public QuadraForm(Matrix Q, Vector b) {
        this.Q = Q;
        this.b = b;
    }

    @Override
    public double eval(Vector x) {
        return x.leftmul(0.5).scalar(Q.mult(x)) - b.scalar(x);
    }

    @Override
    public Vector grad(Vector x) {
        return Q.mult(x).sub(b);
    }

    @Override
    public int dim() {
        return Q.nb_cols();
    }
}