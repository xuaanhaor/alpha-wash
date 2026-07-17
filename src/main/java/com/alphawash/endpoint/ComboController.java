package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.API_SERVICE_COMBO;

import com.alphawash.constant.Constant;
import com.alphawash.dto.ServiceComboDto;
import com.alphawash.service.ComboService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API_SERVICE_COMBO)
@RequiredArgsConstructor
@Tag(name = "Service Combo", description = "Combo management")
public class ComboController {

    private final ComboService comboService;

    @GetMapping
    public ResponseEntity<List<ServiceComboDto>> search() {
        var result = comboService.getAllCombos();
        return ResponseEntity.ok(result);
    }

    @PostMapping(Constant.DELETE_ENDPOINT)
    public ResponseEntity<Integer> delete(@RequestBody List<String> codes) {
        var result = comboService.deleteComboByCode(codes);
        return ResponseEntity.ok(result);
    }
}
