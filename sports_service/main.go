package main

import (
    "github.com/gin-gonic/gin"
    "sports_service/controller"
    "sports_service/repository"
    "sports_service/service"
)

func main() {
    repo := repository.NewSportRepository()
    svc := service.NewSportService(repo)
    ctrl := controller.NewSportController(svc)

    r := gin.Default()
    ctrl.RegisterRoutes(r)

    r.Run(":29002") // Servidor para el servicio de deportes
}
