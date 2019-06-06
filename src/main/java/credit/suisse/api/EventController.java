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
 * 
 * @author adheli.tavares
 *
 */
public class EventController {

    private static final Logger logger = Logger.getGlobal();
    private EventResultService service = new EventResultService();

    /**
     * Read json file and serializes the data to a list of events.
     * @param inputFilePath
     * @return Event list
     */
    private List<Event> getEventsFromJsonFile(String inputFilePath) {
    	List<Event> events = new ArrayList<Event>();
        try {
        	EventController.logger.info(String.format("Reading input file %s to get json data.", inputFilePath));
            String jsonString = new String(Files.readAllBytes(Paths.get(inputFilePath)));

            Gson gson = new Gson();

            Type listType = new TypeToken<ArrayList<Event>>(){}.getType();
            events = gson.fromJson(jsonString, listType);
        } catch (IOException ioExcp) {
        	EventController.logger.severe(String.format("Error while trying to read input file %s.", inputFilePath));
        }
        
        return events;
    }

    /**
     * Prepare a list for unique id's.
     * @param events
     * @return 
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
     * 
     * @param id
     * @param started
     * @param finished
     * @return
     */
    private EventResult prepareResult(String id, Event started, Event finished) {
    	Long duration = finished.getTimestamp() - started.getTimestamp();

        EventResult result = new EventResult(id, duration.intValue(), (duration > 4));
        result.setHost(started.getHost() != null ? started.getHost() : finished.getHost());
        result.setType(started.getType() != null ? started.getType() : finished.getType());
        
        return result;
    }
    
    private void saveResults(List<EventResult> results) {
    	results.forEach(result -> this.service.saveEventResult(result));
    }

    /**
     * 
     * @param inputFilePath
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
                	EventController.logger.info(String.format("Event %s processed.", result.getId()));
                	
                	results.add(result);
                }
            }
            
            this.saveResults(results);
        }
    }
    
    public List<EventResult> getEventResults() {
    	return this.service.listAllEvents();
    }

}
