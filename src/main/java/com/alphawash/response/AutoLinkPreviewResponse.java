package com.alphawash.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoLinkPreviewResponse {
    private int totalUnlinked;
    private int safeToLink;
    private int conflicts;
    private int noOrders;
    private List<AutoLinkItemResponse> previewItems;
    private List<AutoLinkConflictItemResponse> conflictItems;
}
