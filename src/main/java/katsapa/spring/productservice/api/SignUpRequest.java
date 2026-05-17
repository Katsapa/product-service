package katsapa.spring.productservice.api;

public record SignUpRequest (
        String username,
        String email,
        String password
){
}
