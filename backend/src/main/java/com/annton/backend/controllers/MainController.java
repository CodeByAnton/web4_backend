package com.annton.backend.controllers;

import com.annton.backend.dto.DataRequestDTO;
import com.annton.backend.dto.DataResponseDTO;
import com.annton.backend.entities.Result;
import com.annton.backend.entities.User;
import com.annton.backend.repositories.ResultRepository;
import com.annton.backend.service.AreaCheckService;
import com.annton.backend.service.UserService;
import com.annton.backend.utils.JwtTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/main")

public class MainController {

    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private AreaCheckService areaCheckService;
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private UserService userService;

    //Добавляем данные в таблицу
    @Transactional
    @PostMapping
    public ResponseEntity<?> addData(@RequestBody DataRequestDTO requestDTO, HttpServletRequest httpServletRequest){
        try {
            String jwtToken=extractTokenFromRequest(httpServletRequest);

            if (jwtToken==null){
                throw new AuthenticationException("Нет jwt токена в запросе");
            }
            String username=jwtTokenUtils.getUsername(jwtToken);
            User user=userService.findByUsername(username);
            if (user==null) {
                throw new AuthenticationException("Пользователь с таким именем не найден");
            }


            Result result=new Result();
            result.setX(requestDTO.getX());
            result.setY(requestDTO.getY());
            result.setR(requestDTO.getR());
            result.setResult(areaCheckService.isInsideArea(requestDTO));
            result.setUser(user);
            resultRepository.save(result);

            DataResponseDTO responseDTO=new DataResponseDTO(result.getX(),result.getY(),result.getR(),result.isResult());

            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        } catch (AuthenticationException e){
            return new ResponseEntity<>("Ошибка аутентификации: "+e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e){
            return new ResponseEntity<>("Ошибка на стороне сервера: "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    //Удаление данных пользователя из бд
    @Transactional
    @DeleteMapping
    public ResponseEntity<?> clearTable(HttpServletRequest httpServletRequest){
        try {
            String jwtToken=extractTokenFromRequest(httpServletRequest);
            if (jwtToken==null){
                throw new AuthenticationException("Нет jwt токена в запросе");
            }
            String username=jwtTokenUtils.getUsername(jwtToken);
            User user=userService.findByUsername(username);
            if (user==null) {
                throw new AuthenticationException("Пользователь с таким именем не найден");
            }
            resultRepository.deleteAllByUser(user);
            return new ResponseEntity<>("Данные пользователя удалены",HttpStatus.OK);



        } catch (AuthenticationException e){
            return new ResponseEntity<>("Ошибка аутентификации: "+e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e){
            return new ResponseEntity<>("Ошибка на стороне сервера: "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @GetMapping
    public ResponseEntity<?> getUserData(HttpServletRequest httpServletRequest){
        try {
            String jwtToken=extractTokenFromRequest(httpServletRequest);
            if (jwtToken==null){
                throw new AuthenticationException("Нет jwt токена в запросе");
            }
            String username=jwtTokenUtils.getUsername(jwtToken);
            User user=userService.findByUsername(username);
            if (user==null) {
                throw new AuthenticationException("Пользователь с таким именем не найден");
            }
            List<Result> resultList = resultRepository.findAllByUser(user);
            List<DataResponseDTO> userData = resultList.stream()
                    .map(result -> new DataResponseDTO(result.getX(), result.getY(), result.getR(), result.isResult()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(userData);


        } catch (AuthenticationException e){
            return new ResponseEntity<>("Ошибка аутентификации: "+e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e){
            return new ResponseEntity<>("Ошибка на стороне сервера: "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
