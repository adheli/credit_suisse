package credit.suisse.dao;

import credit.suisse.pojo.EventResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventResultService {

    @Autowired
    private EventResultRepository repository;

    public EventResult save(EventResult result) {
        return repository.save(result);
    }
}
