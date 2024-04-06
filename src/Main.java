import model.EpicTask;
import model.Status;
import model.SubTask;
import model.Task;
import service.InMemoryTaskManager;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new InMemoryTaskManager();

///////////////////////////////////////////////// СОЗДАНИЕ ЗАДАЧ ///////////////////////////////////////////////////////
        System.out.println("Создание задач");
        System.out.println();

        int task1Id = 0;
        Task task1 = new Task("Задача 1" , "Описание задачи", Status.NEW);
        taskManager.createTask(task1);

        int task2Id = 1;
        Task task2 = new Task("Задача 2", "Описание задачи", Status.NEW);
        taskManager.createTask(task2);


        int epicTask1Id = 2;
        EpicTask epicTask1 = new EpicTask("Эпик 1", "Описание эпика");
        taskManager.createTask(epicTask1);

        int subTask1Id = 3;
        SubTask subTask1 = new SubTask("Подзадача 1",
                "Подзадача для эпика 1", Status.NEW, epicTask1Id);
        taskManager.createTask(subTask1);

        int subTask2Id = 4;
        SubTask subTask2 = new SubTask("Подзадача 2",
                "Подзадача для эпика 1", Status.NEW, epicTask1Id);
        taskManager.createTask(subTask2);


        int epicTask2Id = 5;
        EpicTask epicTask2 = new EpicTask("Эпик 2", "Описание эпика");
        taskManager.createTask(epicTask2);

        int subTask3Id = 6;
        SubTask subTask3 = new SubTask("Подзадача 3",
                "Подзадача для эпика 2", Status.NEW, epicTask2Id);
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

        task1 = new Task("Задача 1" , "Описание задачи", Status.IN_PROGRESS);
        task1.setId(task1Id);
        taskManager.updateTask(task1);
        System.out.println(taskManager.getTask(task1Id));
        System.out.println();

        task2 = new Task("Задача 2" , "Описание задачи", Status.DONE);
        task2.setId(task2Id);
        taskManager.updateTask(task2);
        System.out.println(taskManager.getTask(task2Id));
        System.out.println();
        System.out.println();


        System.out.println("Изменение Подзадачи 1 в Эпике 1 на IN_PROGRESS");
        subTask1 = new SubTask("Подзадача 1",
                "Подзадача для эпика 1", Status.IN_PROGRESS, epicTask1Id);
        subTask1.setId(subTask1Id);
        taskManager.updateTask(subTask1);
        System.out.println(taskManager.getTask(epicTask1Id));
        System.out.println(taskManager.getSubTasksFromEpicTaskId(epicTask1Id));
        System.out.println();

        System.out.println("Изменение Подзадачи 2 в Эпике 1 на DONE");
        subTask2 = new SubTask("Подзадача 2",
                "Подзадача для эпика 1", Status.DONE, epicTask1Id);
        subTask2.setId(subTask2Id);
        taskManager.updateTask(subTask2);
        System.out.println(taskManager.getTask(epicTask1Id));
        System.out.println(taskManager.getSubTasksFromEpicTaskId(epicTask1Id));
        System.out.println();

        System.out.println("Изменение Подзадачи 1 в Эпике 1 на DONE");
        subTask1 = new SubTask("Подзадача 1",
                "Подзадача для эпика 1", Status.DONE, epicTask1Id);
        subTask1.setId(subTask1Id);
        taskManager.updateTask(subTask1);
        System.out.println(taskManager.getTask(epicTask1Id));
        System.out.println(taskManager.getSubTasksFromEpicTaskId(epicTask1Id));
        System.out.println();
        System.out.println();


        System.out.println("Изменение Подзадачи 3 в Эпике 2 на IN_PROGRESS");
        subTask3 = new SubTask("Подзадача 3",
                "Подзадача для эпика 2", Status.IN_PROGRESS, epicTask2Id);
        subTask3.setId(subTask3Id);
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
}
