import TaskData.EpicTask;
import TaskData.SubTask;
import models.Managers;
import models.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

public class EpicTaskTests {
    private TaskManager inMemoryTestTaskManager;

    @BeforeEach
    void settingDefaultTaskManagerWithEmptyEpic() throws IOException, InterruptedException {
        inMemoryTestTaskManager = Managers.getDefault();

        EpicTask testEpicTask = new EpicTask("testEpicTitle", "TestEpicDesc");
        inMemoryTestTaskManager.addEmptyEpicTask(testEpicTask);
    }

    @DisplayName("Should return empty ArrayList when there are no SubTasks.")
    @Test
    public void shouldEpicReturnNullForEmptySubTaskList(){
        Assertions.assertEquals(new ArrayList<>(),
                inMemoryTestTaskManager.getEpic(1).getFullSubTasksList(), "Should return new ArrayList");
    }

    @DisplayName("Epic Task status should be NEW when all subtasks are NEW or there is no subtask")
    @Test
    public void shouldReturnNewEpicStatus(){
        Assertions.assertEquals("NEW", inMemoryTestTaskManager.getEpic(1).getStatus(),
                "NEW status when haven't any subtask");

        SubTask testSubTaskFirst = new SubTask("FirstSubTaskTitle", "FirstSubTaskDesc",  1);
        inMemoryTestTaskManager.addSubTask(testSubTaskFirst);

        SubTask testSubTaskSecond = new SubTask("SecondSubTaskTitle", "SecondSubTaskDesc",  1);
        inMemoryTestTaskManager.addSubTask(testSubTaskSecond);

        SubTask testSubTaskThird = new SubTask("ThirdSubTaskTitle", "ThirdSubTaskDesc",  1);
        inMemoryTestTaskManager.addSubTask(testSubTaskThird);

        Assertions.assertEquals("NEW", inMemoryTestTaskManager.getEpic(1).getStatus(),
                "NEW status when all subtasks are NEW");
    }

    @DisplayName("Epic Task status should be IN_PROGRESS while there are all subtasks now equals or NEW or DONE")
    @Test
    public void shouldReturnInProgressEpicStatus(){
        SubTask testSubTaskFirst = new SubTask("FirstSubTaskTitle", "FirstSubTaskDesc",  1);
        testSubTaskFirst.setStatus("IN_PROGRESS");
        inMemoryTestTaskManager.addSubTask(testSubTaskFirst);

        Assertions.assertEquals("IN_PROGRESS", inMemoryTestTaskManager.getEpic(1).getStatus(),
                "Epic status should be in IN_PROGRESS");

        SubTask testSubTaskSecond = new SubTask("SecondSubTaskTitle", "SecondSubTaskDesc",  1);
        testSubTaskSecond.setStatus("IN_PROGRESS");
        inMemoryTestTaskManager.addSubTask(testSubTaskSecond);

        Assertions.assertEquals("IN_PROGRESS", inMemoryTestTaskManager.getEpic(1).getStatus(),
                "Epic status should be in IN_PROGRESS");

        SubTask testSubTaskThird = new SubTask("ThirdSubTaskTitle", "ThirdSubTaskDesc",  1);
        testSubTaskThird.setStatus("IN_PROGRESS");
        inMemoryTestTaskManager.addSubTask(testSubTaskThird);

        Assertions.assertEquals("IN_PROGRESS", inMemoryTestTaskManager.getEpic(1).getStatus(),
                "Epic status should be in IN_PROGRESS");
    }

    @DisplayName("Epic Task status should be DONE while there are all subtasks equals DONE")
    @Test
    public void shouldReturnDoneEpicStatus(){
        SubTask testSubTaskFirst = new SubTask("FirstSubTaskTitle", "FirstSubTaskDesc",  1);
        testSubTaskFirst.setStatus("DONE");
        inMemoryTestTaskManager.addSubTask(testSubTaskFirst);

        Assertions.assertEquals("DONE", inMemoryTestTaskManager.getEpic(1).getStatus(),
                "Epic Task status should be DONE");

        SubTask testSubTaskSecond = new SubTask("SecondSubTaskTitle", "SecondSubTaskDesc",  1);
        testSubTaskSecond.setStatus("DONE");
        inMemoryTestTaskManager.addSubTask(testSubTaskSecond);

        Assertions.assertEquals("DONE", inMemoryTestTaskManager.getEpic(1).getStatus(),
                "Epic Task status should be DONE");

        SubTask testSubTaskThird = new SubTask("ThirdSubTaskTitle", "ThirdSubTaskDesc",  1);
        testSubTaskThird.setStatus("DONE");
        inMemoryTestTaskManager.addSubTask(testSubTaskThird);

        Assertions.assertEquals("DONE", inMemoryTestTaskManager.getEpic(1).getStatus(),
                "Epic Task status should be DONE");
    }

    @DisplayName("Epic Task status should be IN_PROGRESS while there are all subtasks now equals or NEW or DONE")
    @Test
    public void shouldReturnEpicInProgressForVariableSubTasksStatuses(){
        SubTask testSubTaskFirst = new SubTask("FirstSubTaskTitle", "FirstSubTaskDesc",  1);
        inMemoryTestTaskManager.addSubTask(testSubTaskFirst);


        SubTask testSubTaskSecond = new SubTask("SecondSubTaskTitle", "SecondSubTaskDesc",  1);
        testSubTaskSecond.setStatus("IN_PROGRESS");
        inMemoryTestTaskManager.addSubTask(testSubTaskSecond);

        Assertions.assertEquals("IN_PROGRESS", inMemoryTestTaskManager.getEpic(1).getStatus(),
                "Epic status should be IN_PROGRESS");

        SubTask testSubTaskThird = new SubTask("ThirdSubTaskTitle", "ThirdSubTaskDesc",  1);
        testSubTaskThird.setStatus("DONE");
        inMemoryTestTaskManager.addSubTask(testSubTaskThird);

        Assertions.assertEquals("IN_PROGRESS", inMemoryTestTaskManager.getEpic(1).getStatus(),
                "Epic status should be IN_PROGRESS");
    }
}
