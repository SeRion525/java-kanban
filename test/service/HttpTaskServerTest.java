package service;

import com.google.gson.Gson;
import model.EpicTask;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.*;
import type_token.EpicTaskTypeToken;
import type_token.SubTaskListTypeToken;
import type_token.TaskListTypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static util.TaskTestUtil.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    HttpClient httpClient = HttpClient.newHttpClient();
    Gson gson = HttpTaskServer.getGson();
    String serverAddress = "http://localhost:8080";

    @BeforeEach
    void setup() {
        taskManager.removeAllTasks();
        taskManager.removeAllSubTasks();
        taskManager.removeAllEpicTasks();
        httpTaskServer.start();
    }

    @AfterEach
    void shutDown() {
        httpTaskServer.stop();
    }

    @Nested
    @DisplayName( "Тестировать обработчик задач" )
    class TaskHandlerTest {
        Task task;
        String urlString = serverAddress + "/tasks";
        URI tasksUrl = URI.create(urlString);
        String taskJson;

        @BeforeEach
        void setup() {
            task = new Task("task title", "task descr", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
            taskJson = gson.toJson(task);
        }

        @DisplayName( "Создать задачу ")
        @Test
        void shouldCreateTask() throws IOException, InterruptedException {
            HttpResponse<String> response = sendPostRequest(tasksUrl, taskJson);

            assertEquals(201, response.statusCode(), "Неверный код статуса");

            Task createdTask = gson.fromJson(response.body(), Task.class);
            List<Task> tasks = taskManager.getAllTasks();

            assertEquals(1, tasks.size(), "Неккоректное количество задач");
            assertNotNull(createdTask, "Созданная задача не вернулась");
            assertEqualsTask(createdTask, tasks.getFirst(), "Вернулась неккоректная задача");
        }

        @DisplayName( "Обновить задачу ")
        @Test
        void shouldUpdateTask() throws IOException, InterruptedException {
            Task createdtask = gson.fromJson(sendPostRequest(tasksUrl, taskJson).body(), Task.class);

            Task taskToUpdate = copyTask(createdtask);
            taskToUpdate.setStatus(Status.IN_PROGRESS);
            taskJson = gson.toJson(taskToUpdate);

            HttpResponse<String> response = sendPostRequest(tasksUrl, taskJson);

            assertEquals(201, response.statusCode(), "Неверный код статуса");

            Task updatedTask = gson.fromJson(response.body(), Task.class);

            assertEqualsTask(taskToUpdate, taskManager.getAllTasks().getFirst(), "Задача не обновилась");
            assertNotNull(updatedTask, "Обновлённая задача не вернулась");
            assertEqualsTask(taskToUpdate, updatedTask, "Вернулась неккоректная задача");
        }

        @DisplayName( "Получить список задач" )
        @Test
        void shouldGetTasksList() throws IOException, InterruptedException {
            sendPostRequest(tasksUrl, taskJson);

            HttpResponse<String> response = sendGetRequest(tasksUrl);

            assertEquals(200, response.statusCode(), "Неверный код статуса");

            List<Task> gottenTasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());

            assertNotNull(gottenTasks, "Список задач не вернулся");
            assertEquals(taskManager.getAllTasks(), gottenTasks, "Вернулся неккоректный список задач");
            assertEqualsTask(taskManager.getAllTasks().getFirst(), gottenTasks.getFirst(), "В вернувшемся списке неккоректные задачи");
        }

        @DisplayName( "Получить задачу по ID" )
        @Test
        void shouldGetTaskById() throws IOException, InterruptedException {
            sendPostRequest(tasksUrl, taskJson);

            HttpResponse<String> response = sendGetRequest(URI.create(urlString + "/1"));

            assertEquals(200, response.statusCode(), "Неверный код статуса");

            Task gottenTask = gson.fromJson(response.body(), Task.class);

            assertNotNull(gottenTask, "Задача не вернулась");
            assertEqualsTask(taskManager.getTask(1), gottenTask, "Вернулась неккоректная задача");
        }

        @DisplayName( "Удалить задачу по ID" )
        @Test
        void shouldRemoveTaskById() throws IOException, InterruptedException {
            sendPostRequest(tasksUrl, taskJson);

            HttpResponse<String> response = sendDeleteRequest(URI.create(urlString + "/1"));

            assertEquals(204, response.statusCode(), "Неверный код статуса");
            assertEquals(0, taskManager.getAllTasks().size(), "Задача не удалилась");
        }
    }

    @Nested
    @DisplayName( "Тестировать обработчики эпиков и подзадач" )
    class EpicTaskAndSubTaskHandlersTest {
        EpicTask epicTask;
        SubTask subTask1;
        SubTask subTask2;
        String epicTaskJson;
        String subTask1Json;
        String subTask2Json;
        String epicsUrlString = serverAddress + "/epics";
        URI epicsUrl = URI.create(epicsUrlString);
        String subtasksUrlString = serverAddress + "/subtasks";
        URI subtasksUrl = URI.create(subtasksUrlString);

        @BeforeEach
        void setup() {
            epicTask = new EpicTask("epicTask title", "epicTask descr");
            subTask1 = new SubTask("subTask1 title", "subTask1 descr", Status.NEW, 1,
                    LocalDateTime.now(), Duration.ofMinutes(1));
            subTask2 = new SubTask("subTask2 title", "subTask2 descr", Status.NEW, 1,
                    subTask1.getStartTime().plusMinutes(1), Duration.ofMinutes(1));

            epicTaskJson = gson.toJson(epicTask);
            subTask1Json = gson.toJson(subTask1);
            subTask2Json = gson.toJson(subTask2);
        }

        @Nested
        @DisplayName( "Тестировать обработчик эпиков" )
        class EpicTaskHandlerTest {
            @DisplayName( "Создать эпик" )
            @Test
            void shouldCreateEpicTask() throws IOException, InterruptedException {
                HttpResponse<String> response = sendPostRequest(epicsUrl, epicTaskJson);

                assertEquals(201, response.statusCode(), "Неверный код статуса");

                EpicTask createdEpicTask = gson.fromJson(response.body(), EpicTask.class);
                List<EpicTask> epicTasks = taskManager.getAllEpicTasks();

                assertEquals(1, epicTasks.size(), "Неккоректное количество эпиков");
                assertNotNull(createdEpicTask, "Созданный эпик не вернулся");
                assertEqualsEpicTask(createdEpicTask, epicTasks.getFirst(), "Вернулся неккоректный эпик");
            }

            @DisplayName( "Обновить эпик ")
            @Test
            void shouldUpdateEpicTask() throws IOException, InterruptedException {
                EpicTask createdEpicTask = gson.fromJson(sendPostRequest(epicsUrl, epicTaskJson).body(), EpicTask.class);

                EpicTask epicTaskToUpdate = (EpicTask) copyTask(createdEpicTask);
                epicTaskToUpdate.setTitle("updatedEpicTask title");
                epicTaskJson = gson.toJson(epicTaskToUpdate);

                HttpResponse<String> response = sendPostRequest(epicsUrl, epicTaskJson);

                assertEquals(201, response.statusCode(), "Неверный код статуса");

                EpicTask updatedEpicTask = gson.fromJson(response.body(), EpicTask.class);

                assertEqualsEpicTask(epicTaskToUpdate, taskManager.getAllEpicTasks().getFirst(), "Эпик не обновился");
                assertNotNull(updatedEpicTask, "Обновлённый эпик не вернулся");
                assertEqualsEpicTask(epicTaskToUpdate, updatedEpicTask, "Вернулся неккоректный эпик");
            }

            @DisplayName( "Получить список эпиков" )
            @Test
            void shouldGetEpicTasksList() throws IOException, InterruptedException {
                sendPostRequest(epicsUrl, epicTaskJson);

                HttpResponse<String> response = sendGetRequest(epicsUrl);

                assertEquals(200, response.statusCode(), "Неверный код статуса");

                List<EpicTask> gottenEpicTasks = gson.fromJson(response.body(), new EpicTaskTypeToken().getType());

                assertNotNull(gottenEpicTasks, "Список эпиков не вернулся");
                assertEquals(taskManager.getAllEpicTasks(), gottenEpicTasks, "Вернулся неккоректный список эпиков");
                assertEqualsEpicTask(taskManager.getAllEpicTasks().getFirst(), gottenEpicTasks.getFirst(), "В вернувшемся списке неккоректные эпики");
            }

            @DisplayName( "Получить задачу по ID" )
            @Test
            void shouldGetEpicTaskById() throws IOException, InterruptedException {
                sendPostRequest(epicsUrl, epicTaskJson);

                HttpResponse<String> response = sendGetRequest(URI.create(epicsUrlString + "/1"));

                assertEquals(200, response.statusCode(), "Неверный код статуса");

                EpicTask gottenEpicTask = gson.fromJson(response.body(), EpicTask.class);

                assertNotNull(gottenEpicTask, "Эпик не вернулся");
                assertEqualsTask(taskManager.getEpicTask(1), gottenEpicTask, "Вернулся неккоректный эпик");
            }

            @DisplayName( "Получить список подзадач по ID" )
            @Test
            void shouldGetSubTasksListById() throws IOException, InterruptedException {
                sendPostRequest(epicsUrl, epicTaskJson);
                sendPostRequest(subtasksUrl, subTask1Json);

                HttpResponse<String> response = sendGetRequest(URI.create(epicsUrlString + "/1/subtasks"));

                assertEquals(200, response.statusCode(), "Неверный код статуса");

                List<SubTask> gottenSubTasks = gson.fromJson(response.body(), new SubTaskListTypeToken().getType());

                assertNotNull(gottenSubTasks, "Список подзадач из эпика не вернулся");
                assertEquals(taskManager.getSubTasksFromEpicTaskId(1), gottenSubTasks, "Вернулся неккоректный список подзадач из эпика");
                assertEqualsSubTask(taskManager.getSubTasksFromEpicTaskId(1).getFirst(), gottenSubTasks.getFirst(),
                        "В вернувшемся списке неккоректные подзадачи");
            }

            @DisplayName( "Удалить эпик по ID" )
            @Test
            void shouldRemoveEpicTaskById() throws IOException, InterruptedException {
                sendPostRequest(epicsUrl, epicTaskJson);

                HttpResponse<String> response = sendDeleteRequest(URI.create(epicsUrlString + "/1"));

                assertEquals(204, response.statusCode(), "Неверный код статуса");
                assertEquals(0, taskManager.getAllEpicTasks().size(), "Эпик не удалился");
            }
        }

        @Nested
        @DisplayName( "Тестировать обработчик подзадач" )
        class SubTaskHandlerTest {
            @BeforeEach
            void setup() throws IOException, InterruptedException{
                sendPostRequest(epicsUrl, epicTaskJson);
            }

            @DisplayName( "Создать подзадачу ")
            @Test
            void shouldCreateSubTask() throws IOException, InterruptedException {
                HttpResponse<String> response = sendPostRequest(subtasksUrl, subTask1Json);

                assertEquals(201, response.statusCode(), "Неверный код статуса");

                SubTask createdSubTask = gson.fromJson(response.body(), SubTask.class);
                List<SubTask> subTasks = taskManager.getAllSubTasks();

                assertEquals(1, subTasks.size(), "Неккоректное количество подзадач");
                assertNotNull(createdSubTask, "Созданная подзадача не вернулась");
                assertEqualsSubTask(createdSubTask, subTasks.getFirst(), "Вернулась неккоректная подзадача");
            }

            @DisplayName( "Обновить подзадачу ")
            @Test
            void shouldUpdateSubTask() throws IOException, InterruptedException {
                SubTask createdSubTask = gson.fromJson(sendPostRequest(subtasksUrl, subTask1Json).body(), SubTask.class);

                SubTask subTaskToUpdate = (SubTask) copyTask(createdSubTask);
                subTaskToUpdate.setStatus(Status.IN_PROGRESS);
                subTask1Json = gson.toJson(subTaskToUpdate);

                HttpResponse<String> response = sendPostRequest(subtasksUrl, subTask1Json);

                assertEquals(201, response.statusCode(), "Неверный код статуса");

                SubTask updatedSubTask = gson.fromJson(response.body(), SubTask.class);

                assertEqualsTask(subTaskToUpdate, taskManager.getAllSubTasks().getFirst(), "Подадача не обновилась");
                assertNotNull(updatedSubTask, "Обновлённая подзадача не вернулась");
                assertEqualsSubTask(subTaskToUpdate, updatedSubTask, "Вернулась неккоректная подзадача");

                EpicTask updatedEpicTask = gson.fromJson(sendGetRequest(URI.create(epicsUrl + "/1")).body(), EpicTask.class);
                assertEquals(Status.IN_PROGRESS, updatedEpicTask.getStatus(), "Статус эпика не обновился");
                assertEquals(subTask1.getStartTime(), updatedEpicTask.getStartTime(), "Время начала эпика не обновилось");
                assertEquals(subTask1.getEndTime(), updatedEpicTask.getEndTime(), "Время конца эпика не обновилось");
                assertEquals(subTask1.getDuration(), updatedEpicTask.getDuration(), "Продолжительность эпика не обновилась");
            }

            @DisplayName( "Получить список подзадач" )
            @Test
            void shouldGetSubTasksList() throws IOException, InterruptedException {
                sendPostRequest(subtasksUrl, subTask1Json);

                HttpResponse<String> response = sendGetRequest(subtasksUrl);

                assertEquals(200, response.statusCode(), "Неверный код статуса");

                List<SubTask> gottenSubTasks = gson.fromJson(response.body(), new SubTaskListTypeToken().getType());

                assertNotNull(gottenSubTasks, "Список подзадач не вернулся");
                assertEquals(taskManager.getAllSubTasks(), gottenSubTasks, "Вернулся неккоректный список подзадач");
                assertEqualsSubTask(taskManager.getAllSubTasks().getFirst(), gottenSubTasks.getFirst(), "В вернувшемся списке неккоректные подзадачи");
            }

            @DisplayName( "Получить подзадачу по ID" )
            @Test
            void shouldGetSubTaskById() throws IOException, InterruptedException {
                sendPostRequest(subtasksUrl, subTask1Json);

                HttpResponse<String> response = sendGetRequest(URI.create(subtasksUrlString + "/2"));

                assertEquals(200, response.statusCode(), "Неверный код статуса");

                SubTask gottenSubTask = gson.fromJson(response.body(), SubTask.class);

                assertNotNull(gottenSubTask, "Подзадача не вернулась");
                assertEqualsSubTask(taskManager.getSubTask(2), gottenSubTask, "Вернулась неккоректная подзадача");
            }

            @DisplayName( "Удалить подзадачу по ID" )
            @Test
            void shouldRemoveSubTaskById() throws IOException, InterruptedException {
                sendPostRequest(subtasksUrl, subTask1Json);

                HttpResponse<String> response = sendDeleteRequest(URI.create(subtasksUrlString + "/2"));

                assertEquals(204, response.statusCode(), "Неверный код статуса");
                assertEquals(0, taskManager.getAllSubTasks().size(), "Подзадача не удалилась");
            }
        }
    }

    @Nested
    @DisplayName( "Тестировать обработчик истории" )
    class HistoryHandlerTest {
        Task task;
        String historyUrlString = serverAddress + "/history";
        URI historyUrl = URI.create(historyUrlString);

        @BeforeEach
        void setup() throws IOException, InterruptedException {
            task = new Task("task title", "task descr", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
            sendPostRequest(URI.create(serverAddress + "/tasks"), gson.toJson(task));
            sendGetRequest(URI.create(serverAddress + "/tasks/1"));
        }

        @DisplayName( "Получить историю" )
        @Test
        void shouldGetHistory() throws IOException, InterruptedException {
            HttpResponse<String> response = sendGetRequest(historyUrl);

            assertEquals(200, response.statusCode(), "Неверный код статуса");

            List<Task> history = gson.fromJson(response.body(), new TaskListTypeToken().getType());

            assertNotNull(history, "История не вернулась");
            assertEquals(taskManager.getHistory().size(), history.size(), "Неккоректное количество задач в истории");
            assertEqualsTask(taskManager.getHistory().getFirst(), history.getFirst(), "В истории неккоректные задачи");
        }
    }

    @Nested
    @DisplayName( "Тестирование обработчика задач по приоритету" )
    class PrioritizedHandlerTest {
        Task task1;
        Task task2;
        String prioritizedUrlString = serverAddress + "/prioritized";
        URI prioritizedUrl = URI.create(prioritizedUrlString);

        @BeforeEach
        void setup() throws IOException, InterruptedException {
            task1 = new Task("task1 title", "task1 descr", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
            task2 = new Task("task2 title", "task2 descr", Status.NEW, task1.getStartTime().plusMinutes(1), Duration.ofMinutes(1));
            sendPostRequest(URI.create(serverAddress + "/tasks"), gson.toJson(task2));
            sendPostRequest(URI.create(serverAddress + "/tasks"), gson.toJson(task1));
        }

        @DisplayName( "Получить список задач по приоритету" )
        @Test
        void shouldGetPrioritized() throws IOException, InterruptedException {
            HttpResponse<String> response = sendGetRequest(prioritizedUrl);

            assertEquals(200, response.statusCode(), "Неверный код статуса");

            List<Task> prioritized = gson.fromJson(response.body(), new TaskListTypeToken().getType());

            assertNotNull(prioritized, "Список задач по приоритету не вернулся");
            assertEquals(taskManager.getPrioritizedTasks().size(), prioritized.size(), "Неккоректное количество задач");
            assertEquals(taskManager.getPrioritizedTasks(), prioritized, "Неккоректный порядок задач");
            assertEqualsTask(taskManager.getPrioritizedTasks().getFirst(), prioritized.getFirst(), "В списке неккоректные задачи");
            assertEqualsTask(taskManager.getPrioritizedTasks().get(1), prioritized.get(1), "В списке неккоректные задачи");
        }
    }

    @Nested
    @DisplayName( "Тестировать коды ошибок" )
    class ErrorCodesTest {
        @DisplayName( "Не найден ресурс" )
        @Test
        void shouldNotFound() throws IOException, InterruptedException{
            assertEquals(404, sendGetRequest(URI.create(serverAddress + "/test")).statusCode(), "Неверный код статуса при ненайденном ресурсе");
        }

        @DisplayName( "Не найдена задача" )
        @Test
        void shouldNotFoundWhenTaskNotFound() throws IOException, InterruptedException {
            HttpResponse<String> response = sendGetRequest(URI.create(serverAddress + "/tasks/1"));
            assertEquals(404, response.statusCode(), "Неверный код статуса при ненайденной задаче");

            response = sendGetRequest(URI.create(serverAddress + "/epics/1"));
            assertEquals(404, response.statusCode(), "Неверный код статуса при ненайденном эпике");

            response = sendGetRequest(URI.create(serverAddress + "/epics/1/subtasks"));
            assertEquals(404, response.statusCode(), "Неверный код статуса при ненайденном эпике");

            response = sendGetRequest(URI.create(serverAddress + "/subtasks/1"));
            assertEquals(404, response.statusCode(), "Неверный код статуса при ненайденной подзадаче");
        }

        @DisplayName( "Пересечение задач при создании" )
        @Test
        void shouldNotAcceptableWhenTimeIntersectionOnCreate() throws IOException, InterruptedException {
            Task task1 = new Task("task1 title", "task1 descr", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
            Task task2 = new Task("task2 title", "task2 descr", Status.NEW, task1.getStartTime(), Duration.ofMinutes(1));
            EpicTask epicTask = new EpicTask("epicTask title", "epicTask descr");
            SubTask subTask = new SubTask("subTask1 title", "subTask1 descr", Status.NEW, 3,
                    task1.getStartTime(), Duration.ofMinutes(1));

            sendPostRequest(URI.create(serverAddress + "/tasks"), gson.toJson(task1));
            sendPostRequest(URI.create(serverAddress + "/epics"), gson.toJson(epicTask));

            HttpResponse<String> response = sendPostRequest(URI.create(serverAddress + "/tasks"), gson.toJson(task2));
            assertEquals(406, response.statusCode(), "Неверный код статуса при пересечении при создании задачи");

            response = sendPostRequest(URI.create(serverAddress + "/subtasks"), gson.toJson(subTask));
            assertEquals(406, response.statusCode(), "Неверный код статуса при пересечении при создании подзадачи");
        }

        @DisplayName( "Пересечение задач при обновлении" )
        @Test
        void shouldNotAcceptableWhenTimeIntersectionOnUpdate() throws IOException, InterruptedException {
            Task task = new Task("task title", "task descr", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
            EpicTask epicTask = new EpicTask("epicTask title", "epicTask descr");
            SubTask subTask = new SubTask("subTask1 title", "subTask1 descr", Status.NEW, 2,
                    task.getStartTime().plusMinutes(1), Duration.ofMinutes(1));

            sendPostRequest(URI.create(serverAddress + "/tasks"), gson.toJson(task));
            sendPostRequest(URI.create(serverAddress + "/epics"), gson.toJson(epicTask));
            sendPostRequest(URI.create(serverAddress + "/subtasks"), gson.toJson(subTask));

            task = new Task("task title", "task descr", Status.NEW, subTask.getStartTime(), Duration.ofMinutes(1));
            task.setId(1);

            HttpResponse<String> response = sendPostRequest(URI.create(serverAddress + "/tasks"), gson.toJson(task));
            assertEquals(406, response.statusCode(), "Неверный код статуса при пересечении при обновлении задачи");

            subTask = new SubTask("subTask1 title", "subTask1 descr", Status.NEW, 2,
                    taskManager.getTask(1).getStartTime(), Duration.ofMinutes(1));
            subTask.setId(3);

            response = sendPostRequest(URI.create(serverAddress + "/subtasks"), gson.toJson(subTask));
            assertEquals(406, response.statusCode(), "Неверный код статуса при пересечении при обновлении подзадачи");
        }

        @DisplayName( "Передать неиспользуемый метод запроса" )
        @Test
        void shouldBadRequestWhenUnusualRequestMethod() throws IOException, InterruptedException {
            HttpResponse<String> response = sendHeadRequest(URI.create(serverAddress + "/tasks"));
            assertEquals(400, response.statusCode(), "Неверный код статуса при неиспользуемом теле запроса при обработке задач");

            response = sendHeadRequest(URI.create(serverAddress + "/history"));
            assertEquals(400, response.statusCode(), "Неверный код статуса при неиспользуемом теле запроса при обработке истории");

            response = sendHeadRequest(URI.create(serverAddress + "/prioritized"));
            assertEquals(400, response.statusCode(), "Неверный код статуса при неиспользуемом теле запроса при обработке списка задач по приоритету");
        }

        @DisplayName( "Передать неккоректное тело запроса" )
        @Test
        void shouldBadRequestWhenIncorrectRequestBody() throws IOException, InterruptedException {
            HttpResponse<String> response = sendPostRequest(URI.create(serverAddress + "/tasks"), "");
            assertEquals(400, response.statusCode(), "Неверный код статуса при пустом теле запроса при обработке задачи");

            response = sendPostRequest(URI.create(serverAddress + "/epics"), "");
            assertEquals(400, response.statusCode(), "Неверный код статуса при пустом теле запроса при обработке эпика");

            response = sendPostRequest(URI.create(serverAddress + "/subtasks"), "");
            assertEquals(400, response.statusCode(), "Неверный код статуса при пустом теле запроса при обработке подзадачи");


            response = sendPostRequest(URI.create(serverAddress + "/tasks"), "test");
            assertEquals(400, response.statusCode(), "Неверный код статуса при неккоректном теле запроса при обработке задачи");

            response = sendPostRequest(URI.create(serverAddress + "/epics"), "test");
            assertEquals(400, response.statusCode(), "Неверный код статуса при неккоректном теле запроса при обработке эпика");

            response = sendPostRequest(URI.create(serverAddress + "/subtasks"), "test");
            assertEquals(400, response.statusCode(), "Неверный код статуса при неккоректном теле запроса при обработке подзадачи");
        }

        @DisplayName( "Передать неккоректный тип ID" )
        @Test
        void shouldBadRequestWhenWrongIdType() throws IOException, InterruptedException {
            Task task = new Task("task title", "task descr", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
            EpicTask epicTask = new EpicTask("epicTask title", "epicTask descr");
            SubTask subTask = new SubTask("subTask1 title", "subTask1 descr", Status.NEW, 2,
                    task.getStartTime().plusMinutes(1), Duration.ofMinutes(1));
            sendPostRequest(URI.create(serverAddress + "/tasks"), gson.toJson(task));
            sendPostRequest(URI.create(serverAddress + "/epics"), gson.toJson(epicTask));
            sendPostRequest(URI.create(serverAddress + "/subtasks"), gson.toJson(subTask));

            HttpResponse<String> response = sendGetRequest(URI.create(serverAddress + "/tasks/xd"));
            assertEquals(400, response.statusCode(), "Неверный код статуса при неккоректном типе ID при получении задачи");

            response = sendGetRequest(URI.create(serverAddress + "/epics/xd"));
            assertEquals(400, response.statusCode(), "Неверный код статуса при неккоректном типе ID при получении эпика");

            response = sendGetRequest(URI.create(serverAddress + "/subtasks/xd"));
            assertEquals(400, response.statusCode(), "Неверный код статуса при неккоректном типе ID при получении подзадачи");

            response = sendGetRequest(URI.create(serverAddress + "/epics/xd/subtasks"));
            assertEquals(400, response.statusCode(), "Неверный код статуса при неккоректном типе ID при получении списка подазадач у эпика");
        }
    }

    private HttpResponse<String> sendPostRequest(URI url, String body) throws IOException, InterruptedException{
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(body)).build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendGetRequest(URI url) throws IOException, InterruptedException{
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDeleteRequest(URI url) throws IOException, InterruptedException{
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendHeadRequest(URI url) throws IOException, InterruptedException{
        HttpRequest request = HttpRequest.newBuilder().uri(url).HEAD().build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
