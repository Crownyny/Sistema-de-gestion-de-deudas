package dto

type DebtOutputDTO struct {
	StudentCode string `json:"studentCode"`
    Amount   float64 `json:"amount"`
    Reason   string  `json:"reason"`
    DebtDate string  `json:"debtDate"`
    DueDate  string  `json:"dueDate"`
    Status   string  `json:"status"`
}
