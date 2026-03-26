package com.webtoon.domain.event.service;

import com.webtoon.domain.event.dto.request.EventCreateRequest;
import com.webtoon.domain.event.dto.response.EventResponse;
import com.webtoon.domain.event.entity.Event;
import com.webtoon.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;

    public List<EventResponse> findActiveEvents() {
        return eventRepository.findActiveEvents(LocalDate.now())
                .stream()
                .map(EventResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long create(EventCreateRequest request) {
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .linkUrl(request.getLinkUrl())
                .bgColor(request.getBgColor())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        return eventRepository.save(event).getId();
    }
}
