package services;

import HistoryData.*;
import TaskData.*;
import models.*;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private HistoryLinkedList historyLinkedList;

    public InMemoryHistoryManager(){
        historyLinkedList = new HistoryLinkedList();
    }
    @Override
    public void add(Task task){
        if (task != null) {
            historyLinkedList.linkLast(task);
        }
    }

    @Override
    public void remove(int id){
        historyLinkedList.removeById(id);
    }

    @Override
    public List<Task> getHistory(){
        return historyLinkedList.getTasks();
    }

    class HistoryLinkedList {
        public HashMap<Integer, HistoryNode> historyNodeHashMap;
        private HistoryNode headNode;
        private HistoryNode tailNode;
        public HistoryLinkedList (){
            historyNodeHashMap = new HashMap<>();
            headNode = null;
            tailNode = null;
        }

        public void removeById(int id){
            if (historyNodeHashMap.containsKey(id)){
                removeNode(historyNodeHashMap.get(id));
            }
        }

        public void removeNode(HistoryNode historyNode) {
            if (historyNode == null) {
                return;
            }
            if (headNode.equals(historyNode)) {
                headNode = historyNode.nextNode;
                if (historyNode.nextNode != null) {
                    historyNode.nextNode.prevNode = null;
                }
            } else if (tailNode.equals(historyNode)) {
                tailNode = historyNode.prevNode;
                if (historyNode.prevNode != null) {
                    historyNode.prevNode.nextNode = null;
                }
            } else {
                historyNode.nextNode.prevNode = historyNode.prevNode;
                historyNode.prevNode.nextNode = historyNode.nextNode;
            }
            historyNodeHashMap.remove(historyNode.data.getId());
            }

        public void linkLast(Task task){
            if(historyNodeHashMap.isEmpty()) {
                historyNodeHashMap.put(task.getId(), new HistoryNode(null, task, null));
                headNode = historyNodeHashMap.get(task.getId());
                tailNode = historyNodeHashMap.get(task.getId());
            } else if (historyNodeHashMap.containsKey(task.getId())){
                HistoryNode thisNode = historyNodeHashMap.get(task.getId());

                if (thisNode.equals(tailNode)){
                    return;
                }
                if (thisNode.equals(headNode)){
                    headNode = thisNode.nextNode;
                }

                thisNode.nextNode.prevNode = thisNode.prevNode;

                if (thisNode.prevNode != null) {
                    thisNode.prevNode.nextNode = thisNode.nextNode;
                }

                thisNode.nextNode = null;
                thisNode.prevNode = tailNode;
                tailNode.nextNode = thisNode;
                tailNode = thisNode;
            } else {
                historyNodeHashMap.put(task.getId(), new HistoryNode(tailNode, task, null));
                tailNode.nextNode = historyNodeHashMap.get(task.getId());
                tailNode = historyNodeHashMap.get(task.getId());
            }
        }


        public ArrayList<Task> getTasks(){

            ArrayList<Task> returningList = new ArrayList<>();

            if (!historyNodeHashMap.isEmpty()){
                HistoryNode writingData = headNode;
                while (writingData != null) {
                    returningList.add(writingData.data);
                    writingData = writingData.nextNode;
                }
            }

            return returningList;
        }
    }
}
