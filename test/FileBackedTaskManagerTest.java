import TaskData.EpicTask;
import TaskData.SubTask;
import TaskData.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManagerTest extends TaskManagerTests<FileBackedTaskManager>{
    @BeforeEach
    public void creatingTaskManager() {
        File file = new File("TaskDataFile.csv");
        file.delete();
        super.taskManager = (FileBackedTaskManager) Managers.getFileBacked("TaskDataFile.csv");
    }

    @Test
    public void managerShouldWriteAndReadDataFileCorrect(){
        File file = new File("TestForLoadingTaskDataFile.csv");
        file.delete();
        super.taskManager = (FileBackedTaskManager) Managers.getFileBacked("TestForLoadingTaskDataFile.csv");

        Task newTask = new Task("newTaskTitle", "newTaskDesc");
        Task anotherTask = new Task("anotherTaskTitle", "anotherTaskDesc");

        taskManager.addRegularTask(newTask);
        taskManager.addRegularTask(anotherTask);

        EpicTask newEpicTask = new EpicTask("newEpicTaskTitle", "newEpicTaskDesc");

        taskManager.addEmptyEpicTask(newEpicTask);

        EpicTask anotherEpicTask = new EpicTask("anotherEpicTaskTitle", "anotherEpicTaskDesc");
        SubTask newSubTask = new SubTask("newSubTaskTitle", "newSubTaskDesc", 4);
        SubTask anotherSubTask = new SubTask("anotherSubTaskTitle", "anotherSubTaskDesc", 4);

        ArrayList<SubTask> subTaskArrayList = new ArrayList<>();
        subTaskArrayList.add(newSubTask);
        subTaskArrayList.add(anotherSubTask);

        taskManager.addEpicTask(anotherEpicTask, subTaskArrayList);

        Task forHistoryTestTask = taskManager.getTask(2);
        SubTask forHistoryTestSubTask = taskManager.getSubtask(6);
        EpicTask forHistoryTestEpicTask = taskManager.getEpic(3);

        super.taskManager = (FileBackedTaskManager) Managers.getFileBacked("TestForLoadingTaskDataFile.csv");

        Assertions.assertEquals(5, taskManager.getSubtask(5).getId());
        Assertions.assertEquals("newSubTaskTitle", taskManager.getSubtask(5).getTitle());
        Assertions.assertEquals("newSubTaskDesc", taskManager.getSubtask(5).getDescription());
        Assertions.assertEquals(4, taskManager.getSubtask(5).getBoundedTo());

        List<Task> testHistory = taskManager.getHistory();
        Assertions.assertEquals(2, testHistory.get(0).getId());
        Assertions.assertEquals(6, testHistory.get(1).getId());
        Assertions.assertEquals(3, testHistory.get(2).getId());

        file.delete();
    }
}
