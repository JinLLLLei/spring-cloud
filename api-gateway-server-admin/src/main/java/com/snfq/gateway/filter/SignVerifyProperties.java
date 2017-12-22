package com.snfq.gateway.filter;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 签名配置
 * 
 * @author zenghua
 *
 */
@ConfigurationProperties("sign.verify")
public class SignVerifyProperties {
	// 签名秘钥
	private String signKey;
	// 无需签名验证的URI资源位置
	private List<String> excludeURIs;

	private Boolean isFilterOpen;

	public String getSignKey() {
		return signKey;
	}

	public void setSignKey(String signKey) {
		this.signKey = signKey;
	}

	public List<String> getExcludeURIs() {
		return excludeURIs;
	}

	public void setExcludeURIs(List<String> excludeURIs) {
		this.excludeURIs = excludeURIs;
	}

	public Boolean getIsFilterOpen() {
		return isFilterOpen;
	}

	public void setIsFilterOpen(Boolean isFilterOpen) {
		this.isFilterOpen = isFilterOpen;
	}

	@Override
	public String toString() {
		return "SignVerifyProperties [signKey=" + signKey + ", excludeURIs=" + excludeURIs+ ", isFilterOpen=" + isFilterOpen + "]";
	}
}
