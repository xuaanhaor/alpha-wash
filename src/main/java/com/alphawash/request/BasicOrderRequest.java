package com.alphawash.request;

import org.springframework.lang.Nullable;

public record BasicOrderRequest(
        BasicInformationRequest information,
        BasicVehicleRequest vehicle,
        BasicServiceRequest service,
        @Nullable BasicCustomerRequest customer) {}
