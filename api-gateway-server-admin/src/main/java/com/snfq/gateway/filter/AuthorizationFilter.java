package com.snfq.gateway.filter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.snfq.base.constant.AuthorizationResultEnum;
import com.snfq.base.dto.TokenDTO;
import com.snfq.gateway.dto.response.AuthorizationResponse;
import com.snfq.gateway.remote.AuthorizationOperate;

public class AuthorizationFilter extends ZuulFilter {
    private static final Logger  logger = LoggerFactory.getLogger(AuthorizationFilter.class);
    private List<String>         excludeURIs;
    private Boolean              isFilterOpen;
    @Resource
    private AuthorizationOperate authorizationService;

    public AuthorizationFilter(List<String> excludeURIs, Boolean isFilterOpen) {
        this.excludeURIs = excludeURIs;
        this.isFilterOpen = isFilterOpen;
    }

    @Override
    public boolean shouldFilter() {
        if (!this.isFilterOpen) {
            return false;
        }
        // TODO 不需要鉴权的路径都在这里进行filter
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String method = request.getMethod();
        // 跨域请求OPTIONS忽略
        if (method.equalsIgnoreCase("OPTIONS")) {
            return false;
        }
        //前过滤器过滤失败则不继续过滤
        if (!ctx.sendZuulResponse()) {
            return false;
        }
        String uri = request.getRequestURI();
        if (isExclude(uri)) {
            return false;
        }
        logger.info("should filter true");
        return true;
    }

    private boolean isExclude(String uri) {
        if (excludeURIs == null)
            return false;
        for (String excludeURI : excludeURIs) {
            if (uri.startsWith(excludeURI)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String method = request.getMethod();
        logger.info("{} request to {}", method, request.getRequestURL());

        String uri = request.getRequestURI();
        //        String uriSplit = "/api-" + uri.split("api-")[1];
        try {
            //            Map<String, String> map = getAllRequestHeader(request);
            //            String token = map.get("token".toLowerCase());
            String token = request.getHeader("token").toLowerCase();
            if (token == null || "".equals(token.trim())) {
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(HttpServletResponse.SC_BAD_REQUEST);
                logger.error("auth error ： token is null");
                return null;
            }
            AuthorizationResponse checkResult = authorizationService.checkPermission(token, uri,
                request.getMethod());
            if (checkResult.getIsPermit()) {
                ctx.setSendZuulResponse(true);
                //token转用户信息
                TokenDTO tokenDTO = checkResult.getTokenDto();
                if (tokenDTO != null) {
                    ctx.addZuulRequestHeader("id", tokenDTO.getId());

                    ctx.addZuulRequestHeader("name", encodeStr(tokenDTO.getName()));
                    ctx.addZuulRequestHeader("organizeName", encodeStr(tokenDTO.getOrganizeName()));
                }
                return null;
            } else {
                ctx.setSendZuulResponse(false);
                if (AuthorizationResultEnum.OVERTIME.getCode().equals(checkResult.getCode())) {
                    ctx.setResponseStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
                    logger.error("token expired");
                } else if (AuthorizationResultEnum.LOGIN_ANOTHER.getCode()
                    .equals(checkResult.getCode())) {
                    //提供前端用户是否被挤下标志
                    ctx.setResponseStatusCode(HttpServletResponse.SC_PRECONDITION_FAILED);
                    logger.error("login another");
                } else {
                    ctx.setResponseStatusCode(HttpServletResponse.SC_BAD_REQUEST);
                    logger.error("auth error [{}]", checkResult.toString());
                }

                return null;
            }

        } catch (Exception e) {
            logger.error("读取request流异常", e);
        }
        return null;

    }

    //    private Map<String, String> getAllRequestHeader(HttpServletRequest request) {
    //        Map<String, String> map = new HashMap<String, String>();
    //        Enumeration<String> headerNames = request.getHeaderNames();
    //        while (headerNames.hasMoreElements()) {
    //            String key = (String) headerNames.nextElement();
    //            String value = request.getHeader(key);
    //            map.put(key, value);
    //        }
    //        return map;
    //    }

    private String encodeStr(String param) throws UnsupportedEncodingException {
        if (StringUtils.isNotBlank(param)) {
            param = URLEncoder.encode(param, "UTF-8");
        }
        return param;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 2;
    }
}
