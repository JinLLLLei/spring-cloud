package com.snfq.gateway.filter;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("authorization")
public class AuthorizationProperties {
	// 无需鉴权验证的URI资源位置
	private List<String> excludeURIs;

	private Boolean isFilterOpen;

	public Boolean getIsFilterOpen() {
		return isFilterOpen;
	}

	public void setIsFilterOpen(Boolean isFilterOpen) {
		this.isFilterOpen = isFilterOpen;
	}

	public List<String> getExcludeURIs() {
		return excludeURIs;
	}

	public void setExcludeURIs(List<String> excludeURIs) {
		this.excludeURIs = excludeURIs;
	}
}
