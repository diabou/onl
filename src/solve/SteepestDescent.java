package solve;

import line.Dichotomy;
import line.LineSearch;
import func.RealFunc;
import util.Vector;

/**
 * Basic steepest descent algorithm for unconstrained minimization problem.
 *
 * @author Gilles Chabert
 */

public class SteepestDescent extends Algorithm {

    private RealFunc f;
    private LineSearch s;
    private Dichotomy dicho;

    /**
     * Build the algorithm
     *
     * @param f function to minimize
     * @param s underlying line search algorithm
     */
    public SteepestDescent(RealFunc f, LineSearch s) {
        this.f = f;
        this.s = s;
        this.dicho = new Dichotomy(f);
    }


    /**
     * Calculate the next iterate.
     */
    public void compute_next() throws EndOfIteration {
        Vector grad = f.grad(iter_vec);
        iter_vec = iter_vec.sub(grad.leftmul(s.search(iter_vec, grad.minus())));
    }

}
