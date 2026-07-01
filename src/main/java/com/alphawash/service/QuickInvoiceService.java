package com.alphawash.service;

import com.alphawash.dto.QuickServiceGroupDto;
import com.alphawash.dto.RecentVehicleDto;
import java.util.List;

public interface QuickInvoiceService {

    List<QuickServiceGroupDto> getGroupedServices();

    List<RecentVehicleDto> getRecentVehicles(int limit);
}
