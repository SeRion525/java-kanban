package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHttpHandler extends BaseHttpHandler {

    public TaskHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case GET:
                handleGetMethod(exchange);
                return;
            case POST:
                handlePostMethod(exchange);
                return;
            case DELETE:
                handleDeleteMethod(exchange);
                return;
            default:
                sendBadRequest(exchange, "Метод " + method + " не используется");
        }
    }

    protected void handleGetMethod(HttpExchange exchange) throws IOException{
        String[] pathSplit = exchange.getRequestURI().getPath().split("/");

        if (pathSplit.length == 2) {
            getAll(exchange);
        } else if (pathSplit.length == 3) {
            int id = 0;
            try {
                id = Integer.parseInt(pathSplit[2]);
            } catch (NumberFormatException exception) {
                sendBadRequest(exchange, "ID задачи неправильного типа");
                return;
            }

            getById(exchange, id);
        } else {
            sendNotFound(exchange, "Не найден ресурс");
        }
    }

    protected void handlePostMethod(HttpExchange exchange) throws IOException {
        String[] pathSplit = exchange.getRequestURI().getPath().split("/");
        createAndUpdate(exchange);
    }

    protected void handleDeleteMethod(HttpExchange exchange) throws IOException {
        String[] pathSplit = exchange.getRequestURI().getPath().split("/");

        if (pathSplit.length == 3) {
            int id = 0;
            try {
                id = Integer.parseInt(pathSplit[2]);
            } catch (NumberFormatException exception) {
                sendBadRequest(exchange, "ID задачи неправильного типа");
                return;
            }

            remove(exchange, id);
        }
    }

    protected void getAll(HttpExchange exchange) throws IOException{
        List<Task> tasks = taskManager.getAllTasks();
        sendText(exchange, HTTP_OK, gson.toJson(tasks));
    }

    protected void getById(HttpExchange exchange, int id) throws IOException {
        Task task = taskManager.getTask(id);
        sendText(exchange, HTTP_OK, gson.toJson(task));
    }

    protected void createAndUpdate(HttpExchange exchange) throws IOException{
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (requestBody.equalsIgnoreCase("null") || requestBody.isEmpty() || requestBody.isBlank()) {
            sendBadRequest(exchange, "Тело запроса пустое или некорректное");
            return;
        }

        Task task = null;
        try {
            task = gson.fromJson(requestBody, Task.class);
        } catch (JsonSyntaxException exception) {
            sendBadRequest(exchange, "Неккоректный JSON");
            return;
        }

        if (task.getId() == 0) {
            Task createdTask = taskManager.createTask(task);
            sendText(exchange, HTTP_CREATED, gson.toJson(createdTask));
        } else {
            Task updatedTask = taskManager.updateTask(task);
            sendText(exchange, HTTP_CREATED, gson.toJson(updatedTask));
        }
    }

    protected void remove(HttpExchange exchange, int id) throws IOException{
        taskManager.removeTask(id);
        sendText(exchange, HTTP_NO_CONTENT);
    }
}
