package repository

import (
    "errors"
    "debt_service/dto"
)

type debtRepositoryImpl struct {
    debts []dto.DebtOutputDTO
}

func NewDebtRepository() DebtRepository {
    return &debtRepositoryImpl{
        debts: []dto.DebtOutputDTO{
            {
                StudentCode: "104622011450",
                Amount:      50000,
                Reason:      "Mora en pago de matrícula",
                DebtDate:    "2024-05-10",
                DueDate:     "2024-06-01",
                Status:      "pendiente",
            },
            {
                StudentCode: "104321123450",
                Amount:      15000,
                Reason:      "Pérdida de material",
                DebtDate:    "2024-04-20",
                DueDate:     "2024-05-15",
                Status:      "pagada",
            },
            {
                StudentCode: "104789567890",
                Amount:      35000,
                Reason:      "Daño a equipo de laboratorio",
                DebtDate:    "2024-03-15",
                DueDate:     "2024-04-15",
                Status:      "en mora",
            },
            {
                StudentCode: "104622011450",
                Amount:      12500,
                Reason:      "No devolución de libro biblioteca",
                DebtDate:    "2024-04-05",
                DueDate:     "2024-05-05",
                Status:      "pendiente",
            },
            {
                StudentCode: "104456789120",
                Amount:      75000,
                Reason:      "Mora en pago de curso de verano",
                DebtDate:    "2024-05-20",
                DueDate:     "2024-06-20",
                Status:      "pendiente",
            },
            {
                StudentCode: "104987123450",
                Amount:      25000,
                Reason:      "Cargo por certificado académico",
                DebtDate:    "2024-02-10",
                DueDate:     "2024-03-10",
                Status:      "pagada",
            },
            {
                StudentCode: "104321123450",
                Amount:      60000,
                Reason:      "Daño a inmobiliario universitario",
                DebtDate:    "2024-01-25",
                DueDate:    "2024-02-25",
                Status:      "en mora",
            },
            {
                StudentCode: "104555123980",
                Amount:      8000,
                Reason:      "Reposición de carné estudiantil",
                DebtDate:    "2024-05-28",
                DueDate:     "2024-06-28",
                Status:      "pendiente",
            },
        },
    }
}

func (r *debtRepositoryImpl)    GetPendingDebts(debtInputDTO dto.DebtInputDTO) ([]dto.DebtOutputDTO, error) {
    var result []dto.DebtOutputDTO
    found := false
    for _, d := range r.debts {
        if d.StudentCode == debtInputDTO.StudentCode {
            found = true
            if d.Status != "pagada" {
                result = append(result, d)
            }
        }
    }
    if !found {
        return nil, errors.New("student not found")
    }
    return result, nil
}

func (r *debtRepositoryImpl) ClearDebts(debtInputDTO dto.DebtInputDTO) error {
    found := false
    for i := range r.debts {
        if r.debts[i].StudentCode == debtInputDTO.StudentCode {
            found = true
            r.debts[i].Status = "pagada"
        }
    }
    if !found {
        return errors.New("student not found")
    }
    return nil
}