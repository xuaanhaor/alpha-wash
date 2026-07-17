package com.alphawash.request;

import java.util.List;
import java.util.UUID;

public record MergeVehiclesRequest(UUID primaryVehicleId, UUID primaryCustomerId, List<UUID> duplicateVehicleIds) {}
