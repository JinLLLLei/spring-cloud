package com.snfq.gateway.dto.response;

import java.io.Serializable;

import com.snfq.base.dto.TokenDTO;

public class AuthorizationResponse implements Serializable {

    private static final long serialVersionUID = 2708414143964057958L;

    private Boolean           isPermit;

    private String            code;

    private String            description;

    private TokenDTO          tokenDto;

    public TokenDTO getTokenDto() {
        return tokenDto;
    }

    public void setTokenDto(TokenDTO tokenDto) {
        this.tokenDto = tokenDto;
    }

    public Boolean getIsPermit() {
        return isPermit;
    }

    public void setIsPermit(Boolean isPermit) {
        this.isPermit = isPermit;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "AuthorizationResponse [isPermit=" + isPermit + ", code=" + code + ", description="
               + description + ", tokenDto=" + tokenDto + "]";
    }

}
