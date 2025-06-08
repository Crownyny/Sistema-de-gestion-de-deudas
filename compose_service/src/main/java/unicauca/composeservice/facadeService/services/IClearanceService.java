package unicauca.composeservice.facadeService.services;

import reactor.core.publisher.Mono;
import unicauca.composeservice.facadeService.dtos.response.ClearanceDTO;

public interface IClearanceService {

    /**
     * Requests clearance synchronously.
     *
     * @param idStudent id of the student for whom clearance is requested
     * @return a ClearanceDTO containing the response details
     */
    ClearanceDTO requestClearance(String idStudent);

    /**
     * Requests clearance asynchronously.
     *
     * @param idStudent id of the student for whom clearance is requested
     * @return a ClearanceDTO containing the response details
     */
    Mono<ClearanceDTO> requestClearance_async(String idStudent);
}
