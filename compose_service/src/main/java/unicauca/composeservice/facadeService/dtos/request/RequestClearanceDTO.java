package unicauca.composeservice.facadeService.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestClearanceDTO {
    private boolean debtService;
    private boolean labService;
    private boolean sportService;
    private InfoStudentDTO infoStudent;
}
