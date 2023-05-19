package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import TaskData.*;
import com.sun.net.httpserver.HttpServer;
import models.*;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.TreeSet;

public class ServerHandler {

    private static TaskManager taskManager;
    public static Gson gson;
    public static HttpServer httpServer;

    public ServerHandler(HttpServer httpServer){
        this.httpServer = httpServer;
        gson = Managers.getGson();
        taskManager = Managers.getFileBacked("dataFile.csv");
        httpServer.createContext("/tasks", new PrioritizedTaskHandler());
        httpServer.createContext("/tasks/task", new TaskHandler());
        httpServer.createContext("/tasks/epic", new EpicTaskHandler());
        httpServer.createContext("/tasks/subtask", new SubTaskHandler());
        httpServer.createContext("/tasks/history", new HistoryHandler());
    }

    public ServerHandler(HttpServer httpServer, String dataFile){
        this.httpServer = httpServer;
        gson = Managers.getGson();
        taskManager = Managers.getFileBacked(dataFile);
        httpServer.createContext("/tasks", new PrioritizedTaskHandler());
        httpServer.createContext("/tasks/task", new TaskHandler());
        httpServer.createContext("/tasks/epic", new EpicTaskHandler());
        httpServer.createContext("/tasks/subtask", new SubTaskHandler());
        httpServer.createContext("/tasks/history", new HistoryHandler());
    }

    static class PrioritizedTaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            switch (method) {
                case"GET":
                    if (taskManager.getPrioritizedTasks().equals(new TreeSet<>())) {
                        sendAnswer(httpExchange, "No_Content", 204);
                        return;
                    }
                    sendAnswer(httpExchange, gson.toJson(taskManager.getPrioritizedTasks()), 200);
                    break;
                case"DELETE":
                    taskManager.deleteAllTasks();
                    sendAnswer(httpExchange, "All_tasks_deleted", 200);
                    break;
                default:
                    sendAnswer(httpExchange, "Method_Not_Allowed", 405);
            }
        }
    }

    public static void sendAnswer(HttpExchange httpExchange, String response, int rCode) throws IOException {
        byte[] responseToBytes = response.getBytes();
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(rCode, responseToBytes.length);
        httpExchange.getResponseBody().write(responseToBytes);
    }

    public static JsonElement getJson(HttpExchange httpExchange) throws IOException {
        byte[] reqBody = httpExchange.getRequestBody().readAllBytes();
        String jsonTask = new String(reqBody, StandardCharsets.UTF_8);
        return JsonParser.parseString(jsonTask);
    }
    static class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException{
            URI requestURI = httpExchange.getRequestURI();
            String method = httpExchange.getRequestMethod();
            String path = requestURI.getPath();
            String[] splitPath = path.split("/");
            int taskId;
            switch (method){
                case"GET":
                    if(splitPath.length > 3){
                        sendAnswer(httpExchange, "Bad_Request", 400);
                        return;
                    }
                    taskId = Integer.parseInt(httpExchange.getRequestURI().getQuery().substring("id=".length()));
                    if (taskManager.getTask(taskId) == null){
                        sendAnswer(httpExchange, "Not_Found", 404);
                        return;
                    }
                    sendAnswer(httpExchange, gson.toJson(taskManager.getTask(taskId)), 200);
                    break;
                case"POST":
                    try {
                        Task task = gson.fromJson(getJson(httpExchange), Task.class);
                        if (task == null){
                            sendAnswer(httpExchange, "Unsupported_Media_Type", 415);
                            return;
                        }
                        if (taskManager.getTask(task.getId()) == null){
                            taskManager.addRegularTask(task);
                            sendAnswer(httpExchange, "Created", 201);
                        } else {
                            taskManager.editRegularTask(task);
                            sendAnswer(httpExchange, "Accepted", 202);
                        }
                    } catch (IOException e){
                        System.out.println("reading Error");
                        sendAnswer(httpExchange, "Reading_Error", 500);
                    }
                    break;
                case"DELETE":
                    if(splitPath.length > 3){
                        taskManager.deleteAllRegularTask();
                        sendAnswer(httpExchange, "All_Regular_Tasks_Deleted", 200);
                        return;
                    }
                    taskId = Integer.parseInt(httpExchange.getRequestURI().getQuery().substring("id=".length()));
                    if (taskManager.getTask(taskId) == null){
                        sendAnswer(httpExchange, "Task_not_Found", 404);
                        return;
                    }
                    taskManager.deleteTaskByID(taskId);
                    sendAnswer(httpExchange, "Task_" + taskId + "_Deleted", 200);
            }
        }
    }

    static class EpicTaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException{
            URI requestURI = httpExchange.getRequestURI();
            String method = httpExchange.getRequestMethod();
            String path = requestURI.getPath();
            String[] splitPath = path.split("/");
            int epicTaskId;
            switch (method){
                case"GET":
                    if(splitPath.length > 3){
                        sendAnswer(httpExchange, "Bad_Request", 400);
                        return;
                    }
                    epicTaskId = Integer.parseInt(httpExchange.getRequestURI().getQuery().substring("id=".length()));
                    if (taskManager.getEpic(epicTaskId) == null){
                        sendAnswer(httpExchange, "Not_Found", 404);
                        return;
                    }
                    sendAnswer(httpExchange, gson.toJson(taskManager.getEpic(epicTaskId)), 200);
                    break;
                case"POST":
                    try {
                        EpicTask epicTask = gson.fromJson(getJson(httpExchange), EpicTask.class);
                        if (epicTask == null){
                            sendAnswer(httpExchange, "Unsupported_Media_Type", 415);
                            return;
                        }
                        if (taskManager.getTask(epicTask.getId()) == null){
                            taskManager.addEmptyEpicTask(epicTask);
                            sendAnswer(httpExchange, "Created", 201);
                        } else {
                            taskManager.editEpicTask(epicTask);
                            sendAnswer(httpExchange, "Accepted", 202);
                        }
                    } catch (IOException e){
                        System.out.println("reading Error");
                        sendAnswer(httpExchange, "Reading_Error", 500);
                    }
                    break;
                case"DELETE":
                    if(splitPath.length > 3){
                        taskManager.deleteAllEpicTasks();
                        sendAnswer(httpExchange, "All_Epic_Tasks_Deleted", 200);
                        return;
                    }
                    epicTaskId = Integer.parseInt(httpExchange.getRequestURI().getQuery().substring("id=".length()));
                    if (taskManager.getEpic(epicTaskId) == null){
                        sendAnswer(httpExchange, "Epic_Task_Not_Found", 404);
                        return;
                    }
                    taskManager.deleteTaskByID(epicTaskId);
                    sendAnswer(httpExchange, "Epic_Task_" + epicTaskId + "_Deleted", 200);
            }
        }
    }

    static class SubTaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException{
            URI requestURI = httpExchange.getRequestURI();
            String method = httpExchange.getRequestMethod();
            String path = requestURI.getPath();
            String[] splitPath = path.split("/");
            int subTaskId;
            switch (method){
                case"GET":
                    if(splitPath.length > 3){
                        sendAnswer(httpExchange, "Bad_Request", 400);
                        return;
                    }
                    subTaskId = Integer.parseInt(httpExchange.getRequestURI().getQuery().substring("id=".length()));
                    if (taskManager.getSubtask(subTaskId) == null){
                        sendAnswer(httpExchange, "Not_Found", 404);
                        return;
                    }
                    sendAnswer(httpExchange, gson.toJson(taskManager.getSubtask(subTaskId)), 200);
                    break;
                case"POST":
                    try {
                        SubTask subTask = gson.fromJson(getJson(httpExchange), SubTask.class);
                        if (subTask == null){
                            sendAnswer(httpExchange, "Unsupported_Media_Type", 415);
                            return;
                        }
                        if (taskManager.getSubtask(subTask.getId()) == null){
                            taskManager.addSubTask(subTask);
                            sendAnswer(httpExchange, "Created", 201);
                        } else {
                            taskManager.editSubTask(subTask);
                            sendAnswer(httpExchange, "Accepted", 202);
                        }
                    } catch (IOException e){
                        System.out.println("reading Error");
                        sendAnswer(httpExchange, "Reading_Error", 500);
                    }
                    break;
                case"DELETE":
                    if(splitPath.length > 3){
                        taskManager.deleteAllSubTasks();
                        sendAnswer(httpExchange, "All_SubTasks_Deleted", 200);
                        return;
                    }
                    subTaskId = Integer.parseInt(httpExchange.getRequestURI().getQuery().substring("id=".length()));
                    if (taskManager.getSubtask(subTaskId) == null){
                        sendAnswer(httpExchange, "Not_Found", 404);
                        return;
                    }
                    taskManager.deleteTaskByID(subTaskId);
                    sendAnswer(httpExchange, "SubTask_" + subTaskId + "_Deleted", 200);
            }
        }
    }

    static class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException{
            String method = httpExchange.getRequestMethod();
            if (!method.equals("GET")){
                sendAnswer(httpExchange, "Method_Not_Allowed", 405);
                return;
            }
            if(taskManager.getHistory().equals(new ArrayList<>())){
                sendAnswer(httpExchange, "History_Empty", 400);
                return;
            }
            sendAnswer(httpExchange, gson.toJson(taskManager.getHistory()), 200);
        }
    }
}
