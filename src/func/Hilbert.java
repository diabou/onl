package func;

import util.Matrix;


public class Hilbert extends QuadraForm {

    private static Matrix _hilbert(int n) {
        return makeMatrix(n, params -> 1.0 / (params[1] + params[2] + 1));
    }

    public Hilbert(int n) {
        super(_hilbert(n));
    }
}
