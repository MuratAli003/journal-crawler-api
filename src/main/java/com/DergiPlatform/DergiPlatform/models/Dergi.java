package com.DergiPlatform.DergiPlatform.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.List;

@Getter
@Setter
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Dergi {
    @Id
    private String name;
    private List<List<Sayi>> sayi;
    private List<Sayi> sonSayi;
}
