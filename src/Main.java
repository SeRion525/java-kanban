import model.EpicTask;
import model.Status;
import model.SubTask;
import model.Task;
import service.FileBackedTaskManager;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager();
        taskManager.createTask(new Task("task1", "task1 discr", Status.NEW));
        taskManager.createTask(new Task("task2", "task2 discr", Status.NEW));
        taskManager.createEpicTask(new EpicTask("epicTask1", "epicTask1 discr"));
        taskManager.createEpicTask(new EpicTask("epicTask2", "epicTask2 discr"));
        taskManager.createSubTask(new SubTask("subTask1", "subTask1 discr", Status.NEW, 2));
        taskManager.createSubTask(new SubTask("subTask2", "subTask2 discr", Status.NEW, 3));
        taskManager.createSubTask(new SubTask("subTask3", "subTask3 discr", Status.NEW, 3));

        FileBackedTaskManager newTaskManager = FileBackedTaskManager.loadFromFile(new File("resources/task.csv"));
        System.out.println(taskManager.getAllTasks().equals(newTaskManager.getAllTasks()));
        System.out.println(taskManager.getAllEpicTasks().equals(newTaskManager.getAllEpicTasks()));
        System.out.println(taskManager.getAllSubTasks().equals(newTaskManager.getAllSubTasks()));
    }
}
