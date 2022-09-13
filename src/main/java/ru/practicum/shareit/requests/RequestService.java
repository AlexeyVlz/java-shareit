package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.NullDataException;
import ru.practicum.shareit.requests.dto.InfoItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;


    @Autowired
    public RequestService(RequestRepository requestRepository, UserService userService) {
        this.requestRepository = requestRepository;
        this.userService = userService;
    }

    public InfoItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId) {
        userService.getUserById(userId);
        ItemRequest itemRequest = requestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, userId));
        return ItemRequestMapper.toInfoItemRequestDto(itemRequest);
    }

    public List<InfoItemRequestDto> getRequestsByUserId(Long userId) {
        userService.getUserById(userId);
        return requestRepository.findAllByUserId(userId)
                .stream()
                .map(ItemRequestMapper::toInfoItemRequestDto)
                .collect(Collectors.toList());
    }

    public List<InfoItemRequestDto> getAllRequests(Long userId, PageRequest pageRequest) {
        userService.getUserById(userId);
        return requestRepository.findAll(pageRequest)
                .stream()
                .map(ItemRequestMapper::toInfoItemRequestDto)
                .collect(Collectors.toList());
    }

    public InfoItemRequestDto getRequestById(Long requestId, Long userId) {
        userService.getUserById(userId);
        ItemRequest itemRequest = requestRepository.findById(requestId)
            .orElseThrow(() -> new NullDataException(String.format("Запрос с id %d в базе не обнаружен", requestId)));
        return ItemRequestMapper.toInfoItemRequestDto(itemRequest);
    }
}
