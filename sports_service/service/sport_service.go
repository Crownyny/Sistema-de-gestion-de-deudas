package service

import (
    "sports_service/dto"
    "sports_service/repository"
)

type sportServiceImpl struct {
    repo repository.SportRepository
}

func NewSportService(repo repository.SportRepository) SportService {
    return &sportServiceImpl{repo: repo}
}

func (s *sportServiceImpl) GetUnreturnedItems(sportInputDTO dto.SportInputDTO) ([]dto.SportOutputDTO, error) {
    return s.repo.GetUnreturnedItems(sportInputDTO)
}

func (s *sportServiceImpl) ClearItems(sportInputDTO dto.SportInputDTO) error {
    return s.repo.ClearItems(sportInputDTO)
}
