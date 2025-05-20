package br.com.kauan.postgenerator.domain.posts;

public class PostagemGeradaResponse {
    private final String postagem;
    private final String imagemUrl;

    public PostagemGeradaResponse(String postagem, String imagemUrl) {
        this.postagem = postagem;
        this.imagemUrl = imagemUrl;
    }

    public String getPostagem() {
        return postagem;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }
}

