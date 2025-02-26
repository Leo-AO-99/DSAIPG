package com.assignment4;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.function.Consumer;

import com.phasmidsoftware.dsaipg.adt.pq.PQException;
import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue;
import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;

public class HeapBenchmark {

    public static void main(String[] args) {
        int M = 4095;
        int insertions = 16000;
        int removals = 4000;

        // Benchmark for Basic Binary Heap
        benchmarkHeap("Binary Heap", M, insertions, removals, false, 2);

        // Benchmark for Binary Heap with Floyd's Trick
        benchmarkHeap("Binary Heap with Floyd's Trick", M, insertions, removals, true, 2);

        benchmarkHeap("4-ary Heap", M, insertions, removals, false, 4);

        benchmarkHeap("4-ary Heap with Floyd's Trick", M, insertions, removals, true, 4);
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
        double duration = timer.runFromSupplier(() -> null, 45);
        System.out.println(description + " took " + duration + " ms");
        System.out.println("Spilled items: " + spilledItems.size());
        System.out.println("PQ size: " + pq.size());
        System.out.println("Highest priority spilled item: " + spilledItems.peek());
    }
}