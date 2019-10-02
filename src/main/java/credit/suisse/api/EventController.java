package credit.suisse.api;

import credit.suisse.pojo.EventResult;

import java.util.List;

/**
 * @author adheli.tavares
 */
public interface EventController {

    void prepareEnvironment();
    List<EventResult> showEventResults();
    void cleanEnvironment();
    void processEvents(String inputFilePath);
}
