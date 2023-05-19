package models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import server.HttpTaskManager;
import services.FileBackedTaskManager;
import services.InMemoryHistoryManager;

import java.io.IOException;
import java.time.Instant;

public class Managers {
    public static TaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager();
    }

    public static TaskManager getFileBacked(String dataFile){
        return new FileBackedTaskManager(getDefaultHistory(), dataFile);
    }
    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }

    public static Gson getGson(){
        GsonBuilder gsonBuilder = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Instant.class, new InstantAdapter());
        return gsonBuilder.create();
    }

    static class InstantAdapter extends TypeAdapter<Instant>{

        @Override
        public void write(JsonWriter jsonWriter, Instant instant) throws IOException {
            if (instant == null) {
                jsonWriter.value("null");
                return;
            }
            jsonWriter.value(String.valueOf(instant.toEpochMilli()));
        }

        @Override
        public Instant read(JsonReader jsonReader) throws IOException {
            final String text = jsonReader.nextString();
            if (text.equals("null")) {
                return null;
            }
            return Instant.ofEpochMilli(Long.parseLong(text));
        }
    }

}
