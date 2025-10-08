// Arquivo: MainLayoutController.java
package tela_main_controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class MainLayoutController {

    @FXML private Label labelRelogio;
    @FXML private StackPane painelConteudo;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM, HH:mm", Locale.forLanguageTag("pt-BR"));

    @FXML
    public void initialize() {
        iniciarRelogio();
        abrirDashboard();
    }

    private void iniciarRelogio() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0), _ -> {
            LocalDateTime agora = LocalDateTime.now();
            String textoFormatado = formatter.format(agora);
            String textoComMesMaiusculo = capitalizarMes(textoFormatado);
            labelRelogio.setText(textoComMesMaiusculo);
 
        }), new KeyFrame(Duration.seconds(60)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private String capitalizarMes(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        String[] partes = texto.split(" ");
        if (partes.length < 2) return texto;
        String mes = partes[1].replace(",", "");
        mes = mes.substring(0, 1).toUpperCase() + mes.substring(1);
        int indexVirgula = texto.indexOf(",");
        String resto = indexVirgula != -1 ? texto.substring(indexVirgula) : "";
        return partes[0] + " " + mes + resto;
    }

    // ✅ CORRIGIDO: Método com nome correto
    @FXML
    public void abrirDashboard() {
        carregarTela("/telas/view/TelaDashboard.fxml");
    }

    // ✅ CORRIGIDO: Método com nome correto
    @FXML
    public void abrirListaAnimais() {
        carregarTela("/telas/view/TelaListaAnimal.fxml");
    }

    // ✅ CORRIGIDO: Método para Visão Geral
    @FXML
    public void abrirVisaoGeral() {
        carregarTela("/telas/view/TelaDashboard.fxml");
    }

    @FXML
public void abrirListaVeterinarios() {
    carregarTela("/telas/view/TelaListaVeterinario.fxml");
}

@FXML
public void abrirListaTutores() {
    carregarTela("/telas/view/TelaListaTutor.fxml");
}
    
public void testarCaminhos() {
    System.out.println("=== TESTANDO CAMINHOS FXML ===");
    
    String[] caminhos = {
        "/telas/view/TelaListaAnimal.fxml",
        "/telas/view/TelaCadastroAnimal.fxml",
        "/telas/view/TelaDashboard.fxml",
        "/telas/view/MainLayout.fxml"
    };
    
    for (String caminho : caminhos) {
        java.net.URL url = getClass().getResource(caminho);
        System.out.println(caminho + " -> " + (url != null ? "✅ ENCONTRADO" : "❌ NÃO ENCONTRADO"));
    }
    
    System.out.println("ClassLoader: " + getClass().getClassLoader());
    System.out.println("=== FIM DO TESTE ===");
}

private void carregarTela(String caminho) {
    try {
        System.out.println("🔄 Tentando carregar: " + caminho);
        
        // Verifica se o arquivo existe
        java.net.URL resource = getClass().getResource(caminho);
        if (resource == null) {
            System.err.println("❌ ARQUIVO NÃO ENCONTRADO: " + caminho);
            System.err.println("❌ Caminho absoluto: " + new java.io.File(".").getAbsolutePath());
            return;
        }
        
        System.out.println("✅ Arquivo encontrado: " + resource);
        
        Node tela = FXMLLoader.load(resource);
        tela.setOpacity(0);
        painelConteudo.getChildren().setAll(tela);

        FadeTransition fade = new FadeTransition(Duration.millis(900), tela);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
        
        System.out.println("✅ Tela carregada com sucesso!");

    } catch (IOException e) {
        System.err.println("❌ Erro IO ao carregar tela: " + caminho);
        e.printStackTrace();
    } catch (Exception e) {
        System.err.println("❌ Erro geral ao carregar tela: " + e.getMessage());
        e.printStackTrace();
    }
}



    @FXML
    private void sair() {
        Platform.exit();
    }
}