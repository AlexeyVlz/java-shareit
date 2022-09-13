package ru.practicum.shareit.responses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;



@Service
public class ResponseService {

    private final ResponseRepository responseRepository;

    @Autowired
    public ResponseService(ResponseRepository responseRepository) {
        this.responseRepository = responseRepository;
    }


    public void addResponseByRequestId(Item item, Long requestId) {
        Response response = new Response(item.getId(), requestId, item.getName(), item.getOwner().getId());
        responseRepository.save(response);
    }
}
