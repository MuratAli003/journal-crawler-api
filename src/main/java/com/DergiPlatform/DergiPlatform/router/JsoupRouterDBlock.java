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
public class JsoupRouterDBlock {

    @Autowired
    private DergiRepository dergiRepository;
    @Autowired
    private SayiRepository sayiRepository;


    public List<Dergi> DergiAnaliz(String url) throws IOException, InterruptedException {
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select("a.d-block");
        Dergi makale = new Dergi();

        Element head = doc.selectFirst("meta[name=title]");
        String baslik =  head.attr("content");
        makale.setName(baslik.toLowerCase());

        Element sonSayi = elements.first();
        makale.setSonSayi(SayiAnaliz(sonSayi.absUrl("href")));

        List<List<Sayi>> sayilar = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (Element link : elements) {
            Runnable run = () -> {

                final String link_url = link.absUrl("href");
                try {
                    sayilar.add(SayiAnaliz(link_url));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };
            executor.submit(run);
        }
        executor.shutdown();
        executor.awaitTermination(40, TimeUnit.SECONDS);

        makale.setSayi(sayilar);
        dergiRepository.save(makale);
        return dergiRepository.findAll();
    }

    public List<Sayi> SayiAnaliz(String url) throws IOException
    {
        Document doc = Jsoup.connect(url).get();

        List<Sayi> makaleler = new ArrayList<>();

        Element sayi_yil = doc.selectFirst("span:contains(Year-Number),span:contains(Yıl-Sayı)");
        Element head = doc.selectFirst("meta[name=title]");
        String baslik =  head.attr("content");

        Elements cards = doc.select("div.card-body > div.card");

        for (Element card : cards) {

            Element doi = card.selectFirst("a[href*=doi]");

            if (doi != null) {
                Sayi sayi = new Sayi();
                sayi.setDoi(doi.absUrl("href").replace(" ", ""));
                sayi.setBaslik(baslik.toLowerCase());
                sayi.setSayiYil(sayi_yil.nextSibling().toString().toLowerCase());

                Element aciklama = card.selectFirst("a.text-muted");
                if (aciklama != null) {
                    sayi.setAciklama(aciklama.text().toLowerCase());
                }
                Element pdf_link = card.selectFirst("ul.list-inline > li.list-inline-item  a.text-muted");
                if (pdf_link != null) {
                    sayi.setPdfLink(pdf_link.absUrl("href").replace(" ", ""));
                }

                Element yazar = card.selectFirst("a[href*=yazar]");
                String yazarlar = yazar.text();
                Elements diger_yazarlar = card.select("div.flex-shrink-0s");
                for (Element x : diger_yazarlar) {
                    if (!x.select("a[href*=yazar]").isEmpty()) {
                        yazarlar += x.ownText();
                    }
                }
                sayi.setYazar(yazarlar.toLowerCase());

                sayiRepository.save(sayi);
                makaleler.add(sayi);
            }
        }
        return makaleler;
    }
}
