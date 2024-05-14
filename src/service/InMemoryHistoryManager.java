package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> history = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        Node node = history.remove(task.getId());

        if (node != null) {
            removeNode(node);
        }

        history.put(task.getId(), linkLast(task));

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
        Node lastOld = last;
        Node newNode = new Node(task, null, lastOld);
        last = newNode;
        if (lastOld == null) {
            this.first = newNode;
        } else {
            lastOld.next = newNode;
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
            first = null;
            last = null;
        } else if (prevNode == null) {
            nextNode.prev = null;
            first = nextNode;
        } else if (nextNode == null) {
            prevNode.next = null;
            last = prevNode;
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
