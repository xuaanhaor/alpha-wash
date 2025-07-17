package com.alphawash.request;

public record BasicOrderRequest(
        BasicInformationRequest information,
        BasicVehicleRequest vehicle,
        BasicServiceRequest service,
        BasicCustomerRequest customer) {}
