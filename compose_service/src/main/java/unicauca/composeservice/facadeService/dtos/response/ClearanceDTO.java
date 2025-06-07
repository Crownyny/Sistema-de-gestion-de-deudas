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

    public static ClearanceDTO fromGeneric(String message, String studentCode,List<?> response) {
        ClearanceDTO.ClearanceDTOBuilder builder = ClearanceDTO.builder()
                .message(message);
        builder.studentCode(studentCode);

        if (!response.isEmpty()) {
            Object first = response.get(0);
            switch (first) {
                case DebtResponseDTO debtResponseDTO -> builder.debtResponse((List<DebtResponseDTO>) response);
                case LabResponseDTO labResponseDTO -> builder.labResponse((List<LabResponseDTO>) response);
                case SportResponseDTO sportResponseDTO -> builder.sportResponse((List<SportResponseDTO>) response);
                default ->
                        throw new IllegalArgumentException("Unsupported response type: " + first.getClass().getName());
            }
        }

        return builder.build();
    }
}
