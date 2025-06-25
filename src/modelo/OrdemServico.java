package modelo;

public class OrdemServico {
    private static int contadorId = 1; // Gerador automático de ID
    private int id;
    private String autor;      // Usuário que criou a ordem
    private String descricao;  // Descrição da ordem
    private String status;     // Ex: "ativa", "desativada", "em andamento", etc.

    public OrdemServico(String autor, String descricao, String status) {
        this.id = contadorId++;
        this.autor = autor;
        this.descricao = descricao;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OrdemServico{" +
                "id=" + id +
                ", autor='" + autor + '\'' +
                ", descricao='" + descricao + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
