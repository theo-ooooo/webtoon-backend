package com.webtoon.domain.notice.service;

import com.webtoon.domain.notice.dto.request.NoticeCreateRequest;
import com.webtoon.domain.notice.dto.response.NoticeDetailResponse;
import com.webtoon.domain.notice.dto.response.NoticeListResponse;
import com.webtoon.domain.notice.entity.Notice;
import com.webtoon.domain.notice.repository.NoticeRepository;
import com.webtoon.global.exception.ApiException;
import com.webtoon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public List<NoticeListResponse> findAll() {
        return noticeRepository.findAllByOrderByIsImportantDescCreatedAtDesc()
                .stream()
                .map(NoticeListResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public NoticeDetailResponse findById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOTICE_NOT_FOUND));
        notice.incrementViewCount();
        return NoticeDetailResponse.from(notice);
    }

    @Transactional
    public Long create(NoticeCreateRequest request) {
        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .isImportant(request.getIsImportant())
                .build();

        return noticeRepository.save(notice).getId();
    }
}
