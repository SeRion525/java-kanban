package adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDataTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        if (localDateTime != null) {
            jsonWriter.value(localDateTime.toString());
        } else {
            jsonWriter.value("null");
        }
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        String string = jsonReader.nextString();
        if (string.equalsIgnoreCase("null")) {
            return null;
        } else {
            return LocalDateTime.parse(string);
        }
    }
}
