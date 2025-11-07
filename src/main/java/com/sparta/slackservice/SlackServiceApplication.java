package com.sparta.slackservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class SlackServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlackServiceApplication.class, args);
    }

}
