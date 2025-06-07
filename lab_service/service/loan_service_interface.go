package service

import "lab_service/dto"

type LoanService interface {
    GetPendingLoans(loanInputDTO dto.LoanInputDTO) ([]dto.LoanOutputDTO, error)
    ClearLoans(loanInputDTO dto.LoanInputDTO) error
}