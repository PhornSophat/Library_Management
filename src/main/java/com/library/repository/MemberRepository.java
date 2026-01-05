package com.library.repository;

import com.library.model.Member;
import com.library.model.Member.MemberStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("mongoMemberRepository")
public interface MemberRepository extends MongoRepository<Member, String> {
    boolean existsByEmail(String email);
    Member findByEmail(String email);
    List<Member> findByStatus(MemberStatus status);
    List<Member> findByFullNameContainingIgnoreCase(String name);
}
