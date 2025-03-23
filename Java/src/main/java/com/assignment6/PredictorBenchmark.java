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
import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.setupConfig;
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

    static void benchmarkSort(Config config, int arrayLength, SortWithHelper<Integer> instrumentedSorter, SortWithHelper<Integer> sorter) {
        Random random = new Random(Integer.parseInt(config.get("helper", "seed")));

        Integer[] numbers = generateRandomArray(arrayLength, random);

        instrumentedSorter.sort(numbers);
        Helper<Integer> helper = instrumentedSorter.getHelper();
        System.out.println("Instrumented " + sorter.getDescription() + " with " + arrayLength + " elements");
        System.out.println("Hits: " + helper.getHits());
        System.out.println("Lookups: " + helper.getLookups());
        System.out.println("Swaps: " + helper.getSwaps());
        System.out.println("Copies: " + helper.getCopies());
        System.out.println("Compares: " + helper.getCompares());

        try (Stopwatch stopwatch = new Stopwatch()) {
            new SorterBenchmark<>(Integer.class, sorter::preProcess, sorter, numbers, nRuns, timeLoggersLinearithmic).run(sorter.getDescription(), arrayLength);
            sorter.close();
            logger.info("************************************************************ (" + stopwatch.lap() / 1000.0 + " sec.)");
        }
    }

    public static void main(String[] args) throws IOException {
        Config config = new Config("src/main/java/com/assignment6/config.ini");

        int arrayLengths[] = { 10000, 20000, 40000, 80000, 160000, 320000, 640000, 1280000, 2560000 };
        Config instrumentConfig = setupConfig(
            "true",
            config.get("instrumenting", "fixes"),
            config.get("helper", "seed"), 
            config.get("instrumenting", "inversions"), 
            config.get("helper", "cutoff"), 
            ""
            );
        Config sortConfig = setupConfig(
            "false",
            config.get("instrumenting", "fixes"),
            config.get("helper", "seed"), 
            config.get("instrumenting", "inversions"), 
            config.get("helper", "cutoff"), 
            ""
            );
        for (int arrayLength : arrayLengths) {
            SortWithHelper<Integer> quicksortInstrumentedSorter = new QuickSort_DualPivot<>(arrayLength, instrumentConfig);
            SortWithHelper<Integer> quicksortSorter = new QuickSort_DualPivot<>(arrayLength, sortConfig);
            
            benchmarkSort(config, arrayLength, quicksortInstrumentedSorter, quicksortSorter);

            SortWithHelper<Integer> mergesortInstrumentedSorter = new MergeSort<>(arrayLength, nRuns, instrumentConfig);
            SortWithHelper<Integer> mergesortSorter = new MergeSort<>(arrayLength, nRuns, sortConfig);
            benchmarkSort(config, arrayLength, mergesortInstrumentedSorter, mergesortSorter);

            SortWithHelper<Integer> heapsortInstrumentedSorter = new HeapSort<>(arrayLength, instrumentConfig);
            SortWithHelper<Integer> heapsortSorter = new HeapSort<>(arrayLength, sortConfig);
            benchmarkSort(config, arrayLength, heapsortInstrumentedSorter, heapsortSorter);
        }
    }
}
