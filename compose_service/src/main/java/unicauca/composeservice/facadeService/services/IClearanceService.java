package unicauca.composeservice.facadeService.services;

import reactor.core.publisher.Mono;
import unicauca.composeservice.facadeService.dtos.request.RequestClearanceDTO;
import unicauca.composeservice.facadeService.dtos.response.ClearanceDTO;

public interface IClearanceService {

    /**
     * Requests clearance synchronously.
     *
     * @param requestClearanceDTO the request data transfer object containing clearance request details
     * @return a ClearanceDTO containing the response details
     */
    ClearanceDTO requestClearance(RequestClearanceDTO requestClearanceDTO);

    /**
     * Requests clearance asynchronously.
     *
     * @param requestClearanceDTO the request data transfer object containing clearance request details
     * @return a ClearanceDTO containing the response details
     */
    Mono<ClearanceDTO> requestClearance_async(RequestClearanceDTO requestClearanceDTO);
}
