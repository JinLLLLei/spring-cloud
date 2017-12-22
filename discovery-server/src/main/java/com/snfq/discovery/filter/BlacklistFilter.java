package com.snfq.discovery.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.converters.JsonXStream;

@Component
public class BlacklistFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(BlacklistFilter.class);
    @Value("${eureka.blacklist}")
    private String[]            backlist;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String remoteIp = req.getRemoteAddr();
        String content = IOUtils.toString(request.getInputStream());
        logger.info("remoteIp [{}] request [url:{}][method:{}] [content:{}]", remoteIp,
            req.getRequestURI(), req.getMethod(), content);
        String method = req.getMethod();
        if (!"POST".equalsIgnoreCase(method)) {
            chain.doFilter(req, response);
            return;
        }
        //        String content = IOUtils.toString(request.getInputStream());
        logger.info("remoteIp [{}] request [url:{}][method:{}] [content:{}]", remoteIp,
            req.getRequestURI(), req.getMethod(), content);
        JsonXStream xStream = JsonXStream.getInstance();
        InstanceInfo instance = (InstanceInfo) xStream.fromXML(content);
        String hostName = instance.getHostName();
        String ipAddr = instance.getIPAddr();
        for (String ip : backlist) {
            logger.info("-------->> check ip [{}]", ip);
            if (ip.equals(hostName) || ip.equals(ipAddr)) {
                logger.info("hostName [{}] ipAddr [{}] reject", hostName, ipAddr);
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }
        chain.doFilter(
            new BlacklistHttpServletRequest((HttpServletRequest) request, content.getBytes()),
            response);
    }

    @Override
    public void destroy() {

    }
}
