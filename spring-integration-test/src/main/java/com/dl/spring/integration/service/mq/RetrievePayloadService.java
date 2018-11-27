package com.dl.spring.integration.service.mq;

import org.springframework.stereotype.Service;

@Service
public class RetrievePayloadService {
    public String getPayload(String id){
        // retrieve the payload for the specified id ...
        return "Payload for " + id;
    }
}
