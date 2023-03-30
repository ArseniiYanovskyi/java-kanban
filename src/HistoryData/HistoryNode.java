package HistoryData;

import TaskData.*;

public class HistoryNode {
    public HistoryNode prevNode;
    public Task data;
    public HistoryNode nextNode;
    public HistoryNode( HistoryNode prevNode, Task task, HistoryNode nextNode){
        this.prevNode = prevNode;
        this.data = task;
        this.nextNode = nextNode;
    }

    @Override
    public boolean equals(Object o){
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != o.getClass()) {
            return false;
        }

        return (this.data.equals(((HistoryNode) o).data) &&
                this.nextNode.equals(((HistoryNode) o).nextNode) &&
                this.prevNode.equals(((HistoryNode) o).prevNode));
    }
}