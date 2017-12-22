package com.snfq.gateway.remote;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.snfq.base.dto.TokenDTO;
import com.snfq.gateway.dto.response.AuthorizationResponse;

@FeignClient(name = "authorization-service")
public interface AuthorizationOperate {
    @RequestMapping("/provider/authorization/checkPermission")
    public AuthorizationResponse checkPermission(@RequestParam("token") String token,
                                                 @RequestParam("uri") String uri,
                                                 @RequestParam("requestType") String requestType);

    @RequestMapping("/provider/authorization/getTokenDto")
    public TokenDTO geTokenDto(@RequestParam("token") String token);
}
