package main

import (
    "github.com/gin-gonic/gin"
    "debt_service/controller"
    "debt_service/repository"
    "debt_service/service"
)

func main() {
    repo := repository.NewDebtRepository()
    svc := service.NewDebtService(repo)
    ctrl := controller.NewDebtController(svc)

    r := gin.Default()
    ctrl.RegisterRoutes(r)

    r.Run(":29001") 
}
