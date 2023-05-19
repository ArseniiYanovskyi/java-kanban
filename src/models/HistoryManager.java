package models;

import TaskData.*;

import java.util.List;

public interface HistoryManager {
    public void add(Task task);
    public void remove(int id);
    public List<Task> getHistory();
}
