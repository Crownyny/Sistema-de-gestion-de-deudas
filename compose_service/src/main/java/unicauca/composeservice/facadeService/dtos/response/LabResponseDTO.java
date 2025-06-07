package unicauca.composeservice.facadeService.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LabResponseDTO {
    private String studentCode;
    private String loanDate;
    private String estimatedReturnDate;
    private String realReturnDate;
    private String status;
    private String equipment;
}
