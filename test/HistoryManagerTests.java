import TaskData.Task;
import models.HistoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.InMemoryHistoryManager;

import java.util.ArrayList;
import java.util.List;

public class HistoryManagerTests {
    private HistoryManager historyManagerTest;

    @BeforeEach
    public void creationOfTestManager(){
        historyManagerTest = new InMemoryHistoryManager();
    }

    @DisplayName("Should return empty ArrayList when history empty")
    @Test
    public void shouldReturnEmptyArrayListWhenHistoryEmpty() {
        Assertions.assertEquals(new ArrayList<>(), historyManagerTest.getHistory());

        Task testingTask = new Task("testTaskTitle", "testTaskDesc");
        testingTask.setId(123);

        historyManagerTest.add(testingTask);
        Assertions.assertNotEquals(new ArrayList<>(), historyManagerTest.getHistory());

        historyManagerTest.remove(123);
        Assertions.assertEquals(new ArrayList<>(), historyManagerTest.getHistory());
    }

    @DisplayName("Should correct amount of elements")
    @Test
    public void shouldReturnCorrectAmountOfElements() {
        Task testingTask = new Task("testTaskTitle", "testTaskDesc");
        testingTask.setId(123);

        historyManagerTest.add(testingTask);
        Assertions.assertEquals(1, historyManagerTest.getHistory().size());

        Task anotherTestingTask = new Task("anotherTestTaskTitle", "anotherTestTaskDesc");
        anotherTestingTask.setId(1234);

        historyManagerTest.add(anotherTestingTask);
        Assertions.assertEquals(2, historyManagerTest.getHistory().size());

        historyManagerTest.add(testingTask);
        Assertions.assertEquals(2, historyManagerTest.getHistory().size(),
                "added task twice");
    }

    @DisplayName("Should return correct sequence after node deleting")
    @Test
    public void shouldReturnCorrectSequence(){
        Task testingTask = new Task("testTaskTitle", "testTaskDesc");
        testingTask.setId(123);

        Task anotherTestingTask = new Task("anotherTestTaskTitle", "anotherTestTaskDesc");
        anotherTestingTask.setId(1234);

        Task andAnotherTestingTask = new Task("anotherTestTaskTitle", "anotherTestTaskDesc");
        andAnotherTestingTask.setId(12345);

        Task andOneMoreTestingTask = new Task("anotherTestTaskTitle", "anotherTestTaskDesc");
        andOneMoreTestingTask.setId(123456);

        historyManagerTest.add(testingTask);
        historyManagerTest.add(anotherTestingTask);
        historyManagerTest.add(andAnotherTestingTask);
        historyManagerTest.add(andOneMoreTestingTask);


        List<Task> testHistory = historyManagerTest.getHistory();
        Assertions.assertEquals(123, testHistory.get(0).getId());
        Assertions.assertEquals(1234, testHistory.get(1).getId());
        Assertions.assertEquals(12345, testHistory.get(2).getId());
        Assertions.assertEquals(123456, testHistory.get(3).getId());

        historyManagerTest.remove(12345);
        testHistory = historyManagerTest.getHistory();
        Assertions.assertEquals(123, testHistory.get(0).getId());
        Assertions.assertEquals(1234, testHistory.get(1).getId());
        Assertions.assertEquals(123456, testHistory.get(2).getId());

        historyManagerTest.remove(123);
        testHistory = historyManagerTest.getHistory();
        Assertions.assertEquals(1234, testHistory.get(0).getId());
        Assertions.assertEquals(123456, testHistory.get(1).getId());

        historyManagerTest.remove(12356);
        testHistory = historyManagerTest.getHistory();
        Assertions.assertEquals(1234, testHistory.get(0).getId());
    }
}
