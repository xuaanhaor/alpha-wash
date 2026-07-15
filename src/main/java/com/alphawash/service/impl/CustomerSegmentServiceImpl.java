package com.alphawash.service.impl;

import com.alphawash.dto.CustomerSegmentDto;
import com.alphawash.dto.CustomerSegmentDto.*;
import com.alphawash.entity.CustomerSegment;
import com.alphawash.entity.CustomerSegmentMembership;
import com.alphawash.repository.CustomerMetricsRepository;
import com.alphawash.repository.CustomerSegmentMembershipRepository;
import com.alphawash.repository.CustomerSegmentRepository;
import com.alphawash.request.CustomerSegmentRequest;
import com.alphawash.service.CustomerSegmentService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerSegmentServiceImpl implements CustomerSegmentService {

    private final CustomerSegmentRepository segmentRepo;
    private final CustomerSegmentMembershipRepository membershipRepo;
    private final CustomerMetricsRepository metricsRepo;
    private final ObjectMapper objectMapper;

    @Override
    public List<CustomerSegmentDto> getAllSegments() {
        var segments = segmentRepo.findByDeleteFlagFalseOrderByDisplayOrderAsc();
        return segments.stream().map(s -> toDto(s, membershipRepo.countBySegmentCode(s.getCode()))).toList();
    }

    @Override
    @Transactional
    public CustomerSegmentDto createSegment(CustomerSegmentRequest request) {
        if (segmentRepo.existsByCodeAndDeleteFlagFalse(request.code())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Segment code already exists");
        }

        var segment = CustomerSegment.builder()
                .code(request.code())
                .segmentName(request.segmentName())
                .description(request.description())
                .color(request.color())
                .icon(request.icon())
                .conditions(request.conditions())
                .logicOperator(request.logicOperator() != null ? request.logicOperator() : "AND")
                .isSystem(false)
                .displayOrder(request.displayOrder() != null ? request.displayOrder() : 0)
                .isActive(request.isActive() != null ? request.isActive() : true)
                .build();
        segment.setDeleteFlag(false);

        segment = segmentRepo.save(segment);
        recomputeSegment(segment.getCode());
        return toDto(segment, membershipRepo.countBySegmentCode(segment.getCode()));
    }

    @Override
    @Transactional
    public CustomerSegmentDto updateSegment(Long id, CustomerSegmentRequest request) {
        var segment = segmentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Segment not found"));

        if (request.segmentName() != null) segment.setSegmentName(request.segmentName());
        if (request.description() != null) segment.setDescription(request.description());
        if (request.color() != null) segment.setColor(request.color());
        if (request.icon() != null) segment.setIcon(request.icon());
        if (request.conditions() != null) segment.setConditions(request.conditions());
        if (request.logicOperator() != null) segment.setLogicOperator(request.logicOperator());
        if (request.displayOrder() != null) segment.setDisplayOrder(request.displayOrder());
        if (request.isActive() != null) segment.setIsActive(request.isActive());

        segment = segmentRepo.save(segment);
        recomputeSegment(segment.getCode());
        return toDto(segment, membershipRepo.countBySegmentCode(segment.getCode()));
    }

    @Override
    @Transactional
    public void deleteSegment(Long id) {
        var segment = segmentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Segment not found"));
        segment.setDeleteFlag(true);
        segmentRepo.save(segment);
        membershipRepo.deleteBySegmentCode(segment.getCode());
    }

    @Override
    @Transactional
    public void recomputeAllSegments() {
        var segments = segmentRepo.findByDeleteFlagFalseAndIsActiveTrueOrderByDisplayOrderAsc();
        var allMetrics = metricsRepo.computeAllCustomerMetrics();

        for (var segment : segments) {
            recomputeSegmentInternal(segment, allMetrics);
        }
    }

    @Override
    @Transactional
    public void recomputeSegment(String segmentCode) {
        var segment = segmentRepo.findByCodeAndDeleteFlagFalse(segmentCode).orElse(null);
        if (segment == null || !Boolean.TRUE.equals(segment.getIsActive())) return;

        var allMetrics = metricsRepo.computeAllCustomerMetrics();
        recomputeSegmentInternal(segment, allMetrics);
    }

    private void recomputeSegmentInternal(CustomerSegment segment, List<CustomerMetrics> allMetrics) {
        List<RuleCondition> rules = parseConditions(segment.getConditions());
        boolean useAnd = !"OR".equalsIgnoreCase(segment.getLogicOperator());

        membershipRepo.deleteBySegmentCode(segment.getCode());

        List<CustomerSegmentMembership> memberships = new ArrayList<>();
        for (var metrics : allMetrics) {
            if (evaluateRules(metrics, rules, useAnd)) {
                memberships.add(CustomerSegmentMembership.builder()
                        .segmentCode(segment.getCode())
                        .customerId(metrics.getCustomerId())
                        .matchedAt(LocalDateTime.now())
                        .build());
            }
        }

        if (!memberships.isEmpty()) {
            membershipRepo.saveAll(memberships);
        }
    }

    @Override
    public SegmentPreview previewSegment(CustomerSegmentRequest request) {
        List<RuleCondition> rules = parseConditions(request.conditions());
        boolean useAnd = !"OR".equalsIgnoreCase(request.logicOperator());
        var allMetrics = metricsRepo.computeAllCustomerMetrics();

        var matched = allMetrics.stream()
                .filter(m -> evaluateRules(m, rules, useAnd))
                .toList();

        var preview = matched.stream()
                .sorted(Comparator.comparing(CustomerMetrics::getTotalSpending).reversed())
                .limit(20)
                .map(m -> PreviewCustomer.builder()
                        .customerId(m.getCustomerId())
                        .customerName(m.getCustomerName())
                        .phone(m.getPhone())
                        .totalSpending(m.getTotalSpending())
                        .visitCount(m.getVisitCount())
                        .build())
                .toList();

        return SegmentPreview.builder()
                .segmentName(request.segmentName())
                .matchCount(matched.size())
                .customers(preview)
                .build();
    }

    @Override
    public SegmentDashboard getSegmentDashboard() {
        var segments = segmentRepo.findByDeleteFlagFalseAndIsActiveTrueOrderByDisplayOrderAsc();
        var allMetrics = metricsRepo.computeAllCustomerMetrics();

        var summaries = segments.stream().map(s -> {
            int count = membershipRepo.countBySegmentCode(s.getCode());
            return SegmentSummary.builder()
                    .code(s.getCode())
                    .segmentName(s.getSegmentName())
                    .color(s.getColor())
                    .icon(s.getIcon())
                    .customerCount(count)
                    .lastMonthCount(0)
                    .growth(0)
                    .build();
        }).toList();

        var topSpenders = allMetrics.stream()
                .sorted(Comparator.comparing(CustomerMetrics::getTotalSpending).reversed())
                .limit(10)
                .map(m -> PreviewCustomer.builder()
                        .customerId(m.getCustomerId())
                        .customerName(m.getCustomerName())
                        .phone(m.getPhone())
                        .totalSpending(m.getTotalSpending())
                        .visitCount(m.getVisitCount())
                        .build())
                .toList();

        var atRisk = allMetrics.stream()
                .filter(m -> m.getDaysSinceLastVisit() >= 60 && m.getDaysSinceLastVisit() < 9999 && m.getVisitCount() >= 2)
                .sorted(Comparator.comparing(CustomerMetrics::getTotalSpending).reversed())
                .limit(10)
                .map(m -> PreviewCustomer.builder()
                        .customerId(m.getCustomerId())
                        .customerName(m.getCustomerName())
                        .phone(m.getPhone())
                        .totalSpending(m.getTotalSpending())
                        .visitCount(m.getVisitCount())
                        .build())
                .toList();

        return SegmentDashboard.builder()
                .segments(summaries)
                .topSpenders(topSpenders)
                .atRiskCustomers(atRisk)
                .build();
    }

    @Override
    public List<CustomerWithSegments> getCustomersWithSegments() {
        var allMetrics = metricsRepo.computeAllCustomerMetrics();
        var segments = segmentRepo.findByDeleteFlagFalseAndIsActiveTrueOrderByDisplayOrderAsc();

        Map<String, SegmentBadge> badgeMap = new HashMap<>();
        for (var s : segments) {
            badgeMap.put(s.getCode(), SegmentBadge.builder()
                    .code(s.getCode())
                    .segmentName(s.getSegmentName())
                    .color(s.getColor())
                    .icon(s.getIcon())
                    .build());
        }

        Map<UUID, List<String>> customerSegments = new HashMap<>();
        for (var s : segments) {
            var memberIds = membershipRepo.findCustomerIdsBySegmentCode(s.getCode());
            for (var cid : memberIds) {
                customerSegments.computeIfAbsent(cid, k -> new ArrayList<>()).add(s.getCode());
            }
        }

        return allMetrics.stream().map(m -> CustomerWithSegments.builder()
                .customerId(m.getCustomerId())
                .customerName(m.getCustomerName())
                .phone(m.getPhone())
                .segments(customerSegments.getOrDefault(m.getCustomerId(), List.of()).stream()
                        .map(badgeMap::get)
                        .filter(Objects::nonNull)
                        .toList())
                .build()
        ).toList();
    }

    @Override
    public List<SegmentBadge> getSegmentsForCustomer(UUID customerId) {
        var memberships = membershipRepo.findByCustomerId(customerId);
        var segments = segmentRepo.findByDeleteFlagFalseAndIsActiveTrueOrderByDisplayOrderAsc();
        var segMap = segments.stream().collect(Collectors.toMap(CustomerSegment::getCode, s -> s));

        return memberships.stream()
                .map(m -> segMap.get(m.getSegmentCode()))
                .filter(Objects::nonNull)
                .map(s -> SegmentBadge.builder()
                        .code(s.getCode())
                        .segmentName(s.getSegmentName())
                        .color(s.getColor())
                        .icon(s.getIcon())
                        .build())
                .toList();
    }

    // --- Rule Engine ---

    private record RuleCondition(String field, String operator, String value) {}

    private List<RuleCondition> parseConditions(String conditionsJson) {
        if (conditionsJson == null || conditionsJson.isBlank()) return List.of();
        try {
            var node = objectMapper.readTree(conditionsJson);
            if (node.has("rules") && node.get("rules").isArray()) {
                List<RuleCondition> rules = new ArrayList<>();
                for (var rule : node.get("rules")) {
                    rules.add(new RuleCondition(
                            rule.get("field").asText(),
                            rule.get("operator").asText(),
                            rule.get("value").asText()
                    ));
                }
                return rules;
            }
        } catch (Exception e) {
            log.warn("Failed to parse segment conditions: {}", e.getMessage());
        }
        return List.of();
    }

    private boolean evaluateRules(CustomerMetrics metrics, List<RuleCondition> rules, boolean useAnd) {
        if (rules.isEmpty()) return false;

        for (var rule : rules) {
            boolean match = evaluateRule(metrics, rule);
            if (useAnd && !match) return false;
            if (!useAnd && match) return true;
        }
        return useAnd;
    }

    private boolean evaluateRule(CustomerMetrics metrics, RuleCondition rule) {
        try {
            return switch (rule.field()) {
                case "visit_count" -> compareNumber(metrics.getVisitCount(), rule.operator(), rule.value());
                case "days_since_last_visit" -> compareNumber(metrics.getDaysSinceLastVisit(), rule.operator(), rule.value());
                case "total_spending" -> compareBigDecimal(metrics.getTotalSpending(), rule.operator(), rule.value());
                case "avg_invoice" -> compareBigDecimal(metrics.getAvgInvoice(), rule.operator(), rule.value());
                case "total_invoices" -> compareNumber(metrics.getTotalInvoices(), rule.operator(), rule.value());
                case "vehicle_count" -> compareNumber(metrics.getVehicleCount(), rule.operator(), rule.value());
                case "days_since_registration" -> compareNumber(metrics.getDaysSinceRegistration(), rule.operator(), rule.value());
                case "avg_visit_interval" -> compareDouble(metrics.getAvgVisitIntervalDays(), rule.operator(), rule.value());
                case "visits_last_30_days" -> compareNumber(metrics.getVisitsLast30Days(), rule.operator(), rule.value());
                case "visits_last_90_days" -> compareNumber(metrics.getVisitsLast90Days(), rule.operator(), rule.value());
                case "service_type_used" -> containsString(metrics.getServiceTypesUsed(), rule.value());
                case "vehicle_brand" -> containsString(metrics.getVehicleBrands(), rule.value());
                default -> false;
            };
        } catch (Exception e) {
            log.debug("Rule evaluation failed for field {}: {}", rule.field(), e.getMessage());
            return false;
        }
    }

    private boolean compareNumber(long actual, String operator, String value) {
        long target = Long.parseLong(value);
        return switch (operator) {
            case ">=" -> actual >= target;
            case "<=" -> actual <= target;
            case ">" -> actual > target;
            case "<" -> actual < target;
            case "==" -> actual == target;
            case "!=" -> actual != target;
            default -> false;
        };
    }

    private boolean compareBigDecimal(BigDecimal actual, String operator, String value) {
        BigDecimal target = new BigDecimal(value);
        int cmp = actual.compareTo(target);
        return switch (operator) {
            case ">=" -> cmp >= 0;
            case "<=" -> cmp <= 0;
            case ">" -> cmp > 0;
            case "<" -> cmp < 0;
            case "==" -> cmp == 0;
            case "!=" -> cmp != 0;
            default -> false;
        };
    }

    private boolean compareDouble(double actual, String operator, String value) {
        double target = Double.parseDouble(value);
        return switch (operator) {
            case ">=" -> actual >= target;
            case "<=" -> actual <= target;
            case ">" -> actual > target;
            case "<" -> actual < target;
            case "==" -> actual == target;
            case "!=" -> actual != target;
            default -> false;
        };
    }

    private boolean containsString(List<String> list, String value) {
        if (list == null || value == null) return false;
        return list.stream().anyMatch(s -> s.trim().equalsIgnoreCase(value.trim()));
    }

    private CustomerSegmentDto toDto(CustomerSegment s, int count) {
        return CustomerSegmentDto.builder()
                .id(s.getId())
                .code(s.getCode())
                .segmentName(s.getSegmentName())
                .description(s.getDescription())
                .color(s.getColor())
                .icon(s.getIcon())
                .conditions(s.getConditions())
                .logicOperator(s.getLogicOperator())
                .isSystem(s.getIsSystem())
                .displayOrder(s.getDisplayOrder())
                .isActive(s.getIsActive())
                .customerCount(count)
                .build();
    }
}
