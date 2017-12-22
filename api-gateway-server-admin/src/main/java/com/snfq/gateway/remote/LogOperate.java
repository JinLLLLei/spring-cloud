package com.snfq.gateway.remote;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import com.snfq.base.domain.SystemLogDO;

@FeignClient(name = "log-service")
public interface LogOperate {

    @RequestMapping("/provider/system/log")
    public String insertLog(SystemLogDO logDO);

}
