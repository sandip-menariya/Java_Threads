import java.net.URI;
import java.net.http.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class IoBoundVirtualThreads {
    private static final int TASK_COUNT = 100;  // Number of I/O tasks
    private static final String API_ENDPOINT = "https://jsonplaceholder.typicode.com/posts/";

    private static String fetchData(int id) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_ENDPOINT + id))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
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
        long startTime = System.currentTimeMillis();
        List<Future<String>> futures = new ArrayList<>();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // Submit I/O-bound tasks
            for (int i = 1; i <= TASK_COUNT; i++) {
                final int taskId = i;
                futures.add(executor.submit(() -> fetchData(taskId)));
            }

            // Wait for all tasks to complete
            for (Future<String> future : futures) {
                String result = future.get();
                System.out.println("Received response of length: " + result.length());
            }

            System.out.println("Virtual Threads - Total time: " + 
                (System.currentTimeMillis() - startTime) + " ms");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}