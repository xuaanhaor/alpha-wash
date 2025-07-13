package com.alphawash.request;

import com.alphawash.entity.Brand;
import com.alphawash.entity.Customer;
import com.alphawash.entity.Model;
import java.util.UUID;

public record VehicleRequest(
        UUID id, Customer customer, String licensePlate, Brand brand, Model model, String imageUrl, String note) {}
