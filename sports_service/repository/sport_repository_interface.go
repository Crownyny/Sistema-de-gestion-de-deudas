package repository

import "sports_service/dto"

type SportRepository interface {
    GetUnreturnedItems(sportInputDTO dto.SportInputDTO) ([]dto.SportOutputDTO, error)
    ClearItems(sportInputDTO dto.SportInputDTO) error
}
