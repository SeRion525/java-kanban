package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> history = new HashMap<>();
    private Node first;
    private Node last;

    public InMemoryHistoryManager() {

    }

    @Override
    public void add(Task task) {
        Node node = history.get(task.getId());

        if(node != null) {
            node.item = task;
        } else {
            history.put(task.getId(), linkLast(task));
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node removedNode = history.remove(id);

        if (removedNode != null) {
            removeNode(removedNode);
        }
    }

    private Node linkLast(Task task) {
        Node last = this.last;
        Node newNode = new Node(task, null, last);
        this.last = newNode;
        if (last == null) {
            this.first = newNode;
        } else {
            last.next = newNode;
        }

        return newNode;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();

        for (Node curr = first; curr != null; curr = curr.next) {
           tasks.add(curr.item);
        }

        return tasks;
    }

    private void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;

        if (prevNode == null && nextNode == null) {
            this.first = null;
            this.last = null;
        } else if (prevNode == null) {
            nextNode.prev = null;
            this.first = nextNode;
        } else if (nextNode == null){
            prevNode.next = null;
            this.last = prevNode;
        } else {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
    }

    private static class Node {
        private Task item;
        private Node next;
        private Node prev;

        public Node(Task item, Node next, Node prev) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }
    }
}
