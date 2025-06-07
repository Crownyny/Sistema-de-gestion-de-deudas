package service

import (
    "debt_service/dto"
    "debt_service/repository"
)

type debtServiceImpl struct {
    repo repository.DebtRepository
}

func NewDebtService(repo repository.DebtRepository) DebtService {
    return &debtServiceImpl{repo: repo}
}

func (s *debtServiceImpl) GetPendingDebts(debtInputDTO dto.DebtInputDTO) ([]dto.DebtOutputDTO, error) {
    return s.repo.GetPendingDebts(debtInputDTO)
}

func (s *debtServiceImpl) ClearDebts(debtInputDTO dto.DebtInputDTO) error {
    return s.repo.ClearDebts(debtInputDTO)
}
