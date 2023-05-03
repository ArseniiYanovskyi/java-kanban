import TaskData.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager{
    protected HashMap<Integer, Task> tasksData;
    protected int idCounter;
    protected HistoryManager history;
    TreeMap<Instant, Task> prioritizedByStartTimeTaskMap;
    protected static final long FOR_KEY_TO_PRIORITIZED_MAP_WHEN_NO_VALUE = 10413792000000l; // 01Jan2300 UTC+0
    public InMemoryTaskManager(HistoryManager history){
        tasksData = new HashMap<>();
        idCounter = 0;
        this.history = history;
        prioritizedByStartTimeTaskMap = new TreeMap<>();
    }

    public void removeTimeIfNotValidated(int taskId){
        //в задании не сказано что именно нужно делать при валидизации,
        //пока что просто сделаю удаление времени при ошибке валидизации
        if (tasksData.get(taskId).getOptionalOfStartTime().isEmpty()){
            return;
        }
        if (tasksData.get(taskId).getDurationOfMillis() == 0){
            tasksData.get(taskId).resetTimeValues();
            return;
        }

        List<Task> crossingTimeTasks = prioritizedByStartTimeTaskMap.keySet().stream()
                .map(key -> prioritizedByStartTimeTaskMap.get(key))
                .filter(task -> task.getOptionalOfStartTime().isPresent() && task.getDurationOfMillis() != 0)
                .filter(task -> {
                        if (tasksData.get(taskId).getStartTimeOfMillis() > task.getStartTimeOfMillis()) {
                                return (tasksData.get(taskId).getStartTimeOfMillis()
                                        - task.getStartTimeOfMillis()
                                        - task.getDurationOfMillis()) < 0;
                            } else {
                                return (task.getStartTimeOfMillis()
                                        - tasksData.get(taskId).getStartTimeOfMillis()
                                        - tasksData.get(taskId).getDurationOfMillis()) < 0;
                            }})
                .collect(Collectors.toList());
        if (crossingTimeTasks.isEmpty()){
            return;
        }
        System.out.println("Validation of task with ID " + taskId + " failed, time crossing with:" );
        for (Task task : crossingTimeTasks){
            task.printInfo();
        }
        tasksData.get(taskId).resetTimeValues();
    }

    protected void addTaskToPrioritizedMap(int taskId){
        if (tasksData.get(taskId).getOptionalOfStartTime().isPresent()) {
            prioritizedByStartTimeTaskMap.put(tasksData.get(taskId).getOptionalOfStartTime().get(), tasksData.get(taskId));
        } else {
            prioritizedByStartTimeTaskMap.put(
                    Instant.ofEpochMilli(FOR_KEY_TO_PRIORITIZED_MAP_WHEN_NO_VALUE + tasksData.get(taskId).getId()),
                    tasksData.get(taskId));
        }
    }

    public TreeSet<Task> getPrioritizedTasks() {
        //в задании советуют использовать эту структуру, или изменить её к TreeMap?
        TreeSet<Task> returningTreeSet = new TreeSet<>();
        for (Instant instantKey : prioritizedByStartTimeTaskMap.keySet()){
            returningTreeSet.add(prioritizedByStartTimeTaskMap.get(instantKey));
        }
        return returningTreeSet;
    }

    protected void removeTaskFromPrioritizedMap(int taskId){
        if (!tasksData.containsKey(taskId)){
            return;
        }
        if (tasksData.get(taskId).getOptionalOfStartTime().isPresent()) {
            prioritizedByStartTimeTaskMap.remove(tasksData.get(taskId).getOptionalOfStartTime().get());
        } else {
            prioritizedByStartTimeTaskMap.remove(
                    Instant.ofEpochMilli(FOR_KEY_TO_PRIORITIZED_MAP_WHEN_NO_VALUE + tasksData.get(taskId).getId())
            );
        }
    }

    public class ComparatorForEpicTimers implements Comparator<Instant>{
        @Override
        public int compare (Instant o1, Instant o2){
            return (int) (o1.toEpochMilli() - o2.toEpochMilli());
        }
    }
    protected void calculateEpicTaskTimers(int taskId){
        Comparator<? super Instant> comparatorForEpicTimes = new ComparatorForEpicTimers();

        Optional<Instant> earlierStart = subTasksIdentifiers((EpicTask) tasksData.get(taskId)).stream()
                .filter(subTaskId -> tasksData.containsKey(subTaskId))
                .map(subTaskId -> tasksData.get(subTaskId))
                .map(task -> task.getOptionalOfStartTime())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .min(comparatorForEpicTimes);

        tasksData.get(taskId).setStartTime((earlierStart.isPresent()) ? earlierStart.get() : null);

        Optional<Instant> latestFinish = subTasksIdentifiers((EpicTask) tasksData.get(taskId)).stream()
                .filter(subTaskId -> tasksData.containsKey(subTaskId))
                .map(subTaskId -> tasksData.get(subTaskId))
                .map(task -> task.getOptionalOfEndTime())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .max(comparatorForEpicTimes);

        ((EpicTask) tasksData.get(taskId)).setEndTimeForEpic((latestFinish.isPresent()) ? latestFinish.get() : null);
    }

    @Override
    public void addRegularTask(Task newTask){
        if (newTask.getId() != idCounter || newTask.getId() == 0){
            newTask.setId(++idCounter);
        }
        tasksData.put(idCounter, newTask);
        removeTimeIfNotValidated(idCounter);
        addTaskToPrioritizedMap(idCounter);
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
                removeTimeIfNotValidated(idCounter);
                addTaskToPrioritizedMap(idCounter);
            }
            System.out.println("Подзадачи добавлены!");
        }
        tasksData.put(epicTask.getId(), epicTask);
        updateEpicTaskStatus(epicTask.getId());
        calculateEpicTaskTimers(epicTask.getId());
        addTaskToPrioritizedMap(epicTask.getId());
        System.out.println("\"Эпик\" задание добавлено! (ID - " + epicTask.getId() + ")");
    }

    @Override
    public void addEmptyEpicTask(EpicTask epicTask){
        if (epicTask.getId() != idCounter || epicTask.getId() == 0) {
            epicTask.setId(++idCounter);
        }
        tasksData.put(epicTask.getId(), epicTask);
        updateEpicTaskStatus(epicTask.getId());
        addTaskToPrioritizedMap(epicTask.getId());
        System.out.println("\"Эпик\" задание добавлено! (ID - " + idCounter + ")");
    }

    @Override
    public void addSubTask (SubTask newSubTask){
        if (newSubTask.getId() != idCounter || newSubTask.getId() == 0){
            newSubTask.setId(++idCounter);
        }

        tasksData.put(newSubTask.getId(), newSubTask);
        removeTimeIfNotValidated(newSubTask.getId());
        addTaskToPrioritizedMap(newSubTask.getId());

        if (tasksData.containsKey(newSubTask.getBoundedTo())) {
            if (!((EpicTask) tasksData.get(newSubTask.getBoundedTo())).getFullSubTasksList().contains(newSubTask.getId())){
                ((EpicTask) tasksData.get(newSubTask.getBoundedTo())).addSubTaskId(idCounter);
            }
            updateEpicTaskStatus(newSubTask.getBoundedTo());
            calculateEpicTaskTimers(newSubTask.getBoundedTo());
        }
    }

    @Override
    public void editRegularTask(Task editedTask){
        tasksData.remove(editedTask.getId());
        tasksData.put(editedTask.getId(), editedTask);
        removeTimeIfNotValidated(editedTask.getId());
        addTaskToPrioritizedMap(editedTask.getId());
    }

    @Override
    public void editEpicTask(EpicTask editedEpicTask){
        tasksData.remove(editedEpicTask.getId());
        tasksData.put(editedEpicTask.getId(), editedEpicTask);
        updateEpicTaskStatus(editedEpicTask.getId());
        calculateEpicTaskTimers(editedEpicTask.getId());
        addTaskToPrioritizedMap(editedEpicTask.getId());
    }

    @Override
    public void editSubTask(SubTask editedSubTask){
        tasksData.remove(editedSubTask.getId());
        tasksData.put(editedSubTask.getId(), editedSubTask);
        updateEpicTaskStatus(editedSubTask.getBoundedTo());
        calculateEpicTaskTimers(editedSubTask.getBoundedTo());
        removeTimeIfNotValidated(editedSubTask.getId());
        addTaskToPrioritizedMap(editedSubTask.getId());
    }

    @Override
    public void deleteAllRegularTask(){
        Set<Integer> taskIDs = new HashSet<>(tasksData.keySet());
        for (Integer taskId: taskIDs){
            if (!(tasksData.get(taskId) instanceof SubTask) &&
                !(tasksData.get(taskId) instanceof EpicTask)){
                removeTaskFromPrioritizedMap(taskId);
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
                removeTaskFromPrioritizedMap(taskId);
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
                removeTaskFromPrioritizedMap(taskId);
                tasksData.remove(taskId);
                history.remove(taskId);
                ((EpicTask) tasksData.get(boundedId)).removeSubTaskId(taskId);
                updateEpicTaskStatus(boundedId);
                calculateEpicTaskTimers(boundedId);
            }
        }
    }

    @Override
    public void deleteTaskByID(int taskId){
        if (!tasksData.containsKey(taskId)){
            return;
        }
        removeTaskFromPrioritizedMap(taskId);
        if (tasksData.get(taskId) instanceof SubTask) {
            int updateId = ((SubTask) tasksData.get(taskId)).getBoundedTo();
            tasksData.remove(taskId);
            ((EpicTask) tasksData.get(updateId)).removeSubTaskId(taskId);
            updateEpicTaskStatus(updateId);
            calculateEpicTaskTimers(updateId);
        } else if (tasksData.get(taskId) instanceof EpicTask) {
            for (Integer subTaskId : subTasksIdentifiers((EpicTask) tasksData.get(taskId))) {
                tasksData.remove(subTaskId);
                history.remove(taskId);
            }
            tasksData.remove(taskId);
        } else {
            tasksData.remove(taskId);
        }
        history.remove(taskId);
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
        System.out.println();
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