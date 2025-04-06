// Pacote e imports
package Agencia;

import javax.swing.*;
import java.util.List;
import java.awt.Dimension;
import java.util.ArrayList;


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

// Pacote abstrato
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

    public String getResumo() {
        return nome + " - " + tipo + " (" + destino + ", " + duracao + " dias, R$" + preco + ")";
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

    public String getResumo() {
        return descricao + " (R$" + preco + ")";
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
        return sb.toString();
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<PacoteViagem> getPacotes() {
        return pacotes;
    }
}

public class AgenciaViagens {
    public static void main(String[] args) {
        List<Cliente> clientes = new ArrayList<>();
        List<PacoteViagem> pacotes = new ArrayList<>();
        List<ServicoAdicional> servicos = new ArrayList<>();
        List<Pedido> pedidos = new ArrayList<>();

        while (true) {
            String[] opcoes = {
                "Cadastrar Cliente", "Cadastrar Pacote", "Cadastrar Serviço",
                "Criar Pedido", "Visualizar Clientes", "Visualizar Pacotes",
                "Visualizar Pedidos", "Excluir Cliente", "Excluir Pacote", "Sair"
            };

            int opcao = JOptionPane.showOptionDialog(null, "Escolha uma opção:", "🧭 Agência de Viagens",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

            if (opcao == 0) {
                String nome = JOptionPane.showInputDialog("Nome:");
                String tel = JOptionPane.showInputDialog("Telefone:");
                String email = JOptionPane.showInputDialog("Email:");
                String[] tiposCliente = {"Nacional", "Estrangeiro"};
                int tipoCliente = JOptionPane.showOptionDialog(null, "Tipo de cliente:", "Cadastro de Cliente",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, tiposCliente, tiposCliente[0]);
                if (tipoCliente == 0) {
                    String cpf = JOptionPane.showInputDialog("CPF:");
                    clientes.add(new ClienteNacional(nome, tel, email, cpf));
                } else {
                    String pass = JOptionPane.showInputDialog("Passaporte:");
                    clientes.add(new ClienteEstrangeiro(nome, tel, email, pass));
                }

            } else if (opcao == 1) {
                String nomePacote = JOptionPane.showInputDialog("Nome do Pacote:");
                String destino = JOptionPane.showInputDialog("Destino:");
                int duracao = Integer.parseInt(JOptionPane.showInputDialog("Duração (dias):"));
                double preco = Double.parseDouble(JOptionPane.showInputDialog("Preço:"));
                String[] tiposPacote = {"Aventura", "Luxo", "Cultural"};
                int tipo = JOptionPane.showOptionDialog(null, "Tipo de Pacote:", "Cadastro de Pacote",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, tiposPacote, tiposPacote[0]);
                switch (tipo) {
                    case 0 -> pacotes.add(new PacoteAventura(nomePacote, destino, duracao, preco));
                    case 1 -> pacotes.add(new PacoteLuxo(nomePacote, destino, duracao, preco));
                    case 2 -> pacotes.add(new PacoteCultural(nomePacote, destino, duracao, preco));
                }

            } else if (opcao == 2) {
                String descricao = JOptionPane.showInputDialog("Descrição do Serviço:");
                double preco = Double.parseDouble(JOptionPane.showInputDialog("Preço:"));
                servicos.add(new ServicoAdicional(descricao, preco));

            } else if (opcao == 3) {
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

                JOptionPane.showMessageDialog(null, new JScrollPane(new JTextArea(pedido.getResumo(), 10, 40)), "🧾 Pedido Criado", JOptionPane.INFORMATION_MESSAGE);
                pedidos.add(pedido);

            } else if (opcao == 4) {
                if (clientes.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Nenhum cliente cadastrado.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (Cliente c : clientes) sb.append("- ").append(c.getResumo()).append("\n");
                    mostrarTexto("📋 Lista de Clientes", sb.toString());
                }

            } else if (opcao == 5) {
                if (pacotes.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Nenhum pacote cadastrado.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (PacoteViagem p : pacotes) sb.append("- ").append(p.getResumo()).append("\n");
                    mostrarTexto("🧳 Lista de Pacotes", sb.toString());
                }

            } else if (opcao == 6) {
                if (pedidos.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Nenhum pedido criado.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    int count = 1;
                    for (Pedido p : pedidos) {
                        sb.append("Pedido ").append(count++).append(":\n").append(p.getResumo()).append("\n");
                    }
                    mostrarTexto("📦 Lista de Pedidos", sb.toString());
                }

            } else if (opcao == 7) {
                if (clientes.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Nenhum cliente para excluir.");
                    continue;
                }
                String[] nomesClientes = clientes.stream().map(c -> c.nome).toArray(String[]::new);
                int idx = JOptionPane.showOptionDialog(null, "Excluir qual cliente?", "Excluir Cliente",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, nomesClientes, nomesClientes[0]);
                clientes.remove(idx);
                JOptionPane.showMessageDialog(null, "Cliente removido.");

            } else if (opcao == 8) {
                if (pacotes.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Nenhum pacote para excluir.");
                    continue;
                }
                String[] nomesPacotes = pacotes.stream().map(p -> p.nome).toArray(String[]::new);
                int idx = JOptionPane.showOptionDialog(null, "Excluir qual pacote?", "Excluir Pacote",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, nomesPacotes, nomesPacotes[0]);
                boolean estaEmPedido = pedidos.stream().anyMatch(p -> p.getPacotes().contains(pacotes.get(idx)));
                if (estaEmPedido) {
                    JOptionPane.showMessageDialog(null, "Este pacote está associado a um pedido e não pode ser removido.");
                } else {
                    pacotes.remove(idx);
                    JOptionPane.showMessageDialog(null, "Pacote removido.");
                }

            } else if (opcao == 9 || opcao == JOptionPane.CLOSED_OPTION) {
                break;
            }
        }
    }

    private static void mostrarTexto(String titulo, String texto) {
        JTextArea textArea = new JTextArea(texto);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 250));
        JOptionPane.showMessageDialog(null, scrollPane, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
}

//arrumar a interface principal
//adicionar um botao de encerrar em criar pacoote  
