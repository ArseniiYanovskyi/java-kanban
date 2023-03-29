import TaskData.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private HistoryLinkedList historyLinkedList;
    public InMemoryHistoryManager(){
        historyLinkedList = new HistoryLinkedList();
    }
    @Override
    public void add(Task task){
        /*
        подскажите пожалуйста, чем эта реализация плоха,
        ведь по теории её время выполнения равняется О(1)
        создать вместо списка из 7 строчки LinkedHashMap
        и удалять/добавлять таким образом

        if (reviewHistory.containsKey(task.getId())){
            this.remove(task.getId());
        }
        reviewHistory.put(task.getId(), task);
        */

        historyLinkedList.linkLast(task);
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
            removeNode(historyNodeHashMap.get(id));
        }

        public void removeNode(HistoryNode historyNode){
            if (headNode.equals(historyNode)){
                headNode = historyNode.nextNode;
            }

            if (tailNode.equals(historyNode)){
                tailNode = historyNode.prevNode;
            }

            if (historyNode.prevNode != null && historyNode.nextNode != null){
                historyNode.nextNode.prevNode = historyNode.prevNode;
                historyNode.prevNode.nextNode = historyNode.nextNode;
            } else if (historyNode.prevNode == null && historyNode.nextNode != null) {
                historyNode.nextNode.prevNode = null;
            } else if (historyNode.prevNode != null && historyNode.nextNode == null){
                historyNode.prevNode.nextNode = null;
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
                if (headNode.equals(thisNode)){
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

        //он должен возвращать в таком порядке, или в обратном?
        public ArrayList<Task> getTasks(){
            ArrayList<Task> returningList = new ArrayList<>();

            if (!historyNodeHashMap.isEmpty()){
                HistoryNode writingData = headNode;
                while (writingData.hasNext()) {
                    returningList.add(writingData.data);
                    writingData = writingData.nextNode;
                }
                returningList.add(writingData.data);
            }

            return returningList;
        }
    }
}
