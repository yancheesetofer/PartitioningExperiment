import java.util.Arrays;
import java.util.Random;

class IntWrapper {
    public int value;

    public IntWrapper(int value) {
        this.value = value;
    }
}

public class SetPartitionExperiment {

    // Dynamic Programming implementation for Set Partition
    public static boolean dynamicProgrammingSetPartition(int[] set) {
        int sum = Arrays.stream(set).sum();
        if (sum % 2 != 0) return false; 

        return subsetSum(set, sum / 2);
    }

    private static boolean subsetSum(int[] set, int sum) {
        boolean[][] dp = new boolean[set.length + 1][sum + 1];

        for (int i = 0; i <= set.length; i++) {
            dp[i][0] = true;
        }

        for (int i = 1; i <= set.length; i++) {
            for (int j = 1; j <= sum; j++) {
                if (set[i - 1] <= j) {
                    dp[i][j] = dp[i - 1][j] || dp[i - 1][j - set[i - 1]];
                } else {
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }

        return dp[set.length][sum];
    }


    public static boolean branchAndBoundSetPartition(int[] values) {
        int totalValue = Arrays.stream(values).sum();
        boolean[] testAssignment = new boolean[values.length];
        boolean[] bestAssignment = new boolean[values.length];
        IntWrapper bestErr = new IntWrapper(Integer.MAX_VALUE);

        partitionValuesFromIndex(values, 0, totalValue, totalValue, testAssignment, 0, bestAssignment, bestErr);
        return bestErr.value == 0;
    }

    private static void partitionValuesFromIndex(int[] values, int startIndex, int totalValue, int unassignedValue, boolean[] testAssignment, int testValue, boolean[] bestAssignment, IntWrapper bestErr) {
        if (startIndex >= values.length) {
            int testErr = Math.abs(2 * testValue - totalValue);
            if (testErr < bestErr.value) {
                bestErr.value = testErr;
                System.arraycopy(testAssignment, 0, bestAssignment, 0, testAssignment.length);
            }
        } else {
            int testErr = Math.abs(2 * testValue - totalValue);
            if (testErr - unassignedValue < bestErr.value) {
                unassignedValue -= values[startIndex];

                testAssignment[startIndex] = true;
                partitionValuesFromIndex(values, startIndex + 1, totalValue, unassignedValue, testAssignment, testValue + values[startIndex], bestAssignment, bestErr);

                testAssignment[startIndex] = false;
                partitionValuesFromIndex(values, startIndex + 1, totalValue, unassignedValue, testAssignment, testValue, bestAssignment, bestErr);
            }
        }
    }

    // Method to generate random datasets
    public static int[] generateDataset(int size) {
        int[] dataset = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            dataset[i] = random.nextInt(100); 
        return dataset;
    }

    // Method to benchmark Set Partition algorithms
    public static long[] benchmarkSetPartitionAlgorithms(int[] dataset) {
        Runtime runtime = Runtime.getRuntime();
        
        // Memory benchmark for Dynamic Programming
        long memoryBeforeDP = runtime.totalMemory() - runtime.freeMemory();
        long startTimeDP = System.currentTimeMillis();
        dynamicProgrammingSetPartition(dataset.clone());
        long endTimeDP = System.currentTimeMillis();
        long memoryAfterDP = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsedDP = memoryAfterDP - memoryBeforeDP;

        // Memory benchmark for Branch and Bound
        long memoryBeforeBB = runtime.totalMemory() - runtime.freeMemory();
        long startTimeBB = System.currentTimeMillis();
        branchAndBoundSetPartition(dataset.clone());
        long endTimeBB = System.currentTimeMillis();
        long memoryAfterBB = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsedBB = memoryAfterBB - memoryBeforeBB;

        return new long[]{endTimeDP - startTimeDP, memoryUsedDP, endTimeBB - startTimeBB, memoryUsedBB};
    }


    public static void main(String[] args) {
        System.out.println("| Dataset Size | DP Time (ms) | DP Memory (bytes) | BB Time (ms) | BB Memory (bytes) |");
        System.out.println("|--------------|--------------|------------------|--------------|--------------------|");

        int[] sizes = {10, 40, 80};

        for (int size : sizes) {
            int[] dataset = generateDataset(size);
            long[] results = benchmarkSetPartitionAlgorithms(dataset);
            System.out.printf("| %12d | %12d | %16d | %12d | %18d |%n", size, results[0], results[1], results[2], results[3]);
        }
    }
}
