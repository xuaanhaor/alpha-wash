package com.alphawash.response;

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
public class AutoLinkExecuteResponse {
    private int linked;
    private int skipped;
    private int conflicts;
}
