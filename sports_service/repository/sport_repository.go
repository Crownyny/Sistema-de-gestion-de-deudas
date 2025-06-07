package repository

import (
	"errors"
	"sports_service/dto"
	"time"
)


type sportRepositoryImpl struct {
    items []dto.SportOutputDTO
}

func NewSportRepository() SportRepository {
    return &sportRepositoryImpl{
        items: []dto.SportOutputDTO{
            {
                StudentCode:         "104622011450",
                Item:                "Balón de fútbol",
                LoanDate:            "2025-01-15",
                EstimatedReturnDate: "2025-01-20",
                RealReturnDate:      "",
            },
            {
                StudentCode:         "104624011420",
                Item:                "Raqueta de tenis",
                LoanDate:            "2025-02-03",
                EstimatedReturnDate: "2025-02-07",
                RealReturnDate:      "2025-02-06",
            },
            {
                StudentCode:         "104623018760",
                Item:                "Uniforme de baloncesto",
                LoanDate:            "2025-01-10",
                EstimatedReturnDate: "2025-01-17",
                RealReturnDate:      "2025-01-16",
            },
            {
                StudentCode:         "104622011450",
                Item:                "Red de voleibol",
                LoanDate:            "2025-03-01",
                EstimatedReturnDate: "2025-03-03",
                RealReturnDate:      "",
            },
            {
                StudentCode:         "104621019870",
                Item:                "Balón de baloncesto",
                LoanDate:            "2025-02-15",
                EstimatedReturnDate: "2025-02-20",
                RealReturnDate:      "2025-02-19",
            },
            {
                StudentCode:         "104625017830",
                Item:                "Cronómetro",
                LoanDate:            "2025-03-10",
                EstimatedReturnDate: "2025-03-15",
                RealReturnDate:      "",
            },
            {
                StudentCode:         "104623018760",
                Item:                "Cinta métrica",
                LoanDate:            "2025-04-01",
                EstimatedReturnDate: "2025-04-03",
                RealReturnDate:      "2025-04-02",
            },
            {
                StudentCode:         "104620013340",
                Item:                "Pesas (set)",
                LoanDate:            "2025-01-20",
                EstimatedReturnDate: "2025-01-27",
                RealReturnDate:      "2025-01-25",
            },
            {
                StudentCode:         "104625017830",
                Item:                "Colchoneta",
                LoanDate:            "2025-03-20",
                EstimatedReturnDate: "2025-03-25",
                RealReturnDate:      "",
            },
            {
                StudentCode:         "104621019870",
                Item:                "Peto deportivo",
                LoanDate:            "2025-05-05",
                EstimatedReturnDate: "2025-05-10",
                RealReturnDate:      "",
            },
        },
    }
}
func (r *sportRepositoryImpl) GetUnreturnedItems(sportInputDTO dto.SportInputDTO) ([]dto.SportOutputDTO, error) {
    var result []dto.SportOutputDTO
    found := false

    for _, item := range r.items {
        if item.StudentCode == sportInputDTO.StudentCode {
            found = true
            if item.RealReturnDate == "" {
                result = append(result, dto.SportOutputDTO{
                    Item:                item.Item,
                    LoanDate:            item.LoanDate,
                    EstimatedReturnDate: item.EstimatedReturnDate,
                })
            }
        }
    }

    if !found {
        return nil, errors.New("student not found")
    }

    return result, nil
}

func (r *sportRepositoryImpl) ClearItems(sportInputDTO dto.SportInputDTO) error {
    found := false
    updated := false
    currentDate := time.Now().Format("2006-01-02") // Formato YYYY-MM-DD
    
    for i := range r.items {
        if r.items[i].StudentCode == sportInputDTO.StudentCode {
            found = true
            // Solo actualizamos los items que no han sido devueltos
            if r.items[i].RealReturnDate == "" {
                r.items[i].RealReturnDate = currentDate
                updated = true
            }
        }
    }

    if !found {
        return errors.New("student not found")
    }
    
    if !updated {
        return errors.New("no unreturned items found for this student")
    }

    return nil
}