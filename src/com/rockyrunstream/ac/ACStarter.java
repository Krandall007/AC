package com.rockyrunstream.ac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class ACStarter {

    public static void main(String[] args) {
        SpringApplication.run(ACStarter.class, args);
    }

}
