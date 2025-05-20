package br.com.kauan.postgenerator.domain.chatclient;

import br.com.kauan.postgenerator.domain.posts.Post;
import br.com.kauan.postgenerator.domain.token.TokenService;
import br.com.kauan.postgenerator.utils.PromptUtils;
import com.knuddels.jtokkit.api.ModelType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
public class ChatClientService {

    private final ChatClient chatClient;
    private final ImageModel imageModel;
    private final TokenService tokenService;
    private final PromptUtils promptUtils;

    public ChatClientService(ChatClient.Builder chatClientBuilder, ImageModel imageModel, TokenService tokenService, PromptUtils promptUtils) {
        this.chatClient = chatClientBuilder.build();
        this.imageModel = imageModel;
        this.tokenService = tokenService;
        this.promptUtils = promptUtils;
    }

    public String chatGerarPost(String promptUsuario) {
        String system = promptUtils.lerArquivoERetornarConteudo("src/main/resources/system-prompts/system-post-mensagem.txt");

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
        String system = promptUtils.lerArquivoERetornarConteudo("src/main/resources/system-prompts/system-formatar-prompt.txt");

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
        String imageSystem = promptUtils.lerArquivoERetornarConteudo("src/main/resources/system-prompts/image-mensagem.txt");
        String promptFinal = PromptUtils.gerarPrompt(textoPost, post, imageSystem);

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
}
