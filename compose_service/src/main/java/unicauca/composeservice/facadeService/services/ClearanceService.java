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
import unicauca.composeservice.facadeService.dtos.request.RequestClearanceDTO;
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
    public ClearanceDTO requestClearance(RequestClearanceDTO requestClearanceDTO) {
        System.out.println("Starting the clearance request process...");
        eventPublisher.publishEvent(new ClearanceEvent(
                EventType.REQUEST,
                ServiceType.ALL,
                ClearanceDTO.builder()
                    .studentCode(requestClearanceDTO.getInfoStudent().getStudentCode())
                    .message("Solicitud de paz y salvo iniciada para estudiante: " + requestClearanceDTO.getInfoStudent().getStudentCode())
                    .build()
        ));
        try {
            List<DebtResponseDTO> debtResponse = requestClearanceDTO.isDebtService()
                    ? callService(urlDebtService, requestClearanceDTO.getInfoStudent(),
                    new ParameterizedTypeReference<List<DebtResponseDTO>>() {}, "DebtService").block()
                    : List.of();

            List<LabResponseDTO> labResponse = requestClearanceDTO.isLabService()
                    ? callService(urlLabService, requestClearanceDTO.getInfoStudent(),
                    new ParameterizedTypeReference<List<LabResponseDTO>>() {}, "LabService").block()
                    : List.of();

            List<SportResponseDTO> sportResponse = requestClearanceDTO.isSportService()
                    ? callService(urlSportsService, requestClearanceDTO.getInfoStudent(),
                    new ParameterizedTypeReference<List<SportResponseDTO>>() {}, "SportsService").block()
                    : List.of();

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

        } catch (ServiceUnavailableException e) {
            System.out.println("Servicio no disponible: " + e.getServiceName() + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("An error occurred while processing the clearance request: " + e.getMessage());
            throw e;
        }
    }
    @Override
    public Mono<ClearanceDTO> requestClearance_async(RequestClearanceDTO requestClearanceDTO) {
        System.out.println("Starting the ASYNC clearance request process...");

        eventPublisher.publishEvent(new ClearanceEvent(
                EventType.REQUEST,
                ServiceType.ALL,
                ClearanceDTO.builder()
                        .studentCode(requestClearanceDTO.getInfoStudent().getStudentCode())
                        .message("Solicitud de paz y salvo iniciada para estudiante: " + requestClearanceDTO.getInfoStudent().getStudentCode())
                        .build()
        ));

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
                .defaultIfEmpty(List.of())
                .doOnNext(response -> {
                    // Determinar el tipo de servicio para el evento
                    ServiceType serviceType = switch (serviceName) {
                        case "DebtService" -> ServiceType.DEBT;
                        case "LabService" -> ServiceType.LAB;
                        case "SportsService" -> ServiceType.SPORTS;
                        default -> ServiceType.ALL;
                    };

                    // Publicar evento según si hay deudas o no
                    if (response.isEmpty()) {
                        eventPublisher.publishEvent(new ClearanceEvent(
                                EventType.DEBT_NOT_FOUND,
                                serviceType,
                                ClearanceDTO.builder()
                                        .studentCode(student.getStudentCode())
                                        .message("No tiene deudas el estudiante: " + student.getStudentCode())
                                        .build()
                        ));
                    } else {
                        eventPublisher.publishEvent(new ClearanceEvent(
                                EventType.DEBT_FOUND,
                                serviceType,
                                ClearanceDTO.fromGeneric(
                                        "El estudiante tiene " + response.size() + " deudas pendientes en " + serviceName,
                                        student.getStudentCode(),
                                        response.stream().toList()
                                )
                        ));
                    }
                })
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    System.out.println("[" + serviceName + "] Not Found: " + ex.getMessage());
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
                .onErrorResume(e -> {
                    return Mono.just(List.of());
                });
    }

    // Método auxiliar para obtener el ServiceType a partir del nombre del servicio
    private ServiceType getServiceTypeFromName(String serviceName) {
        return switch (serviceName) {
            case "DebtService" -> ServiceType.DEBT;
            case "LabService" -> ServiceType.LAB;
            case "SportsService" -> ServiceType.SPORTS;
            default -> ServiceType.ALL;
        };
    }
}
