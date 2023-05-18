import TaskData.EpicTask;
import TaskData.SubTask;
import TaskData.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exceptions.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTaskManager{
    private KVTaskClient KVClient;
    private Gson gson;

    public HttpTaskManager() throws IOException, InterruptedException {
        super();
        gson = Managers.getGson();
        KVClient = new KVTaskClient();
        this.load();
    }

    public HttpTaskManager(KVTaskClient kvTaskClient) throws IOException, InterruptedException {
        super();
        gson = Managers.getGson();
        this.KVClient = kvTaskClient;
        this.load();
    }

    public String getClientApi(){
        return KVClient.getClientApi();
    }
    public void setApi(String api){
        KVClient.setApi(api);
    }

    @Override
    public void load() throws IOException{
        String epicTasksListJson = "";
        String subTasksListJson = "";
        String regularTasksListJson = "";

        try {
            epicTasksListJson = KVClient.load("epic/");
            subTasksListJson = KVClient.load("subTasks/");
            regularTasksListJson = KVClient.load("regularTasks/");
        } catch (InterruptedException e){
            System.out.println(e.getMessage());
        }

        ArrayList<EpicTask> epicTaskList = gson.fromJson(epicTasksListJson, new TypeToken<ArrayList<EpicTask>>() {
        }.getType());
        ArrayList<SubTask> subTaskList = gson.fromJson(subTasksListJson, new TypeToken<ArrayList<SubTask>>() {
        }.getType());
        ArrayList<Task> regularTasksList = gson.fromJson(regularTasksListJson, new TypeToken<ArrayList<Task>>() {
        }.getType());

        if (epicTaskList != null) {
            for (EpicTask epicTask : epicTaskList) {
                addEmptyEpicTask(epicTask);
            }
        }
        if(subTaskList != null) {
            for (SubTask subTask : subTaskList) {
                addSubTask(subTask);
            }
        }
        if(regularTasksList != null) {
            for (Task task : regularTasksList) {
                addRegularTask(task);
            }
        }
    }

    @Override
    public void save() throws ManagerSaveException, IOException, InterruptedException {
        try {
            ArrayList<EpicTask> epicTaskList = new ArrayList<>();
            ArrayList<SubTask> subTaskList = new ArrayList<>();
            ArrayList<Task> regularTasksList = new ArrayList<>();
            List<Task> history = getHistory();


            for (Integer taskId : tasksData.keySet()) {
                if (tasksData.get(taskId) instanceof SubTask) {
                    subTaskList.add((SubTask) tasksData.get(taskId));
                } else if (tasksData.get(taskId) instanceof EpicTask) {
                    epicTaskList.add((EpicTask) tasksData.get(taskId));
                } else {
                    regularTasksList.add(tasksData.get(taskId));
                }
            }

            String epicTasksListJson = gson.toJson(epicTaskList);
            KVClient.put("epic/", epicTasksListJson);

            String subTasksListJson = gson.toJson(subTaskList);
            KVClient.put("subTasks/", subTasksListJson);

            String regularTasksListJson = gson.toJson(regularTasksList);
            KVClient.put("regularTasks/", regularTasksListJson);

            if (!history.isEmpty()) {
                List<Integer> historyIdInOrder = history.stream()
                        .map(task -> {
                            return task.getId();
                        })
                        .collect(Collectors.toList());
                String historyIDsJson = gson.toJson(historyIdInOrder);
                KVClient.put("history/", historyIDsJson);
            }
        } catch (ManagerSaveException | IOException | InterruptedException e){
            throw new SaveToServerException("Error occurred while trying to upload data on server");
        }
    }

}
