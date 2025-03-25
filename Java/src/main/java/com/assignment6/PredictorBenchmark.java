package com.assignment6;

import java.io.IOException;
import java.util.Random;

import com.phasmidsoftware.dsaipg.sort.elementary.HeapSort;
import com.phasmidsoftware.dsaipg.sort.generic.SortWithHelper;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.sort.linearithmic.MergeSort;
import com.phasmidsoftware.dsaipg.sort.linearithmic.QuickSort_DualPivot;
import com.phasmidsoftware.dsaipg.util.benchmark.SorterBenchmark;
import com.phasmidsoftware.dsaipg.util.benchmark.Stopwatch;
import com.phasmidsoftware.dsaipg.util.benchmark.TimeLogger;
import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.logging.LazyLogger;

public class PredictorBenchmark {

    public static final TimeLogger TIME_LOGGER_RAW = new TimeLogger("Raw time per run {mSec}: ", null);

    public final static TimeLogger[] timeLoggersLinearithmic = {
        TIME_LOGGER_RAW
    };

    final static LazyLogger logger = new LazyLogger(PredictorBenchmark.class);
    static int nRuns = 200;

    static Integer[] generateRandomArray(int n, Random random) {
        Integer[] array = new Integer[n];
        for (int i = 0; i < n; i++) {
            array[i] = random.nextInt(100000);
        }
        return array;
    }

    static boolean isSorted(Integer[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i-1] > array[i]) {
                return false;
            }
        }
        return true;
    }

    static void benchmarkSort(Config config, int arrayLength, SortWithHelper<Integer> instrumentedSorter, SortWithHelper<Integer> sorter) {
        Random random = new Random(Integer.parseInt(config.get("helper", "seed")));

        Integer[] numbers = generateRandomArray(arrayLength, random);

        Integer[] sortedNumbers = instrumentedSorter.sort(numbers);
        if (!isSorted(sortedNumbers)) {
            throw new RuntimeException("Array is not sorted");
        }
        
        Helper<Integer> helper = instrumentedSorter.getHelper();
        System.out.println("Instrumented " + sorter.getDescription() + " with " + arrayLength + " elements");
        System.out.println("Hits: " + helper.getHits());
        System.out.println("Swaps: " + helper.getSwaps());
        System.out.println("Copies: " + helper.getCopies());
        System.out.println("Compares: " + helper.getCompares());

        try (Stopwatch stopwatch = new Stopwatch()) {
            try (sorter) {
                new SorterBenchmark<>(Integer.class, sorter::preProcess, sorter, numbers, nRuns, timeLoggersLinearithmic).run(sorter.getDescription(), arrayLength);
            }
            logger.info("************************************************************ (" + stopwatch.lap() / 1000.0 + " sec.)");
        }
    }

    public static void main(String[] args) throws IOException {
        Config config = new Config("src/main/java/com/assignment6/config.ini");
        Config instrumentConfig = new Config("src/main/java/com/assignment6/instrumentedConfig.ini");
        

        int arrayLengths[] = { 10000, 20000, 40000, 80000, 160000, 320000, 640000, 1280000, 2560000 };
        for (int arrayLength : arrayLengths) {

            SortWithHelper<Integer> quicksortInstrumentedSorter = new QuickSort_DualPivot<>(arrayLength, instrumentConfig);
            SortWithHelper<Integer> quicksortSorter = new QuickSort_DualPivot<>(arrayLength, config);
            benchmarkSort(config, arrayLength, quicksortInstrumentedSorter, quicksortSorter);


            SortWithHelper<Integer> mergesortInstrumentedSorter = new MergeSort<>(arrayLength, nRuns, instrumentConfig);
            SortWithHelper<Integer> mergesortSorter = new MergeSort<>(arrayLength, nRuns, config);
            benchmarkSort(config, arrayLength, mergesortInstrumentedSorter, mergesortSorter);
            

            SortWithHelper<Integer> heapsortInstrumentedSorter = new HeapSort<>(arrayLength, instrumentConfig);
            SortWithHelper<Integer> heapsortSorter = new HeapSort<>(arrayLength, config);
            benchmarkSort(config, arrayLength, heapsortInstrumentedSorter, heapsortSorter);
        }
    }
}
