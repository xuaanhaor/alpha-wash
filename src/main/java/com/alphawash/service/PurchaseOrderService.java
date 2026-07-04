package com.alphawash.service;

import com.alphawash.dto.PurchaseOrderDto;
import com.alphawash.request.PurchaseOrderReceiveRequest;
import com.alphawash.request.PurchaseOrderRequest;
import java.util.List;

public interface PurchaseOrderService {
    List<PurchaseOrderDto> getAll();

    PurchaseOrderDto getByCode(String code);

    PurchaseOrderDto create(PurchaseOrderRequest request);

    PurchaseOrderDto update(String code, PurchaseOrderRequest request);

    PurchaseOrderDto receive(String code, PurchaseOrderReceiveRequest request);

    void cancel(String code);
}
