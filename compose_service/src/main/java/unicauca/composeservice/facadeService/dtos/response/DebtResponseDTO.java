package unicauca.composeservice.facadeService.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DebtResponseDTO {
    private String studentCode;
    private float amount;
    private String reason;
    private String debtDate;
    private String dueDate;
    private String status;

}
