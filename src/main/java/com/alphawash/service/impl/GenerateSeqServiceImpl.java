package com.alphawash.service.impl;

import com.alphawash.repository.SequenceRepository;
import com.alphawash.service.GenerateSeqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenerateSeqServiceImpl implements GenerateSeqService {

    private final SequenceRepository sequenceRepository;

    @Override
    public String generateSeqCode(String code) {
        return sequenceRepository.generateSeqCode(code);
    }
}
