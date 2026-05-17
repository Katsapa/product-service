package katsapa.spring.productservice.api;

import katsapa.spring.productservice.domain.JWT.JwtCore;
import katsapa.spring.productservice.domain.db.UserEntity;
import katsapa.spring.productservice.domain.db.UserRepository;
import org.springdoc.core.service.GenericResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class SecurityController {

    private UserRepository repository;
    private PasswordEncoder encoder;
    private AuthenticationManager authenticationManager;
    private JwtCore jwtCore;
    private GenericResponseService responseBuilder;


    @Autowired
    @Qualifier("authenticationManager")
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setJwtCore(JwtCore jwtCore) {
        this.jwtCore = jwtCore;
    }

    @Autowired
    public void setEncoder(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Autowired
    public void setRepository(UserRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setResponseBuilder(GenericResponseService responseBuilder) {
        this.responseBuilder = responseBuilder;
    }

    @PostMapping("/signup")
    ResponseEntity<String> signup(@RequestBody SignUpRequest signUpRequest){
        if(repository.existsByUsername(signUpRequest.username())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose different name");
        }

        if(repository.existsByEmail(signUpRequest.email())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose different email");
        }

        String hashed = encoder.encode(signUpRequest.password());

        UserEntity user = new UserEntity();
        user.setPassword(hashed);
        user.setUsername(signUpRequest.username());
        user.setEmail(signUpRequest.email());

        repository.save(user);

        return ResponseEntity.ok("Success");
    }

    @PostMapping("/signin")
    ResponseEntity<String> signIn(@RequestBody SignInRequest signInRequest){
        Authentication authentication = null;
        try{
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    signInRequest.username(),
                    signInRequest.password()
            ));
        } catch(BadCredentialsException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);
        return ResponseEntity.ok(jwt);
    }
}
