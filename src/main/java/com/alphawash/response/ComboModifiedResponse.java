package com.alphawash.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComboModifiedResponse {
    private Long id;
    private String comboCode;
    private String comboName;
    private String note;
    private String price;
    private List<String> serviceCatalogCodes;
}
