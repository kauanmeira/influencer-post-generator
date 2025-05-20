package br.com.kauan.postgenerator.domain.posts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Post {
    private String segmento;
    private String redeSocial;
    private String tema;
    private String publicoAlvo;
}
