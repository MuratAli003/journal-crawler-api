package com.DergiPlatform.DergiPlatform.router;

import com.DergiPlatform.DergiPlatform.models.Dergi;
import com.DergiPlatform.DergiPlatform.models.Sayi;
import com.DergiPlatform.DergiPlatform.repository.SayiRepository;
import com.DergiPlatform.DergiPlatform.repository.DergiRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class JsoupRouter_OncekiSayi {
    @Autowired
    private DergiRepository dergiRepository;
    @Autowired
    private SayiRepository sayiRepository;

    public List<Dergi> DergiAnaliz(String url) throws IOException, InterruptedException {
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select("a[href*=detay&sayi]");

        Dergi dergi = new Dergi();

        Element baslik = doc.selectFirst("meta[name=title]");
        dergi.setName(baslik.attr("content").toLowerCase());

        Element sonSayi = elements.first();
        dergi.setSonSayi(SayiAnaliz(sonSayi.absUrl("href")));

        List<List<Sayi>> sayilar = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (Element link : elements) {

            Runnable run = () -> {
                final String link_url = link.absUrl("href");
                try
                {
                   sayilar.add(SayiAnaliz(link_url));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };
            executor.submit(run);
        }
        executor.shutdown();
        executor.awaitTermination(40, TimeUnit.SECONDS);

        dergi.setSayi(sayilar);
        dergiRepository.save(dergi);
        return dergiRepository.findAll();
    }

    public List<Sayi> SayiAnaliz(String url)throws IOException
    {
        Document doc = Jsoup.connect(url).get();
        List<Sayi> makaleler = new ArrayList<>();

        Element baslik = doc.selectFirst("div.text-muted > div.container > div.row span");
        Element yilSayiStrong = doc.selectFirst("strong:contains(Yıl-sayı)");

        Elements cards = doc.select("div.media");

        for (Element card : cards) {
            Element doi = card.selectFirst("a[href*=dx.doi]");

            if (doi != null) {
                Sayi sayi = new Sayi();
                sayi.setDoi(doi.absUrl("href").replace(" ", ""));
                sayi.setBaslik(baslik.text().toLowerCase());
                sayi.setSayiYil(yilSayiStrong.nextSibling().toString());

                Element aciklama_and_pdf = card.selectFirst("a[href*=makaleadi]");
                sayi.setAciklama(aciklama_and_pdf.text().toLowerCase());
                sayi.setPdfLink(aciklama_and_pdf.absUrl("href").replace(" ", ""));

                Element ana_yazar = card.selectFirst("strong.d-block > a");
                String yazarlar = ana_yazar.text();
                Element diger_yazarlar = card.selectFirst("strong.d-block");
                yazarlar += diger_yazarlar.ownText();
                sayi.setYazar(yazarlar.toLowerCase());

                sayiRepository.save(sayi);
                makaleler.add(sayi);
            }
        }
        return makaleler;
    }
}

