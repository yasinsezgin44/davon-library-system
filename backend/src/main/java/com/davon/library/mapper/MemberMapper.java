package com.davon.library.mapper;

import com.davon.library.dto.MemberResponseDTO;
import com.davon.library.model.Member;

public class MemberMapper {

    public static MemberResponseDTO toResponseDTO(Member member) {
        if (member == null) {
            return null;
        }
        return new MemberResponseDTO(
                member.getId(),
                UserMapper.toResponseDTO(member.getUser()),
                member.getMembershipStartDate(),
                member.getMembershipEndDate(),
                member.getAddress(),
                member.getFineBalance()
        );
    }
}
