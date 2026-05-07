package com.minsic.mcserver_dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private RconService rconService;

    @Autowired
    private LogParserService logParserService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("playerList", rconService.getPlayerList());
        model.addAttribute("playerStats", logParserService.parsePlayerStats());
        return "index";
    }
}