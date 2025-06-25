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

    public static String alterarStatusOrdem(int id, String novoStatus, String perfil) {
        if (!"adm".equalsIgnoreCase(perfil)) {
            return "Token inválido";
        }

        if (!"finalizada".equalsIgnoreCase(novoStatus) && !"cancelada".equalsIgnoreCase(novoStatus)) {
            return "Novo status inválido";
        }

        OrdemServico ordem = buscarPorId(id);
        if (ordem == null) {
            return "Ordem não encontrada";
        }

        if ("finalizada".equalsIgnoreCase(ordem.getStatus()) || "cancelada".equalsIgnoreCase(ordem.getStatus())) {
            return "Ordem já finalizada/cancelada";
        }

        ordem.setStatus(novoStatus.toLowerCase());
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
