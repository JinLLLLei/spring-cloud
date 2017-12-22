package com.snfq.gateway.filter;


import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 签名配置
 * 
 * @author jinlei
 *
 */
@ConfigurationProperties("cross.domain")
public class CrossDomainProperties {
	// 允许域名
	private String origin;
	//允许头
	private String header;
	//允许方法
	private String method;
    public String getOrigin() {
        return origin;
    }
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    
    @Override
    public String toString() {
        return "CrossDomainProperties [origin=" + origin + ", header=" + header + ", method="
               + method + "]";
    }
}
