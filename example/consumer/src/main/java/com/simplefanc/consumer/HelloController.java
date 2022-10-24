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
    public void test() throws InterruptedException {
        String hello = this.helloService.hello(new Hello("111", "222"));
        // 如需使用 assert 断言，需要在 VM options 添加参数：-ea
        assert "Hello description is 222".equals(hello);
        Thread.sleep(12000);
        for (int i = 0; i < 10; i++) {
            System.out.println(helloService.hello(new Hello("111", "222")));
        }
    }
}
