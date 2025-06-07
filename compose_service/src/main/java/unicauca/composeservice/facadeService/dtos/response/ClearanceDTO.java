package unicauca.composeservice.facadeService.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ClearanceDTO {
    private String studentCode;
    private String message;
    private List<DebtResponseDTO> debtResponse;
    private List<LabResponseDTO> labResponse;
    private List<SportResponseDTO> sportResponse;
}
