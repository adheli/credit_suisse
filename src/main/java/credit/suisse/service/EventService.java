package credit.suisse.service;

import credit.suisse.pojo.EventResult;

import java.util.List;

/**
 * @author adheli.tavares
 */
public interface EventService {
    void createTable();
    void cleanTable();
    void saveEventResult(EventResult result);
    List<EventResult> listAllEvents();
}
