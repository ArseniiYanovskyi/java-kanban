import TaskData.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager{
    protected HashMap<Integer, Task> tasksData;
    protected int idCounter;
    protected HistoryManager history;
    public InMemoryTaskManager(HistoryManager history){
        tasksData = new HashMap<>();
        idCounter = 0;
        this.history = history;
    }

    @Override
    public void addRegularTask(Task newTask){
        if (newTask.getId() != idCounter || newTask.getId() == 0){
            newTask.setId(++idCounter);
        }
        tasksData.put(idCounter, newTask);
        System.out.println("Задание добавлено! (ID - " + idCounter + ")");
    }

    @Override
    public void addEpicTask(EpicTask epicTask, ArrayList<SubTask> subTasks){
        epicTask.setId(++idCounter);
        if (subTasks != null) {
            for (SubTask subTask : subTasks) {
                subTask.setId(++idCounter);
                subTask.setBoundedTo(epicTask.getId());
                tasksData.put(idCounter, subTask);
                epicTask.addSubTaskId(idCounter);
            }
            System.out.println("Подзадачи добавлены!");
        }
        tasksData.put(epicTask.getId(), epicTask);
        updateEpicTaskStatus(epicTask.getId());
        System.out.println("\"Эпик\" задание добавлено! (ID - " + epicTask.getId() + ")");
    }

    @Override
    public void addEmptyEpicTask(EpicTask epicTask){
        if (epicTask.getId() != idCounter || epicTask.getId() == 0) {
            epicTask.setId(++idCounter);
        }
        tasksData.put(epicTask.getId(), epicTask);
        updateEpicTaskStatus(epicTask.getId());
        System.out.println("\"Эпик\" задание добавлено! (ID - " + idCounter + ")");
    }

    @Override
    public void addSubTask (SubTask newSubTask){
        if (newSubTask.getId() != idCounter || newSubTask.getId() == 0){
            newSubTask.setId(++idCounter);
        }

        tasksData.put(newSubTask.getId(), newSubTask);
        if (tasksData.containsKey(newSubTask.getBoundedTo())) {
            if (!((EpicTask) tasksData.get(newSubTask.getBoundedTo())).getFullSubTasksList().contains(newSubTask.getId())){
                ((EpicTask) tasksData.get(newSubTask.getBoundedTo())).addSubTaskId(idCounter);
            }
            updateEpicTaskStatus(newSubTask.getBoundedTo());
        }
    }

    @Override
    public void editRegularTask(Task editedTask){
            tasksData.remove(editedTask.getId());
            tasksData.put(editedTask.getId(), editedTask);
    }

    @Override
    public void editEpicTask(EpicTask editedEpicTask){
        tasksData.remove(editedEpicTask.getId());
        tasksData.put(editedEpicTask.getId(), editedEpicTask);
        updateEpicTaskStatus(editedEpicTask.getId());
    }

    @Override
    public void editSubTask(SubTask editedSubTask){
        tasksData.remove(editedSubTask.getId());
        tasksData.put(editedSubTask.getId(), editedSubTask);
        updateEpicTaskStatus(editedSubTask.getBoundedTo());
    }

    @Override
    public void deleteAllRegularTask(){
        Set<Integer> taskIDs = new HashSet<>(tasksData.keySet());
        for (Integer taskId: taskIDs){
            if (!(tasksData.get(taskId) instanceof SubTask) &&
                !(tasksData.get(taskId) instanceof EpicTask)){
                tasksData.remove(taskId);
                history.remove(taskId);
            }
        }
    }

    @Override
    public void deleteAllEpicTasks(){
        Set<Integer> taskIDs = new HashSet<>(tasksData.keySet());
        for (Integer taskId: taskIDs){
            if (tasksData.get(taskId) instanceof SubTask ||
                tasksData.get(taskId) instanceof EpicTask){
                tasksData.remove(taskId);
                history.remove(taskId);
            }
        }
    }

    @Override
    public void deleteAllSubTasks(){
        int boundedId;
        Set<Integer> taskIDs = new HashSet<>(tasksData.keySet());
        for (Integer taskId: taskIDs){
            if (tasksData.get(taskId) instanceof SubTask){
                boundedId = (((SubTask) tasksData.get(taskId)).getBoundedTo());
                tasksData.remove(taskId);
                history.remove(taskId);
                ((EpicTask) tasksData.get(boundedId)).removeSubTaskId(taskId);
                updateEpicTaskStatus(boundedId);
            }
        }
    }

    @Override
    public void deleteTaskByID(int taskId){
        if (tasksData.get(taskId) instanceof SubTask) {
            int updateId = ((SubTask) tasksData.get(taskId)).getBoundedTo();
            tasksData.remove(taskId);
            history.remove(taskId);
            ((EpicTask) tasksData.get(updateId)).removeSubTaskId(taskId);
            updateEpicTaskStatus(updateId);
        } else if (tasksData.get(taskId) instanceof EpicTask) {
            for (Integer subTaskId : subTasksIdentifiers((EpicTask) tasksData.get(taskId))) {
                tasksData.remove(subTaskId);
                history.remove(taskId);
            }
            tasksData.remove(taskId);
            history.remove(taskId);
        } else {
            tasksData.remove(taskId);
            history.remove(taskId);
        }
        System.out.println("Задача " + taskId + " удалена из списка.");
    }

    @Override
    public void deleteAllTasks() {
        if (tasksData.isEmpty()){
            System.out.println("Список обычных задач пуст.");
        } else {
            deleteAllRegularTask();
            deleteAllSubTasks();
            deleteAllEpicTasks();
            System.out.println("Все задачи удалены.");
        }
    }

    private ArrayList<Integer> subTasksIdentifiers(EpicTask epicTask){
        return epicTask.getFullSubTasksList();
    }

    @Override
    public void updateEpicTaskStatus(int epicTaskId) {
        ((EpicTask) tasksData.get(epicTaskId)).resetStatus();
            //я видел ваш комментарий о предыдущей строчке, но не знаю как сделать лучше =(
        if (((EpicTask) tasksData.get(epicTaskId)).getFullSubTasksList().size() > 0){
            for(Integer subTaskId : subTasksIdentifiers((EpicTask) tasksData.get(epicTaskId))){
                if (tasksData.containsKey(subTaskId) &&
                        !tasksData.get(subTaskId).getStatus().equals("NEW")){
                    tasksData.get(epicTaskId).setStatus("IN_PROGRESS");
                    break;
                }
            }
        } else {
            return;
        }

        if (tasksData.get(epicTaskId).getStatus().equals("IN_PROGRESS")){
            boolean isDone = true;
            for(Integer subTaskId : subTasksIdentifiers((EpicTask) tasksData.get(epicTaskId))){
                if (tasksData.containsKey(subTaskId) &&
                        !tasksData.get(subTaskId).getStatus().equals("DONE")){
                    isDone = false;
                    break;
                }
            }
            if (isDone){
                tasksData.get(epicTaskId).setStatus("DONE");
            }
        }
    }

    @Override
    public void printAllTasks() {
        for (Integer taskId : tasksData.keySet()) {
            if (tasksData.get(taskId) instanceof EpicTask){
                System.out.println("\nТип задачи: \"Эпик\" задача");
                tasksData.get(taskId).printInfo();
                for (Integer subTaskId : subTasksIdentifiers((EpicTask) tasksData.get(taskId))){
                    System.out.println("Подзадача \"Эпик\" задачи номер " + taskId + ":");
                    tasksData.get(subTaskId).printInfo();
                }
            } else if (!(tasksData.get(taskId) instanceof SubTask)) {
                System.out.println("\nТип задачи: Обычная задача");
                tasksData.get(taskId).printInfo();
            }
        }
    }

    @Override
    public void printInProgress() {
        System.out.println("Вывод текущих задач: ");
        for (Integer taskId : tasksData.keySet()) {
            if (tasksData.get(taskId).getStatus().equals("IN_PROGRESS")) {
                tasksData.get(taskId).printInfo();
            }
        }
    }

    @Override
    public void printAllDone() {
        System.out.println("Вывод текущих задач: ");
        for (Integer taskId : tasksData.keySet()) {
            if (tasksData.get(taskId).getStatus().equals("DONE")) {
                tasksData.get(taskId).printInfo();
            }
        }
    }


     @Override
    public Task getTask(int taskId){
        history.add(tasksData.get(taskId));
        return tasksData.get(taskId);
    }

    @Override
    public EpicTask getEpic(int taskId) {
        history.add(tasksData.get(taskId));
        return ((EpicTask) tasksData.get(taskId));
    }

    @Override
    public SubTask getSubtask(int taskId){
        history.add(tasksData.get(taskId));
        return ((SubTask) tasksData.get(taskId));
    }

    @Override
    public List<Task> getHistory(){
        return history.getHistory();
    }
}