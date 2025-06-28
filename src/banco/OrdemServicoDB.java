package banco;

import modelo.OrdemServico;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrdemServicoDB {
    private static List<OrdemServico> ordens = new ArrayList<>();

    public static void criarOrdemServico(OrdemServico ordem) {
        ordens.add(ordem);
    }

    public static List<OrdemServico> listarOrdensServico(String usuario, String perfil, String filtroStatus) {
        return ordens.stream()
                .filter(ordem -> {
                    // Administrador vê todas as ordens
                    if ("adm".equalsIgnoreCase(perfil)) {
                        if ("todas".equalsIgnoreCase(filtroStatus) || filtroStatus == null || filtroStatus.isEmpty()) {
                            return true; // Todas as ordens sem filtro
                        } else {
                            return ordem.getStatus().equalsIgnoreCase(filtroStatus);
                        }
                    }

                    // Usuário comum só vê as próprias ordens, com ou sem filtro
                    boolean ehDono = ordem.getAutor().equals(usuario);
                    if ("todas".equalsIgnoreCase(filtroStatus)) {
                        return ehDono;
                    }

                    boolean statusBate = filtroStatus == null || filtroStatus.isEmpty()
                            || ordem.getStatus().equalsIgnoreCase(filtroStatus);
                    return ehDono && statusBate;
                })
                .collect(Collectors.toList());
    }



    public static boolean editarOrdemServico(int id, String usuario, String perfil, String novaDescricao, String novoStatus) {
        for (OrdemServico ordem : ordens) {
            boolean podeEditar = perfil.equals("adm") || ordem.getAutor().equals(usuario);
            if (ordem.getId() == id && podeEditar) {
                ordem.setDescricao(novaDescricao);
                ordem.setStatus(novoStatus);
                return true;
            }
        }
        return false;
    }

    public static String alterarOrdem(int id, String novoStatus, String novaDescricao, String perfil) {
        if (!"adm".equalsIgnoreCase(perfil)) {
            return "Token inválido";
        }

        if (novoStatus == null || 
            !(novoStatus.equalsIgnoreCase("pendente") || 
              novoStatus.equalsIgnoreCase("finalizada") || 
              novoStatus.equalsIgnoreCase("cancelada"))) {
            return "Novo status inválido";
        }

        if (novaDescricao == null || novaDescricao.trim().length() < 3 || novaDescricao.length() > 150) {
            return "Descrição inválida";
        }

        OrdemServico ordem = buscarPorId(id);
        if (ordem == null) {
            return "Ordem não encontrada";
        }

        ordem.setStatus(novoStatus.toLowerCase());
        ordem.setDescricao(novaDescricao.trim());
        return "sucesso";
    }



    public static OrdemServico buscarPorId(int id) {
        for (OrdemServico ordem : ordens) {
            if (ordem.getId() == id) {
                return ordem;
            }
        }
        return null;
    }
}
