package util;

import java.util.Arrays;
import java.util.function.IntToDoubleFunction;
import java.util.stream.DoubleStream;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
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
    public Vector(Vector vectors[]) {
        this.tab = Arrays.stream(vectors).flatMap(v -> v.stream().boxed()).mapToDouble(Double::new).toArray();
        this.n = tab.length;
    }

    /**
     * Build x from an array of double.
     */
    public Vector(double[] x) {
        this.n = x.length;
        this.tab = x.clone();
    }

    public static Vector makeVector(int n, IntToDoubleFunction valueBuilder) {
        return new Vector(range(0, n).mapToDouble(valueBuilder).toArray());
    }

    public static void main(String[] args) {
        System.out.println(new Vector(
            new Vector[]{
                makeVector(2, i -> i),
                makeVector(2, i -> i + 2),
                makeVector(2, i -> i + 4)
            }
        ));

    }

    private DoubleStream doubleRange(int n) {
        return range(0, n).asDoubleStream();
    }

    /**
     * Get the sub-vector (x[start],...,x[end])
     */
    public Vector subvector(int start, int end) {
        assert (start >= 0);
        assert (end >= start);
        return new Vector(stream(start, end).toArray());
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

    public DoubleStream stream() {
        return Arrays.stream(tab);
    }

    public DoubleStream stream(int start, int end) {
        return Arrays.stream(tab, start, end);
    }

    /**
     * Set x to y.
     */
    public void set(Vector y) {
        tab = y.stream().toArray();
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
        return doubleRange(n).reduce(0, (s, i) -> s + get((int) i) * v.get((int) i));
    }

    /**
     * Return ||x||.
     */
    public double norm() {
        return sqrt(stream().reduce(0, (s, i) -> s + pow(i, 2)));
    }

    /**
     * Return x as a string.
     */
    public String toString() {
        return stream().mapToObj(Double::toString).collect(joining(", ", "(", ")"));
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
 