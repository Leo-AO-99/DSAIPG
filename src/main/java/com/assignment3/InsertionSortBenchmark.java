package com.assignment3;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.HelperFactory;
import com.phasmidsoftware.dsaipg.sort.SortWithHelper;
import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSort;
import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;
import com.phasmidsoftware.dsaipg.util.Config;
import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.setupConfig;


public class InsertionSortBenchmark {

    static Integer[] generateOrderedList(int n) {
        Integer[] array = new Integer[n];
        for (int i = 0; i < n; i++) {
            array[i] = i;
        }
        return array;
    }

    static Integer[] generateReverseOrderedList(int n) {
        Integer[] array = new Integer[n];
        for (int i = 0; i < n; i++) {
            array[i] = n - i;
        }
        return array;
    }

    static Integer[] generateRandomList(int n) {
        Integer[] array = new Integer[n];
        for (int i = 0; i < n; i++) {
            array[i] = i;
        }
        java.util.Collections.shuffle(java.util.Arrays.asList(array));
        return array;
    }

    static Integer[] generatePartiallyOrderedList(int n) {
        Integer[] array = generateOrderedList(n);
        java.util.Collections.shuffle(java.util.Arrays.asList(array));
        int start = (int) (Math.random() * (n / 2));
        int end = start + (int) (Math.random() * (n - start));
        java.util.Arrays.sort(array, start, end);
        return array;
    }

    static boolean isSorted(Integer[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i - 1] > array[i]) {
                return false;
            }
        }
        return true;
    }


    public static void main(String[] args) {
        int[] sizes = {128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768};
        int iteration = 45;
        ArrayList<ArrayList<Double>> results = new ArrayList<>();

        for (int size: sizes) {
            System.out.println("Size: " + size);
            Integer[] orderedList = generateOrderedList(size);
            Integer[] reverseOrderedList = generateReverseOrderedList(size);
            Integer[] randomList = generateRandomList(size);
            Integer[] partiallyOrderedList = generatePartiallyOrderedList(size);
            ArrayList<Double> times = new ArrayList<>();

            final Config config = setupConfig("true", "false", "0", "1", "", "");
            Helper<Integer> helper = HelperFactory.create("InsertionSort", size, config);
            helper.init(size);

            SortWithHelper<Integer> sorter = new InsertionSort<>(helper);
            sorter.preProcess(orderedList);

            Consumer<Integer[]> fRun = xs -> {
                sorter.sort(xs);
            };

            
            // Ordered List
            Benchmark_Timer<Integer[]> orderedTimer = new Benchmark_Timer<>("Insertion Sort", fRun);
            times.add(orderedTimer.runFromSupplier(() -> orderedList, iteration));

            // Reverse Ordered List
            Benchmark_Timer<Integer[]> reverseOrderedTimer = new Benchmark_Timer<>("Insertion Sort", fRun);
            times.add(reverseOrderedTimer.runFromSupplier(() -> reverseOrderedList, iteration));
            

            // Random List
            Benchmark_Timer<Integer[]> randomTimer = new Benchmark_Timer<>("Insertion Sort", fRun);
            times.add(randomTimer.runFromSupplier(() -> randomList, iteration));

            // Partially Ordered List
            Benchmark_Timer<Integer[]> partiallyOrderedTimer = new Benchmark_Timer<>("Insertion Sort", fRun);
            times.add(partiallyOrderedTimer.runFromSupplier(() -> partiallyOrderedList, iteration));

            results.add(times);
            sorter.close();

        }

        for (int i = 0; i < results.size(); i++) {
            System.out.println("Size: " + sizes[i]);
            System.out.println(results.get(i));
        }
    }
}
