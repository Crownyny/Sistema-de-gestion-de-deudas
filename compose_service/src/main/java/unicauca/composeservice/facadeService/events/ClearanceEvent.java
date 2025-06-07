package unicauca.composeservice.facadeService.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import unicauca.composeservice.facadeService.dtos.response.ClearanceDTO;

@Data
@AllArgsConstructor
public class ClearanceEvent {
    private EventType eventType;
    private ServiceType serviceType;
    private ClearanceDTO clearanceDTO;
}
