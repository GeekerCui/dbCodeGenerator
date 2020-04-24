package com.github.geekercui.generator.controller;

import com.github.geekercui.generator.service.SysGeneratorService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sys/generator")
@Api(value="user",tags = {"用户服务/统计"},produces = MediaType.APPLICATION_JSON_VALUE)
public class SysGeneratorController {

    @Resource
    private SysGeneratorService sysGeneratorService;

    @Value("${mainconf.generator.tablePrefix}")
    private String wildTableName;

    @Value("${mainconf.generator.wordPath}")
    private String docPath;

    @Value("${server.servlet.context-path}")
    private String path;


    @GetMapping("doc")
    @ResponseBody
    public String generateDoc() {

        sysGeneratorService.generatorDbDoc(wildTableName,docPath);
        return "OK";
    }

}
