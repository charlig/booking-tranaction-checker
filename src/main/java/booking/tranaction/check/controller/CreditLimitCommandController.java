package booking.tranaction.check.controller;

import booking.tranaction.check.dto.request.RequestCreditLimitDto;
import booking.tranaction.check.dto.response.ResponseCreditLimitDto;
import booking.tranaction.check.service.CreditLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/credit")
@RequiredArgsConstructor
public class CreditLimitCommandController {

    final private CreditLimitService creditLimitService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseCreditLimitDto> saveCreditLimit(@Valid @RequestBody RequestCreditLimitDto requestCreditLimitDto){
        return creditLimitService.save(requestCreditLimitDto);
    }


}
