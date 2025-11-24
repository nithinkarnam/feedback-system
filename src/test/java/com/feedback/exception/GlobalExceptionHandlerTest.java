package com.feedback.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

public class GlobalExceptionHandlerTest {

    @Test
    void handleRuntimeReturnsBadRequestBody() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        RuntimeException ex = new RuntimeException("sample error");
        ResponseEntity<?> resp = handler.handleRuntime(ex);
        assertEquals(400, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) resp.getBody();
        assertTrue(body.containsKey("error"));
        assertEquals("sample error", body.get("error"));
    }
}
