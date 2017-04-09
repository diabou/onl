package util;

import java.util.Arrays;
import java.util.function.IntToDoubleFunction;
import java.util.stream.DoubleStream;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

/**
 * Class representing a vector x of R^n
 *
 * @author Gilles Chabert
 */
public class Vector {
    private int n;
    private double[] tab;

    /**
     * Build x as an uninitialized vector of R^n.
     */
    public Vector(int n) {
        this.n = n;
        this.tab = range(0, n).mapToDouble(i -> 0).toArray();
    }

    /**
     * Build x as a copy of v.
     */
    public Vector(Vector v) {
        this(new Vector[]{v});
    }

    /**
     * Create a vector by concatenation of two vectors
     */
    public Vector(Vector v1, Vector v2) {
        this(new Vector[]{v1, v2});
    }

    /**
     * Create a vector by concatenation of three vectors
     */
    public Vector(Vector v1, Vector v2, Vector v3) {
        this(new Vector[]{v1, v2, v3});
    }

    /**
     * Create a vector by concatenation of several vectors
     */
    public Vector(Vector v[]) {
        // calculate total size
        int n = stream(v).mapToInt(Vector::size).reduce(0, (x, y) -> x + y);
        this.n = n;
        this.tab = new double[n];
        // fill the vector
        int k = 0; // index in this vector
        for (int i = 0; i < v.length; i++) {
            for (int j = 0; j < v[i].size(); j++) {
                tab[k++] = v[i].get(j);
            }
        }
    }

    /**
     * Build x from an array of double.
     */
    public Vector(double[] x) {
        this.n = x.length;
        this.tab = x.clone();
    }

    private Vector makeVector(int n, IntToDoubleFunction valueBuilder) {
        return new Vector(range(0, n).mapToDouble(valueBuilder).toArray());
    }


    private DoubleStream doubleStream(int n) {
        return range(0, n).asDoubleStream();
    }


    /**
     * Get the sub-vector (x[start],...,x[end])
     */
    public Vector subvector(int start, int end) {
        assert (start >= 0);
        assert (end >= start);
        return new Vector(stream(tab, start, end).toArray());
    }

    /**
     * Return the size (n) of x.
     */
    public int size() {
        return n;
    }

    /**
     * Return x[i].
     */
    public double get(int i) {
        return tab[i];
    }

    /**
     * Set x[i].
     */
    public void set(int i, double d) {
        tab[i] = d;
    }

    /**
     * Set x to y.
     */
    public void set(Vector y) {
        for (int i = 0; i < n; i++)
            set(i, y.get(i));
    }

    /**
     * Return -x.
     */
    public Vector minus() {
        return makeVector(n, i -> -1 * get(i));
    }

    /**
     * Return this + v.
     */
    public Vector add(Vector v) {
        assert (v.size() == n);
        return makeVector(n, i -> get(i) + v.get(i));
    }

    /**
     * Return x-v.
     */
    public Vector sub(Vector v) {
        return add(v.minus());
    }

    /**
     * Return lambda*x.
     */
    public Vector leftmul(double lambda) {
        return makeVector(n, i -> lambda * get(i));
    }

    /**
     * Return <x,v>.
     */
    public double scalar(Vector v) {
        assert (n == v.size());
        return doubleStream(n).reduce(0, (s, i) -> s + get((int) i) * v.get((int) i));
    }

    /**
     * Return ||x||.
     */
    public double norm() {
        return sqrt(stream(tab).reduce(0, (s, i) -> s + pow(i, 2)));
    }

    /**
     * Return x as a string.
     */
    public String toString() {
        return stream(tab).mapToObj(Double::toString).collect(joining(", ", "(", ")"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector vector = (Vector) o;
        return Arrays.equals(tab, vector.tab);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(tab);
    }
}
 