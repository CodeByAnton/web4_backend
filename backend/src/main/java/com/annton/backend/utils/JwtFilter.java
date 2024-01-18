package com.annton.backend.utils;

import com.annton.backend.service.UserDetailImplService;
import com.annton.backend.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private  JwtTokenUtils jwtTokenUtils;
    private  UserDetailsService userDetailsService;

    public JwtFilter(JwtTokenUtils jwtTokenUtils, UserDetailImplService userDetailImplService) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.userDetailsService = userDetailImplService;
    }
    @Autowired
    public JwtFilter(JwtTokenUtils jwtTokenUtils, UserDetailsService userDetailsService) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt=null;
        String username=null;
        UserDetails userDetails=null;
        UsernamePasswordAuthenticationToken authenticationToken=null;
        try {
            String headerAuth=request.getHeader("Authorization");
            if(headerAuth!=null && headerAuth.startsWith("Bearer")){
                jwt=headerAuth.substring(7);

            }
            if (jwt!=null){
                try{
                    username=jwtTokenUtils.getUsername(jwt);
                } catch (ExpiredJwtException e){

                }
            }


            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                userDetails = userDetailsService.loadUserByUsername(username);


                authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }
        } catch (Exception e){


        }
        filterChain.doFilter(request,response);
    }
}
