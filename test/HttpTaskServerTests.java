import TaskData.EpicTask;
import TaskData.SubTask;
import TaskData.Task;
import com.google.gson.Gson;
import models.Managers;
import org.junit.jupiter.api.*;
import server.HttpTaskServer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class HttpTaskServerTests {
    private HttpTaskServer taskServer;
    private HttpClient client;
    Gson gson;

    @BeforeEach
    public void creatingServerAndCleanFile() throws IOException {
        File file = new File("TaskDataFile.csv");
        file.delete();
        taskServer = new HttpTaskServer("TaskDataFile.csv");
        gson = Managers.getGson();
    }

    @AfterEach
    public void closeServer(){
        taskServer.close();
    }

    @Test
    public void shouldReturnCorrectInfoAndHTTPCodesForRegularTask() throws IOException, InterruptedException {
        String newTaskJson;
        HttpRequest.BodyPublisher body;
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request;
        HttpResponse<String> response;

        Task newTask = new Task("RegularTaskTitle", "RegularTaskDesc", 1);
        newTaskJson = gson.toJson(newTask);
        client = HttpClient.newHttpClient();
        body = HttpRequest.BodyPublishers.ofString(newTaskJson);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 201);
        Assertions.assertEquals(response.body(), "Created");



        newTask.setStatus("IN_PROGRESS");
        newTaskJson = gson.toJson(newTask);
        client = HttpClient.newHttpClient();
        body = HttpRequest.BodyPublishers.ofString(newTaskJson);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(202, response.statusCode());
        Assertions.assertEquals("Accepted", response.body());

        url = URI.create("http://localhost:8080/tasks/task/?id=1");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String jsonAnswer = response.body();
        Task answerTask = gson.fromJson(jsonAnswer, Task.class);

        Assertions.assertEquals("RegularTaskTitle", answerTask.getTitle());
        Assertions.assertEquals("RegularTaskDesc", answerTask.getDescription());
    }


    @Test
    public void shouldReturnCorrectSubTaskInfoAndEpicStatusAndHTTPCodes() throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body;
        HttpRequest request;
        HttpResponse<String> response;
        URI url;

        EpicTask testEpicTask = new EpicTask("testEpicTitle", "TestEpicDesc");
        String newEpicTaskJson = gson.toJson(testEpicTask);
        url = URI.create("http://localhost:8080/tasks/epic/");
        client = HttpClient.newHttpClient();
        body = HttpRequest.BodyPublishers.ofString(newEpicTaskJson);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 201);
        Assertions.assertEquals(response.body(), "Created");


        SubTask testSubTaskFirst = new SubTask("FirstSubTaskDesc", "FirstSubTaskDesc", 1);
        String newSubTaskJson = gson.toJson(testSubTaskFirst);
        url = URI.create("http://localhost:8080/tasks/subtask/");
        client = HttpClient.newHttpClient();
        body = HttpRequest.BodyPublishers.ofString(newSubTaskJson);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 201);
        Assertions.assertEquals(response.body(), "Created");

        url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String jsonAnswerEpic = response.body();
        EpicTask answerEpicTask = gson.fromJson(jsonAnswerEpic, EpicTask.class);

        ArrayList<Integer> subTasksIds = new ArrayList<>();
        subTasksIds.add(2);
        Assertions.assertEquals(subTasksIds, answerEpicTask.getFullSubTasksList());
        Assertions.assertEquals("testEpicTitle", answerEpicTask.getTitle());
        Assertions.assertEquals("TestEpicDesc", answerEpicTask.getDescription());
        Assertions.assertEquals(1, answerEpicTask.getId());

        url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String jsonAnswerSub = response.body();
        SubTask answerSubTask = gson.fromJson(jsonAnswerSub, SubTask.class);

        Assertions.assertEquals("FirstSubTaskDesc", answerSubTask.getTitle());
        Assertions.assertEquals("FirstSubTaskDesc", answerSubTask.getDescription());
        Assertions.assertEquals(2, answerSubTask.getId());
        Assertions.assertEquals(1, answerSubTask.getBoundedTo());
    }


    @Test
    public void HTTPCodeShouldBeNotFoundForWrongTaskId() throws IOException, InterruptedException {
        HttpRequest request;
        HttpResponse<String> response;
        URI url;

        url = URI.create("http://localhost:8080/tasks/task/?id=100");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());

        url = URI.create("http://localhost:8080/tasks/epic/?id=100");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());

        url = URI.create("http://localhost:8080/tasks/subtask/?id=100");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void ShouldReturnCorrectHistoryHTTPCodes() throws IOException, InterruptedException {
        String newTaskJson;
        HttpRequest.BodyPublisher body;
        URI url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request;
        HttpResponse<String> response;

        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());

        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertEquals("History_Empty", response.body());

        Task newTask = new Task("RegularTaskTitle", "RegularTaskDesc");
        newTaskJson = gson.toJson(newTask);
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/task/");
        body = HttpRequest.BodyPublishers.ofString(newTaskJson);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 201);
        Assertions.assertEquals(response.body(), "Created");

        newTask = new Task("AnotherRegularTaskTitle", "AnotherRegularTaskDesc");
        newTaskJson = gson.toJson(newTask);
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/task/");
        body = HttpRequest.BodyPublishers.ofString(newTaskJson);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 201);
        Assertions.assertEquals(response.body(), "Created");

        url = URI.create("http://localhost:8080/tasks/task/?id=2");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String jsonAnswer = response.body();
        Task answerTask = gson.fromJson(jsonAnswer, Task.class);

        Assertions.assertEquals("AnotherRegularTaskTitle", answerTask.getTitle());
        Assertions.assertEquals("AnotherRegularTaskDesc", answerTask.getDescription());

        url = URI.create("http://localhost:8080/tasks/task/?id=1");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        jsonAnswer = response.body();
        answerTask = gson.fromJson(jsonAnswer, Task.class);

        Assertions.assertEquals("RegularTaskTitle", answerTask.getTitle());
        Assertions.assertEquals("RegularTaskDesc", answerTask.getDescription());

        url = URI.create("http://localhost:8080/tasks/history/");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
    }
}
