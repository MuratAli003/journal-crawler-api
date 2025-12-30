package com.DergiPlatform.DergiPlatform.router;

import com.DergiPlatform.DergiPlatform.models.Dergi;
import com.DergiPlatform.DergiPlatform.models.Sayi;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsoupRouterMain {
    @Autowired
    private JsoupRouterDBlock jsoupRouter_DBlock;
    @Autowired
    private JsoupRouterOncekiSayi jsoupRouter_OncekiSayi;

    public String url_control(String URL)throws IOException {
        Document doc = Jsoup.connect(URL).get();

        String res = "";
        if(doc.body().text().isEmpty()){
            if(!doc.head().select("script").isEmpty()){
                Element scriptTag = doc.head().selectFirst("script");
                String script = scriptTag.html();
                String[] parts = script.split("\"");
                res = URL + parts[1].substring(1);
                return res;
            }
        }
        return URL;
    }

    public List<List<Dergi>> TumDergilerAnaliz(String url) throws IOException, InterruptedException {
        String new_url = url_control(url);
        List<List<Dergi>> list = new ArrayList<>();
        Document x = Jsoup.connect(new_url).
                userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.5 Safari/605.1.15")
                .get();

        //DERGİ PLATFORM ANA SAYFASININ İÇERİĞİ
        if (!x.select("div.sj-upcomingbook > div.sj-postcontent > h3 > a").isEmpty())
        {
            Elements elements = x.select("div.sj-upcomingbook > div.sj-postcontent > h3 > a");
            for(Element element : elements){
                list.add(DergiAnaliz(element.absUrl("href")));
            }
        }
        return list;
    }

    public List<Dergi> DergiAnaliz(String url) throws IOException, InterruptedException {
        String new_url = url_control(url);
        Document x = Jsoup.connect(new_url).
                userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.5 Safari/605.1.15")
                .get();

        if(!x.select("a.d-block").isEmpty()){

            return jsoupRouter_DBlock.DergiAnaliz(new_url);
        }

        else if(!x.select("a[href*=onceki]").isEmpty()){
            Element url_new = x.selectFirst("a[href*=onceki]");
            Document doc = Jsoup.connect(url_new.absUrl("href")).get();

            if(!doc.select("a.d-block").isEmpty())
            {
                return jsoupRouter_DBlock.DergiAnaliz(url_new.absUrl("href"));
            }
            else if (!doc.select("a[href*=detay&sayi]").isEmpty()){
                return jsoupRouter_OncekiSayi.DergiAnaliz(url_new.absUrl("href"));
            }
        }
        //TURKİSH STUDİES
        else if(!x.select("section#section a").isEmpty()){
            Elements doc = x.select("div.panel-grid a[href]:not([href*=edu.tr])");

            for (Element e : doc) {
                DergiAnaliz(e.absUrl("href"));
            }
        }
        return null;
    }

    public List<Sayi> SayiAnaliz(String url) throws IOException {
        String new_url = url_control(url);
        Document x = Jsoup.connect(new_url).
                userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.5 Safari/605.1.15")
                .get();

        if (!x.select("div.card-body > div.card").isEmpty())
        {
            return jsoupRouter_DBlock.SayiAnaliz(new_url);
        }

        else if(!x.select("div.text-muted > div.container > div.row span").isEmpty() )
        {
            return jsoupRouter_OncekiSayi.SayiAnaliz(new_url);
        }
        return null;
    }

}
