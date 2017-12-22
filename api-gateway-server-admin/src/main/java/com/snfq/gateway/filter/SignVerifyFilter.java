package com.snfq.gateway.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * 签名校验接口
 * 
 * 1.POST & PUT请求签名采用md5(body + signKey) 
 * 2.GET & DELETE请求签名采用md5(uri +sort(paramName+paramValue) + signKey)
 * 
 * @author zenghua
 *
 */
public class SignVerifyFilter extends ZuulFilter {
	private static final Logger logger = LoggerFactory.getLogger(SignVerifyFilter.class);
	private String secKey;
	private List<String> excludeURIs;
	private Boolean isFilterOpen;

	public SignVerifyFilter(String secKey, List<String> excludeURIs, Boolean isFilterOpen) {
		this.secKey = secKey;
		this.excludeURIs = excludeURIs;
		this.isFilterOpen = isFilterOpen;
	}

	@Override
	public boolean shouldFilter() {
		if(!this.isFilterOpen){
			return false;
		}
		// TODO 不需要签名的路径都在这里进行filter，比如：文件上传
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		String method = request.getMethod();
		// 跨域请求OPTIONS忽略
		if (method.equalsIgnoreCase("OPTIONS")) {
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
		for(String excludeURI : excludeURIs) {
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

		// POST & PUT & contentType 为 application/json
		if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT")) {
			String sign = request.getHeader("sign");
			try {
				String body = IOUtils.toString(request.getInputStream(),"UTF-8");
				String signContent = new StringBuilder(body).append(this.secKey).toString();
				logger.info("signContent {}", signContent);
				String verify = DigestUtils.md5Hex(signContent.getBytes());
				if (!StringUtils.equals(sign, verify)) {
					ctx.setSendZuulResponse(false);
					ctx.setResponseStatusCode(HttpServletResponse.SC_BAD_REQUEST);
					logger.error("verify sign error [sign:{}] [verify:{}]", sign, verify);
					return null;
				}
			} catch (Exception e) {
				logger.error("读取request流异常", e);
			}
			return null;
		}
		// GET & DELETE
		if (method.equalsIgnoreCase("GET") || method.equalsIgnoreCase("DELETE")) {
			String sign = request.getHeader("sign");
			try {
				String uri = request.getRequestURI();
				List<String> paramList = new ArrayList<String>(request.getParameterMap().keySet());
				// 参数排序
				Collections.sort(paramList);
				StringBuilder sb = new StringBuilder(uri);
				for (String paramName : paramList) {
					sb.append(paramName).append(request.getParameter(paramName));
				}
				String signContent = sb.append(this.secKey).toString();
				logger.info("signContent {}", signContent);
				String verify = DigestUtils.md5Hex(signContent.getBytes());
				if (!StringUtils.equals(sign, verify)) {
					ctx.setSendZuulResponse(false);
					ctx.setResponseStatusCode(HttpServletResponse.SC_BAD_REQUEST);
					logger.error("verify sign error [sign:{}] [verify:{}]", sign, verify);
					return null;
				}
			} catch (Exception e) {
				logger.error("读取request流异常", e);
			}
			return null;
		}
		return null;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 1;
	}

//	public static void main(String[] args) {
//
//		System.out.println(DigestUtils.md5Hex("159357"));
//	}
}
