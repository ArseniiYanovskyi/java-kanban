import TaskData.*;
import models.Managers;
import org.junit.jupiter.api.*;
import server.HttpTaskManager;
import server.KVServer;
import server.KVTaskClient;

import java.io.IOException;
import java.util.TreeSet;

public class HttpTaskManagerTests{
    HttpTaskManager taskManager;
    static KVServer kvServer;

    @BeforeEach
    public void creatingTaskManager() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = (HttpTaskManager) Managers.getDefault();
    }

    @AfterEach
    public void deleteAllTasksAndCloseServer(){
        taskManager.deleteAllTasks();
        kvServer.stop();
    }

    @Test
    public void shouldLoadCorrectly() throws IOException, InterruptedException {
        KVTaskClient kvTaskClient = new KVTaskClient();

        Task newTask = new Task("newTaskTitle", "newTaskDesc", 1);

        taskManager = new HttpTaskManager(kvTaskClient);

        taskManager.addRegularTask(newTask);
        Assertions.assertEquals(1, taskManager.getTask(newTask.getId()).getId());
        Assertions.assertEquals("newTaskTitle", taskManager.getTask(newTask.getId()).getTitle());
        Assertions.assertEquals("newTaskDesc", taskManager.getTask(newTask.getId()).getDescription());

        taskManager = new HttpTaskManager(kvTaskClient);
        Assertions.assertEquals(1, taskManager.getTask(newTask.getId()).getId());
        Assertions.assertEquals("newTaskTitle", taskManager.getTask(newTask.getId()).getTitle());
        Assertions.assertEquals("newTaskDesc", taskManager.getTask(newTask.getId()).getDescription());
    }

    @Test
    public void shouldAddRegularTaskCorrectly(){
        Task newTask = new Task("newTaskTitle", "newTaskDesc");

        taskManager.addRegularTask(newTask);
        Assertions.assertEquals(1, taskManager.getTask(1).getId());
        Assertions.assertEquals("newTaskTitle", taskManager.getTask(1).getTitle());
        Assertions.assertEquals("newTaskDesc", taskManager.getTask(1).getDescription());

        Task editingTask = taskManager.getTask(1);
        editingTask.setTitle("editedTitle");
        editingTask.setDescription("editedDescription");

        taskManager.addRegularTask(editingTask);
        Assertions.assertEquals(1, taskManager.getTask(1).getId());
        Assertions.assertEquals("editedTitle", taskManager.getTask(1).getTitle());
        Assertions.assertEquals("editedDescription", taskManager.getTask(1).getDescription());
    }

    @Test
    public void shouldAddEpicTaskCorrectly(){
        EpicTask newTask = new EpicTask("newTaskTitle", "newTaskDesc");

        taskManager.addEmptyEpicTask(newTask);
        Assertions.assertEquals(1, taskManager.getTask(1).getId());
        Assertions.assertEquals("newTaskTitle", taskManager.getTask(1).getTitle());
        Assertions.assertEquals("newTaskDesc", taskManager.getTask(1).getDescription());

        EpicTask editingTask = taskManager.getEpic(1);
        editingTask.setTitle("editedTitle");
        editingTask.setDescription("editedDescription");

        taskManager.addEmptyEpicTask(editingTask);
        Assertions.assertEquals(1, taskManager.getTask(1).getId());
        Assertions.assertEquals("editedTitle", taskManager.getTask(1).getTitle());
        Assertions.assertEquals("editedDescription", taskManager.getTask(1).getDescription());
    }

    @Test
    public void shouldAddSubTaskCorrectly(){
        EpicTask newEpicTask = new EpicTask("EpicTaskTitle", "EpicTaskDesc");
        taskManager.addEmptyEpicTask(newEpicTask);

        SubTask newTask = new SubTask("newTaskTitle", "newTaskDesc", 1);

        taskManager.addSubTask(newTask);
        Assertions.assertEquals(2, taskManager.getTask(2).getId());
        Assertions.assertEquals("newTaskTitle", taskManager.getTask(2).getTitle());
        Assertions.assertEquals("newTaskDesc", taskManager.getTask(2).getDescription());

        SubTask editingTask = taskManager.getSubtask(2);
        editingTask.setTitle("editedTitle");
        editingTask.setDescription("editedDescription");

        taskManager.addSubTask(editingTask);
        Assertions.assertEquals(2, taskManager.getTask(2).getId());
        Assertions.assertEquals("editedTitle", taskManager.getTask(2).getTitle());
        Assertions.assertEquals("editedDescription", taskManager.getTask(2).getDescription());
    }

    @Test
    public void shouldUpdateEpicTaskStatusCorrectly() {
        EpicTask newEpicTask = new EpicTask("EpicTaskTitle", "EpicTaskDesc");
        taskManager.addEmptyEpicTask(newEpicTask);

        EpicTask recievingEpicTask = taskManager.getEpic(1);
        Assertions.assertEquals("NEW", recievingEpicTask.getStatus());

        SubTask newTask = new SubTask("newTaskTitle", "newTaskDesc", 1);

        taskManager.addSubTask(newTask);

        recievingEpicTask = taskManager.getEpic(1);
        Assertions.assertEquals("NEW", recievingEpicTask.getStatus());

        SubTask editingTask = taskManager.getSubtask(2);
        editingTask.setStatus("IN_PROGRESS");
        taskManager.addSubTask(editingTask);

        recievingEpicTask = taskManager.getEpic(1);
        Assertions.assertEquals("IN_PROGRESS", recievingEpicTask.getStatus());

        editingTask = taskManager.getSubtask(2);
        editingTask.setStatus("DONE");
        taskManager.addSubTask(editingTask);

        recievingEpicTask = taskManager.getEpic(1);
        Assertions.assertEquals("DONE", recievingEpicTask.getStatus());

    }

    @Test
    public void shouldDeleteCorrectly() {
        Task newTask = new Task("newTaskTitle", "newTaskDesc", 1);
        taskManager.addRegularTask(newTask);

        EpicTask newEpicTask = new EpicTask("EpicTaskTitle", "EpicTaskDesc", 2);
        taskManager.addEmptyEpicTask(newEpicTask);

        SubTask newSubTask = new SubTask("newTaskTitle", "newTaskDesc", 2, 3);
        taskManager.addSubTask(newSubTask);

        Assertions.assertEquals(newTask, taskManager.getTask(1));

        taskManager.deleteTaskByID(1);
        Assertions.assertNull(taskManager.getTask(1));

        taskManager.addRegularTask(newTask);
        taskManager.deleteAllTasks();
        Assertions.assertEquals(new TreeSet<>(), taskManager.getPrioritizedTasks());


    }

}
