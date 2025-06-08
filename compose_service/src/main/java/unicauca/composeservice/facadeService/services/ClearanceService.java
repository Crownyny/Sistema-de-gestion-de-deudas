package unicauca.composeservice.facadeService.services;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import unicauca.composeservice.exceptions.ServiceUnavailableException;
import unicauca.composeservice.facadeService.dtos.request.InfoStudentDTO;
import unicauca.composeservice.facadeService.dtos.response.ClearanceDTO;
import unicauca.composeservice.facadeService.dtos.response.DebtResponseDTO;
import unicauca.composeservice.facadeService.dtos.response.LabResponseDTO;
import unicauca.composeservice.facadeService.dtos.response.SportResponseDTO;
import unicauca.composeservice.facadeService.events.ClearanceEvent;
import unicauca.composeservice.facadeService.events.EventType;
import unicauca.composeservice.facadeService.events.ServiceType;

import java.util.List;

@Service
@AllArgsConstructor
public class ClearanceService implements IClearanceService {

    private final WebClient webClient;
    private final ApplicationEventPublisher eventPublisher;
    private final String urlDebtService = "http://debt-service:29001/finance/pending";
    private final String urlLabService = "http://lab-service:29000/lab/pending";
    private final String urlSportsService = "http://sports-service:29002/sports/pending";

    @Override
    public ClearanceDTO requestClearance(String idStudent) {
        System.out.println("Starting the clearance request process...");
        eventPublisher.publishEvent(new ClearanceEvent(
                EventType.REQUEST,
                ServiceType.ALL,
                ClearanceDTO.builder()
                    .studentCode(idStudent)
                    .message("Solicitud de paz y salvo iniciada para estudiante: " + idStudent)
                    .build()
        ));
        try {
            List<DebtResponseDTO> debtResponse =
                    callService(urlDebtService, idStudent,
                    new ParameterizedTypeReference<List<DebtResponseDTO>>() {}, "DebtService").block();

            List<LabResponseDTO> labResponse =
                    callService(urlLabService, idStudent,
                    new ParameterizedTypeReference<List<LabResponseDTO>>() {}, "LabService").block();

            List<SportResponseDTO> sportResponse =
                    callService(urlSportsService, idStudent,
                    new ParameterizedTypeReference<List<SportResponseDTO>>() {}, "SportsService").block();

            if(debtResponse==null || labResponse==null || sportResponse==null)
                throw new RuntimeException("One or more service responses are null. Please check the service availability.");

            int totalDebts = debtResponse.size() + labResponse.size() + sportResponse.size();
            String message = totalDebts > 0
                    ? "The student has pending debts in the system."
                    : "The student has no pending debts in the system.";

            return ClearanceDTO.builder()
                    .studentCode(idStudent)
                    .message(message)
                    .debtResponse(debtResponse)
                    .labResponse(labResponse)
                    .sportResponse(sportResponse)
                    .build();

        } catch (ServiceUnavailableException e) {
            System.out.println("Servicio no disponible: " + e.getServiceName() + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("An error occurred while processing the clearance request: " + e.getMessage());
            throw e;
        }
    }
    @Override
    public Mono<ClearanceDTO> requestClearance_async(String idStudent) {
        System.out.println("Starting the ASYNC clearance request process...");

        eventPublisher.publishEvent(new ClearanceEvent(
                EventType.REQUEST,
                ServiceType.ALL,
                ClearanceDTO.builder()
                        .studentCode(idStudent)
                        .message("Solicitud de paz y salvo iniciada para estudiante: " + idStudent)
                        .build()
        ));

        Mono<List<DebtResponseDTO>> debtMono =
                callService(urlDebtService, idStudent, new ParameterizedTypeReference<>() {}, "DebtService");
                

        Mono<List<LabResponseDTO>> labMono =
                callService(urlLabService, idStudent, new ParameterizedTypeReference<>() {}, "LabService");
                

        Mono<List<SportResponseDTO>> sportMono =
                callService(urlSportsService, idStudent, new ParameterizedTypeReference<>() {}, "SportsService");
                

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
                            .studentCode(idStudent)
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

    private <T> Mono<List<T>> callService(String url, String student, ParameterizedTypeReference<List<T>> typeRef, String serviceName) {
        ServiceType serviceType = switch (serviceName) {
            case "DebtService" -> ServiceType.DEBT;
            case "LabService" -> ServiceType.LAB;
            case "SportsService" -> ServiceType.SPORTS;
            default -> ServiceType.ALL;
        };
        return
                webClient.post()
                .uri(url)
                .bodyValue(new InfoStudentDTO(student))
                .retrieve()
                .bodyToMono(typeRef)
                .defaultIfEmpty(List.of())
                .doOnNext(response -> {

                    // Publicar evento según si hay deudas o no
                    if (response.isEmpty()) {
                        eventPublisher.publishEvent(new ClearanceEvent(
                                EventType.DEBT_NOT_FOUND,
                                serviceType,
                                ClearanceDTO.builder()
                                        .studentCode(student)
                                        .message("No tiene deudas el estudiante: " + student)
                                        .build()
                        ));
                    } else {
                        eventPublisher.publishEvent(new ClearanceEvent(
                                EventType.DEBT_FOUND,
                                serviceType,
                                ClearanceDTO.fromGeneric(
                                        "El estudiante tiene " + response.size() + " deudas pendientes en " + serviceName,
                                        student,
                                        response.stream().toList()
                                )
                        ));
                    }
                })
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    System.out.println("[" + serviceName + "] Not Found: " + ex.getMessage());
                    eventPublisher.publishEvent(new ClearanceEvent(
                            EventType.DEBT_NOT_FOUND,
                            serviceType,
                            ClearanceDTO.builder()
                                    .studentCode(student)
                                    .message("No tiene deudas el estudiante: " + student)
                                    .build()
                    ));
                    return Mono.just(List.of());
                })
                .onErrorResume(WebClientRequestException.class, ex -> {
                    ServiceUnavailableException serviceException = new ServiceUnavailableException(
                            serviceName,
                            ex,
                            ex.getMethod(),
                            ex.getUri()
                    );
                    return Mono.error(serviceException);
                })
                .onErrorResume(e -> Mono.just(List.of()));
    }

}
