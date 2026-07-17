package com.alphawash.request;

import com.alphawash.constant.CustomerGender;
import com.fasterxml.jackson.annotation.JsonAlias;
import java.time.LocalDate;

public record CustomerCreateRequest(
        @JsonAlias("customerName") String name,
        String phone,
        String email,
        CustomerGender gender,
        LocalDate birthday,
        String address,
        String note,
        CustomerVehicleRequest vehicle) {}
