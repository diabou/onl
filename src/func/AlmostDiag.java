package func;

import util.Matrix;

public class AlmostDiag extends QuadraForm {

    private static Matrix almostDiag(int n) {
        return makeMatrix(n, params -> params[1] == params[2] ? 1 : - 1.0 / params[0]);
    }

    public AlmostDiag(int n) {
        super(almostDiag(n));
    }
}
