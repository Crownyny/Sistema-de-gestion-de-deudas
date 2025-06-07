package repository

import "lab_service/dto"

type LoanRepository interface {
    GetPendingLoans(loanInputDTO dto.LoanInputDTO) ([]dto.LoanOutputDTO, error)
    ClearLoans(loanInputDTO dto.LoanInputDTO) error
}