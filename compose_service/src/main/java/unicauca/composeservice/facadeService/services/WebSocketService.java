package unicauca.composeservice.facadeService.services;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import unicauca.composeservice.facadeService.events.ClearanceEvent;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyDebtService(ClearanceEvent event) {
        messagingTemplate.convertAndSend("/debt-service", event);
    }

    public void notifyLabService(ClearanceEvent event) {
        messagingTemplate.convertAndSend("/lab_service", event);
    }

    public void notifySportsService(ClearanceEvent event) {
        messagingTemplate.convertAndSend("/sports-service", event);
    }

    public void notifyAllServices(ClearanceEvent event) {
        messagingTemplate.convertAndSend("/notify", event);
    }
}