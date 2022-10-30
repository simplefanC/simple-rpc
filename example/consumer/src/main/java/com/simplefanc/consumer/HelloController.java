package com.simplefanc.consumer;

import com.simplefanc.api.HelloService;
import com.simplefanc.api.dto.Hello;
import com.simplefanc.autoconfigure.annotation.RpcReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hello")
public class HelloController {

    @RpcReference(version = "version1", group = "test1")
    private HelloService helloService;

    @GetMapping("/test")
    public String test() throws InterruptedException {
        return this.helloService.hello(new Hello("111", "222"));
    }
}
