package com.alphawash.service.impl;

import com.alphawash.dto.FavoriteServiceRevenueDto;
import com.alphawash.dto.RevenueDto;
import com.alphawash.repository.RevenueRepository;
import com.alphawash.service.RevenueService;
import com.alphawash.util.DateTimeUtils;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RevenueServiceImpl implements RevenueService {

    private final RevenueRepository revenueRepository;

    @Override
    public List<RevenueDto> getRevenue(String startDate, String endDate, String orderStatus) {
        LocalDate convertedStartDate = DateTimeUtils.convertToLocalDate(startDate);
        LocalDate convertedEndDate = DateTimeUtils.convertToLocalDate(endDate);
        return revenueRepository.getRevenue(convertedStartDate, convertedEndDate, orderStatus);
    }

    @Override
    public List<FavoriteServiceRevenueDto> getFavoriteServiceRevenue(String startDate, String endDate) {
        LocalDate convertedStartDate = DateTimeUtils.convertToLocalDate(startDate);
        LocalDate convertedEndDate = DateTimeUtils.convertToLocalDate(endDate);
        return revenueRepository.getFavoriteServiceRevenue(convertedStartDate, convertedEndDate);
    }
}
