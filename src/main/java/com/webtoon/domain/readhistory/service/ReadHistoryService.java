package com.webtoon.domain.readhistory.service;

import com.webtoon.domain.readhistory.dto.response.ReadHistoryResponse;
import com.webtoon.domain.readhistory.repository.ReadHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadHistoryService {

    private final ReadHistoryRepository readHistoryRepository;

    public List<ReadHistoryResponse> getReadHistory(Long userId) {
        return readHistoryRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(ReadHistoryResponse::from)
                .toList();
    }
}
