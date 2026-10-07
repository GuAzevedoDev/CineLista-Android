# CineLista 🎬

App Android em **Kotlin + Jetpack Compose**, com o tema **lista de filmes favoritos**, desenvolvido para a atividade de navegação Android.

## Telas implementadas

- **Splash:** apresentação automática de 1,2 segundos. O componente que hospeda o `NavHost` decide entre Login e Início conforme a sessão salva.
- **Login:** sessão local demonstrativa usando o nome do usuário; não é autenticação de produção.
- **Início:** saudação, total da coleção e acesso ao catálogo.
- **Filmes:** lista de 12 filmes, remoção individual e estado vazio com ação para restaurar.
- **Perfil:** nome, total de favoritos, esvaziar/restaurar catálogo e sair da sessão.
- **Detalhe:** título, ano, gênero e sinopse do filme correspondente ao identificador recebido.

As três abas são Início, Filmes e Perfil. A barra inferior permanece disponível também no detalhe.

## Como rodar no Android Studio

1. Instale uma versão do Android Studio compatível com Android Gradle Plugin 8.9.1 (Meerkat ou posterior).
2. Clone ou baixe este repositório e abra a pasta raiz do projeto (a que contém `settings.gradle.kts`).
3. Use JDK 17 para o Gradle e instale o Android SDK 35 quando solicitado.
4. Aguarde a sincronização das dependências.
5. Crie um emulador ou conecte um dispositivo Android com API 24 ou superior.
6. Execute a configuração `app`.

Alternativa pela linha de comando, com JDK 17 e SDK configurados:

```sh
chmod +x gradlew
./gradlew assembleDebug lintDebug
```

No Windows, use `gradlew.bat`. O APK fica em `app/build/outputs/apk/debug/app-debug.apk`. A automação **Android Build** compila e executa lint a cada envio, disponibilizando o APK como artefato.

## Como cada requisito foi implementado

| Requisito | Implementação |
|---|---|
| Rotas tipadas | `Routes.kt`: `sealed interface Route` com `@Serializable`, objetos para telas e `Detail(movieId: Int)` |
| Navegação linear | `SplashScreen` usa `LaunchedEffect` e emite uma conclusão após o tempo de apresentação |
| Navegação condicional | `CineApp` consulta `vm.loggedIn` e decide o destino, fora da Splash |
| Parâmetro type-safe | Lista passa `Route.Detail(id)`; destino lê `toRoute<Route.Detail>()` |
| Estado das abas | `saveState`, `restoreState` e `launchSingleTop` juntos; lista usa `rememberLazyListState` |
| ViewModel compartilhado | Uma chamada a `viewModel()` em `CineApp`; mesma instância passada a todas as telas |
| Paleta própria | `ui/theme/Theme.kt`, com `lightColorScheme`; telas usam `MaterialTheme.colorScheme` |
| Estado vazio | Mensagem e botão para restaurar o catálogo quando não existem filmes |
| Acessibilidade | Ícones de ação sem rótulo recebem descrições específicas; ícones decorativos usam `null` |
| Persistência | Sessão, nome e IDs dos filmes salvos em `SharedPreferences`, inclusive lista vazia |

## Roteiro de verificação manual

1. Primeiro início: Splash → Login; nome em branco mantém o botão desabilitado.
2. Entre com um nome: abre Início; Voltar não reabre Splash/Login.
3. Encerre e reabra o app: Splash → Início, mantendo a sessão.
4. Abra Filmes, role até o final, troque para Perfil e retorne: a posição deve permanecer.
5. Abra dois filmes diferentes e confirme título e sinopse correspondentes.
6. Remova um filme; volte e confirme que a coleção e a contagem foram atualizadas.
7. Em Perfil, esvazie a lista; abra Filmes e confira a mensagem de lista vazia.
8. Restaure o catálogo, saia e reinicie: Splash → Login.
9. Com leitor de tela, confira os botões de remover filme e voltar.

## Validação

O projeto inclui compilação e lint automatizados no GitHub Actions. A validação visual e o roteiro acima precisam ser executados em emulador ou dispositivo Android; não devem ser considerados concluídos apenas pela leitura do código.

## Referência

[Documentação oficial: segurança de tipos na navegação](https://developer.android.com/guide/navigation/design/type-safety).
