package com.DergiPlatform.DergiPlatform.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Sayi {

    @Id
    private String doi;
    private String baslik;
    private String sayiYil;
    private String aciklama;
    private String yazar;
    private String pdfLink;

}
