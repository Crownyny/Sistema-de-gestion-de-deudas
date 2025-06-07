package repository

import "debt_service/dto"

type DebtRepository interface {
    GetPendingDebts(debtInputDTO dto.DebtInputDTO) ([]dto.DebtOutputDTO, error)
    ClearDebts(debtInputDTO dto.DebtInputDTO) error
}
