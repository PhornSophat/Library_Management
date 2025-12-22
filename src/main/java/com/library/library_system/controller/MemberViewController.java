package com.library.library_system.controller;

import com.library.library_system.model.Member;
import com.library.library_system.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
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
        return "members"; // points to src/main/resources/templates/members.html
    }
}
