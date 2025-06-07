package service

import "debt_service/dto"

type DebtService interface {
    GetPendingDebts(debtInputDTO dto.DebtInputDTO) ([]dto.DebtOutputDTO, error)
    ClearDebts(debtInputDTO dto.DebtInputDTO) error
}
