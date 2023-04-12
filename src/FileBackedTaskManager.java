import TaskData.EpicTask;
import TaskData.SubTask;
import TaskData.Task;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager{
    String dataFile;
    Writer writer;
    FileReader reader;
    BufferedReader bufferedReader;
    File dataCSV;

    public FileBackedTaskManager(HistoryManager historyManager, String dataFile) throws ManagerSaveException {
        super(historyManager);
        this.dataFile = dataFile;
        try {
            this.dataCSV = new File(dataFile);
            if (dataCSV.exists() && !dataCSV.isDirectory()) {
                loadFromFile();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("ManagerSaveException");
        }


    }

    public void loadFromFile() throws IOException {
        this.reader = new FileReader(dataFile);
        bufferedReader = new BufferedReader(reader);

        bufferedReader.readLine();
        while (bufferedReader.ready()){
            String dataLine = bufferedReader.readLine();
            if (!dataLine.trim().isBlank()) {
                String[] dataLinesParts = dataLine.split(",");
                switch (dataLinesParts[1]){
                    case "Epic":
                        EpicTask epicTask = new EpicTask(dataLinesParts[2], dataLinesParts[4]);
                        epicTask.setStatus(dataLinesParts[3]);
                        super.idCounter = Integer.valueOf(dataLinesParts[0]);
                        epicTask.setId(super.idCounter);
                        for (int i = 5; i <= dataLinesParts.length-1; i++){
                            epicTask.addSubTaskId(Integer.valueOf(dataLinesParts[i]));
                        }
                        super.addEmptyEpicTask(epicTask);
                        break;
                    case "SubTask":
                        SubTask subTask = new SubTask(dataLinesParts[2], dataLinesParts[4],
                                Integer.valueOf(dataLinesParts[5]));
                        subTask.setStatus(dataLinesParts[3]);
                        super.idCounter = Integer.valueOf(dataLinesParts[0]);
                        subTask.setId(super.idCounter);
                        super.addSubTask(subTask);
                        break;
                    case "Regular":
                        Task task = new Task(dataLinesParts[2], dataLinesParts[4]);
                        task.setStatus(dataLinesParts[3]);
                        super.idCounter = Integer.valueOf(dataLinesParts[0]);
                        task.setId(super.idCounter);
                        super.addRegularTask(task);
                }
            } else {
                dataLine = bufferedReader.readLine();
                if (!dataLine.trim().isBlank()){
                    String[] historyIDs = dataLine.split(",");
                    for (int i = 0; i <= historyIDs.length-1; i++) {
                        super.history.add(tasksData.get(Integer.valueOf(historyIDs[i])));
                    }
                }
            }
        }

        bufferedReader.close();
    }

    public void save() throws ManagerSaveException {
        try {
            this.writer = new FileWriter(dataFile);
            writer.write("id,type,name,status,description,bounds\n");

            for (Integer key : tasksData.keySet()) {
                writer.write(tasksData.get(key).getId() + ",");
                if (tasksData.get(key) instanceof EpicTask) {
                    writer.write("Epic,");
                } else if (tasksData.get(key) instanceof SubTask) {
                    writer.write("SubTask,");
                } else {
                    writer.write("Regular,");
                }
                writer.write(tasksData.get(key).getTitle() + ",");
                writer.write(tasksData.get(key).getStatus() + ",");
                writer.write(tasksData.get(key).getDescription());
                if (tasksData.get(key) instanceof EpicTask) {
                    for (Integer subTasksID : ((EpicTask) tasksData.get(key)).getFullSubTasksList()) {
                        writer.write("," + subTasksID);
                    }
                    writer.write("\n");
                } else if (tasksData.get(key) instanceof SubTask) {
                    writer.write("," + ((SubTask) tasksData.get(key)).getBoundedTo() + "\n");
                } else {
                    writer.write("\n");
                }
            }

            writer.write("\n");

            List<Task> historylist = history.getHistory();

            if (historylist != null) {
                writer.write("\n");
                for (Task task : historylist) {
                    writer.write(task.getId() + ",");
                }
            }
            writer.close();
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка записи в файл.");
        }
    }

    @Override
    public void addRegularTask(Task newTask){
        super.addRegularTask(newTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void addEpicTask(EpicTask epicTask, ArrayList<SubTask> subTasks){
        super.addEpicTask(epicTask, subTasks);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void addEmptyEpicTask(EpicTask epicTask){
        super.addEmptyEpicTask(epicTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void addSubTask (SubTask newSubTask){
        super.addSubTask(newSubTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void editRegularTask(Task editedTask){
        super.editRegularTask(editedTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void editEpicTask(EpicTask epicTask){
        super.editEpicTask(epicTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void editSubTask(SubTask subTask){
        super.editSubTask(subTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void deleteTaskByID(int taskId){
        super.deleteTaskByID(taskId);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void deleteAllRegularTask(){
        super.deleteAllRegularTask();
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void deleteAllEpicTasks(){
        super.deleteAllEpicTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void deleteAllSubTasks(){
        super.deleteAllSubTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void deleteAllTasks(){
        super.deleteAllTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public Task getTask(int id){
        Task returningTask = super.getTask(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
        return returningTask;
    }
    @Override
    public EpicTask getEpic(int id){
        EpicTask returningTask = super.getEpic(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
        return returningTask;
    }
    @Override
    public SubTask getSubtask(int id){
        SubTask returningTask = super.getSubtask(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
        return returningTask;
    }
}
