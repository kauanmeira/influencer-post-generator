# 📝 PostGenerator - Geração de Conteúdo com Spring AI

O **PostGenerator** é uma API construída com **Spring Boot + Spring AI** que utiliza inteligência artificial para **gerar postagens completas para redes sociais**, acompanhadas de uma **imagem gerada via IA**, a partir de parâmetros simples fornecidos pelo usuário.

---

## 🚀 Objetivo

Este projeto foi criado para facilitar a vida de:
- Pessoas que querem começar a se posicionar nas redes sociais mas **não sabem o que postar**;
- Profissionais que desejam **aumentar seu engajamento** online;
- Criadores de conteúdo e influencers com **alta demanda de publicações**.

Com apenas algumas informações, você recebe uma **postagem pronta para ser usada**.

---

## 🧠 Como funciona?

A API recebe um JSON com os seguintes campos:

```json
{
  "segmento": "Tecnologia",
  "tema": "Inteligência Artificial",
  "redeSocial": "LinkedIn",
  "publicoAlvo": "Desenvolvedores de Software"
}
```

E retorna:

✅ Uma postagem pronta,

✅ Uma imagem gerada com IA,

✅ Um HTML completo com a postagem e imagem embutida.

## 💻 Tecnologias utilizadas

Java 17

Spring Boot 3

Spring AI (Integração com OpenAI)

OpenAI GPT-4/GPT-3.5 para geração de texto

Geração de Imagens com OpenAI DALL·E

Prompt Engineering via arquivos .txt

Contagem de tokens com tiktoken-java para otimização de custos


### 💰 Otimização de tokens
Como a cobrança da OpenAI é feita por token, foi implementada uma estratégia para:

Contar os tokens dos prompts com precisão;

Selecionar o modelo de IA mais econômico (como GPT-3.5) sempre que possível, sem comprometer a qualidade;

Reduzir o custo da operação, tornando a API mais sustentável e acessível.

### ✨ Prompt Engineering
O Prompt Engineering foi aplicado para garantir consistência e adaptabilidade.
Os prompts estão estruturados em arquivos externos com campos dinâmicos, permitindo:

Substituição de variáveis como {{segmento}}, {{redeSocial}}, {{publicoAlvo}}, {{tema}}

Reutilização de prompts de forma escalável

Versionamento e fácil manutenção de templates
