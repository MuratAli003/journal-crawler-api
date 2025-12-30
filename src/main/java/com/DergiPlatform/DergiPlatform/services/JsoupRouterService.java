package com.DergiPlatform.DergiPlatform.services;

import com.DergiPlatform.DergiPlatform.models.Sayi;
import com.DergiPlatform.DergiPlatform.models.Dergi;
import com.DergiPlatform.DergiPlatform.router.JsoupRouterMain;
import org.jsoup.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class JsoupRouterService {

    @Autowired
    private JsoupRouterMain jsoupRouterMain;

    //SERVİCE METHODS
    public List<List<Dergi>> getDergiler(String url) throws IOException, InterruptedException {
        return jsoupRouterMain.TumDergilerAnaliz(url);//Router belirleme
    }
    public List<Dergi> getDergi(String url) throws IOException, InterruptedException {
        return jsoupRouterMain.DergiAnaliz(url);//Router belirleme
    }
    public List<Sayi> getSayi(String url) throws IOException {
        return jsoupRouterMain.SayiAnaliz(url);//Router belirleme
    }
}