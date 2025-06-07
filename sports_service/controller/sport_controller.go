package controller

import (
	"net/http"
	"sports_service/dto"
	"sports_service/service"

	"github.com/gin-gonic/gin"
)

type SportController struct {
	service service.SportService
}

func NewSportController(s service.SportService) *SportController {
	return &SportController{service: s}
}

func (c *SportController) RegisterRoutes(r *gin.Engine) {
	group := r.Group("/sports")
	{
		group.POST("/pending", c.getUnreturned)
		group.DELETE("/clear", c.clearItems)
	}
}

func (c *SportController) getUnreturned(ctx *gin.Context) {
	var input dto.SportInputDTO
	if err := ctx.ShouldBindJSON(&input); err != nil || input.StudentCode == "" {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid student code"})
		return
	}

	items, err := c.service.GetUnreturnedItems(input)
	if err != nil {
		ctx.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, items)
}

func (c *SportController) clearItems(ctx *gin.Context) {
	var input dto.SportInputDTO
	if err := ctx.ShouldBindJSON(&input); err != nil || input.StudentCode == "" {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid student code"})
		return
	}

	err := c.service.ClearItems(input)
	if err != nil {
		ctx.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
		return
	}

	ctx.Status(http.StatusOK)
}
