package com.DergiPlatform.DergiPlatform.controller;

import com.DergiPlatform.DergiPlatform.models.Sayi;
import com.DergiPlatform.DergiPlatform.models.Dergi;
import com.DergiPlatform.DergiPlatform.services.JsoupRouterService;
import com.DergiPlatform.DergiPlatform.services.MakaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/rest/api")
public class RestMakaleController {

    @Autowired
    private MakaleService makaleService;
    @Autowired
    private JsoupRouterService jsoupRouter_Service;

    //CRAWLER
    @PostMapping("/get-dergiler")
    public List<List<Dergi>> getDergiler(@RequestParam(name = "url") String url) throws IOException, InterruptedException {
        return jsoupRouter_Service.getDergiler(url);
    }
    @PostMapping("/get-dergi")
    public List<Dergi> getDergi(@RequestParam(name = "url") String url) throws IOException, InterruptedException {
        return jsoupRouter_Service.getDergi(url);
    }

    @PostMapping("/get-sayi")
    public List<Sayi> getSayi(@RequestParam(name ="url") String url , @RequestParam(name = "sayi_id") String sayi_id) throws IOException {
        String newUrl = url + "&sayi_id="+ sayi_id;
        return jsoupRouter_Service.getSayi(newUrl);
    }

    //DATABASE
    @GetMapping("/list-son-sayi")
    public List<Sayi> listSonSayi(@RequestParam(name ="name") String name){
        return makaleService.listSonSayi(name);
    }

    @GetMapping("/list-dergi")
    public List<Dergi> listDergi(@RequestParam(name ="name") String name){
        return makaleService.listDergi(name);
    }

    @GetMapping("/list-dergiler")
    public List<Dergi> listDergiler(){
        return makaleService.listDergiler();
    }

    @DeleteMapping("/delete")
    public boolean Sil()
    {
        return makaleService.Delete();
    }

}
