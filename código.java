package projetoSistemaAgencia;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

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
    public static void cadastrarCliente(List<Cliente> clientes) {
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

    public static void cadastrarPacote(List<PacoteViagem> pacotes) {
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

    public static void cadastrarServico(List<ServicoAdicional> servicos) {
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

    public static void criarPedido(List<Cliente> clientes, List<PacoteViagem> pacotes, List<ServicoAdicional> servicos, List<Pedido> pedidos) {
        if (clientes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum cliente cadastrado.", null, JOptionPane.WARNING_MESSAGE); return;
        }

        JList<String> listaClientes = new JList<>(clientes.stream().map(c -> c.nome).toArray(String[]::new));
        listaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollClientes = new JScrollPane(listaClientes);
        scrollClientes.setPreferredSize(new Dimension(250, 100));
        int cliRes = JOptionPane.showConfirmDialog(null, scrollClientes, "Escolha o Cliente",
                JOptionPane.OK_CANCEL_OPTION);
        if (cliRes != JOptionPane.OK_OPTION) return;
        Pedido pedido = new Pedido(clientes.get(listaClientes.getSelectedIndex()));

        if (!pacotes.isEmpty()) {
            JList<String> listaPacotes = new JList<>(pacotes.stream().map(p -> p.nome).toArray(String[]::new));
            listaPacotes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            JScrollPane scrollPacotes = new JScrollPane(listaPacotes);
            scrollPacotes.setPreferredSize(new Dimension(250, 150));
            int pacRes = JOptionPane.showConfirmDialog(null, scrollPacotes, "Escolha os Pacotes",
                    JOptionPane.OK_CANCEL_OPTION);
            if (pacRes == JOptionPane.OK_OPTION) {
                for (int i : listaPacotes.getSelectedIndices()) pedido.adicionarPacote(pacotes.get(i));
            }
        }

        if (!servicos.isEmpty()) {
            JList<String> listaServicos = new JList<>(servicos.stream().map(s -> s.descricao).toArray(String[]::new));
            listaServicos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            JScrollPane scrollServ = new JScrollPane(listaServicos);
            scrollServ.setPreferredSize(new Dimension(250, 150));
            int servRes = JOptionPane.showConfirmDialog(null, scrollServ, "Escolha os Serviços",
                    JOptionPane.OK_CANCEL_OPTION);
            if (servRes == JOptionPane.OK_OPTION) {
                for (int i : listaServicos.getSelectedIndices()) pedido.adicionarServico(servicos.get(i));
            }
        }

        JOptionPane.showMessageDialog(null, new JScrollPane(new JTextArea(pedido.getResumo(), 10, 40)),
                "\uD83D\uDCDC Pedido Criado", JOptionPane.INFORMATION_MESSAGE);
        pedidos.add(pedido);
    }

    public static void listar(List<String> itens, String titulo) {
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

    public static void excluirItem(List<?> lista, String tipo) {
        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum " + tipo + " para excluir.", null, JOptionPane.WARNING_MESSAGE);
            return;
        }
        String[] nomes = lista.stream().map(o -> o.toString()).toArray(String[]::new);
        int idx = JOptionPane.showOptionDialog(null, "Excluir qual " + tipo + "?", "Excluir",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, nomes, nomes[0]);
        if (idx >= 0) {
            lista.remove(idx);
            JOptionPane.showMessageDialog(null, tipo.substring(0, 1).toUpperCase() + tipo.substring(1) + " removido.");
        }
    }

    public static void excluirPacote(List<PacoteViagem> pacotes, List<Pedido> pedidos) {
        if (pacotes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum pacote para excluir.", null, JOptionPane.WARNING_MESSAGE); return;
        }
        String[] nomes = pacotes.stream().map(p -> p.nome).toArray(String[]::new);
        int idx = JOptionPane.showOptionDialog(null, "Excluir qual pacote?", "Excluir Pacote",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, nomes, nomes[0]);
        if (idx >= 0) {
            boolean emPedido = pedidos.stream().anyMatch(p -> p.getPacotes().contains(pacotes.get(idx)));
            if (emPedido) {
                JOptionPane.showMessageDialog(null, "Este pacote está em um pedido e não pode ser excluído.", "Atenção", JOptionPane.ERROR_MESSAGE);
            } else {
                pacotes.remove(idx);
                JOptionPane.showMessageDialog(null, "Pacote removido.");
            }
        }
    }

    public static void main(String[] args) {
        InterfaceInicial.main(args);
    }
}

class InterfaceInicial {
    public static void main(String[] args) {
        aplicarTemaVisual();

        List<Cliente> clientes = new ArrayList<>();
        List<PacoteViagem> pacotes = new ArrayList<>();
        List<ServicoAdicional> servicos = new ArrayList<>();
        List<Pedido> pedidos = new ArrayList<>();

        while (true) {
            String[] opcoesPrincipais = {"Clientes", "Pacotes", "Pedidos", "Serviços", "Sair"};
            int opcao = mostrarMenu("\uD83C\uDF0E Agência de Viagens", opcoesPrincipais);

            switch (opcao) {
                case 0 -> menuClientes(clientes);
                case 1 -> menuPacotes(pacotes);
                case 2 -> AgenciaViagens.criarPedido(clientes, pacotes, servicos, pedidos);
                case 3 -> AgenciaViagens.cadastrarServico(servicos);
                default -> System.exit(0);
            }
        }
    }

    private static void menuClientes(List<Cliente> clientes) {
        String[] opcoes = {"Cadastrar", "Visualizar", "Excluir", "Voltar"};
        while (true) {
            int escolha = mostrarMenu("\uD83D\uDC64 Menu de Clientes", opcoes);
            switch (escolha) {
                case 0 -> AgenciaViagens.cadastrarCliente(clientes);
                case 1 -> AgenciaViagens.listar(clientes.stream().map(Cliente::getResumo).toList(), "Lista de Clientes");
                case 2 -> AgenciaViagens.excluirItem(clientes, "cliente");
                default -> { return; }
            }
        }
    }

    private static void menuPacotes(List<PacoteViagem> pacotes) {
        String[] opcoes = {"Cadastrar", "Visualizar", "Excluir", "Voltar"};
        while (true) {
            int escolha = mostrarMenu("\uD83D\uDEEB Menu de Pacotes", opcoes);
            switch (escolha) {
                case 0 -> AgenciaViagens.cadastrarPacote(pacotes);
                case 1 -> AgenciaViagens.listar(pacotes.stream().map(PacoteViagem::getResumo).toList(), "Lista de Pacotes");
                case 2 -> AgenciaViagens.excluirPacote(pacotes, new ArrayList<>()); // ou passe pedidos se necessário
                default -> { return; }
            }
        }
    }

    private static int mostrarMenu(String titulo, String[] opcoes) {
        return JOptionPane.showOptionDialog(null, "Escolha uma opção:", titulo,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);
    }

    private static void aplicarTemaVisual() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.out.println("Tema Nimbus não disponível.");
        }
    }
}
