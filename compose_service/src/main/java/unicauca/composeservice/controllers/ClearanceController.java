package unicauca.composeservice.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import unicauca.composeservice.facadeService.dtos.request.RequestClearanceDTO;
import unicauca.composeservice.facadeService.dtos.response.ClearanceDTO;
import unicauca.composeservice.facadeService.services.IClearanceService;

@RestController
@RequestMapping("/api/clearance")
@AllArgsConstructor
public class ClearanceController {

    private final IClearanceService clearanceService;

    @PostMapping
    public Mono<ClearanceDTO> createClearance(
        @RequestBody
        RequestClearanceDTO clearanceDTO,
        @RequestParam(value = "async", required = false, defaultValue = "false")
        boolean isAsync,
        @RequestParam(value = "simluate_fail", required = false, defaultValue = "false")
        boolean simulateFail
    ) throws InterruptedException {

        if(simulateFail)
            Thread.sleep(10000);

        if (isAsync) {
            return clearanceService.requestClearance_async(clearanceDTO);
        } else {
            return Mono.just(clearanceService.requestClearance(clearanceDTO));
        }
    }


}
