package com.alphawash.service;

import com.alphawash.request.AutoLinkExecuteRequest;
import com.alphawash.response.AutoLinkExecuteResponse;
import com.alphawash.response.AutoLinkPreviewResponse;

public interface AutoLinkVehicleService {

    AutoLinkPreviewResponse preview();

    AutoLinkExecuteResponse execute(AutoLinkExecuteRequest request);
}
