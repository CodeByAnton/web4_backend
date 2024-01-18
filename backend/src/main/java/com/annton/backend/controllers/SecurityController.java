package com.annton.backend.controllers;

import com.annton.backend.dto.UserDTO;
import com.annton.backend.entities.User;
import com.annton.backend.service.UserService;
import com.annton.backend.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class SecurityController {


    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;
//    @CrossOrigin(origins = "http://localhost:3000")

    //Регистрация
    @Transactional
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDTO userDTO){
        try {
            User registredUser=userService.createNewUser(userDTO.getUsername(), userDTO.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(registredUser.getUsername());
        }
        catch (Exception exception){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "Ошибка","Данное имя уже существует"
            ));
        }

    }
//    @CrossOrigin(origins = "http://localhost:3000")
    //Аутентификация
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody UserDTO userDTO){
        if (userService.checkUserCredentials(userDTO.getUsername(),userDTO.getPassword())){
            String jwtToken=jwtTokenUtils.generateToken(userDTO.getUsername());
            return ResponseEntity.ok( jwtToken);

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверное имя пользователя или пароль");
        }

    }



}
