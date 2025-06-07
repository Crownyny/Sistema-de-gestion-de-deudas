package controller

import (
    "lab_service/dto"
    "lab_service/service"
    "github.com/gin-gonic/gin"
    "net/http"
)

type LoanController struct {
    service service.LoanService
}

func NewLoanController(s service.LoanService) *LoanController {
    return &LoanController{service: s}
}

func (c *LoanController) RegisterRoutes(r *gin.Engine) {
    lab := r.Group("/lab")
    {
        lab.GET("/pending", c.getPending)
        lab.DELETE("/clear", c.clearLoans)
    }
}

func (c *LoanController) getPending(ctx *gin.Context) {
    var loanInput dto.LoanInputDTO
    if err := ctx.ShouldBindJSON(&loanInput); err != nil {
        ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid input"})
        return
    }
    
    loans, err := c.service.GetPendingLoans(loanInput)
    if err != nil {
        ctx.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
        return
    }
    
    ctx.JSON(http.StatusOK, loans)
}

func (c *LoanController) clearLoans(ctx *gin.Context) {
    var loanInput dto.LoanInputDTO
    if err := ctx.ShouldBindJSON(&loanInput); err != nil {
        ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid input"})
        return
    }
    
    if err := c.service.ClearLoans(loanInput); err != nil {
        ctx.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
        return
    }
    
    ctx.Status(http.StatusOK)
}