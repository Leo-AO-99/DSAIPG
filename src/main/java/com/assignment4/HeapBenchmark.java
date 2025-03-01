package com.assignment4;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import com.phasmidsoftware.dsaipg.adt.pq.PQException;
import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue;
import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;

public class HeapBenchmark {

    public static void main(String[] args) {

        benchmarkHeapMultiple(4095, 16000, 4000);
        benchmarkHeapMultiple(8191, 32000, 8000);
        benchmarkHeapMultiple(16383, 64000, 16000);
        benchmarkHeapMultiple(32767, 128000, 32000);
        benchmarkHeapMultiple(65535, 256000, 64000);
    }

    private static void benchmarkHeapMultiple(int M, int insertions, int removals) {
        System.out.println("================================================");
        System.out.println("Benchmark for M = " + M + ", insertions = " + insertions + ", removals = " + removals);
        System.out.println("================================================");

        benchmarkHeap("Binary Heap", M, insertions, removals, false, 2);

        benchmarkHeap("Binary Heap with Floyd's Trick", M, insertions, removals, true, 2);

        benchmarkHeap("4-ary Heap", M, insertions, removals, false, 4);

        benchmarkHeap("4-ary Heap with Floyd's Trick", M, insertions, removals, true, 4);

        benchmarkFibonacciHeap("Fibonacci Heap", M, insertions, removals);
    }

    private static void benchmarkFibonacciHeap(String description, int M, int insertions, int removals) {
        FibonacciHeap<Integer> heap = new FibonacciHeap<>(M);
        LinkedList<Integer> spilledItems = new LinkedList<>();
        Consumer<Void> fRun = v -> {
            for (int i = 0; i < insertions; i++) {
                FibonacciHeap.Entry<Integer> spilled = heap.enqueue(i, i);
                if (spilled != null) {
                    spilledItems.add(spilled.getValue());
                }
            }
            for (int i = 0; i < removals; i++) {
                try {
                    heap.dequeueMin();
                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                }
            }
        };

        Benchmark_Timer<Void> timer = new Benchmark_Timer<>(description, fRun);
        double duration = timer.runFromSupplier(() -> null, 300);
        System.out.println(description + " took " + duration + " ms");
        System.out.println("Spilled items: " + spilledItems.size());
        System.out.println("Heap size: " + heap.size());
        System.out.println("Highest priority spilled item: " + (spilledItems.isEmpty() ? "none" : spilledItems.peek()));
    }

    private static void benchmarkHeap(String description, int M, int insertions, int removals, boolean floyd, int dAry) {
        PriorityQueue<Integer> pq = new PriorityQueue<Integer>(M, true, Comparator.naturalOrder(), floyd);
        pq.setDary(dAry);
        LinkedList<Integer> spilledItems = new LinkedList<>();
        Consumer<Void> fRun = v -> {
            for (int i = 0; i < insertions; i++) {
                Integer spilled = pq.give(i);
                if (spilled != null) {
                    spilledItems.add(spilled);
                }
            }
            for (int i = 0; i < removals; i++) {
                try {
                    pq.take();
                } catch (PQException e) {
                    e.printStackTrace();
                }
            }
        };

        Benchmark_Timer<Void> timer = new Benchmark_Timer<>(description, fRun);
        double duration = timer.runFromSupplier(() -> null, 300);
        System.out.println(description + " took " + duration + " ms");
        System.out.println("Spilled items: " + spilledItems.size());
        System.out.println("PQ size: " + pq.size());
        System.out.println("Highest priority spilled item: " + spilledItems.peek());
    }
}