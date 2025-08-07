package com.DergiPlatform.DergiPlatform.services;

import com.DergiPlatform.DergiPlatform.models.Dergi;
import com.DergiPlatform.DergiPlatform.models.Sayi;
import com.DergiPlatform.DergiPlatform.repository.SayiRepository;
import com.DergiPlatform.DergiPlatform.repository.DergiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MakaleService {

    @Autowired
    private DergiRepository dergiRepository;
    @Autowired
    private SayiRepository sayiRepository;

    public List<Sayi> listSonSayi(String name)  {
        if(dergiRepository.count()==0){
            return null;
        }
        List<Dergi> dergiler = dergiRepository.findByNameContaining(name.toLowerCase());
        List<Sayi> sonSayilar = new ArrayList<>();
        for (Dergi dergi : dergiler) {
            for(Sayi sayi : dergi.getSonSayi())
            {
                sonSayilar.add(sayi);
            }
        }
        return sonSayilar;
    }

    public List<Dergi> listDergi(String name){
        return dergiRepository.findByNameContaining(name.toLowerCase());
    }

    public List<Dergi> listDergiler(){
        return dergiRepository.findAll();
    }

    public boolean Delete(){
        sayiRepository.deleteAll();
        dergiRepository.deleteAll();
        return true;
    }

}
