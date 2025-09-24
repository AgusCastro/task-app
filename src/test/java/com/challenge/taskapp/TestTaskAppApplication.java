package com.challenge.taskapp;

import org.springframework.boot.SpringApplication;

public class TestTaskAppApplication {

    public static void main(String[] args) {
        SpringApplication.from(TaskAppApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
