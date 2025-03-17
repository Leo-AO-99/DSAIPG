package com.phasmidsoftware.dsaipg.sort.par;

import java.util.Random;

public class BenchMark {

    static int equivalentMaxDepth(int length, int cutoff) {
        return (int) Math.ceil(Math.log(length / cutoff) / Math.log(2));
    }

    public static void main(String[] args) {
        int lengths[] = {2000000, 4000000, 8000000, 16000000, 32000000, 64000000, 128000000};
        int cutoffSizes[] = {1, 2, 4, 8, 16, 32, 64, 128, 256};
        // mix
        for (int length: lengths) {
            int[] array = new int[length];
            Random random = new Random();
            for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
            System.out.println("================================================");
            for (int cutoffSize: cutoffSizes) {
                int cutoff = length / cutoffSize;
                int maxDepth = equivalentMaxDepth(length, cutoff);
                ParSort.maxDepth = maxDepth;
                ParSort.cutoff = cutoff;
                long time = 0;
                for (int t = 0; t < 10; t++) {
                    for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
                    long startTime = System.currentTimeMillis();
                    ParSort.sort(array, 0, array.length, 0);
                    long endTime = System.currentTimeMillis();
                    time += (endTime - startTime);
                }
                System.out.println("length: " + length + "\tcutoff: " + cutoff + "\tmaxDepth: " + maxDepth + "\t10times Time:" + time + "ms");
            }

        }
    }
    
}
