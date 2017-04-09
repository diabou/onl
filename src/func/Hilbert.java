package func;

import static util.Matrix.makeMatrix;

public class Hilbert extends QuadraForm {

    public Hilbert(int n) {
        super(makeMatrix(n, (i, j) -> 1.0 / (i + j + 1)));
    }
}
