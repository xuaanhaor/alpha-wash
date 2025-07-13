package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.*;

import com.alphawash.converter.EmployeeSkillConverter;
import com.alphawash.dto.EmployeeSkillDto;
import com.alphawash.request.EmployeeSkillRequest;
import com.alphawash.response.EmployeeSkillResponse;
import com.alphawash.service.EmployeeSkillService;
import com.alphawash.util.ObjectUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API_EMPLOYEE_SKILL)
@RequiredArgsConstructor
@Tag(name = "Employee Skill", description = "Operations related to employee skills")
public class EmployeeSkillController {

    private final EmployeeSkillService employeeSkillService;

    @Operation(summary = "Get all employee skills")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Successfully retrieved employee skill list"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @GetMapping(ROOT)
    public ResponseEntity<List<EmployeeSkillResponse>> getAll() {
        List<EmployeeSkillDto> dtos = employeeSkillService.getAll();
        return ResponseEntity.ok(EmployeeSkillConverter.INSTANCE.toResponse(dtos));
    }

    @Operation(summary = "Get an employee skill by ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Employee skill found"),
                @ApiResponse(responseCode = "404", description = "Employee skill not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @GetMapping(ID_PATH_PARAMETER)
    public ResponseEntity<EmployeeSkillResponse> getById(@PathVariable Long id) {
        EmployeeSkillDto dto = employeeSkillService.getById(id);
        return dto != null
                ? ResponseEntity.ok(EmployeeSkillConverter.INSTANCE.toResponse(dto))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Create a new employee skill")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Employee skill created successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid request data"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @PostMapping(INSERT_ENDPOINT)
    public ResponseEntity<EmployeeSkillResponse> create(@RequestBody EmployeeSkillRequest request) {
        EmployeeSkillDto dto = EmployeeSkillConverter.INSTANCE.fromRequest(request);
        EmployeeSkillDto saved = employeeSkillService.create(dto);
        return ResponseEntity.ok(EmployeeSkillConverter.INSTANCE.toResponse(saved));
    }

    @PatchMapping(UPDATE_WITH_PATH_PARAMETER)
    @Operation(summary = "Partially update employee-skill")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Update successful"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EmployeeSkillResponse> patchUpdate(
            @PathVariable Long id, @RequestBody EmployeeSkillRequest request) {

        EmployeeSkillDto dto = EmployeeSkillConverter.INSTANCE.fromRequest(request);
        EmployeeSkillDto updated = employeeSkillService.update(id, dto);

        return ObjectUtils.isNotNull(updated)
                ? ResponseEntity.ok(EmployeeSkillConverter.INSTANCE.toResponse(updated))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete an employee skill by ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Employee skill deleted successfully"),
                @ApiResponse(responseCode = "404", description = "Employee skill not found"),
                @ApiResponse(responseCode = "400", description = "Invalid request data"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @DeleteMapping(DELETE_WITH_PATH_PARAMETER)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeSkillService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
