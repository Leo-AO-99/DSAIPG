package com.assignment6;

import java.io.IOException;
import java.util.Random;

import com.phasmidsoftware.dsaipg.sort.generic.SortWithHelper;
import com.phasmidsoftware.dsaipg.sort.linearithmic.MergeSort;
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
    static Integer[] generateRandomArray(int n, Random random) {
        Integer[] array = new Integer[n];
        for (int i = 0; i < n; i++) {
            array[i] = random.nextInt(100000);
        }
        return array;
    }

    static void benchmarkMergeSort(Config config, int arrayLength) {
        int nRuns = 10;
        SortWithHelper<Integer> sorter = new MergeSort<>(arrayLength, 1, config);
        Random random = new Random(Integer.parseInt(config.get("helper", "seed")));

        Integer[] numbers = generateRandomArray(arrayLength, random);

        try (Stopwatch stopwatch = new Stopwatch()) {
            new SorterBenchmark<>(Integer.class, sorter::preProcess, sorter, numbers, nRuns, timeLoggersLinearithmic).run("Merge Sort", arrayLength);
            sorter.close();
            logger.info("************************************************************ (" + stopwatch.lap() / 1000.0 + " sec.)");
        }

    }

    public static void main(String[] args) throws IOException {
        Config config = new Config("src/main/java/com/assignment6/config.ini");

        int arrayLengths[] = { 10000, 20000, 40000, 80000, 160000, 320000, 640000, 1280000, 2560000 };
        // Random random = new Random(Integer.parseInt(config.get("helper", "seed")));
        for (int arrayLength : arrayLengths) {
            benchmarkMergeSort(config, arrayLength);
        }
    }
}
