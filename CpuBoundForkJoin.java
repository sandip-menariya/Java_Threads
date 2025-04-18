import java.util.concurrent.*;

public class CpuBoundForkJoin {
    static class ComputationTask extends RecursiveTask<Double> {
        private final int start;
        private final int end;
        private static final int THRESHOLD = 10000;  // Split if larger than this

        ComputationTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Double compute() {
            if (end - start <= THRESHOLD) {
                return computeDirectly();
            } else {
                int mid = (start + end) / 2;
                ComputationTask left = new ComputationTask(start, mid);
                ComputationTask right = new ComputationTask(mid + 1, end);
                left.fork();
                return right.compute() + left.join();
            }
        }

        private double computeDirectly() {
            double sum = 0;
            for (int i = start; i <= end; i++) {
                sum += Math.sqrt(i) * Math.sin(i) * Math.log(i + 1);
            }
            return sum;
        }
    }

    public static void main(String[] args) {
        try
        {
            Thread.sleep(15000);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        final int TOTAL_NUMBERS = 10_000_000;  // Total numbers to process
        
        ForkJoinPool pool = new ForkJoinPool();
        long startTime = System.currentTimeMillis();

        double result = pool.invoke(new ComputationTask(1, TOTAL_NUMBERS));
        System.out.printf("ForkJoinPool - Final Sum: %.2f%n", result);
        System.out.println("Time taken: " + (System.currentTimeMillis() - startTime) + " ms");
        pool.close();
    }
}