package credit.suisse.dao;

import credit.suisse.pojo.EventResult;

import java.util.List;

public interface EventResultService {

    void createTable();
    void cleanTable();
    void saveEventResult(EventResult result);
    List<EventResult> listAllEvents();
}
