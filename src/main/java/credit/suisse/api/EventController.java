package credit.suisse.api;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import credit.suisse.dao.EventResultService;
import credit.suisse.pojo.Event;
import credit.suisse.pojo.EventResult;

/**
 * @author adheli.tavares
 */
public class EventController {

    private static final Logger logger = Logger.getGlobal();
    private EventResultService service = new EventResultService();

    /**
     * Read json file and serializes the data to a list of events.
     *
     * @param inputFilePath: file to be read and get event logs.
     * @return Event list
     */
    private List<Event> getEventsFromJsonFile(String inputFilePath) {
        List<Event> events = new ArrayList<Event>();
        try {
            EventController.logger.info(String.format("Reading input file %s to get json data.", inputFilePath));
            String jsonString = new String(Files.readAllBytes(Paths.get(inputFilePath)));

            Gson gson = new Gson();

            Type listType = new TypeToken<ArrayList<Event>>() {
            }.getType();
            events = gson.fromJson(jsonString, listType);
        } catch (IOException ioExcp) {
            EventController.logger.severe(String.format("Error while trying to read input file %s.", inputFilePath));
        }

        return events;
    }

    /**
     * Prepare a list for unique id's.
     *
     * @param events: processed log events from json file.
     * @return list of unique log events id.
     */
    private List<String> getIds(List<Event> events) {
        List<String> ids = new ArrayList<String>();

        List<Event> distinct = events.stream().distinct().collect(Collectors.toList());

        for (Event event : distinct) {
            ids.add(event.getId());
        }

        return ids;
    }

    /**
     * Get start and finish log events, caluculate timestamp difference and prepare the result.
     *
     * @param id:       log event id.
     * @param started:  start event log entry
     * @param finished: finish event log entry.
     * @return log event result.
     */
    private EventResult prepareResult(String id, Event started, Event finished) {
        Long duration = finished.getTimestamp() - started.getTimestamp();

        EventResult result = new EventResult(id, duration.intValue(), (duration > 4));
        result.setHost(started.getHost() != null ? started.getHost() : finished.getHost());
        result.setType(started.getType() != null ? started.getType() : finished.getType());

        return result;
    }

    private void saveResults(List<EventResult> results) {
        results.forEach(result -> {
            this.service.saveEventResult(result);
        });
    }

    /**
     * Read json file with log event entries and process them to get duration of events, then save the result on database.
     *
     * @param inputFilePath: json file with log entries to be processed.
     */
    public void processEvents(String inputFilePath) {
        List<Event> events = this.getEventsFromJsonFile(inputFilePath);

        if (!events.isEmpty()) {
            EventController.logger.info("Starting to process events.");

            List<String> ids = this.getIds(events);

            List<EventResult> results = new ArrayList<EventResult>();

            for (String id : ids) {
                List<Event> eventsForId = events.stream().filter(event -> event.getId().equals(id)).collect(Collectors.toList());
                Optional<Event> started = eventsForId.stream().filter(event -> event.getState().equals("STARTED")).findFirst();
                Optional<Event> finished = eventsForId.stream().filter(event -> event.getState().equals("FINISHED")).findFirst();

                if (finished.isPresent() && started.isPresent()) {
                    EventResult result = prepareResult(id, started.get(), finished.get());
                    results.add(result);
                    EventController.logger.info(String.format("Event %s processed.", result.getId()));

                }
            }

            this.saveResults(results);
        }
    }

    public void getEventResults() {
        this.service.listAllEvents().forEach(System.out::println);
    }

    public void prepareTable() {
        this.service.createTable();
    }

    public void cleanData() {
        this.service.cleanTable();
    }
}
