package br.com.kauan.postgenerator.utils;

import br.com.kauan.postgenerator.domain.posts.Post;
import org.springframework.boot.json.JsonParseException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PromptUtils {

    public static String gerarPrompt(String textoPost, Post post, String imageSystem) {
        return String.format("""
                %s
                
                1. Rede Social: %s
                2. Postagem: %s
                """, imageSystem, post.getRedeSocial(), textoPost);
    }

    public String lerArquivoERetornarConteudo(String filePath) {
        byte[] bytes;

        try {
            bytes = Files.readAllBytes(Paths.get(filePath));
        } catch (Exception e) {
            throw new JsonParseException(e);
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String formatarPromptUsuario(Post post) {
        return String.format("""
                1. Segmento: %s
                2. Rede Social: %s
                3. Tema: %s
                4. Público-alvo: %s
                """, post.getSegmento(), post.getRedeSocial(), post.getTema(), post.getPublicoAlvo());

    }

    public static String formatarResultado(String postFormatado, String base64Imagem) {
        return """
        <!DOCTYPE html>
        <html lang="pt-br">
        <head>
            <meta charset="UTF-8">
            <title>Post Gerado</title>
        </head>
        <body>
            %s
            <br/>
            <img src="data:image/png;base64,%s" alt="Imagem gerada"/>
        </body>
        </html>
        """.formatted(postFormatado, base64Imagem);
    }
}
