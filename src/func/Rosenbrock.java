package func;

import util.Vector;

import static java.lang.Math.pow;

public class Rosenbrock implements RealFunc {


    public double eval(Vector v) {
        return pow(1 - v.get(0), 2) + 100 * pow(v.get(1) - pow(v.get(0), 2), 2);
    }


    public Vector grad(Vector v) {
        double x = v.get(0);
        double y = v.get(1);
        return new Vector(new double[]{
            400 * pow(x, 3) + 2 * (1 - 200 * y) * x - 2,
            200 * (y - pow(x, 2))
        });
    }

    @Override
    public int dim() {
        return 2;
    }


}
