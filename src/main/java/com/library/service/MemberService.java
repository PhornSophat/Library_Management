package com.library.service;

import com.library.model.Member;
import com.library.model.Member.MemberStatus;
import com.library.repository.MemberRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member create(@Valid Member member) {
        if (member.getStatus() == null) {
            member.setStatus(MemberStatus.ACTIVE);
        }
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + member.getEmail());
        }
        return memberRepository.save(member);
    }

    public Member getById(@NotBlank String id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + id));
    }

    public Member getByEmail(@NotBlank String email) {
        Member found = memberRepository.findByEmail(email);
        if (found == null) {
            throw new ResourceNotFoundException("Member not found by email: " + email);
        }
        return found;
    }

    public List<Member> listAll() {
        return memberRepository.findAll();
    }

    public List<Member> searchByName(@NotBlank String name) {
        return memberRepository.findByFullNameContainingIgnoreCase(name);
    }

    public Member update(@NotBlank String id, @Valid Member update) {
        Member existing = getById(id);
        if (update.getFullName() != null && !update.getFullName().isBlank()) {
            existing.setFullName(update.getFullName());
        }
        if (update.getPhone() != null && !update.getPhone().isBlank()) {
            existing.setPhone(update.getPhone());
        }
        if (update.getEmail() != null && !update.getEmail().isBlank() && !update.getEmail().equals(existing.getEmail())) {
            if (memberRepository.existsByEmail(update.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + update.getEmail());
            }
            existing.setEmail(update.getEmail());
        }
        if (update.getAddress() != null) {
            existing.setAddress(update.getAddress());
        }
        if (update.getStatus() != null) {
            existing.setStatus(update.getStatus());
        }
        return memberRepository.save(existing);
    }

    public void delete(@NotBlank String id) {
        Member existing = getById(id);
        memberRepository.delete(existing);
    }

    public Member updateStatus(@NotBlank String id, @NotNull MemberStatus status) {
        Member existing = getById(id);
        existing.setStatus(status);
        return memberRepository.save(existing);
    }
}
