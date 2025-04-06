package Agencia;
import java.util.*;
import javax.swing.*;

// Classe abstrata Cliente
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
}

// Cliente Nacional
class ClienteNacional extends Cliente {
    private String cpf;

    public ClienteNacional(String nome, String telefone, String email, String cpf) {
        super(nome, telefone, email);
        this.cpf = cpf;
    }

    @Override
    public String getIdentificacao() {
        return cpf;
    }
}

// Cliente Estrangeiro
class ClienteEstrangeiro extends Cliente {
    private String passaporte;

    public ClienteEstrangeiro(String nome, String telefone, String email, String passaporte) {
        super(nome, telefone, email);
        this.passaporte = passaporte;
    }

    @Override
    public String getIdentificacao() {
        return passaporte;
    }
}

// Classe abstrata PacoteViagem
abstract class PacoteViagem {
    protected String nome;
    protected String destino;
    protected int duracao;
    protected double preco;
    protected String tipo;

    public PacoteViagem(String nome, String destino, int duracao, double preco, String tipo) {
        if (destino.isEmpty() || preco <= 0) {
            throw new IllegalArgumentException("Destino e preço são obrigatórios.");
        }
        this.nome = nome;
        this.destino = destino;
        this.duracao = duracao;
        this.preco = preco;
        this.tipo = tipo;
    }
}

// Pacote de Aventura
class PacoteAventura extends PacoteViagem {
    public PacoteAventura(String nome, String destino, int duracao, double preco) {
        super(nome, destino, duracao, preco, "Aventura");
    }
}

// Classe de Serviços Adicionais
class ServicoAdicional {
    String descricao;
    double preco;

    public ServicoAdicional(String descricao, double preco) {
        this.descricao = descricao;
        this.preco = preco;
    }
}

// Classe Pedido para relacionar Cliente, Pacotes e Serviços
class Pedido {
    private Cliente cliente;
    private List<PacoteViagem> pacotes;
    private List<ServicoAdicional> servicos;

    public Pedido(Cliente cliente) {
        this.cliente = cliente;
        this.pacotes = new ArrayList<>();
        this.servicos = new ArrayList<>();
    }

    public void adicionarPacote(PacoteViagem pacote) {
        pacotes.add(pacote);
    }

    public void adicionarServico(ServicoAdicional servico) {
        servicos.add(servico);
    }

    public List<PacoteViagem> getPacotes() {
        return pacotes;
    }

    public List<ServicoAdicional> getServicos() {
        return servicos;
    }
}

// Classe principal com função main
public class AgenciaViagens {
    public static void main(String[] args) {
        List<Cliente> clientes = new ArrayList<>();
        List<PacoteViagem> pacotes = new ArrayList<>();
        List<ServicoAdicional> servicos = new ArrayList<>();
        List<Pedido> pedidos = new ArrayList<>();

        while (true) {
            String[] opcoes = {"Cadastrar Cliente", "Cadastrar Pacote", "Cadastrar Serviço", "Criar Pedido", "Sair"};
            int opcao = JOptionPane.showOptionDialog(null, "Escolha uma opção:", "Agência de Viagens",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opcoes, opcoes[0]);

            if (opcao == 0) { // Cadastrar Cliente
                String nome = JOptionPane.showInputDialog("Nome:");
                String tel = JOptionPane.showInputDialog("Telefone:");
                String email = JOptionPane.showInputDialog("Email:");
                String tipo = JOptionPane.showInputDialog("Tipo (nacional/estrangeiro):");

                if (tipo.equalsIgnoreCase("nacional")) {
                    String cpf = JOptionPane.showInputDialog("CPF:");
                    clientes.add(new ClienteNacional(nome, tel, email, cpf));
                } else {
                    String pass = JOptionPane.showInputDialog("Passaporte:");
                    clientes.add(new ClienteEstrangeiro(nome, tel, email, pass));
                }
            } else if (opcao == 1) { // Cadastrar Pacote
                String nomePacote = JOptionPane.showInputDialog("Nome do Pacote:");
                String destino = JOptionPane.showInputDialog("Destino:");
                int duracao = Integer.parseInt(JOptionPane.showInputDialog("Duração (dias):"));
                double preco = Double.parseDouble(JOptionPane.showInputDialog("Preço:"));

                pacotes.add(new PacoteAventura(nomePacote, destino, duracao, preco));
            } else if (opcao == 2) { // Cadastrar Serviço
                String descricao = JOptionPane.showInputDialog("Descrição do Serviço:");
                double preco = Double.parseDouble(JOptionPane.showInputDialog("Preço:"));

                servicos.add(new ServicoAdicional(descricao, preco));
            } else if (opcao == 3) { // Criar Pedido
                if (clientes.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Nenhum cliente cadastrado.");
                    continue;
                }
                String[] nomesClientes = clientes.stream().map(c -> c.nome).toArray(String[]::new);
                int idxCliente = JOptionPane.showOptionDialog(null, "Escolha o cliente:", "Clientes",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, nomesClientes, nomesClientes[0]);

                Pedido pedido = new Pedido(clientes.get(idxCliente));

                while (true) {
                    String[] nomesPacotes = pacotes.stream().map(p -> p.nome).toArray(String[]::new);
                    if (nomesPacotes.length == 0) break;
                    int idxPacote = JOptionPane.showOptionDialog(null, "Adicionar Pacote (Cancelar para encerrar):", "Pacotes",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, nomesPacotes, nomesPacotes[0]);
                    if (idxPacote == -1) break;
                    pedido.adicionarPacote(pacotes.get(idxPacote));
                }

                while (true) {
                    String[] nomesServicos = servicos.stream().map(s -> s.descricao).toArray(String[]::new);
                    if (nomesServicos.length == 0) break;
                    int idxServico = JOptionPane.showOptionDialog(null, "Adicionar Serviço (Cancelar para encerrar):", "Serviços",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, nomesServicos, nomesServicos[0]);
                    if (idxServico == -1) break;
                    pedido.adicionarServico(servicos.get(idxServico));
                }

                pedidos.add(pedido);
                JOptionPane.showMessageDialog(null, "Pedido criado com sucesso!");

            } else if (opcao == 4 || opcao == JOptionPane.CLOSED_OPTION) {
                break;
            }
        }
    }
}
