package com.alphawash.request;

public record AutoLinkExecuteRequest(Boolean dryRun) {
    public boolean isDryRun() {
        return Boolean.TRUE.equals(dryRun);
    }
}
