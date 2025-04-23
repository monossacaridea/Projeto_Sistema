//nao sei q codigo é essekkkkkkkkkk

package Agencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class Conexao {
    private static Conexao instance;
    private Connection connection;
    
    // Configurações do banco de dados - altere conforme necessário
    private final String URL = "jdbc:mysql://localhost:3306/agencia_viagens";
    private final String USER = "root";
    private final String PASSWORD = "";
    
    private Conexao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            criarTabelas(); // Cria as tabelas se não existirem
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco de dados: " + e.getMessage(), 
                "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static Conexao getInstance() {
        if (instance == null) {
            synchronized (Conexao.class) {
                if (instance == null) {
                    instance = new Conexao();
                }
            }
        }
        return instance;
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    private void criarTabelas() {
        String sqlClientes = "CREATE TABLE IF NOT EXISTS clientes (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "nome VARCHAR(100) NOT NULL," +
                "telefone VARCHAR(20) NOT NULL," +
                "email VARCHAR(100) NOT NULL," +
                "tipo VARCHAR(20) NOT NULL," +
                "documento VARCHAR(30) NOT NULL)";
        
        String sqlPacotes = "CREATE TABLE IF NOT EXISTS pacotes (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "nome VARCHAR(100) NOT NULL," +
                "destino VARCHAR(100) NOT NULL," +
                "duracao INT NOT NULL," +
                "preco DOUBLE NOT NULL," +
                "tipo VARCHAR(20) NOT NULL)";
        
        String sqlServicos = "CREATE TABLE IF NOT EXISTS servicos (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "descricao VARCHAR(100) NOT NULL," +
                "preco DOUBLE NOT NULL)";
        
        String sqlPedidos = "CREATE TABLE IF NOT EXISTS pedidos (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "id_cliente INT NOT NULL," +
                "FOREIGN KEY (id_cliente) REFERENCES clientes(id))";
        
        String sqlPedidosPacotes = "CREATE TABLE IF NOT EXISTS pedidos_pacotes (" +
                "id_pedido INT NOT NULL," +
                "id_pacote INT NOT NULL," +
                "PRIMARY KEY (id_pedido, id_pacote)," +
                "FOREIGN KEY (id_pedido) REFERENCES pedidos(id)," +
                "FOREIGN KEY (id_pacote) REFERENCES pacotes(id))";
        
        String sqlPedidosServicos = "CREATE TABLE IF NOT EXISTS pedidos_servicos (" +
                "id_pedido INT NOT NULL," +
                "id_servico INT NOT NULL," +
                "PRIMARY KEY (id_pedido, id_servico)," +
                "FOREIGN KEY (id_pedido) REFERENCES pedidos(id)," +
                "FOREIGN KEY (id_servico) REFERENCES servicos(id))";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlClientes);
            stmt.execute(sqlPacotes);
            stmt.execute(sqlServicos);
            stmt.execute(sqlPedidos);
            stmt.execute(sqlPedidosPacotes);
            stmt.execute(sqlPedidosServicos);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Métodos para Clientes
    public int inserirCliente(Cliente cliente) {
        String sql = "INSERT INTO clientes (nome, telefone, email, tipo, documento) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, cliente.getNome());
            pstmt.setString(2, cliente.getTelefone());
            pstmt.setString(3, cliente.getEmail());
            
            if (cliente instanceof ClienteNacional) {
                pstmt.setString(4, "Nacional");
                pstmt.setString(5, ((ClienteNacional) cliente).getIdentificacao());
            } else {
                pstmt.setString(4, "Estrangeiro");
                pstmt.setString(5, ((ClienteEstrangeiro) cliente).getIdentificacao());
            }
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public List<Cliente> listarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String telefone = rs.getString("telefone");
                String email = rs.getString("email");
                String tipo = rs.getString("tipo");
                String documento = rs.getString("documento");
                
                if (tipo.equals("Nacional")) {
                    clientes.add(new ClienteNacional(nome, telefone, email, documento));
                } else {
                    clientes.add(new ClienteEstrangeiro(nome, telefone, email, documento));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }
    
    public boolean atualizarCliente(Cliente cliente) {
        // Implementação similar à inserção, mas com UPDATE
        return false;
    }
    
    public boolean removerCliente(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Métodos para Pacotes
    public int inserirPacote(PacoteViagem pacote) {
        String sql = "INSERT INTO pacotes (nome, destino, duracao, preco, tipo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, pacote.getNome());
            pstmt.setString(2, pacote.getDestino());
            pstmt.setInt(3, pacote.getDuracao());
            pstmt.setDouble(4, pacote.getPreco());
            pstmt.setString(5, pacote.getTipo());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public List<PacoteViagem> listarPacotes() {
        List<PacoteViagem> pacotes = new ArrayList<>();
        String sql = "SELECT * FROM pacotes";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String nome = rs.getString("nome");
                String destino = rs.getString("destino");
                int duracao = rs.getInt("duracao");
                double preco = rs.getDouble("preco");
                String tipo = rs.getString("tipo");
                
                switch (tipo) {
                    case "Aventura":
                        pacotes.add(new PacoteAventura(nome, destino, duracao, preco));
                        break;
                    case "Luxo":
                        pacotes.add(new PacoteLuxo(nome, destino, duracao, preco));
                        break;
                    case "Cultural":
                        pacotes.add(new PacoteCultural(nome, destino, duracao, preco));
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pacotes;
    }
    
    // Métodos para Serviços
    public int inserirServico(ServicoAdicional servico) {
        String sql = "INSERT INTO servicos (descricao, preco) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, servico.getDescricao());
            pstmt.setDouble(2, servico.getPreco());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public List<ServicoAdicional> listarServicos() {
        List<ServicoAdicional> servicos = new ArrayList<>();
        String sql = "SELECT * FROM servicos";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String descricao = rs.getString("descricao");
                double preco = rs.getDouble("preco");
                servicos.add(new ServicoAdicional(descricao, preco));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servicos;
    }
    
    // Métodos para Pedidos
    public int inserirPedido(Pedido pedido) {
        String sql = "INSERT INTO pedidos (id_cliente) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Primeiro precisamos obter o ID do cliente
            int idCliente = obterIdCliente(pedido.getCliente());
            if (idCliente == -1) return -1;
            
            pstmt.setInt(1, idCliente);
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int idPedido = rs.getInt(1);
                    
                    // Inserir pacotes do pedido
                    for (PacoteViagem pacote : pedido.getPacotes()) {
                        int idPacote = obterIdPacote(pacote);
                        if (idPacote != -1) {
                            inserirPedidoPacote(idPedido, idPacote);
                        }
                    }
                    
                    // Inserir serviços do pedido
                    for (ServicoAdicional servico : pedido.getServicos()) {
                        int idServico = obterIdServico(servico);
                        if (idServico != -1) {
                            inserirPedidoServico(idPedido, idServico);
                        }
                    }
                    
                    return idPedido;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    private int obterIdCliente(Cliente cliente) {
        String sql = "SELECT id FROM clientes WHERE documento = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, cliente.getIdentificacao());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    private int obterIdPacote(PacoteViagem pacote) {
        String sql = "SELECT id FROM pacotes WHERE nome = ? AND destino = ? AND duracao = ? AND preco = ? AND tipo = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, pacote.getNome());
            pstmt.setString(2, pacote.getDestino());
            pstmt.setInt(3, pacote.getDuracao());
            pstmt.setDouble(4, pacote.getPreco());
            pstmt.setString(5, pacote.getTipo());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    private int obterIdServico(ServicoAdicional servico) {
        String sql = "SELECT id FROM servicos WHERE descricao = ? AND preco = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, servico.getDescricao());
            pstmt.setDouble(2, servico.getPreco());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    private boolean inserirPedidoPacote(int idPedido, int idPacote) {
        String sql = "INSERT INTO pedidos_pacotes (id_pedido, id_pacote) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idPedido);
            pstmt.setInt(2, idPacote);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private boolean inserirPedidoServico(int idPedido, int idServico) {
        String sql = "INSERT INTO pedidos_servicos (id_pedido, id_servico) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idPedido);
            pstmt.setInt(2, idServico);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Pedido> listarPedidos() {
        List<Pedido> pedidos = new ArrayList<>();
        List<Cliente> clientes = listarClientes();
        
        String sql = "SELECT * FROM pedidos";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int idPedido = rs.getInt("id");
                int idCliente = rs.getInt("id_cliente");
                
                // Encontrar o cliente
                Cliente cliente = null;
                for (Cliente c : clientes) {
                    if (obterIdCliente(c) == idCliente) {
                        cliente = c;
                        break;
                    }
                }
                
                if (cliente != null) {
                    Pedido pedido = new Pedido(cliente);
                    
                    // Adicionar pacotes ao pedido
                    String sqlPacotes = "SELECT p.* FROM pacotes p " +
                            "JOIN pedidos_pacotes pp ON p.id = pp.id_pacote " +
                            "WHERE pp.id_pedido = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(sqlPacotes)) {
                        pstmt.setInt(1, idPedido);
                        try (ResultSet rsPacotes = pstmt.executeQuery()) {
                            while (rsPacotes.next()) {
                                String nome = rsPacotes.getString("nome");
                                String destino = rsPacotes.getString("destino");
                                int duracao = rsPacotes.getInt("duracao");
                                double preco = rsPacotes.getDouble("preco");
                                String tipo = rsPacotes.getString("tipo");
                                
                                PacoteViagem pacote;
                                switch (tipo) {
                                    case "Aventura":
                                        pacote = new PacoteAventura(nome, destino, duracao, preco);
                                        break;
                                    case "Luxo":
                                        pacote = new PacoteLuxo(nome, destino, duracao, preco);
                                        break;
                                    case "Cultural":
                                        pacote = new PacoteCultural(nome, destino, duracao, preco);
                                        break;
                                    default:
                                        continue;
                                }
                                pedido.adicionarPacote(pacote);
                            }
                        }
                    }
                    
                    // Adicionar serviços ao pedido
                    String sqlServicos = "SELECT s.* FROM servicos s " +
                            "JOIN pedidos_servicos ps ON s.id = ps.id_servico " +
                            "WHERE ps.id_pedido = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(sqlServicos)) {
                        pstmt.setInt(1, idPedido);
                        try (ResultSet rsServicos = pstmt.executeQuery()) {
                            while (rsServicos.next()) {
                                String descricao = rsServicos.getString("descricao");
                                double preco = rsServicos.getDouble("preco");
                                pedido.adicionarServico(new ServicoAdicional(descricao, preco));
                            }
                        }
                    }
                    
                    pedidos.add(pedido);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pedidos;
    }
    
    public void fecharConexao() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
