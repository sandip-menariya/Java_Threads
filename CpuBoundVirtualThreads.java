import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CpuBoundVirtualThreads {
    // CPU-intensive task: Sum of square roots with additional computation
    private static double computeTask(int start, int end) {
        try {
            Thread.sleep(15000); // Simulate startup delay
           } catch (InterruptedException e) {
            System.err.println("Sleep interrupted: " + e.getMessage());
           }
        double sum = 0;
        for (int i = start; i <= end; i++) {
            sum += Math.sqrt(i) * Math.sin(i) * Math.log(i + 1);
        }
        return sum;
    }

    public static void main(String[] args) {
        final int TASK_COUNT = 1000;  // Number of tasks
        final int NUMBERS_PER_TASK = 1000;  // Numbers to process per task
        
        long startTime = System.currentTimeMillis();
        List<Future<Double>> futures = new ArrayList<>();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // Submit tasks
            for (int i = 0; i < TASK_COUNT; i++) {
                int start = i * NUMBERS_PER_TASK + 1;
                int end = (i + 1) * NUMBERS_PER_TASK;
                
                futures.add(executor.submit(() -> computeTask(start, end)));
            }

            // Combine results
            double totalSum = 0;
            for (Future<Double> future : futures) {
                totalSum += future.get();
            }
            
            System.out.printf("Virtual Threads - Final Sum: %.2f%n", totalSum);
            System.out.println("Time taken: " + (System.currentTimeMillis() - startTime) + " ms");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

  

