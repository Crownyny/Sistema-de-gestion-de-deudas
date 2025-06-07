package unicauca.composeservice.facadeService.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SportResponseDTO {
    private String studentCode;
    private String item;
    private String loanDate;
    private String estimatedReturnDate;
    private String realReturnDate;
}
