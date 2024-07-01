package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import model.EpicTask;
import model.SubTask;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicTaskHttpHandler extends TaskHttpHandler {

    public EpicTaskHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleGetMethod(HttpExchange exchange) throws IOException {
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
        } else if (pathSplit.length == 4 && pathSplit[3].equalsIgnoreCase("subtasks")) {
            int id = 0;
            try {
                id = Integer.parseInt(pathSplit[2]);
            } catch (NumberFormatException exception) {
                sendBadRequest(exchange, "ID задачи неправильного типа");
                return;
            }

            getSubTaskByEpicTaskId(exchange, id);
        } else {
            sendNotFound(exchange, "Не найден ресурс");
        }
    }

    @Override
    protected void getAll(HttpExchange exchange) throws IOException {
        List<EpicTask> epicTasks = taskManager.getAllEpicTasks();
        sendText(exchange, HTTP_OK, gson.toJson(epicTasks));
    }

    @Override
    protected void getById(HttpExchange exchange, int id) throws IOException {
        EpicTask epicTask = taskManager.getEpicTask(id);
        sendText(exchange, HTTP_OK, gson.toJson(epicTask));
    }

    @Override
    protected void createAndUpdate(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (requestBody.equalsIgnoreCase("null") || requestBody.isEmpty() || requestBody.isBlank()) {
            sendBadRequest(exchange, "Тело запроса пустое или некорректное");
            return;
        }

        EpicTask epicTask = null;
        try {
            epicTask = gson.fromJson(requestBody, EpicTask.class);
        } catch (JsonSyntaxException exception) {
            sendBadRequest(exchange, "Неккоректный JSON");
            return;
        }

        if (epicTask.getId() == 0) {
            EpicTask createdEpicTask = taskManager.createEpicTask(epicTask);
            sendText(exchange, HTTP_CREATED, gson.toJson(createdEpicTask));
        } else {
            EpicTask updatedEpicTask = taskManager.updateEpicTask(epicTask);
            sendText(exchange, HTTP_CREATED, gson.toJson(updatedEpicTask));
        }
    }

    @Override
    protected void remove(HttpExchange exchange, int id) throws IOException {
        taskManager.removeEpicTask(id);
        sendText(exchange, HTTP_NO_CONTENT);
    }

    private void getSubTaskByEpicTaskId(HttpExchange exchange, int id) throws IOException {
        List<SubTask> subTasks = taskManager.getSubTasksFromEpicTaskId(id);
        sendText(exchange, HTTP_OK, gson.toJson(subTasks));
    }
}
