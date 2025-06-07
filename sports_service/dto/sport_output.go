package dto

type SportOutputDTO struct {
	StudentCode string `json:"studentCode"`
    Item                string `json:"item"`
    LoanDate            string `json:"loanDate"`
    EstimatedReturnDate string `json:"estimatedReturnDate"`
    RealReturnDate      string `json:"realReturnDate,omitempty"`
}
