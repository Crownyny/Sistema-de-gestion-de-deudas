package service

import "sports_service/dto"

type SportService interface {
    GetUnreturnedItems(sportInputDTO dto.SportInputDTO ) ([]dto.SportOutputDTO, error)
    ClearItems(sportInputDTO dto.SportInputDTO) error
}
