package service

import (
	"lab_service/dto"
	"lab_service/repository"
)

type loanServiceImpl struct {
	repo repository.LoanRepository
}

func NewLoanService(repo repository.LoanRepository) LoanService {
	return &loanServiceImpl{repo: repo}
}

func (s *loanServiceImpl) GetPendingLoans(loanInputDTO dto.LoanInputDTO) ([]dto.LoanOutputDTO, error) {
	return s.repo.GetPendingLoans(loanInputDTO)
}

func (s *loanServiceImpl) ClearLoans(loanInputDTO dto.LoanInputDTO) error {
	return s.repo.ClearLoans(loanInputDTO)
}
