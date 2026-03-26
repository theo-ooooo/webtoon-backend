package com.webtoon.domain.coin.service;

import com.webtoon.domain.coin.dto.request.CoinChargeRequest;
import com.webtoon.domain.coin.dto.response.CoinTransactionResponse;
import com.webtoon.domain.coin.entity.CoinTransaction;
import com.webtoon.domain.coin.repository.CoinTransactionRepository;
import com.webtoon.domain.user.entity.User;
import com.webtoon.domain.user.repository.UserRepository;
import com.webtoon.global.enums.CoinTransactionType;
import com.webtoon.global.exception.ApiException;
import com.webtoon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoinService {

    private final UserRepository userRepository;
    private final CoinTransactionRepository coinTransactionRepository;

    @Transactional
    public Integer charge(Long userId, CoinChargeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        user.chargeCoin(request.getAmount());

        coinTransactionRepository.save(CoinTransaction.builder()
                .user(user)
                .type(CoinTransactionType.CHARGE)
                .amount(request.getAmount())
                .reason("코인 충전")
                .build());

        return user.getCoinBalance();
    }

    public Page<CoinTransactionResponse> getTransactions(Long userId, Pageable pageable) {
        return coinTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(CoinTransactionResponse::from);
    }
}
