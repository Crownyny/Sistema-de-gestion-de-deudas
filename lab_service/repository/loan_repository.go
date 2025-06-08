package repository

import (
    "fmt"
    "lab_service/dto"
    "time"
)

type loanRepositoryImpl struct {
    loans []dto.LoanOutputDTO
}

func NewLoanRepository() LoanRepository {
    return &loanRepositoryImpl{
        loans: []dto.LoanOutputDTO{
            {
                StudentCode:         "104622011450",
                LoanDate:            "2024-06-01",
                EstimatedReturnDate: "2024-06-05",
                RealReturnDate:      "2024-06-03",
                Status:              "devuelto",
                Equipment:           "Osciloscopio",
            },
            {
                StudentCode:         "104622022345",
                LoanDate:            "2025-02-15",
                EstimatedReturnDate: "2025-02-20",
                RealReturnDate:      "",
                Status:              "vencido",
                Equipment:           "Generador de señales",
            },
            {
                StudentCode:         "104622022333",
                LoanDate:            "2025-02-15",
                EstimatedReturnDate: "2025-02-20",
                RealReturnDate:      "",
                Status:              "vencido",
                Equipment:           "Arduino",
            },

            {
                StudentCode:         "104622033456",
                LoanDate:            "2025-06-10",
                EstimatedReturnDate: "2025-06-15",
                RealReturnDate:      "",
                Status:              "activo",
                Equipment:           "Multímetro",
            },
        },
    }
}

func (r *loanRepositoryImpl) GetPendingLoans(loanInput dto.LoanInputDTO) ([]dto.LoanOutputDTO, error) {
    var pending []dto.LoanOutputDTO
    found := false
    studentCode := loanInput.StudentCode

    for _, loan := range r.loans {
        if loan.StudentCode == studentCode {
            found = true
            if loan.Status != "devuelto" {
                // Convertir LoanDTO a LoanOutputDTO
                outputDTO := dto.LoanOutputDTO{
                    StudentCode:         loan.StudentCode,
                    LoanDate:            loan.LoanDate,
                    EstimatedReturnDate: loan.EstimatedReturnDate,
                    RealReturnDate:      loan.RealReturnDate,
                    Status:              loan.Status,
                    Equipment:           loan.Equipment,
                }
                pending = append(pending, outputDTO)
            }
        }
    }

    if !found {
        return nil, fmt.Errorf("estudiante con código %s no encontrado", studentCode)
    }

    return pending, nil
}

func (r *loanRepositoryImpl) ClearLoans(loanInput dto.LoanInputDTO) error {
    found := false
    studentCode := loanInput.StudentCode
    
    for i := range r.loans {
        if r.loans[i].StudentCode == studentCode {
            found = true
            r.loans[i].Status = "devuelto"
            
            // Si no tiene fecha de devolución, establecer la fecha actual
            if r.loans[i].RealReturnDate == "" {
                r.loans[i].RealReturnDate = time.Now().Format("2006-01-02")
            }
        }
    }

    if !found {
        return fmt.Errorf("estudiante con código %s no encontrado", studentCode)
    }

    return nil
}