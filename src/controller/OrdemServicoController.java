package controller;

import banco.OrdemServicoDB;
import banco.UsuarioDB;
import modelo.OrdemServico;
import modelo.Usuario;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class OrdemServicoController {

    public static JSONObject realizarCadastroOrdem(JSONObject entrada) {
        JSONObject resposta = new JSONObject();
        resposta.put("operacao", "cadastrar_ordem");

        String token = (String) entrada.get("token");
        String descricao = (String) entrada.get("descricao");

        Usuario usuario = UsuarioDB.getUsuario(token);
        if (usuario == null) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Token invalido");
            return resposta;
        }

        if (descricao == null || descricao.trim().length() < 3 || descricao.length() > 150) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Descrição inválida");
            return resposta;
        }

        OrdemServico ordem = new OrdemServico(usuario.getUsuario(), descricao, "pendente");
        OrdemServicoDB.criarOrdemServico(ordem);

        resposta.put("status", "sucesso");
        resposta.put("mensagem", "Ordem cadastrada com sucesso");
        return resposta;
    }

    public static JSONObject realizarListagemOrdens(JSONObject entrada) {
        JSONObject resposta = new JSONObject();
        resposta.put("operacao", "listar_ordens");

        String token = (String) entrada.get("token");
        String filtro = (String) entrada.get("filtro");

        Usuario usuario = UsuarioDB.getUsuario(token);
        if (usuario == null) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Token invalido");
            return resposta;
        }

        var ordens = OrdemServicoDB.listarOrdensServico(usuario.getUsuario(), usuario.getPerfil(), filtro);

        if (ordens.isEmpty()) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Nenhuma ordem disponível");
            return resposta;
        }

        JSONArray ordensJson = new JSONArray();
        for (OrdemServico ordem : ordens) {
            JSONObject obj = new JSONObject();
            obj.put("id", ordem.getId());
            obj.put("autor", ordem.getAutor());
            obj.put("descricao", ordem.getDescricao());
            obj.put("status", ordem.getStatus());
            ordensJson.add(obj);
        }

        resposta.put("status", "sucesso");
        resposta.put("ordens", ordensJson);
        return resposta;
    }

    public static JSONObject realizarEdicaoOrdem(JSONObject entrada) {
        JSONObject resposta = new JSONObject();
        resposta.put("operacao", "editar_ordem");

        String token = (String) entrada.get("token");
        String novaDescricao = (String) entrada.get("nova_descricao");
        Long idLong = (Long) entrada.get("id_ordem");

        Usuario usuario = UsuarioDB.getUsuario(token);
        if (usuario == null) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Token invalido");
            return resposta;
        }

        if (novaDescricao == null || novaDescricao.trim().length() < 3 || novaDescricao.length() > 150) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Descrição inválida");
            return resposta;
        }

        int id = idLong.intValue();
        OrdemServico ordem = OrdemServicoDB.buscarPorId(id);
        if (ordem == null) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Ordem não encontrada");
            return resposta;
        }

        boolean podeEditar = usuario.getPerfil().equals("adm") || ordem.getAutor().equals(usuario.getUsuario());
        if (!podeEditar) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Permissão negada. Só administradores podem editar ordens de outros usuários!");
            return resposta;
        }

        if (!ordem.getStatus().equalsIgnoreCase("pendente")) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Ordem já finalizada. Somente ordens pendentes podem ser editadas!");
            return resposta;
        }

        boolean sucesso = OrdemServicoDB.editarOrdemServico(id, usuario.getUsuario(), usuario.getPerfil(), novaDescricao, "pendente");
        if (sucesso) {
            resposta.put("status", "sucesso");
            resposta.put("mensagem", "Ordem editada com sucesso");
        } else {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Erro ao editar a ordem");
        }

        return resposta;
    }
    
    public static JSONObject realizarAlteracaoStatusOrdem(JSONObject entrada) {
        JSONObject resposta = new JSONObject();
        resposta.put("operacao", "alterar_ordem");

        String token = (String) entrada.get("token");
        String novoStatus = (String) entrada.get("novo_status");
        Long idLong = (Long) entrada.get("id_ordem");

        Usuario usuario = UsuarioDB.getUsuario(token);
        if (usuario == null || !"adm".equalsIgnoreCase(usuario.getPerfil())) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Token inválido");
            return resposta;
        }

        int id = idLong.intValue();
        String resultado = OrdemServicoDB.alterarStatusOrdem(id, novoStatus, usuario.getPerfil());

        switch (resultado) {
            case "sucesso":
                resposta.put("status", "sucesso");
                resposta.put("mensagem", "Ordem alterada com sucesso");
                break;
            default:
                resposta.put("status", "erro");
                resposta.put("mensagem", resultado);
                break;
        }

        return resposta;
    }

}
