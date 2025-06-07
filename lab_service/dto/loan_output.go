package dto

type LoanOutputDTO struct {
    StudentCode         string `json:"studentCode"`
    LoanDate            string `json:"loanDate"`
    EstimatedReturnDate string `json:"estimatedReturnDate"`
    RealReturnDate      string `json:"realReturnDate"`
    Status              string `json:"status"`
    Equipment           string `json:"equipment"`
}