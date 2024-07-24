package com.sascom.chickenstock.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public record PrefixNicknameInfosResponse(List<MemberInfoResponse> memberList) { }
