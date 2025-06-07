package controller

import (
	"debt_service/dto"
	"debt_service/service"
	"net/http"

	"github.com/gin-gonic/gin"
)

type DebtController struct {
    service service.DebtService
}

func NewDebtController(s service.DebtService) *DebtController {
    return &DebtController{service: s}
}

func (c *DebtController) RegisterRoutes(r *gin.Engine) {
    finance := r.Group("/finance")
    {
        finance.GET("/pending", c.getPending)
        finance.DELETE("/clear", c.clearDebts)
    }
}



func (c *DebtController) getPending(ctx *gin.Context) {
    var req dto.DebtInputDTO
    if err := ctx.ShouldBindJSON(&req); err != nil || req.StudentCode == "" {
        ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid student code"})
        return
    }

    debts, err := c.service.GetPendingDebts(req)
    if err != nil {
        ctx.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
        return
    }

    ctx.JSON(http.StatusOK, debts)
}

func (c *DebtController) clearDebts(ctx *gin.Context) {
    var req dto.DebtInputDTO
    if err := ctx.ShouldBindJSON(&req); err != nil || req.StudentCode == "" {
        ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid student code"})
        return
    }

    err := c.service.ClearDebts(req)
    if err != nil {
        ctx.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
        return
    }

    ctx.Status(http.StatusOK)
}
