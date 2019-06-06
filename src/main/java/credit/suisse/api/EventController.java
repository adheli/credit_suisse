package credit.suisse.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import credit.suisse.dao.EventResultService;
import credit.suisse.pojo.Event;
import credit.suisse.pojo.EventResult;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventController {

    private EventResultService service = new EventResultService();

    private List<Event> getEventsFromJsonFile(String inputFilePath) {
        try {
            String jsonString = new String(Files.readAllBytes(Paths.get(inputFilePath)));

            Gson gson = new Gson();

            Type listType = new TypeToken<ArrayList<Event>>(){}.getType();
            return gson.fromJson(jsonString, listType);
        } catch (IOException e) {
            return null;
        }
    }

    private List<String> getIds(List<Event> events) {
        List<String> ids = new ArrayList<String>();

        List<Event> distinct = events.stream().distinct().collect(Collectors.toList());

        for (Event event : distinct) {
            ids.add(event.getId());
        }

        return ids;
    }

    private boolean saveResults(List<EventResult> results) {
        for (EventResult result : results) {
//            service.save(result);
        }

        return true;
    }

    public void processEvents(String inputFilePath) {
        List<Event> events = this.getEventsFromJsonFile(inputFilePath);

        if (events != null && !events.isEmpty()) {
            List<String> ids = this.getIds(events);

            List<EventResult> results = new ArrayList<EventResult>();

            for (String id : ids) {
                List<Event> eventsForId = events.stream().filter(event -> event.getId().equals(id)).collect(Collectors.toList());
                Optional<Event> started = eventsForId.stream().filter(event -> event.getState().equals("STARTED")).findFirst();
                Optional<Event> finished = eventsForId.stream().filter(event -> event.getState().equals("FINISHED")).findFirst();

                if (finished.isPresent() && started.isPresent()) {
                    Long duration = finished.get().getTimestamp() - started.get().getTimestamp();

                    EventResult result = new EventResult(id, duration.intValue(), (duration > 4));
                    result.setHost(started.get().getHost() != null ? started.get().getHost() : finished.get().getHost());
                    result.setType(started.get().getType() != null ? started.get().getType() : finished.get().getType());
                    results.add(result);
                }
            }

            this.saveResults(results);
        }
    }

    public static void main(String[] args) {
        EventController controller = new EventController();

        controller.processEvents("/home/vagrant/IdeaProjects/credit_suisse/src/test/resources/input_file.json");
    }
}
