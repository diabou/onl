package solve;

import func.RealFunc;
import line.LineSearch;
import util.Vector;

import static java.lang.Math.pow;

/**
 * Non-linear variant of the conjugate gradients.
 *
 * @author Gilles Chabert
 */
public class ConjugateGradients extends Algorithm {
    private RealFunc f;
    private LineSearch s;
    private Vector iter_dir;

    /**
     * Build the algorithm for a given function and
     * with an underlying line search technique.
     *
     * @param f the function
     * @param s the line search algorithm
     */
    public ConjugateGradients(RealFunc f, LineSearch s) {
        this.f = f;
        this.s = s;
        this.iter_dir = new Vector(f.dim());
    }

    /**
     * Start the iteration
     */
    public void start(Vector x0) {
        super.start(x0);
        this.iter_dir = f.grad(x0).minus();
    }

    /**
     * Calculate the next iterate.
     * <p>
     * (update iter_vec).
     */
    public void compute_next() throws EndOfIteration {
        Vector gk = f.grad(iter_vec);
        iter_vec = iter_vec.add(iter_dir.leftmul(s.search(iter_vec, iter_dir)));
        Vector gkPlus1 = f.grad(iter_vec);
        iter_dir = gkPlus1.minus().add(iter_dir.leftmul(pow(gkPlus1.norm() / gk.norm(), 2)));
    }
}
