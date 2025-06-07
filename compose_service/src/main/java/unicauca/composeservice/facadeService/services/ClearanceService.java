package unicauca.composeservice.facadeService.services;

import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import unicauca.composeservice.facadeService.dtos.request.InfoStudentDTO;
import unicauca.composeservice.facadeService.dtos.request.RequestClearanceDTO;
import unicauca.composeservice.facadeService.dtos.response.ClearanceDTO;
import unicauca.composeservice.facadeService.dtos.response.DebtResponseDTO;
import unicauca.composeservice.facadeService.dtos.response.LabResponseDTO;
import unicauca.composeservice.facadeService.dtos.response.SportResponseDTO;

import java.util.List;

@Service
@AllArgsConstructor
public class ClearanceService implements IClearanceService {

    private final WebClient webClient;
    private final String urlDebtService = "http://localhost:29001/finance/pending";
    private final String urlLabService = "http://localhost:29000/lab/pending";
    private final String urlSportsService = "http://localhost:29002/sports/pending";

    @Override
    public ClearanceDTO requestClearance(RequestClearanceDTO requestClearanceDTO) {
        System.out.println("Starting the clearance request process...");

        try {
            List<DebtResponseDTO> debtResponse = List.of();
            List<LabResponseDTO> labResponse = List.of();
            List<SportResponseDTO> sportResponse = List.of();
            if(requestClearanceDTO.isDebtService())
                debtResponse = webClient.post()
                    .uri(urlDebtService)
                    .bodyValue(requestClearanceDTO.getInfoStudent())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<DebtResponseDTO>>() {})
                    .onErrorResume(WebClientResponseException.NotFound.class, ex -> Mono.just(List.of()))
                    .defaultIfEmpty(List.of())
                    .block();

            if(requestClearanceDTO.isLabService())
                labResponse = webClient.post()
                    .uri(urlLabService)
                    .bodyValue(requestClearanceDTO.getInfoStudent())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<LabResponseDTO>>() {})
                    .onErrorResume(WebClientResponseException.NotFound.class, ex -> Mono.just(List.of()))
                    .defaultIfEmpty(List.of())
                    .block();

            if(requestClearanceDTO.isSportService())
                sportResponse = webClient.post()
                    .uri(urlSportsService)
                    .bodyValue(requestClearanceDTO.getInfoStudent())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<SportResponseDTO>>() {})
                    .onErrorResume(WebClientResponseException.NotFound.class, ex -> Mono.just(List.of()))
                    .defaultIfEmpty(List.of())
                    .block();

            int totalDebts = debtResponse.size() + labResponse.size() + sportResponse.size();
            String message = totalDebts > 0
                    ? "The student has pending debts in the system."
                    : "The student has no pending debts in the system.";

            return ClearanceDTO.builder()
                    .studentCode(requestClearanceDTO.getInfoStudent().getStudentCode())
                    .message(message)
                    .debtResponse(debtResponse)
                    .labResponse(labResponse)
                    .sportResponse(sportResponse)
                    .build();

        } catch (Exception e) {
            System.out.println("An error occurred while processing the clearance request: " + e.getMessage());
            throw e;
//            return ClearanceDTO.builder()
  //                  .studentCode(requestClearanceDTO.getInfoStudent().getStudentCode())
    //                .message("An error occurred while processing the request.")
      //              .debtResponse(List.of())
        //            .labResponse(List.of())
          //          .sportResponse(List.of())
            //        .build();
        }
    }

    @Override
    public Mono<ClearanceDTO> requestClearance_async(RequestClearanceDTO requestClearanceDTO) {
        System.out.println("Starting the ASYNC clearance request process...");

        Mono<List<DebtResponseDTO>> debtMono = requestClearanceDTO.isDebtService()
                ? callService(urlDebtService, requestClearanceDTO.getInfoStudent(), new ParameterizedTypeReference<>() {}, "DebtService")
                : Mono.just(List.of());

        Mono<List<LabResponseDTO>> labMono = requestClearanceDTO.isLabService()
                ? callService(urlLabService, requestClearanceDTO.getInfoStudent(), new ParameterizedTypeReference<>() {}, "LabService")
                : Mono.just(List.of());

        Mono<List<SportResponseDTO>> sportMono = requestClearanceDTO.isSportService()
                ? callService(urlSportsService, requestClearanceDTO.getInfoStudent(), new ParameterizedTypeReference<>() {}, "SportsService")
                : Mono.just(List.of());

        return Mono.zip(debtMono, labMono, sportMono)
                .map(tuple -> {
                    List<DebtResponseDTO> debtResponse = tuple.getT1();
                    List<LabResponseDTO> labResponse = tuple.getT2();
                    List<SportResponseDTO> sportResponse = tuple.getT3();

                    int totalDebts = debtResponse.size() + labResponse.size() + sportResponse.size();
                    String message = totalDebts > 0
                            ? "The student has pending debts in the system."
                            : "The student has no pending debts in the system.";

                    return ClearanceDTO.builder()
                            .studentCode(requestClearanceDTO.getInfoStudent().getStudentCode())
                            .message(message)
                            .debtResponse(debtResponse)
                            .labResponse(labResponse)
                            .sportResponse(sportResponse)
                            .build();
                })
                .onErrorResume(e -> {
                    System.out.println("An error occurred in ASYNC processing: " + e.toString());
                    return Mono.empty();
                });
    }

    private <T> Mono<List<T>> callService(String url, InfoStudentDTO student, ParameterizedTypeReference<List<T>> typeRef, String serviceName) {
        return webClient.post()
                .uri(url)
                .bodyValue(student)
                .retrieve()
                .bodyToMono(typeRef)
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    System.out.println("[" + serviceName + "] Not Found: " + ex.getMessage());
                    return Mono.just(List.of());
                })
                .onErrorResume(e -> {
                    System.out.println("[" + serviceName + "] Error: " + e.toString());
                    return Mono.just(List.of());
                });
    }

}
