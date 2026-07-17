package com.alphawash.request;

import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderReceiveRequest {
    private List<ReceiveItem> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReceiveItem {
        private String productCode;
        private Integer receivedQuantity;
    }
}
