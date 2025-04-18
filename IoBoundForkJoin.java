import java.net.URI;
import java.util.List;
import java.util.ArrayList;
import java.net.http.*;
import java.util.concurrent.*;

public class IoBoundForkJoin {
    private static final int TASK_COUNT = 100;
    private static final String API_ENDPOINT = "https://jsonplaceholder.typicode.com/posts/";

    static class FetchTask extends RecursiveTask<String> {
        private final int id;

        FetchTask(int id) {
            this.id = id;
        }

        @Override
        protected String compute() {
            try {
                return fetchData(id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private String fetchData(int id) throws Exception {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_ENDPOINT + id))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        }
    }

    public static void main(String[] args) {
        //         try
        // {
        //     Thread.sleep(15000);
        // }
        // catch(Exception e)
        // {
        //     e.printStackTrace();
        // }
        ForkJoinPool pool = new ForkJoinPool();
        long startTime = System.currentTimeMillis();

        List<FetchTask> tasks = new ArrayList<>();
        for (int i = 1; i <= TASK_COUNT; i++) {
            tasks.add(new FetchTask(i));
        }

        for (FetchTask task : tasks) {
            String result = pool.invoke(task);
            System.out.println("Received response of length: " + result.length());
        }

        System.out.println("ForkJoinPool - Total time: " + 
            (System.currentTimeMillis() - startTime) + " ms");
            pool.close();
    }
}