import model.EpicTask;
import model.Status;
import model.SubTask;
import model.Task;
import service.TaskManager;

public class Main {
    private static int tasksCount = 0;

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

///////////////////////////////////////////////// СОЗДАНИЕ ЗАДАЧ ///////////////////////////////////////////////////////
        System.out.println("Создание задач");
        System.out.println();

        int task1Id = giveID();
        Task task1 = new Task("Задача 1" , "Описание задачи", task1Id, Status.NEW);
        taskManager.createTask(task1);

        int task2Id = giveID();
        Task task2 = new Task("Задача 2", "Описание задачи", task2Id, Status.NEW);
        taskManager.createTask(task2);


        int epicTask1Id = giveID();
        EpicTask epicTask1 = new EpicTask("Эпик 1", "Описание эпика", epicTask1Id);
        taskManager.createTask(epicTask1);

        int subTask1Id = giveID();
        SubTask subTask1 = new SubTask("Подзадача 1",
                "Подзадача для эпика 1", subTask1Id, Status.NEW, epicTask1Id);
        taskManager.createTask(subTask1);

        int subTask2Id = giveID();
        SubTask subTask2 = new SubTask("Подзадача 2",
                "Подзадача для эпика 1", subTask2Id, Status.NEW, epicTask1Id);
        taskManager.createTask(subTask2);


        int epicTask2Id = giveID();
        EpicTask epicTask2 = new EpicTask("Эпик 2", "Описание эпика", epicTask2Id);
        taskManager.createTask(epicTask2);

        int subTask3Id = giveID();
        SubTask subTask3 = new SubTask("Подзадача 3",
                "Подзадача для эпика 2", subTask3Id, Status.NEW, epicTask2Id);
        taskManager.createTask(subTask3);


        System.out.println(taskManager.getAllTasks());
        System.out.println();
        System.out.println(taskManager.getAllEpicTasks());
        System.out.println();
        System.out.println(taskManager.getAllSubTasks());
        System.out.println();
        System.out.println();

/////////////////////////////////////////////////// ИЗМЕНЕНИЕ ЗАДАЧ ////////////////////////////////////////////////////
        System.out.println("Изменение задач");
        System.out.println();

        task1 = new Task("Задача 1" , "Описание задачи", task1Id, Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        System.out.println(taskManager.getTask(task1Id));
        System.out.println();

        task2 = new Task("Задача 2" , "Описание задачи", task1Id, Status.DONE);
        taskManager.updateTask(task2);
        System.out.println(taskManager.getTask(task2Id));
        System.out.println();
        System.out.println();


        System.out.println("Изменение Подзадачи 1 в Эпике 1 на IN_PROGRESS");
        subTask1 = new SubTask("Подзадача 1",
                "Подзадача для эпика 1", subTask1Id, Status.IN_PROGRESS, epicTask1Id);
        taskManager.updateTask(subTask1);
        System.out.println(taskManager.getTask(epicTask1Id));
        System.out.println(taskManager.getSubTasksFromEpicTaskId(epicTask1Id));
        System.out.println();

        System.out.println("Изменение Подзадачи 2 в Эпике 1 на DONE");
        subTask2 = new SubTask("Подзадача 2",
                "Подзадача для эпика 1", subTask2Id, Status.DONE, epicTask1Id);
        taskManager.updateTask(subTask2);
        System.out.println(taskManager.getTask(epicTask1Id));
        System.out.println(taskManager.getSubTasksFromEpicTaskId(epicTask1Id));
        System.out.println();

        System.out.println("Изменение Подзадачи 1 в Эпике 1 на DONE");
        subTask1 = new SubTask("Подзадача 1",
                "Подзадача для эпика 1", subTask1Id, Status.DONE, epicTask1Id);
        taskManager.updateTask(subTask1);
        System.out.println(taskManager.getTask(epicTask1Id));
        System.out.println(taskManager.getSubTasksFromEpicTaskId(epicTask1Id));
        System.out.println();
        System.out.println();


        System.out.println("Изменение Подзадачи 3 в Эпике 2 на IN_PROGRESS");
        subTask3 = new SubTask("Подзадача 3",
                "Подзадача для эпика 2", subTask3Id, Status.IN_PROGRESS, epicTask2Id);
        taskManager.updateTask(subTask3);
        System.out.println(taskManager.getTask(epicTask2Id));
        System.out.println(taskManager.getSubTasksFromEpicTaskId(epicTask2Id));
        System.out.println();
        System.out.println();


/////////////////////////////////////////////////// УДАЛЕНИЕ ЗАДАЧ /////////////////////////////////////////////////////
        System.out.println("Удаление задач");
        System.out.println();

        System.out.println("Удаление Задачи 1");
        taskManager.removeTask(task1Id);
        System.out.println(taskManager.getAllTasks());
        System.out.println();

        System.out.println("Удаление Подзачи 1 в Эпике 1");
        taskManager.removeTask(subTask1Id);
        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getSubTasksFromEpicTaskId(epicTask1Id));
        System.out.println();

        System.out.println("Удаление Эпика 2");
        taskManager.removeTask(epicTask2Id);
        System.out.println(taskManager.getAllEpicTasks());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println();
    }

    private static int giveID() {
        int id = tasksCount;
        tasksCount++;
        return id;
    }
}
