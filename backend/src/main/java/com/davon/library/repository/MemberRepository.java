package com.davon.library.repository;

import com.davon.library.model.Member;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface MemberRepository extends PanacheRepository<Member> {
}
