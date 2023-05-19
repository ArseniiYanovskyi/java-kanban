package services;

import exceptions.*;

import TaskData.EpicTask;
import TaskData.SubTask;
import TaskData.Task;
import models.*;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    String fileName;
    File dataFileCSV;

    public FileBackedTaskManager(HistoryManager historyManager, String fileName) throws ManagerSaveException {
        super(historyManager);
        this.fileName = fileName;
        try {
            this.dataFileCSV = new File(fileName);
            if (dataFileCSV.exists() && !dataFileCSV.isDirectory()) {
                load();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла.");
        }
    }

    public FileBackedTaskManager() {
        super(Managers.getDefaultHistory());
        this.dataFileCSV = new File("dataFile.csv");
    }

    public void load() throws IOException {
        FileReader reader = new FileReader(fileName);
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            bufferedReader.readLine();

            int maxIdValue = 1;

            while (bufferedReader.ready()) {
                String dataLine = bufferedReader.readLine();
                if (!dataLine.isBlank()) {
                    String[] dataLinesParts = dataLine.split(",");
                    if (dataLinesParts.length < 5) {
                        continue;
                    }
                    switch (dataLinesParts[1]) {
                        case "Epic":
                            EpicTask epicTask = new EpicTask(dataLinesParts[2], dataLinesParts[4], Integer.valueOf(dataLinesParts[0]));
                            epicTask.setStatus(dataLinesParts[3]);
                            if (maxIdValue < epicTask.getId()){
                                maxIdValue = epicTask.getId();
                            }
                            for (int i = 7; i <= dataLinesParts.length - 1; i++) {
                                epicTask.addSubTaskId(Integer.valueOf(dataLinesParts[i]));
                            }
                            super.addEmptyEpicTask(epicTask);
                            break;
                        case "SubTask":
                            SubTask subTask = new SubTask(dataLinesParts[2], dataLinesParts[4],
                                    Integer.valueOf(dataLinesParts[7]), Integer.valueOf(dataLinesParts[0]));
                            subTask.setStatus(dataLinesParts[3]);
                            if (!dataLinesParts[5].equals("0")) {
                                subTask.setStartTime(Instant.ofEpochMilli(Long.parseLong(dataLinesParts[5])));
                            }
                            if (!dataLinesParts[6].equals("0")) {
                                subTask.setDuration(Instant.ofEpochMilli(Long.parseLong(dataLinesParts[6])));
                            }
                            if (maxIdValue < subTask.getId()){
                                maxIdValue = subTask.getId();
                            }
                            super.addSubTask(subTask);
                            break;
                        case "Regular":
                            Task task = new Task(dataLinesParts[2], dataLinesParts[4], Integer.valueOf(dataLinesParts[0]));
                            task.setStatus(dataLinesParts[3]);
                            if (!dataLinesParts[5].equals("0")) {
                                task.setStartTime(Instant.ofEpochMilli(Long.parseLong(dataLinesParts[5])));
                            }
                            if (!dataLinesParts[6].equals("0")) {
                                task.setDuration(Instant.ofEpochMilli(Long.parseLong(dataLinesParts[6])));
                            }
                            if (maxIdValue < task.getId()){
                                maxIdValue = task.getId();
                            }
                            super.addRegularTask(task);
                    }
                } else {
                    dataLine = bufferedReader.readLine();
                    if (dataLine != null && !dataLine.isBlank()) {
                        String[] historyIDs = dataLine.split(",");
                        for (int i = 0; i <= historyIDs.length - 1; i++) {
                            history.add(tasksData.get(Integer.valueOf(historyIDs[i])));
                        }
                    }
                }
            }
            idCounter = maxIdValue;
        }
    }

    public String serializeEpic(int taskID){
        StringBuilder returningString = new StringBuilder();
        returningString.append(tasksData.get(taskID).getId() + ",");
        returningString.append("Epic,");
        returningString.append(tasksData.get(taskID).getTitle() + ",");
        returningString.append(tasksData.get(taskID).getStatus() + ",");
        returningString.append(tasksData.get(taskID).getDescription() + ",");
        returningString.append("EPIC,");
        returningString.append("EPIC,");
        for (Integer subTasksID : ((EpicTask) tasksData.get(taskID)).getFullSubTasksList()) {
            returningString.append(subTasksID + ",");
        }
        returningString.deleteCharAt(returningString.length()-1);
        return returningString.toString();
    }

    public String serializeSubTask(int taskID){
        StringBuilder returningString = new StringBuilder();
        returningString.append(tasksData.get(taskID).getId() + ",");
        returningString.append("SubTask,");
        returningString.append(tasksData.get(taskID).getTitle() + ",");
        returningString.append(tasksData.get(taskID).getStatus() + ",");
        returningString.append(tasksData.get(taskID).getDescription() + ",");
        returningString.append(tasksData.get(taskID).getStartTimeOfMillis() + ",");
        returningString.append(tasksData.get(taskID).getDurationOfMillis() + ",");
        returningString.append(((SubTask) tasksData.get(taskID)).getBoundedTo());
        return returningString.toString();
    }

    public String serializeRegularTask(int taskID){
        StringBuilder returningString = new StringBuilder();
        returningString.append(tasksData.get(taskID).getId() + ",");
        returningString.append("Regular,");
        returningString.append(tasksData.get(taskID).getTitle() + ",");
        returningString.append(tasksData.get(taskID).getStatus() + ",");
        returningString.append(tasksData.get(taskID).getDescription() + ",");
        returningString.append(tasksData.get(taskID).getStartTimeOfMillis() + ",");
        returningString.append(tasksData.get(taskID).getDurationOfMillis());
        return returningString.toString();
    }


    public void save() throws ManagerSaveException, IOException, InterruptedException {
        try (Writer writer = new FileWriter(fileName)){
            writer.write("id,type,name,status,description,startTimeInMillis,durationInMillis,bounds\n");

            for (Integer taskID : tasksData.keySet()) {
                if (tasksData.get(taskID) instanceof EpicTask) {
                    writer.write(serializeEpic(taskID) + "\n");
                } else if (tasksData.get(taskID) instanceof SubTask) {
                    writer.write(serializeSubTask(taskID) + "\n");
                } else {
                    writer.write(serializeRegularTask(taskID) + "\n");
                }
            }

            List<Task> historylist = history.getHistory();

            if (historylist != null) {
                writer.write("\n");
                for (Task task : historylist) {
                    writer.write(task.getId() + ",");
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка записи в файл.");
        }
    }

    @Override
    public void addRegularTask(Task newTask){
        super.addRegularTask(newTask);
        try {
            save();
        }  catch (ManagerSaveException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void addEpicTask(EpicTask epicTask, ArrayList<SubTask> subTasks){
        super.addEpicTask(epicTask, subTasks);
        try {
            save();
        }  catch (ManagerSaveException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void addEmptyEpicTask(EpicTask epicTask){
        super.addEmptyEpicTask(epicTask);
        try {
            save();
        }  catch (ManagerSaveException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void addSubTask (SubTask newSubTask){
        super.addSubTask(newSubTask);
        try {
            save();
        }  catch (ManagerSaveException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void editRegularTask(Task editedTask){
        super.editRegularTask(editedTask);
        try {
            save();
        }  catch (ManagerSaveException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void editEpicTask(EpicTask epicTask){
        super.editEpicTask(epicTask);
        try {
            save();
        }  catch (ManagerSaveException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void editSubTask(SubTask subTask){
        super.editSubTask(subTask);
        try {
            save();
        } catch (ManagerSaveException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void deleteTaskByID(int taskId){
        super.deleteTaskByID(taskId);
        try {
            save();
        }  catch (ManagerSaveException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void deleteAllRegularTask(){
        super.deleteAllRegularTask();
        try {
            save();
        }  catch (ManagerSaveException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void deleteAllEpicTasks(){
        super.deleteAllEpicTasks();
        try {
            save();
        }  catch (ManagerSaveException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void deleteAllSubTasks(){
        super.deleteAllSubTasks();
        try {
            save();
        }  catch (ManagerSaveException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void deleteAllTasks(){
        super.deleteAllTasks();
        try {
            save();
        }  catch (ManagerSaveException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public Task getTask(int id){
        Task returningTask = super.getTask(id);
        try {
            save();
        }  catch (ManagerSaveException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return returningTask;
    }
    @Override
    public EpicTask getEpic(int id){
        EpicTask returningTask = super.getEpic(id);
        try {
            save();
        }  catch (ManagerSaveException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return returningTask;
    }
    @Override
    public SubTask getSubtask(int id){
        SubTask returningTask = super.getSubtask(id);
        try {
            save();
        }  catch (ManagerSaveException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return returningTask;
    }
}
