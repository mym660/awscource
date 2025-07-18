package com.awsbasics.simpleapp.controller;

import com.awsbasics.simpleapp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import org.springframework.beans.factory.annotation.Value;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Value("${aws.lambda.data-consistency-function}")
    private String dataConsistencyFunctionName;

    @PostMapping("/subscriptions/{email}")
    public ResponseEntity<?> subscribeEmail(@PathVariable String email) {
        return notificationService.subscribeEmail(email);
    }

    @DeleteMapping("/subscriptions/{email}")
    public ResponseEntity<?> unsubscribeEmail(@PathVariable String email) {
        return notificationService.unsubscribeEmail(email);
    }

    @PostMapping("/data-consistency/check")
    public ResponseEntity<?> checkDataConsistency() {
        try {
            AWSLambda lambdaClient = AWSLambdaClientBuilder.defaultClient();
            String payload = "{\"detail-type\": \"web-app\"}";
            InvokeRequest request = new InvokeRequest()
                    .withFunctionName(dataConsistencyFunctionName)
                    .withPayload(payload);
            InvokeResult result = lambdaClient.invoke(request);
            String responseJson = new String(result.getPayload().array(), StandardCharsets.UTF_8);
            return ResponseEntity.ok(responseJson);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lambda invocation failed: " + e.getMessage());
        }
    }
}
