package com.roomerang.contoller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TipsController {

    @GetMapping("/tips/policy")
    public String showPolicyTips() {
        return "tips/policy";
    }

    @GetMapping("/tips/scam")
    public String showScamTips() {
        return "tips/scam";
    }

    @GetMapping("/tips/items")
    public String showItemsTips() {
        return "tips/items";
    }

    @GetMapping("/tips/deposit")
    public String showDepositTips() {
        return "tips/deposit";
    }
}
