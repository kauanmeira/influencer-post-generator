package br.com.kauan.postgenerator.controller;

import br.com.kauan.postgenerator.domain.chatclient.ChatClientService;
import br.com.kauan.postgenerator.domain.posts.Post;
import br.com.kauan.postgenerator.domain.posts.PostagemGeradaResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/post")
public class GeradorDePostController {

    private final ChatClientService chatService;

    public GeradorDePostController(ChatClientService chatService) {
        this.chatService = chatService;
    }

    private String higienizar(String textoPost) {
        textoPost.replace("\n", "").replace("\\n", "");
        return textoPost;
    }

    @PostMapping
    public ResponseEntity<String> gerarPostagem(@RequestBody Post post) throws IOException {
        String promptUsuario = ChatClientService.formatarPromptUsuario(post);

        String textoPost = chatService.chatGerarPost(promptUsuario);
        String postFormatado = chatService.chatFormatarPost(textoPost.translateEscapes());
        byte[] imagemGerada = chatService.chatGerarImagem(textoPost, post);

        String base64Imagem = Base64.getEncoder().encodeToString(imagemGerada);
        String htmlCompleto = ChatClientService.formatarResultado(postFormatado, base64Imagem);

        salvarHtml(htmlCompleto);

        return ResponseEntity.ok(htmlCompleto);
    }

    private static void salvarHtml(String htmlCompleto) throws IOException {
        String id = UUID.randomUUID().toString();
        Path caminho = Paths.get("posts-gerados", id + ".html");
        Files.createDirectories(caminho.getParent());
        Files.writeString(caminho, htmlCompleto);
    }


    @GetMapping(value = "/imagem/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImagem(@PathVariable String id) throws IOException {
        Path caminho = Paths.get("imagens-geradas", id + ".png");

        if (!Files.exists(caminho)) {
            return ResponseEntity.notFound().build();
        }

        byte[] imagem = Files.readAllBytes(caminho);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imagem);
    }
}
