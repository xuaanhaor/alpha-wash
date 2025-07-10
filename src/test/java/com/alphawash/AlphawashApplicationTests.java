package com.alphawash;

import com.alphawash.endpoint.ProductionController;
import com.alphawash.entity.Production;
import com.alphawash.response.ProductionResponse;
import com.alphawash.service.ProductionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

@SpringBootTest
class AlphawashApplicationTests {
    @Mock ProductionService productionService;
    @InjectMocks
    ProductionController productionController;

    @Test
    void testGetAllProductions1() {
        Mockito.when(productionService.getAllProductions())
                .thenReturn(Collections.emptyList());
        var resp = productionController.getAllProductions();
        Assertions.assertEquals(0, resp.getBody().size());
    }
}
