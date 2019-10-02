package credit.suisse.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import credit.suisse.service.EventService;
import credit.suisse.service.EventServiceImpl;
import credit.suisse.pojo.Event;
import credit.suisse.pojo.EventResult;
import credit.suisse.pojo.EventType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author adheli.tavares
 */
public class EventControllerImpl implements EventController {

    private static final Logger logger = Logger.getGlobal();
    private EventService service = new EventServiceImpl();

    /**
     * Read json file and serializes the data to a list of events.
     *
     * @param inputFilePath: file to be read and get event logs.
     * @return Event list
     */
    private List<Event> getEventsFromJsonFile(String inputFilePath) {
        List<Event> events = new ArrayList<>();
        try {
            EventControllerImpl.logger.info(String.format("Reading input file %s to get json data.", inputFilePath));
            String jsonString = new String(Files.readAllBytes(Paths.get(inputFilePath)));

            Gson gson = new Gson();

            Type listType = new TypeToken<ArrayList<Event>>() {
            }.getType();
            events = gson.fromJson(jsonString, listType);
        } catch (IOException ioExcp) {
            EventControllerImpl.logger.severe(String.format("Error while trying to read input file %s.", inputFilePath));
        }

        return events;
    }

    /**
     * Get start and finish log events, calculate timestamp difference and prepare the result.
     *
     * @param id:       log event id.
     * @param started:  start event log entry
     * @param finished: finish event log entry.
     * @return log event result.
     */
    private EventResult prepareResult(String id, Event started, Event finished) {
        long duration = finished.getTimestamp() - started.getTimestamp();

        EventResult result = new EventResult(id, (int) duration, (duration > 4));
        result.setHost(started.getHost() != null ? started.getHost() : finished.getHost());
        result.setType(started.getType() != null ? started.getType() : finished.getType());

        return result;
    }

    private void saveResults(List<EventResult> results) {
        results.forEach(result -> this.service.saveEventResult(result));
    }

    /**
     * Read json file with log event entries and process them to get duration of events, then save the result on database.
     *
     * @param inputFilePath: json file with log entries to be processed.
     */
    public void processEvents(String inputFilePath) {
        List<Event> events = this.getEventsFromJsonFile(inputFilePath);

        if (!events.isEmpty()) {
            EventControllerImpl.logger.info("Starting to process events.");

            List<String> ids = new ArrayList<>();

            events.stream().distinct().forEach(event -> ids.add(event.getId()));

            List<EventResult> results = new ArrayList<>();

            ids.forEach(id -> {
                HashMap<EventType, Event> myEvents = new HashMap<>();

                events.stream().filter(event -> event.getId().equals(id)).forEach(event -> {
                    if (EventType.STARTED.name().equals(event.getState()))
                        myEvents.putIfAbsent(EventType.STARTED, event);
                    else if (EventType.FINISHED.name().equals(event.getState()))
                        myEvents.putIfAbsent(EventType.FINISHED, event);
                });

                if (myEvents.values().size() > 1) {
                    EventResult result = prepareResult(id, myEvents.get(EventType.STARTED), myEvents.get(EventType.FINISHED));
                    results.add(result);
                    EventControllerImpl.logger.info(String.format("Event %s processed.", result.getId()));
                }
            });

            this.saveResults(results);
        }
    }

    public void prepareEnvironment() {
        this.service.createTable();
    }

    @Override
    public List<EventResult> showEventResults() {
        return this.service.listAllEvents();
    }

    public void cleanEnvironment() {
        this.service.cleanTable();
    }
}
