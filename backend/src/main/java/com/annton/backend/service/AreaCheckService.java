package com.annton.backend.service;

import com.annton.backend.dto.DataRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class AreaCheckService {
    public boolean isInsideArea(DataRequestDTO requestDTO){
        double x= requestDTO.getX();
        double y=requestDTO.getY();
        double r=requestDTO.getR();


        if (x>0 && y>0){
            return false;
        }
        if (x>=0 &&y<=0){
            return (x*x+y*y<=r*r/4);
        }
        if (x<=0 &&y>=0){
            return (y<=2*x+r);
        }
        if (x<=0 &&y<=0){
            return ((x>=-r) && (y<=r));
        }
        return false;
    }
}
