package func;

import util.Matrix;
import util.Vector;

import java.util.function.Function;

import static java.util.stream.IntStream.range;

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

    /**
     * @param n Matrix size.
     * @param valueBuilder a function (n, i, j) -> value of (i, j) coefficient, with n = matrix size, i = row j = column.
     * @return a Matrix whose values are the output of valueBuilder.
     */
    protected static Matrix makeMatrix(int n, Function<int[], Double> valueBuilder) {
        return new Matrix(range(0, n).mapToObj(i -> makeRow(n, i, valueBuilder)).toArray(double[][]::new));
    }

    private static double[] makeRow(int n, int i, Function<int[], Double> valueBuilder) {
        return range(0, n).mapToDouble(j -> valueBuilder.apply(new int[] {n, i, j})).toArray();
    }

}
