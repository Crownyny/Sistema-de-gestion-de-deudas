package main

import (
    "github.com/gin-gonic/gin"
    "lab_service/controller"
    "lab_service/repository"
    "lab_service/service"
)

func main() {
    repo := repository.NewLoanRepository()
    svc := service.NewLoanService(repo)
    ctrl := controller.NewLoanController(svc)

    r := gin.Default()
    ctrl.RegisterRoutes(r)

    r.Run(":29000")
}
