package func;

import static util.Matrix.makeMatrix;

public class AlmostDiag extends QuadraForm {

    public AlmostDiag(int n) {
        super(makeMatrix(n, (i, j) -> i == j ? 1 : -1.0 / n));
    }
}