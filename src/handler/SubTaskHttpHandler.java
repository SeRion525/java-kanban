package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import model.SubTask;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubTaskHttpHandler extends TaskHttpHandler {
    public SubTaskHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void getAll(HttpExchange exchange) throws IOException {
        List<SubTask> subTasks = taskManager.getAllSubTasks();
        sendText(exchange, HTTP_OK, gson.toJson(subTasks));
    }

    @Override
    protected void getById(HttpExchange exchange, int id) throws IOException {
        SubTask subTask = taskManager.getSubTask(id);
        sendText(exchange, HTTP_OK, gson.toJson(subTask));
    }

    @Override
    protected void createAndUpdate(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (requestBody.equalsIgnoreCase("null") || requestBody.isEmpty() || requestBody.isBlank()) {
            sendBadRequest(exchange, "Тело запроса пустое или некорректное");
            return;
        }

        SubTask subTask = null;
        try {
            subTask = gson.fromJson(requestBody, SubTask.class);
        } catch (JsonSyntaxException exception) {
            sendBadRequest(exchange, "Неккоректный JSON");
            return;
        }

        if (subTask.getId() == 0) {
            SubTask createdSubTask = taskManager.createSubTask(subTask);
            sendText(exchange, HTTP_CREATED, gson.toJson(createdSubTask));
        } else {
            SubTask updatedSubTask = taskManager.updateSubTask(subTask);
            sendText(exchange, HTTP_CREATED, gson.toJson(updatedSubTask));
        }
    }

    @Override
    protected void remove(HttpExchange exchange, int id) throws IOException {
        taskManager.removeSubTask(id);
        sendText(exchange, HTTP_NO_CONTENT);
    }
}
