package unicauca.composeservice.EventsListeners;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import unicauca.composeservice.facadeService.events.ClearanceEvent;
import unicauca.composeservice.facadeService.events.ServiceType;
import unicauca.composeservice.facadeService.services.WebSocketService;

@Component
@RequiredArgsConstructor
public class ClearanceEventListener {

    private final WebSocketService webSocketService;

    @Async
    @EventListener
    public void handleClearanceEvent(ClearanceEvent event) {
        System.out.println("Procesando evento: " + event.getEventType() + " para " + event.getServiceType());

        switch (event.getServiceType()) {
            case ServiceType.DEBT:
                webSocketService.notifyDebtService(event);
                break;
            case ServiceType.LAB:
                webSocketService.notifyLabService(event);
                break;
            case ServiceType.SPORTS:
                webSocketService.notifySportsService(event);
                break;
            default:
                webSocketService.notifyAllServices(event);
                break;
        }
    }
}