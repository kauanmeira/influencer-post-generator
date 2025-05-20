package br.com.kauan.postgenerator.domain.chatclient;

import br.com.kauan.postgenerator.domain.posts.Post;
import br.com.kauan.postgenerator.domain.token.TokenService;
import com.knuddels.jtokkit.api.ModelType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ChatClientService {
    private final ChatClient chatClient;
    private final ImageModel imageModel;
    private final TokenService tokenService;

    public ChatClientService(ChatClient.Builder chatClientBuilder, ImageModel imageModel, TokenService tokenService) {
        this.chatClient = chatClientBuilder.build();
        this.imageModel = imageModel;
        this.tokenService = tokenService;
    }

    public String chatGerarPost(String promptUsuario) {
        String system = lerArquivoERetornarConteudo("src/main/resources/system-prompts/system-post-mensagem.txt");

        var token = tokenService.contarTokens(system, promptUsuario);
        String model = token <= 3000 ? ModelType.GPT_4O_MINI.getName() : ModelType.GPT_4O.getName();

        try {
            return this.chatClient.prompt()
                    .system(system)
                    .user(promptUsuario)
                    .options(ChatOptions.builder()
                            .temperature(0.85)
                            .model(model)
                            .build())
                    .advisors(new SimpleLoggerAdvisor())
                    .call()
                    .content();
        } catch (Exception e) {
            return "Erro ao gerar resposta: " + e.getMessage();
        }
    }

    public String chatFormatarPost(String post) {
        String system = lerArquivoERetornarConteudo("src/main/resources/system-prompts/system-formatar-prompt.txt");

        try {
            return this.chatClient.prompt()
                    .system(system)
                    .user(post)
                    .options(ChatOptions.builder()
                            .temperature(0.85)
                            .model(ModelType.GPT_4O_MINI.getName())
                            .build())
                    .advisors(new SimpleLoggerAdvisor())
                    .call()
                    .content();
        } catch (Exception e) {
            return "Erro ao gerar resposta: " + e.getMessage();
        }
    }

    public byte[] chatGerarImagem(String textoPost, Post post) {
        String imageSystem = lerArquivoERetornarConteudo("src/main/resources/system-prompts/image-mensagem.txt");
        String promptFinal = gerarPrompt(textoPost, post, imageSystem);

        var options = ImageOptionsBuilder.builder()
                .height(1024)
                .width(1024)
                .build();
        var response = imageModel.call(new ImagePrompt(promptFinal, options));
        var imageUrl = response.getResult().getOutput().getUrl();
        try (InputStream in = new URL(imageUrl).openStream()) {
            return in.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao baixar imagem: " + e.getMessage());
        }
    }

    private static String gerarPrompt(String textoPost, Post post, String imageSystem) {
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
