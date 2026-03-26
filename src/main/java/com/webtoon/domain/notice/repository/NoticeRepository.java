package com.webtoon.domain.notice.repository;

import com.webtoon.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findAllByOrderByIsImportantDescCreatedAtDesc();
}
