package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.ManagerIOException;
import exception.NotFoundException;
import exception.ValidationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static handler.BaseHttpHandler.HTTP_BAD_GATEWAY;
import static handler.BaseHttpHandler.HTTP_INTERNAL_SERVER_ERROR;
import static handler.BaseHttpHandler.HTTP_NOT_ACCEPTABLE;
import static handler.BaseHttpHandler.HTTP_NOT_FOUND;


public class ErrorHandler {
    private final Gson gson;

    public ErrorHandler(Gson gson) {
        this.gson = gson;
    }

    public void handle(HttpExchange exchange, Exception exception) {
        try {
            if (exception instanceof ManagerIOException) {
                exception.printStackTrace();
                sendBadGateWay(exchange, exception.getMessage());
                return;
            }

            if (exception instanceof NotFoundException) {
                exception.printStackTrace();
                sendNotFound(exchange, exception.getMessage());
                return;
            }

            if (exception instanceof ValidationException) {
                exception.printStackTrace();
                sendHasInteractions(exchange, exception.getMessage());
                return;
            }

            exception.printStackTrace();
            sendInternalServerError(exchange, exception.getMessage());
        } catch (Exception otherException) {
            otherException.printStackTrace();
        }
    }

    private void sendBadGateWay(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(HTTP_BAD_GATEWAY, 0);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
    }

    private void sendInternalServerError(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(HTTP_INTERNAL_SERVER_ERROR, 0);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
    }

    protected void sendNotFound(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(HTTP_NOT_FOUND, 0);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
    }

    private void sendHasInteractions(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(HTTP_NOT_ACCEPTABLE, 0);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
    }
}
