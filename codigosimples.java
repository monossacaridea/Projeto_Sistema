package Agencia;

import java.util.*;
import javax.swing.*;

// Cliente abstrato
abstract class Cliente {
    protected String nome;
    protected String telefone;
    protected String email;

    public Cliente(String nome, String telefone, String email) {
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
    }

    public abstract String getIdentificacao();

    public String getResumo() {
        return nome + " (" + getIdentificacao() + ")";
    }
}

class ClienteNacional extends Cliente {
    private String cpf;

    public ClienteNacional(String nome, String telefone, String email, String cpf) {
        super(nome, telefone, email);
        this.cpf = cpf;
    }

    public String getIdentificacao() {
        return cpf;
    }
}

class ClienteEstrangeiro extends Cliente {
    private String passaporte;

    public ClienteEstrangeiro(String nome, String telefone, String email, String passaporte) {
        super(nome, telefone, email);
        this.passaporte = passaporte;
    }

    public String getIdentificacao() {
        return passaporte;
    }
}

abstract class PacoteViagem {
    protected String nome;
    protected String destino;
    protected int duracao;
    protected double preco;
    protected String tipo;

    public PacoteViagem(String nome, String destino, int duracao, double preco, String tipo) {
        this.nome = nome;
        this.destino = destino;
        this.duracao = duracao;
        this.preco = preco;
        this.tipo = tipo;
    }

    public double getPreco() {
        return preco;
    }

    public String getResumo() {
        return nome + " - " + tipo + " (" + destino + ", " + duracao + " dias, R$" + preco + ")";
    }

    public String toString() {
        return getResumo();
    }
}

class PacoteAventura extends PacoteViagem {
    public PacoteAventura(String nome, String destino, int duracao, double preco) {
        super(nome, destino, duracao, preco, "Aventura");
    }
}

class PacoteLuxo extends PacoteViagem {
    public PacoteLuxo(String nome, String destino, int duracao, double preco) {
        super(nome, destino, duracao, preco, "Luxo");
    }
}

class PacoteCultural extends PacoteViagem {
    public PacoteCultural(String nome, String destino, int duracao, double preco) {
        super(nome, destino, duracao, preco, "Cultural");
    }
}

class ServicoAdicional {
    String descricao;
    double preco;

    public ServicoAdicional(String descricao, double preco) {
        this.descricao = descricao;
        this.preco = preco;
    }

    public double getPreco() {
        return preco;
    }

    public String getResumo() {
        return descricao + " (R$" + preco + ")";
    }

    public String toString() {
        return getResumo();
    }
}

class Pedido {
    private Cliente cliente;
    private List<PacoteViagem> pacotes = new ArrayList<>();
    private List<ServicoAdicional> servicos = new ArrayList<>();

    public Pedido(Cliente cliente) {
        this.cliente = cliente;
    }

    public void adicionarPacote(PacoteViagem pacote) {
        pacotes.add(pacote);
    }

    public void adicionarServico(ServicoAdicional servico) {
        servicos.add(servico);
    }

    public String getResumo() {
        StringBuilder sb = new StringBuilder("Cliente: " + cliente.getResumo() + "\nPacotes:\n");
        for (PacoteViagem p : pacotes) sb.append(" - ").append(p.getResumo()).append("\n");
        if (!servicos.isEmpty()) {
            sb.append("Serviços adicionais:\n");
            for (ServicoAdicional s : servicos) sb.append(" - ").append(s.getResumo()).append("\n");
        }
        sb.append("Total do pedido: R$").append(String.format("%.2f", getTotal())).append("\n");
        return sb.toString();
    }

    public double getTotal() {
        double total = 0;
        for (PacoteViagem p : pacotes) total += p.getPreco();
        for (ServicoAdicional s : servicos) total += s.getPreco();
        return total;
    }
}

public class AgenciaViagens {
    public static void main(String[] args) {
        List<Cliente> clientes = new ArrayList<>();
        List<PacoteViagem> pacotes = new ArrayList<>();
        List<ServicoAdicional> servicos = new ArrayList<>();
        List<Pedido> pedidos = new ArrayList<>();

        while (true) {
            String[] opcoes = {"Clientes", "Pacotes", "Serviços", "Pedidos", "Resumo Geral", "Sair"};
            int opcao = JOptionPane.showOptionDialog(null, "Escolha uma opção:", "Agência de Viagens",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

            if (opcao == 0) menuClientes(clientes);
            else if (opcao == 1) menuPacotes(pacotes);
            else if (opcao == 2) cadastrarServico(servicos);
            else if (opcao == 3) criarPedido(clientes, pacotes, servicos, pedidos);
            else if (opcao == 4) mostrarResumoGeral(pedidos);
            else break;
        }
    }

    private static void menuClientes(List<Cliente> clientes) {
        String[] opcoes = {"Cadastrar", "Visualizar", "Excluir", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(null, "Menu de Clientes:", "Clientes",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha == 0) cadastrarCliente(clientes);
        else if (escolha == 1) listar(clientes.stream().map(Cliente::getResumo).toList(), "Clientes");
        else if (escolha == 2) excluirItem(clientes, "cliente");
    }

    private static void menuPacotes(List<PacoteViagem> pacotes) {
        String[] opcoes = {"Cadastrar", "Visualizar", "Excluir", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(null, "Menu de Pacotes:", "Pacotes",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha == 0) cadastrarPacote(pacotes);
        else if (escolha == 1) listar(pacotes.stream().map(PacoteViagem::getResumo).toList(), "Pacotes");
        else if (escolha == 2) excluirItem(pacotes, "pacote");
    }

    private static void cadastrarCliente(List<Cliente> clientes) {
        String nome;
        while (true) {
            nome = JOptionPane.showInputDialog("Nome:");
            if (nome == null) return; 
            if (nome.isBlank()) {
                JOptionPane.showMessageDialog(null, "Nome obrigatório.", "Cadastro", JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }

        String telefone;
        while (true) {
            telefone = JOptionPane.showInputDialog("Telefone:");
            if (telefone == null) return; 
            if (!telefone.matches("\\d+")) {
                JOptionPane.showMessageDialog(null, "O número de telefone deve conter apenas números.", "Cadastro", JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }

        String email;
        while (true) {
            email = JOptionPane.showInputDialog("Email:");
            if (email == null) return; 
            if (email.isBlank()) {
                JOptionPane.showMessageDialog(null, "Email obrigatório.", "Cadastro", JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }

        String[] tipos = {"Nacional", "Estrangeiro"};
        int tipo = JOptionPane.showOptionDialog(null, "Tipo de Cliente:", "Tipo",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, tipos, tipos[0]);
        if (tipo == -1) return;

        String id;
        while (true) {
            id = JOptionPane.showInputDialog(tipo == 0 ? "CPF:" : "Passaporte:");
            if (id == null) return; 
            if (!id.matches("\\d+")) {
                JOptionPane.showMessageDialog(null, "Documento deve conter apenas números.", "Cadastro", JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }

        Cliente novo = tipo == 0 ? new ClienteNacional(nome, telefone, email, id)
                                 : new ClienteEstrangeiro(nome, telefone, email, id);
        clientes.add(novo);
        JOptionPane.showMessageDialog(null, "Cliente cadastrado com sucesso!");
    }


    private static void cadastrarPacote(List<PacoteViagem> pacotes) {
        String nome = JOptionPane.showInputDialog("Nome do pacote:");
        if (nome == null || nome.isBlank()) return;
        String destino = JOptionPane.showInputDialog("Destino:");
        if (destino == null || destino.isBlank()) return;
        int duracao = Integer.parseInt(JOptionPane.showInputDialog("Duração (dias):"));
        double preco = Double.parseDouble(JOptionPane.showInputDialog("Preço:"));
        String[] tipos = {"Aventura", "Luxo", "Cultural"};
        int tipo = JOptionPane.showOptionDialog(null, "Tipo de Pacote:", "Tipo",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, tipos, tipos[0]);
        if (tipo == -1) return;

        switch (tipo) {
            case 0 -> pacotes.add(new PacoteAventura(nome, destino, duracao, preco));
            case 1 -> pacotes.add(new PacoteLuxo(nome, destino, duracao, preco));
            case 2 -> pacotes.add(new PacoteCultural(nome, destino, duracao, preco));
        }

        JOptionPane.showMessageDialog(null, "Pacote cadastrado com sucesso!");
    }

    private static void cadastrarServico(List<ServicoAdicional> servicos) {
        String desc = JOptionPane.showInputDialog("Descrição:");
        if (desc == null || desc.isBlank()) return;
        double preco = Double.parseDouble(JOptionPane.showInputDialog("Preço:"));
        servicos.add(new ServicoAdicional(desc, preco));
        JOptionPane.showMessageDialog(null, "Serviço cadastrado com sucesso!");
    }

    private static void criarPedido(List<Cliente> clientes, List<PacoteViagem> pacotes, List<ServicoAdicional> servicos, List<Pedido> pedidos) {
        if (clientes.isEmpty() || pacotes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Cadastre pelo menos um cliente e um pacote antes.");
            return;
        }

        String[] nomesClientes = clientes.stream().map(Cliente::getResumo).toArray(String[]::new);
        int idxCliente = JOptionPane.showOptionDialog(null, "Escolha o cliente:", "Cliente",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, nomesClientes, nomesClientes[0]);
        if (idxCliente == -1) return;

        Pedido pedido = new Pedido(clientes.get(idxCliente));

        while (true) {
            String[] nomesPacotes = pacotes.stream().map(PacoteViagem::getResumo).toArray(String[]::new);
            int idxPacote = JOptionPane.showOptionDialog(null, "Escolha um pacote:", "Pacote",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, nomesPacotes, nomesPacotes[0]);
            if (idxPacote == -1) break;
            pedido.adicionarPacote(pacotes.get(idxPacote));

            int mais = JOptionPane.showConfirmDialog(null, "Deseja adicionar outro pacote?");
            if (mais != JOptionPane.YES_OPTION) break;
        }

        if (!servicos.isEmpty()) {
            int adicionarServ = JOptionPane.showConfirmDialog(null, "Deseja adicionar serviços adicionais?");
            if (adicionarServ == JOptionPane.YES_OPTION) {
                while (true) {
                    String[] nomesServicos = servicos.stream().map(ServicoAdicional::getResumo).toArray(String[]::new);
                    int idxServ = JOptionPane.showOptionDialog(null, "Escolha um serviço:", "Serviço",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, nomesServicos, nomesServicos[0]);
                    if (idxServ == -1) break;
                    pedido.adicionarServico(servicos.get(idxServ));

                    int mais = JOptionPane.showConfirmDialog(null, "Deseja adicionar outro serviço?");
                    if (mais != JOptionPane.YES_OPTION) break;
                }
            }
        }

        pedidos.add(pedido);
        JOptionPane.showMessageDialog(null, "Pedido criado com sucesso!\n\n" + pedido.getResumo());
    }

    private static void mostrarResumoGeral(List<Pedido> pedidos) {
        if (pedidos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum pedido foi realizado ainda.");
            return;
        }

        StringBuilder resumo = new StringBuilder("Resumo Geral dos Pedidos:\n\n");
        double totalGeral = 0;
        for (Pedido p : pedidos) {
            resumo.append(p.getResumo()).append("\n");
            totalGeral += p.getTotal();
        }
        resumo.append("TOTAL GERAL DE VENDAS: R$").append(String.format("%.2f", totalGeral));
        JOptionPane.showMessageDialog(null, resumo.toString());
    }

    private static void listar(List<String> itens, String titulo) {
        if (itens.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nada cadastrado.", titulo, JOptionPane.WARNING_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String item : itens) sb.append(item).append("\n");
        JOptionPane.showMessageDialog(null, sb.toString(), titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    private static void excluirItem(List<?> lista, String tipo) {
        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum " + tipo + " para excluir.", null, JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nomes = lista.stream().map(Object::toString).toArray(String[]::new);
        int idx = JOptionPane.showOptionDialog(null, "Excluir qual " + tipo + "?", "Excluir",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, nomes, nomes[0]);

        if (idx >= 0) {
            lista.remove(idx);
            JOptionPane.showMessageDialog(null, tipo + " removido.");
        }
    }
}
