package TaskData;

public class HistoryNode {
    public HistoryNode prevNode;
    public Task data;
    public HistoryNode nextNode;
    public HistoryNode( HistoryNode prevNode, Task task, HistoryNode nextNode){
        this.prevNode = prevNode;
        this.data = task;
        this.nextNode = nextNode;
    }

    public boolean hasNext(){
        return nextNode != null;
    }

    @Override
    public boolean equals(Object o){
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != o.getClass()) {
            return false;
        }
        if (data.getId() == ((HistoryNode) o).data.getId() ){
            return true;
        } else {
            return false;
        }
    }
}