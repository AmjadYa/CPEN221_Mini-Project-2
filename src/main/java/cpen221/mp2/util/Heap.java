package cpen221.mp2.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * An instance is a min-heap or a max-heap of distinct values of type E
 * with priorities of type double.
 */
public class Heap<E> {

    /**
     * Class Invariant:
     * 1. d[0..size-1] represents a complete binary tree. d[0] is the root;
     * For each k, d[2k+1] and d[2k+2] are the left and right children of d[k].
     * If k != 0, d[(k-1)/2] (using integer division) is the parent of d[k].
     * <p>
     * 2. For k in 0..size-1, d[k] contains the value and its priority.
     * <p>
     * 3. The values in d[0..size-1] are all different.
     * <p>
     * 4. For k in 1..size-1,
     * if isMinHeap, (d[k]'s priority) >= (d[k]'s parent's priority),
     * otherwise,    (d[k]'s priority) <= (d[k]'s parent's priority).
     * <p>
     * map and the tree are in sync, meaning:
     * <p>
     * 5. The keys of map are the values in d[0..size-1].
     * This implies that this.size = map.size().
     * <p>
     * 6. if value v is in d[k], then map.get(v) returns k.
     */
    protected final boolean isMinHeap;
    protected VP[] d;
    protected int size;
    protected HashMap<E, Integer> map;

    /**
     * Constructor: an empty heap with capacity 10.
     * It is a min-heap if isMin is true, a max-heap if isMin is false.
     */
    public Heap(boolean isMin) {
        isMinHeap = isMin;
        d = createVPArray(10);
        map = new HashMap<E, Integer>();
    }

    /**
     * Add v with priority p to the heap.
     * Throw an illegalArgumentException if v is already in the heap.
     * The expected time is logarithmic and the worst-case time is linear
     * in the size of the heap.
     */
    public void add(E v, double p) throws IllegalArgumentException {
        // Do NOT call bubbleUp until the class invariant is true except
        // for the need to bubble up.
        // Calling bubbleUp is the last thing to be done.
        if (map.containsKey(v)) {
            throw new IllegalArgumentException("v is already in the heap");
        }
        ensureSpace();
        map.put(v, size);
        d[size] = new VP(v, p);
        size = size + 1;
        bubbleUp(size - 1);
    }

    /**
     * If size = length of d, double the length of array d.
     * The worst-case time is proportional to the length of d.
     */
    protected void ensureSpace() {
        if (size == d.length) {
            d = Arrays.copyOf(d, 2 * d.length);
        }
    }

    /**
     * Return the size of this heap.
     * This operation takes constant time.
     */
    public int size() { // Do not change this method
        return size;
    }

    /**
     * Swap d[h] and d[k].
     * Precondition: 0 <= h < heap-size, 0 <= k < heap-size.
     */
    void swap(int h, int k) {
        assert 0 <= h && h < size && 0 <= k && k < size;
        VP temp = d[h];
        d[h] = d[k];
        d[k] = temp;
        map.put(d[h].val, h);
        map.put(d[k].val, k);
    }

    /**
     * If a value with priority p1 should be above a value with priority
     * p2 in the heap, return 1.
     * If priority p1 and priority p2 are the same, return 0.
     * If a value with priority p1 should be below a value with priority
     * p2 in the heap, return -1.
     * This is based on what kind of a heap this is,
     * E.g. a min-heap, the value with the smallest priority is in the root.
     * E.g. a max-heap, the value with the largest priority is in the root.
     */
    public int compareTo(double p1, double p2) {
        if (p1 == p2) {
            return 0;
        }
        if (isMinHeap) {
            return p1 < p2 ? 1 : -1;
        }
        return p1 > p2 ? 1 : -1;
    }

    /**
     * If d[m] should be above d[n] in the heap, return 1.
     * If d[m]'s priority and d[n]'s priority are the same, return 0.
     * If d[m] should be below d[n] in the heap, return -1.
     * This is based on what kind of a heap this is,
     * E.g. a min-heap, the value with the smallest priority is in the root.
     * E.g. a max-heap, the value with the largest priority is in the root.
     */
    public int compareTo(int m, int n) {
        return compareTo(d[m].priority, d[n].priority);
    }

    /**
     * Bubble d[k] up the heap to its right place.
     * Precondition: 0 <= k < size and
     * The class invariant is true, except perhaps
     * that d[k] belongs above its parent (if k > 0)
     * in the heap, not below it.
     */
    void bubbleUp(int k) {
        // to bubble a value up to its proper place, based on its priority.
        // Do not use recursion. Use iteration.
        // Use method compareTo to test whether value k is in its right place.
        assert 0 <= k && k < size;

        // Inv: 0 <= k < size and
        //      The class invariant is true, except perhaps
        //      that d[k] belongs above its parent (if k > 0)
        //      in the heap, not below it.
        while (k > 0) {
            int p = (k - 1) / 2; // p is k's parent
            if (compareTo(k, p) <= 0) {
                return;
            }
            swap(k, p);
            k = p;
        }
    }

    /**
     * If this is a min-heap, return the heap value with lowest priority.
     * If this is a max-heap, return the heap value with highest priority
     * Do not change the heap. This operation takes constant time.
     * Throw a NoSuchElementException if the heap is empty.
     */
    public E peek() {
        if (size <= 0) {
            throw new NoSuchElementException("heap is empty");
        }
        return d[0].val;
    }

    /**
     * Bubble d[k] down in heap until it finds the right place.
     * If there is a choice to bubble down to both the left and
     * right children (because their priorities are equal), choose
     * the right child.
     * Precondition: 0 <= k < size   and
     * Class invariant is true except that perhaps
     * d[k] belongs below one or both of its children.
     */
    void bubbleDown(int k) {
        assert 0 <= k && k < size;

        // Invariant: Class invariant is true except that perhaps
        //            d[k] belongs below one or both of its children
        while (2 * k + 1 < size) { // while d[k] has a child
            int uc = upperChild(k);
            if (compareTo(k, uc) >= 0) {
                return;
            }
            swap(k, uc);
            k = uc;
        }
    }

    /**
     * If d[n] doesn't exist or has no child, return n.
     * If d[n] has one child, return its index.
     * If d[n] has two children with the same priority, return the
     * index of the right one.
     * If d[n] has two children with different priorities return the
     * index of the one that must appear above the other in a heap.
     */
    protected int upperChild(int n) {
        if (size <= n) {
            return n;
        }
        int lc = 2 * n + 1;                  // index of n's left child
        if (size <= lc) {
            return n;         // n has no child
        }
        if (size == lc + 1) {
            return lc;  // n has exactly one child
        }
        return compareTo(lc, lc + 1) > 0 ? lc : lc + 1;
    }

    /**
     * If this is a min-heap, remove and return heap value with lowest priority.
     * If this is a max-heap, remove and return heap value with highest priority.
     * The expected time is logarithmic and the worst-case time is linear
     * in the size of the heap.
     * Throw a NoSuchElementException if the heap is empty.
     */
    public E poll() {
        if (size <= 0) {
            throw new NoSuchElementException("heap is empty");
        }

        E v = d[0].val;
        swap(0, size - 1);
        map.remove(v);
        size = size - 1;
        if (size > 0) {
            bubbleDown(0);
        }
        return v;
    }

    /**
     * Change the priority of value v to p.
     * The expected time is logarithmic and the worst-case time is linear
     * in the size of the heap.
     * Throw an IllegalArgumentException if v is not in the heap.
     */
    public void updatePriority(E v, double p) {
        Integer index = map.get(v);
        if (index == null) {
            throw new IllegalArgumentException("v is not in the priority queue");
        }
        double oldP = d[index].priority;
        d[index].priority = p;
        int t = compareTo(p, oldP);
        if (t == 0) {
            return;
        }
        if (t < 0) {
            bubbleDown(index);
        } else {
            bubbleUp(index);
        }
    }

    /**
     * Create and return an array of size n.
     * This is necessary because generics and arrays don't interoperate nicely.
     */
    @SuppressWarnings("unchecked")
    VP[] createVPArray(int n) {
        return (VP[]) Array.newInstance(VP.class, n);
    }

    /**
     * This method creates and returns an array list that contains all of the values
     * in the order they are presented in the VP array. Thus, if it is a max heap, this will
     * create an ArrayList of the values with the highest priority value being first in the list.
     * PRECONDITION: heap has at least 1 element
     * Returns: ArrayList<E> - contains all values listed in order of their priority
     */
    public ArrayList<E> makeArrayList() {
        ArrayList<E> ret = new ArrayList<E>();
        for (int k = 0; k < d.length; k++) {
            if (d[k] != null) {
                ret.add(d[k].val);
            }
        }
        return ret;
    }

    /**
     * A VP object houses a value and a priority.
     */
    class VP {
        E val;             // The value
        double priority;   // The priority

        /**
         * An instance with value v and priority p.
         */
        VP(E v, double p) {
            val = v;
            priority = p;
        }

        /**
         * Return a representation of this VP object.
         */
        @Override
        public String toString() {
            return "(" + val + ", " + priority + ")";
        }
    }

}