package projetoSistemaAgencia;

import java.util.*;
import java.util.List;
import javax.swing.*;
import java.awt.*;

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
            String[] opcoes = {"Clientes", "Pacotes", "Serviços", "Pedidos", "Sair"};
            int opcao = JOptionPane.showOptionDialog(null, "Escolha uma opção:", "\uD83C\uDF0E Agência de Viagens",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

            if (opcao == 0) menuClientes(clientes);
            else if (opcao == 1) menuPacotes(pacotes);
            else if (opcao == 2) cadastrarServico(servicos);
            else if (opcao == 3) criarPedido(clientes, pacotes, servicos, pedidos);
            else break;
        }
    }

    private static void menuClientes(List<Cliente> clientes) {
        String[] opcoes = {"Cadastrar", "Visualizar", "Excluir", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(null, "Menu de Clientes:", "Clientes",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

        switch (escolha) {
            case 0 -> cadastrarCliente(clientes);
            case 1 -> listar(clientes.stream().map(Cliente::getResumo).toList(), "Clientes");
            case 2 -> excluirItem(clientes, "cliente");
        }
    }

    private static void menuPacotes(List<PacoteViagem> pacotes) {
        String[] opcoes = {"Cadastrar", "Visualizar", "Excluir", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(null, "Menu de Pacotes:", "Pacotes",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

        switch (escolha) {
            case 0 -> cadastrarPacote(pacotes);
            case 1 -> listar(pacotes.stream().map(PacoteViagem::getResumo).toList(), "Pacotes");
            case 2 -> excluirItem(pacotes, "pacote");
        }
    }

    private static void cadastrarCliente(List<Cliente> clientes) {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        JTextField nomeField = new JTextField();
        JTextField telField = new JTextField();
        JTextField emailField = new JTextField();
        panel.add(new JLabel("Nome:")); panel.add(nomeField);
        panel.add(new JLabel("Telefone:")); panel.add(telField);
        panel.add(new JLabel("Email:")); panel.add(emailField);

        String[] tipos = {"Nacional", "Estrangeiro"};
        JComboBox<String> tipoBox = new JComboBox<>(tipos);
        panel.add(new JLabel("Tipo:")); panel.add(tipoBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Cadastro de Cliente",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String nome = nomeField.getText();
            String tel = telField.getText();
            String email = emailField.getText();
            String tipo = (String) tipoBox.getSelectedItem();
            String id = JOptionPane.showInputDialog(tipo.equals("Nacional") ? "CPF:" : "Passaporte:");
            Cliente novo = tipo.equals("Nacional") ? new ClienteNacional(nome, tel, email, id)
                                                    : new ClienteEstrangeiro(nome, tel, email, id);
            clientes.add(novo);
        }
    }

    private static void cadastrarPacote(List<PacoteViagem> pacotes) {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        JTextField nomeField = new JTextField();
        JTextField destinoField = new JTextField();
        JTextField duracaoField = new JTextField();
        JTextField precoField = new JTextField();
        panel.add(new JLabel("Nome:")); panel.add(nomeField);
        panel.add(new JLabel("Destino:")); panel.add(destinoField);
        panel.add(new JLabel("Duração (dias):")); panel.add(duracaoField);
        panel.add(new JLabel("Preço:")); panel.add(precoField);

        String[] tipos = {"Aventura", "Luxo", "Cultural"};
        int result = JOptionPane.showConfirmDialog(null, panel, "Cadastro de Pacote",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String nome = nomeField.getText();
            String destino = destinoField.getText();
            int duracao = Integer.parseInt(duracaoField.getText());
            double preco = Double.parseDouble(precoField.getText());
            String tipo = (String) JOptionPane.showInputDialog(null, "Tipo de Pacote:", "Tipo",
                    JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);
            switch (tipo) {
                case "Aventura" -> pacotes.add(new PacoteAventura(nome, destino, duracao, preco));
                case "Luxo" -> pacotes.add(new PacoteLuxo(nome, destino, duracao, preco));
                case "Cultural" -> pacotes.add(new PacoteCultural(nome, destino, duracao, preco));
            }
        }
    }

    private static void cadastrarServico(List<ServicoAdicional> servicos) {
        JTextField descField = new JTextField();
        JTextField precoField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Descrição:")); panel.add(descField);
        panel.add(new JLabel("Preço:")); panel.add(precoField);
        int result = JOptionPane.showConfirmDialog(null, panel, "Cadastro de Serviço",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            servicos.add(new ServicoAdicional(descField.getText(), Double.parseDouble(precoField.getText())));
        }
    }

    private static void criarPedido(List<Cliente> clientes, List<PacoteViagem> pacotes, List<ServicoAdicional> servicos, List<Pedido> pedidos) {
        // código igual
    }

    private static void listar(List<String> itens, String titulo) {
        if (itens.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nada cadastrado.", null, JOptionPane.WARNING_MESSAGE);
        } else {
            JTextArea area = new JTextArea(String.join("\n", itens));
            area.setEditable(false);
            JScrollPane scroll = new JScrollPane(area);
            scroll.setPreferredSize(new Dimension(450, 250));
            JOptionPane.showMessageDialog(null, scroll, titulo, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static void excluirItem(List<?> lista, String tipo) {
        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum " + tipo + " para excluir.", null, JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nomes;
        if (lista.get(0) instanceof Cliente) {
            nomes = lista.stream().map(o -> ((Cliente) o).getResumo()).toArray(String[]::new);
        } else if (lista.get(0) instanceof PacoteViagem) {
            nomes = lista.stream().map(o -> ((PacoteViagem) o).getResumo()).toArray(String[]::new);
        } else {
            nomes = lista.stream().map(Object::toString).toArray(String[]::new);
        }

        int idx = JOptionPane.showOptionDialog(null, "Excluir qual " + tipo + "?", "Excluir",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, nomes, nomes[0]);

        if (idx >= 0) {
            lista.remove(idx);
            JOptionPane.showMessageDialog(null, tipo.substring(0, 1).toUpperCase() + tipo.substring(1) + " removido.");
        }
    }
}
