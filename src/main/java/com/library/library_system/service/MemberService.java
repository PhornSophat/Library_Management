package com.library.library_system.service;

import com.library.library_system.model.Member;
import com.library.library_system.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // Get all members
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    // Get member by ID
    public Optional<Member> getMemberById(String id) {
        return memberRepository.findById(id);
    }

    // Add or update a member
    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    // Delete a member
    public void deleteMember(String id) {
        memberRepository.deleteById(id);
    }

    // Find members by status
    public List<Member> findMembersByStatus(Member.Status status) {
    // Match the repository change
        return memberRepository.findByStatus(status);
    }
}
