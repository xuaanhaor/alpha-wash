package com.alphawash.service;

import com.alphawash.dto.FavoriteServiceRevenueDto;
import com.alphawash.dto.RevenueDto;
import java.util.List;

public interface RevenueService {
    List<RevenueDto> getRevenue(String startDate, String endDate, String orderStatus);

    List<FavoriteServiceRevenueDto> getFavoriteServiceRevenue(String startDate, String endDate);
}
