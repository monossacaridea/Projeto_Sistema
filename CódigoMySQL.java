package projetoSistemaAgencia;

import java.sql.*;
import java.util.*;
import javax.swing.*;

public class AgenciaViagens {
    // Configurações do banco de dados
    private static final String DB_URL = "jdbc:mysql://localhost:3306/agencia_viagens";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "b1gShO3dev1908!"; // Altere para sua senha

    // Classes internas
    abstract static class Cliente {
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

    static class ClienteNacional extends Cliente {
        private String cpf;

        public ClienteNacional(String nome, String telefone, String email, String cpf) {
            super(nome, telefone, email);
            this.cpf = cpf;
        }

        public String getIdentificacao() {
            return cpf;
        }
    }

    static class ClienteEstrangeiro extends Cliente {
        private String passaporte;

        public ClienteEstrangeiro(String nome, String telefone, String email, String passaporte) {
            super(nome, telefone, email);
            this.passaporte = passaporte;
        }

        public String getIdentificacao() {
            return passaporte;
        }
    }

    abstract static class PacoteViagem {
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

    static class PacoteAventura extends PacoteViagem {
        public PacoteAventura(String nome, String destino, int duracao, double preco) {
            super(nome, destino, duracao, preco, "Aventura");
        }
    }

    static class PacoteLuxo extends PacoteViagem {
        public PacoteLuxo(String nome, String destino, int duracao, double preco) {
            super(nome, destino, duracao, preco, "Luxo");
        }
    }

    static class PacoteCultural extends PacoteViagem {
        public PacoteCultural(String nome, String destino, int duracao, double preco) {
            super(nome, destino, duracao, preco, "Cultural");
        }
    }

    static class ServicoAdicional {
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

    static class Pedido {
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

    // Métodos de conexão com o banco de dados
    private static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC não encontrado", e);
        }
    }

    private static void initializeDatabase() {
        String[] sqlCommands = {
            "CREATE DATABASE IF NOT EXISTS agencia_viagens",
            "USE agencia_viagens",
            "CREATE TABLE IF NOT EXISTS clientes (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "tipo ENUM('NACIONAL', 'ESTRANGEIRO') NOT NULL, " +
                "nome VARCHAR(100) NOT NULL, " +
                "telefone VARCHAR(20) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "cpf VARCHAR(14), " +
                "passaporte VARCHAR(20))",
            "CREATE TABLE IF NOT EXISTS pacotes (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nome VARCHAR(100) NOT NULL, " +
                "destino VARCHAR(100) NOT NULL, " +
                "duracao INT NOT NULL, " +
                "preco DECIMAL(10,2) NOT NULL, " +
                "tipo ENUM('AVENTURA', 'LUXO', 'CULTURAL') NOT NULL)",
            "CREATE TABLE IF NOT EXISTS servicos (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "descricao VARCHAR(100) NOT NULL, " +
                "preco DECIMAL(10,2) NOT NULL)",
            "CREATE TABLE IF NOT EXISTS pedidos (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "cliente_id INT NOT NULL, " +
                "data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "total DECIMAL(10,2) NOT NULL, " +
                "FOREIGN KEY (cliente_id) REFERENCES clientes(id))",
            "CREATE TABLE IF NOT EXISTS pedido_pacotes (" +
                "pedido_id INT NOT NULL, " +
                "pacote_id INT NOT NULL, " +
                "PRIMARY KEY (pedido_id, pacote_id), " +
                "FOREIGN KEY (pedido_id) REFERENCES pedidos(id), " +
                "FOREIGN KEY (pacote_id) REFERENCES pacotes(id))",
            "CREATE TABLE IF NOT EXISTS pedido_servicos (" +
                "pedido_id INT NOT NULL, " +
                "servico_id INT NOT NULL, " +
                "PRIMARY KEY (pedido_id, servico_id), " +
                "FOREIGN KEY (pedido_id) REFERENCES pedidos(id), " +
                "FOREIGN KEY (servico_id) REFERENCES servicos(id))"
        };

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            for (String sql : sqlCommands) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao inicializar banco de dados: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Métodos para carregar dados do banco
    private static List<Cliente> carregarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String tipo = rs.getString("tipo");
                String nome = rs.getString("nome");
                String telefone = rs.getString("telefone");
                String email = rs.getString("email");
                
                if (tipo.equals("NACIONAL")) {
                    String cpf = rs.getString("cpf");
                    clientes.add(new ClienteNacional(nome, telefone, email, cpf));
                } else {
                    String passaporte = rs.getString("passaporte");
                    clientes.add(new ClienteEstrangeiro(nome, telefone, email, passaporte));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar clientes: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return clientes;
    }

    private static List<PacoteViagem> carregarPacotes() {
        List<PacoteViagem> pacotes = new ArrayList<>();
        String sql = "SELECT * FROM pacotes";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String nome = rs.getString("nome");
                String destino = rs.getString("destino");
                int duracao = rs.getInt("duracao");
                double preco = rs.getDouble("preco");
                String tipo = rs.getString("tipo");
                
                switch (tipo) {
                    case "AVENTURA":
                        pacotes.add(new PacoteAventura(nome, destino, duracao, preco));
                        break;
                    case "LUXO":
                        pacotes.add(new PacoteLuxo(nome, destino, duracao, preco));
                        break;
                    case "CULTURAL":
                        pacotes.add(new PacoteCultural(nome, destino, duracao, preco));
                        break;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar pacotes: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return pacotes;
    }

    private static List<ServicoAdicional> carregarServicos() {
        List<ServicoAdicional> servicos = new ArrayList<>();
        String sql = "SELECT * FROM servicos";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String descricao = rs.getString("descricao");
                double preco = rs.getDouble("preco");
                servicos.add(new ServicoAdicional(descricao, preco));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar serviços: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return servicos;
    }

    // Métodos da interface do usuário
    public static void main(String[] args) {
        initializeDatabase();
        
        List<Cliente> clientes = carregarClientes();
        List<PacoteViagem> pacotes = carregarPacotes();
        List<ServicoAdicional> servicos = carregarServicos();
        List<Pedido> pedidos = new ArrayList<>();

        while (true) {
            String[] opcoes = {"Clientes", "Pacotes", "Serviços", "Pedidos", "Resumo Geral", "Sair"};
            int opcao = JOptionPane.showOptionDialog(null, "Escolha uma opção:", "Agência de Viagens",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

            if (opcao == 0) menuClientes(clientes);
            else if (opcao == 1) menuPacotes(pacotes);
            else if (opcao == 2) menuServicos(servicos);
            else if (opcao == 3) menuPedidos(clientes, pacotes, servicos, pedidos);
            else if (opcao == 4) mostrarResumoGeral(pedidos);
            else break;
        }
    }

    private static void mostrarResumoGeral(List<Pedido> pedidos) {
		// TODO Auto-generated method stub
		
	}

	private static void menuClientes(List<Cliente> clientes) {
        String[] opcoes = {"Cadastrar", "Visualizar", "Excluir", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(null, "Menu de Clientes:", "Clientes",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha == 0) cadastrarCliente(clientes);
        else if (escolha == 1) listarClientes(clientes);
        else if (escolha == 2) excluirCliente(clientes);
    }

    private static void menuPacotes(List<PacoteViagem> pacotes) {
        String[] opcoes = {"Cadastrar", "Visualizar", "Excluir", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(null, "Menu de Pacotes:", "Pacotes",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha == 0) cadastrarPacote(pacotes);
        else if (escolha == 1) listarPacotes(pacotes);
        else if (escolha == 2) excluirPacote(pacotes);
    }

    private static void menuServicos(List<ServicoAdicional> servicos) {
        String[] opcoes = {"Cadastrar", "Visualizar", "Excluir", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(null, "Menu de Serviços:", "Serviços",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha == 0) cadastrarServico(servicos);
        else if (escolha == 1) listarServicos(servicos);
        else if (escolha == 2) excluirServico(servicos);
    }

    private static void menuPedidos(List<Cliente> clientes, List<PacoteViagem> pacotes, 
                                  List<ServicoAdicional> servicos, List<Pedido> pedidos) {
        String[] opcoes = {"Criar", "Visualizar", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(null, "Menu de Pedidos:", "Pedidos",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha == 0) criarPedido(clientes, pacotes, servicos, pedidos);
        else if (escolha == 1) listarPedidos(pedidos);
    }

    private static void cadastrarCliente(List<Cliente> clientes) {
        String nome = JOptionPane.showInputDialog("Nome:");
        if (nome == null || nome.isBlank()) return;

        String telefone = JOptionPane.showInputDialog("Telefone:");
        if (telefone == null || !telefone.matches("\\d+")) {
            JOptionPane.showMessageDialog(null, "Telefone deve conter apenas números", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String email = JOptionPane.showInputDialog("Email:");
        if (email == null || email.isBlank()) return;

        String[] tipos = {"Nacional", "Estrangeiro"};
        int tipo = JOptionPane.showOptionDialog(null, "Tipo de Cliente:", "Tipo",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, tipos, tipos[0]);
        if (tipo == -1) return;

        String id = JOptionPane.showInputDialog(tipo == 0 ? "CPF:" : "Passaporte:");
        if (id == null || !id.matches("\\d+")) {
            JOptionPane.showMessageDialog(null, "Documento deve conter apenas números", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Cliente novo = tipo == 0 ? new ClienteNacional(nome, telefone, email, id)
                                   : new ClienteEstrangeiro(nome, telefone, email, id);
            
            String sql = tipo == 0 ?
                "INSERT INTO clientes (tipo, nome, telefone, email, cpf) VALUES (?, ?, ?, ?, ?)" :
                "INSERT INTO clientes (tipo, nome, telefone, email, passaporte) VALUES (?, ?, ?, ?, ?)";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, tipo == 0 ? "NACIONAL" : "ESTRANGEIRO");
                stmt.setString(2, nome);
                stmt.setString(3, telefone);
                stmt.setString(4, email);
                stmt.setString(5, id);
                
                stmt.executeUpdate();
            }
            
            clientes.add(novo);
            JOptionPane.showMessageDialog(null, "Cliente cadastrado com sucesso!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar cliente: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void cadastrarPacote(List<PacoteViagem> pacotes) {
        String nome = JOptionPane.showInputDialog("Nome do pacote:");
        if (nome == null || nome.isBlank()) return;

        String destino = JOptionPane.showInputDialog("Destino:");
        if (destino == null || destino.isBlank()) return;

        String duracaoStr = JOptionPane.showInputDialog("Duração (dias):");
        if (duracaoStr == null || duracaoStr.isBlank()) return;
        int duracao;
        try {
            duracao = Integer.parseInt(duracaoStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Duração deve ser um número", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String precoStr = JOptionPane.showInputDialog("Preço:");
        if (precoStr == null || precoStr.isBlank()) return;
        double preco;
        try {
            preco = Double.parseDouble(precoStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Preço deve ser um número", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] tipos = {"Aventura", "Luxo", "Cultural"};
        int tipo = JOptionPane.showOptionDialog(null, "Tipo de Pacote:", "Tipo",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, tipos, tipos[0]);
        if (tipo == -1) return;

        try {
            PacoteViagem pacote;
            String tipoBanco;
            
            switch (tipo) {
                case 0:
                    pacote = new PacoteAventura(nome, destino, duracao, preco);
                    tipoBanco = "AVENTURA";
                    break;
                case 1:
                    pacote = new PacoteLuxo(nome, destino, duracao, preco);
                    tipoBanco = "LUXO";
                    break;
                case 2:
                    pacote = new PacoteCultural(nome, destino, duracao, preco);
                    tipoBanco = "CULTURAL";
                    break;
                default:
                    return;
            }
            
            String sql = "INSERT INTO pacotes (nome, destino, duracao, preco, tipo) VALUES (?, ?, ?, ?, ?)";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, nome);
                stmt.setString(2, destino);
                stmt.setInt(3, duracao);
                stmt.setDouble(4, preco);
                stmt.setString(5, tipoBanco);
                
                stmt.executeUpdate();
            }
            
            pacotes.add(pacote);
            JOptionPane.showMessageDialog(null, "Pacote cadastrado com sucesso!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar pacote: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void cadastrarServico(List<ServicoAdicional> servicos) {
        String descricao = JOptionPane.showInputDialog("Descrição:");
        if (descricao == null || descricao.isBlank()) return;

        String precoStr = JOptionPane.showInputDialog("Preço:");
        if (precoStr == null || precoStr.isBlank()) return;
        double preco;
        try {
            preco = Double.parseDouble(precoStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Preço deve ser um número", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ServicoAdicional servico = new ServicoAdicional(descricao, preco);
            
            String sql = "INSERT INTO servicos (descricao, preco) VALUES (?, ?)";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, descricao);
                stmt.setDouble(2, preco);
                
                stmt.executeUpdate();
            }
            
            servicos.add(servico);
            JOptionPane.showMessageDialog(null, "Serviço cadastrado com sucesso!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar serviço: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void criarPedido(List<Cliente> clientes, List<PacoteViagem> pacotes, 
                                  List<ServicoAdicional> servicos, List<Pedido> pedidos) {
        if (clientes.isEmpty() || pacotes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Cadastre pelo menos um cliente e um pacote antes.");
            return;
        }

        String[] nomesClientes = clientes.stream().map(Cliente::getResumo).toArray(String[]::new);
        int idxCliente = JOptionPane.showOptionDialog(null, "Escolha o cliente:", "Cliente",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, nomesClientes, nomesClientes[0]);
        if (idxCliente == -1) return;

        Pedido pedido = new Pedido(clientes.get(idxCliente));

        // Adicionar pacotes
        while (true) {
            String[] nomesPacotes = pacotes.stream().map(PacoteViagem::getResumo).toArray(String[]::new);
            int idxPacote = JOptionPane.showOptionDialog(null, "Escolha um pacote:", "Pacote",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, nomesPacotes, nomesPacotes[0]);
            if (idxPacote == -1) break;
            pedido.adicionarPacote(pacotes.get(idxPacote));

            int mais = JOptionPane.showConfirmDialog(null, "Deseja adicionar outro pacote?");
            if (mais != JOptionPane.YES_OPTION) break;
        }

        // Adicionar serviços (se houver)
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

        try {
            salvarPedido(pedido);
            pedidos.add(pedido);
            JOptionPane.showMessageDialog(null, "Pedido criado com sucesso!\n\n" + pedido.getResumo());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar pedido: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void salvarPedido(Pedido pedido) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            
            // 1. Obter ID do cliente
            int clienteId = obterIdCliente(conn, pedido.cliente);
            
            // 2. Inserir pedido
            String sqlPedido = "INSERT INTO pedidos (cliente_id, total) VALUES (?, ?)";
            int pedidoId;
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, clienteId);
                stmt.setDouble(2, pedido.getTotal());
                stmt.executeUpdate();
                
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        pedidoId = rs.getInt(1);
                    } else {
                        throw new SQLException("Falha ao obter ID do pedido");
                    }
                }
            }
            
            // 3. Inserir pacotes do pedido
            for (PacoteViagem pacote : pedido.pacotes) {
                int pacoteId = obterIdPacote(conn, pacote);
                
                String sqlPacote = "INSERT INTO pedido_pacotes (pedido_id, pacote_id) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sqlPacote)) {
                    stmt.setInt(1, pedidoId);
                    stmt.setInt(2, pacoteId);
                    stmt.executeUpdate();
                }
            }
            
            // 4. Inserir serviços do pedido
            for (ServicoAdicional servico : pedido.servicos) {
                int servicoId = obterIdServico(conn, servico);
                
                String sqlServico = "INSERT INTO pedido_servicos (pedido_id, servico_id) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sqlServico)) {
                    stmt.setInt(1, pedidoId);
                    stmt.setInt(2, servicoId);
                    stmt.executeUpdate();
                }
            }
            
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    private static int obterIdCliente(Connection conn, Cliente cliente) throws SQLException {
        String sql = cliente instanceof ClienteNacional ?
            "SELECT id FROM clientes WHERE cpf = ?" :
            "SELECT id FROM clientes WHERE passaporte = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getIdentificacao());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("Cliente não encontrado");
                }
            }
        }
    }

    private static int obterIdPacote(Connection conn, PacoteViagem pacote) throws SQLException {
        String sql = "SELECT id FROM pacotes WHERE nome = ? AND destino = ? AND tipo = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pacote.nome);
            stmt.setString(2, pacote.destino);
            stmt.setString(3, pacote.tipo.toUpperCase());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("Pacote não encontrado");
                }
            }
        }
    }

    private static int obterIdServico(Connection conn, ServicoAdicional servico) throws SQLException {
        String sql = "SELECT id FROM servicos WHERE descricao = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, servico.descricao);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("Serviço não encontrado");
                }
            }
        }
    }

    private static void listarClientes(List<Cliente> clientes) {
        if (clientes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum cliente cadastrado.", "Clientes", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder("Lista de Clientes:\n\n");
        for (Cliente c : clientes) {
            sb.append(c.getResumo()).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Clientes", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void listarPacotes(List<PacoteViagem> pacotes) {
        if (pacotes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum pacote cadastrado.", "Pacotes", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder("Lista de Pacotes:\n\n");
        for (PacoteViagem p : pacotes) {
            sb.append(p.getResumo()).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Pacotes", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void listarServicos(List<ServicoAdicional> servicos) {
        if (servicos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum serviço cadastrado.", "Serviços", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder("Lista de Serviços:\n\n");
        for (ServicoAdicional s : servicos) {
            sb.append(s.getResumo()).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Serviços", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void listarPedidos(List<Pedido> pedidos) {
        if (pedidos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum pedido cadastrado.", "Pedidos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder("Lista de Pedidos:\n\n");
        for (Pedido p : pedidos) {
            sb.append(p.getResumo()).append("\n\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Pedidos", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void excluirCliente(List<Cliente> clientes) {
        if (clientes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum cliente para excluir.", "Clientes", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nomes = clientes.stream().map(Cliente::getResumo).toArray(String[]::new);
        int idx = JOptionPane.showOptionDialog(null, "Excluir qual cliente?", "Excluir Cliente",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, nomes, nomes[0]);

        if (idx >= 0) {
            try {
                Cliente cliente = clientes.get(idx);
                String sql = cliente instanceof ClienteNacional ?
                    "DELETE FROM clientes WHERE cpf = ?" :
                    "DELETE FROM clientes WHERE passaporte = ?";
                
                try (Connection conn = getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    
                    stmt.setString(1, cliente.getIdentificacao());
                    stmt.executeUpdate();
                }
                
                clientes.remove(idx);
                JOptionPane.showMessageDialog(null, "Cliente removido com sucesso.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao excluir cliente: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void excluirPacote(List<PacoteViagem> pacotes) {
        if (pacotes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum pacote para excluir.", "Pacotes", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nomes = pacotes.stream().map(PacoteViagem::getResumo).toArray(String[]::new);
        int idx = JOptionPane.showOptionDialog(null, "Excluir qual pacote?", "Excluir Pacote",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, nomes, nomes[0]);

        if (idx >= 0) {
            try {
                PacoteViagem pacote = pacotes.get(idx);
                String sql = "DELETE FROM pacotes WHERE nome = ? AND destino = ? AND tipo = ?";
                
                try (Connection conn = getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    
                    stmt.setString(1, pacote.nome);
                    stmt.setString(2, pacote.destino);
                    stmt.setString(3, pacote.tipo.toUpperCase());
                    stmt.executeUpdate();
                }
                
                pacotes.remove(idx);
                JOptionPane.showMessageDialog(null, "Pacote removido com sucesso.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao excluir pacote: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void excluirServico(List<ServicoAdicional> servicos) {
        if (servicos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum serviço para excluir.", "Serviços", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nomes = servicos.stream().map(ServicoAdicional::getResumo).toArray(String[]::new);
        int idx = JOptionPane.showOptionDialog(null, "Excluir qual serviço?", "Excluir Serviço",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, nomes, nomes[0]);

        if (idx >= 0) {
            try {
                ServicoAdicional servico = servicos.get(idx);
                String sql = "DELETE FROM servicos WHERE descricao = ?";
                
                try (Connection conn = getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    
                    stmt.setString(1, servico.descricao);
                    stmt.executeUpdate();
                }
                
                servicos.remove(idx);
                JOptionPane.showMessageDialog(null, "Serviço removido com sucesso.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao excluir serviço: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    } 
}
