package com.codegensis.ragstore.mapper;

import com.codegensis.ragstore.dto.response.MessageResponse;
import com.codegensis.ragstore.entity.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageMapper {
    
    public MessageResponse toResponse(Message message) {
        if (message == null) {
            return null;
        }
        return new MessageResponse(
            message.getId(),
            message.getSessionId(),
            message.getSender().getDbValue(),
            message.getContent(),
            message.getContext(),
            message.getTimestamp()
        );
    }
    
    public List<MessageResponse> toResponseList(List<Message> messages) {
        return messages.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}
