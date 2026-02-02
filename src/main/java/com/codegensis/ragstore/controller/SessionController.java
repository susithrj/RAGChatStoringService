package com.codegensis.ragstore.controller;

import com.codegensis.ragstore.dto.request.CreateSessionRequest;
import com.codegensis.ragstore.dto.request.ToggleFavoriteRequest;
import com.codegensis.ragstore.dto.request.UpdateSessionRequest;
import com.codegensis.ragstore.dto.response.SessionListResponse;
import com.codegensis.ragstore.dto.response.SessionResponse;
import com.codegensis.ragstore.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
@Tag(name = "Sessions", description = "Session management APIs")
public class SessionController {
    
    private final SessionService sessionService;
    
    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }
    
    @PostMapping
    @Operation(summary = "Create a new session", description = "Creates a new chat session for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Session created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody CreateSessionRequest request) {
        SessionResponse response = sessionService.createSession(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get sessions by user ID", description = "Retrieves all sessions for a given user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user ID"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SessionListResponse> getSessions(@RequestParam String userId) {
        List<SessionResponse> sessions = sessionService.getSessionsByUserId(userId);
        SessionListResponse response = new SessionListResponse(sessions, (long) sessions.size());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{sessionId}")
    @Operation(summary = "Get session by ID", description = "Retrieves a specific session by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Session retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Session not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SessionResponse> getSession(@PathVariable Long sessionId) {
        SessionResponse response = sessionService.getSessionById(sessionId);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{sessionId}")
    @Operation(summary = "Rename session", description = "Updates the title of a session")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Session updated successfully"),
        @ApiResponse(responseCode = "404", description = "Session not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SessionResponse> updateSession(
            @PathVariable Long sessionId,
            @Valid @RequestBody UpdateSessionRequest request) {
        SessionResponse response = sessionService.updateSessionTitle(sessionId, request);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{sessionId}/favorite")
    @Operation(summary = "Toggle favorite", description = "Marks or unmarks a session as favorite")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Favorite status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Session not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SessionResponse> toggleFavorite(
            @PathVariable Long sessionId,
            @Valid @RequestBody ToggleFavoriteRequest request) {
        SessionResponse response = sessionService.toggleFavorite(sessionId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{sessionId}")
    @Operation(summary = "Delete session", description = "Deletes a session and all its messages")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Session deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Session not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> deleteSession(@PathVariable Long sessionId) {
        sessionService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}
