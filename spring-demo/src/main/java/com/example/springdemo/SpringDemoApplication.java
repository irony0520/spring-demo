package com.example.springdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController

@SpringBootApplication
public class SpringDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDemoApplication.class, args);
	}
		@GetMapping("/")
		public String HelloWorld() {
			return "Hello World";

		}

		@GetMapping(value = "/test")
	public UserDTO test() {
			UserDTO userDto = new UserDTO();
			userDto.setAge(20);
			userDto.setName("hoon");

			return userDto;

		}


}
