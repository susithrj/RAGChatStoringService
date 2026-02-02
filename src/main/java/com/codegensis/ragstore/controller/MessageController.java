package com.codegensis.ragstore.controller;

import com.codegensis.ragstore.dto.request.AddMessageRequest;
import com.codegensis.ragstore.dto.response.MessagePageResponse;
import com.codegensis.ragstore.dto.response.MessageResponse;
import com.codegensis.ragstore.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions/{sessionId}/messages")
@Tag(name = "Messages", description = "Message management APIs")
public class MessageController {
    
    private final MessageService messageService;
    
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }
    
    @PostMapping
    @Operation(summary = "Add message to session", description = "Adds a new message to a session")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Message created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Session not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<MessageResponse> addMessage(
            @PathVariable Long sessionId,
            @Valid @RequestBody AddMessageRequest request) {
        MessageResponse response = messageService.addMessage(sessionId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get messages from session", description = "Retrieves messages from a session with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Session not found"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<MessagePageResponse> getMessages(
            @PathVariable Long sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        MessagePageResponse response = messageService.getMessages(sessionId, page, size);
        return ResponseEntity.ok(response);
    }
}
