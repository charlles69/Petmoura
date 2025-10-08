package crud_veterinario;

import java.math.BigDecimal;
import java.time.LocalDate;

import dao.DAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import model.Veterinario;

public class VeterinarioCreateController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private TextField txtCrv;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtEspecialidade;
    @FXML private TextField txtSalario;
    @FXML private DatePicker dateDataAdmissao;
    @FXML private TextArea txtObservacoes;

    private Veterinario veterinarioEmEdicao;
    private boolean modoEdicao = false;

    @FXML
    public void initialize() {
        dateDataAdmissao.setValue(LocalDate.now());
        System.out.println("✅ VeterinarioCreateController inicializado!");
    }

    public void carregarVeterinarioParaEdicao(Veterinario veterinario) {
        this.veterinarioEmEdicao = veterinario;
        this.modoEdicao = true;
        
        txtNome.setText(veterinario.getNome());
        txtCpf.setText(veterinario.getCpf());
        txtCrv.setText(veterinario.getCrv());
        txtTelefone.setText(veterinario.getTelefone());
        txtEmail.setText(veterinario.getEmail());
        txtEspecialidade.setText(veterinario.getEspecialidade());
        txtSalario.setText(veterinario.getSalario() != null ? veterinario.getSalario().toString() : "");
        dateDataAdmissao.setValue(veterinario.getDataAdmissao());
    }

    @FXML
    private void salvarVeterinario() {
        try {
            if (!validarCampos()) {
                mostrarAlerta("Campos Obrigatórios", "Preencha os campos obrigatórios.", Alert.AlertType.WARNING);
                return;
            }

            String nome = txtNome.getText();
            String cpf = txtCpf.getText();
            String crv = txtCrv.getText();
            String telefone = txtTelefone.getText();
            String email = txtEmail.getText();
            String especialidade = txtEspecialidade.getText();
            BigDecimal salario = parseSalario(txtSalario.getText());
            LocalDate dataAdmissao = dateDataAdmissao.getValue();

            if (modoEdicao && veterinarioEmEdicao != null) {
                veterinarioEmEdicao.setNome(nome);
                veterinarioEmEdicao.setCpf(cpf);
                veterinarioEmEdicao.setCrv(crv);
                veterinarioEmEdicao.setTelefone(telefone);
                veterinarioEmEdicao.setEmail(email);
                veterinarioEmEdicao.setEspecialidade(especialidade);
                veterinarioEmEdicao.setSalario(salario);
                veterinarioEmEdicao.setDataAdmissao(dataAdmissao);

                new DAO<>(Veterinario.class).atualizarTransacional(veterinarioEmEdicao);
                mostrarAlerta("Sucesso", "Veterinário atualizado com sucesso!", Alert.AlertType.INFORMATION);
            } else {
                Veterinario novoVeterinario = new Veterinario(nome, cpf, crv, telefone, email, especialidade, salario, dataAdmissao);
                new DAO<>(Veterinario.class).incluirTransacional(novoVeterinario);
                mostrarAlerta("Sucesso", "Veterinário cadastrado com sucesso!", Alert.AlertType.INFORMATION);
            }

            voltarParaLista();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Falha ao salvar veterinário: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void limparCampos() {
        txtNome.clear();
        txtCpf.clear();
        txtCrv.clear();
        txtTelefone.clear();
        txtEmail.clear();
        txtEspecialidade.clear();
        txtSalario.clear();
        dateDataAdmissao.setValue(LocalDate.now());
        modoEdicao = false;
        veterinarioEmEdicao = null;
    }

    @FXML
    private void voltarParaLista() {
        try {
            StackPane painel = (StackPane) txtNome.getScene().lookup("#painelConteudo");
            if (painel != null) {
                javafx.scene.Node tela = javafx.fxml.FXMLLoader.load(getClass().getResource("/telas/view/TelaListaVeterinario.fxml"));
                painel.getChildren().setAll(tela);
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao voltar: " + e.getMessage());
        }
    }

    private boolean validarCampos() {
        boolean valido = true;
        
        if (txtNome.getText() == null || txtNome.getText().trim().isEmpty()) {
            destacarCampoErro(txtNome);
            valido = false;
        } else removerDestaqueErro(txtNome);
        
        if (txtCpf.getText() == null || txtCpf.getText().trim().isEmpty()) {
            destacarCampoErro(txtCpf);
            valido = false;
        } else removerDestaqueErro(txtCpf);
        
        if (txtCrv.getText() == null || txtCrv.getText().trim().isEmpty()) {
            destacarCampoErro(txtCrv);
            valido = false;
        } else removerDestaqueErro(txtCrv);
        
        return valido;
    }

    private void destacarCampoErro(Control campo) {
        campo.setStyle("-fx-border-color: red; -fx-border-width: 2;");
    }

    private void removerDestaqueErro(Control campo) {
        campo.setStyle("");
    }

    private BigDecimal parseSalario(String texto) {
        if (texto == null || texto.trim().isEmpty()) return null;
        try {
            return new BigDecimal(texto.replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}