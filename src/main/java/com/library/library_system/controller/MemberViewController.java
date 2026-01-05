package com.library.library_system.controller;

// Legacy controller disabled; routes handled elsewhere.
import com.library.model.Member;
import com.library.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public class MemberViewController {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberViewController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("/members")
    public String listMembers(Model model) {
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);
        return "librarian/member/list"; // use existing Thymeleaf template
    }
}
