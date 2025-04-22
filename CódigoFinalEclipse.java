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
    
    public String getNome() {
        return nome;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public String getEmail() {
        return email;
    }
    
    @Override
    public String toString() {
        return getResumo();
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
    
    public String getNome() {
        return nome;
    }
    
    public String getDestino() {
        return destino;
    }
    
    public int getDuracao() {
        return duracao;
    }
    
    public String getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return getResumo();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PacoteViagem that = (PacoteViagem) obj;
        return duracao == that.duracao &&
                Double.compare(that.preco, preco) == 0 &&
                nome.equals(that.nome) &&
                destino.equals(that.destino) &&
                tipo.equals(that.tipo);
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
    
    public String getDescricao() {
        return descricao;
    }

    @Override
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

    public List<PacoteViagem> getPacotes() {
        return pacotes;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public List<ServicoAdicional> getServicos() {
        return servicos;
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

            if (opcao == 0) menuClientes(clientes, pedidos);
            else if (opcao == 1) menuPacotes(pacotes, pedidos);
            else if (opcao == 2) menuServicos(servicos);
            else if (opcao == 3) menuPedidos(clientes, pacotes, servicos, pedidos);
            else if (opcao == 4) mostrarResumoGeral(pedidos);
            else break;
        }
    }

    private static void menuClientes(List<Cliente> clientes, List<Pedido> pedidos) {
        String[] opcoes = {"Cadastrar", "Visualizar", "Buscar", "Excluir", "Pacotes por Cliente", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(null, "Menu de Clientes:", "Clientes",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha == 0) cadastrarCliente(clientes);
        else if (escolha == 1) listar(clientes.stream().map(Cliente::getResumo).toList(), "Clientes");
        else if (escolha == 2) buscarCliente(clientes);
        else if (escolha == 3) excluirItem(clientes, "cliente", null);
        else if (escolha == 4) mostrarPacotesPorCliente(clientes, pedidos);
    }

    private static void menuPacotes(List<PacoteViagem> pacotes, List<Pedido> pedidos) {
        String[] opcoes = {"Cadastrar", "Visualizar", "Buscar", "Excluir", "Clientes por Pacote", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(null, "Menu de Pacotes:", "Pacotes",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha == 0) cadastrarPacote(pacotes);
        else if (escolha == 1) listar(pacotes.stream().map(PacoteViagem::getResumo).toList(), "Pacotes");
        else if (escolha == 2) buscarPacote(pacotes);
        else if (escolha == 3) excluirItem(pacotes, "pacote", pedidos);
        else if (escolha == 4) mostrarClientesPorPacote(pedidos);
    }

    private static void menuServicos(List<ServicoAdicional> servicos) {
        String[] opcoes = {"Cadastrar", "Visualizar", "Buscar", "Excluir", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(null, "Menu de Serviços:", "Serviços",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha == 0) cadastrarServico(servicos);
        else if (escolha == 1) listar(servicos.stream().map(ServicoAdicional::getResumo).toList(), "Serviços");
        else if (escolha == 2) buscarServico(servicos);
        else if (escolha == 3) excluirItem(servicos, "serviço", null);
    }

    private static void menuPedidos(List<Cliente> clientes, List<PacoteViagem> pacotes, 
                                  List<ServicoAdicional> servicos, List<Pedido> pedidos) {
        String[] opcoes = {"Criar Pedido", "Visualizar Pedidos", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(null, "Menu de Pedidos:", "Pedidos",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha == 0) criarPedido(clientes, pacotes, servicos, pedidos);
        else if (escolha == 1) listarPedidos(pedidos);
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
        String nome;
        while (true) {
            nome = JOptionPane.showInputDialog("Nome do pacote:");
            if (nome == null) return;
            if (nome.isBlank()) {
                JOptionPane.showMessageDialog(null, "O pacote deve ter um nome.", "Pacote", JOptionPane.ERROR_MESSAGE);
            } else {
                 break;
            }
        }
        
        String destino;
        while (true) {
            destino = JOptionPane.showInputDialog("Destino:");
            if (destino == null) return;
            if (destino.isBlank()) {
                JOptionPane.showMessageDialog(null, "O pacote deve conter um destino.", "Pacote", JOptionPane.ERROR_MESSAGE);
            } else {
                 break;
            }
        }
        
        String entradaDuracao;
        int duracao;
        while (true) {
            entradaDuracao = JOptionPane.showInputDialog("Duração (dias):");
            if (entradaDuracao == null) return;
            if (entradaDuracao.isBlank()) {
                JOptionPane.showMessageDialog(null, "O pacote deve conter uma duração.", "Pacote", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    duracao = Integer.parseInt(entradaDuracao);
                    break;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Digite um número válido para a duração.", "Pacote", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        String entradaPreco;
        double preco;
        while (true) {
            entradaPreco = JOptionPane.showInputDialog("Preço (R$):");
            if (entradaPreco == null) return;
            if (entradaPreco.isBlank()) {
                JOptionPane.showMessageDialog(null, "O pacote deve conter um preço.", "Pacote", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    preco = Double.parseDouble(entradaPreco);
                    break;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Digite um número válido para o preço.", "Pacote", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        int tipo = JOptionPane.showOptionDialog(null, "Tipo de Pacote:", "Tipo",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                new String[]{"Aventura", "Luxo", "Cultural"}, "Aventura");

        if (tipo == -1) return;

        switch (tipo) {
            case 0 -> pacotes.add(new PacoteAventura(nome, destino, duracao, preco));
            case 1 -> pacotes.add(new PacoteLuxo(nome, destino, duracao, preco));
            case 2 -> pacotes.add(new PacoteCultural(nome, destino, duracao, preco));
        }

        JOptionPane.showMessageDialog(null, "Pacote cadastrado com sucesso!");
    }

    private static void cadastrarServico(List<ServicoAdicional> servicos) {
        String desc;
        while (true) {
            desc = JOptionPane.showInputDialog("Descrição:");
            if (desc == null) return;
            if (desc.isBlank()) {
                JOptionPane.showMessageDialog(null, "O serviço deve conter uma descrição.", "Serviços", JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }

        String entradaPreco;
        double preco;
        while (true) {
            entradaPreco = JOptionPane.showInputDialog("Preço (R$):");
            if (entradaPreco == null) return;
            if (entradaPreco.isBlank()) {
                JOptionPane.showMessageDialog(null, "O serviço deve conter um preço.", "Serviços", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    preco = Double.parseDouble(entradaPreco);
                    break;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Digite um número válido para o preço.", "Serviços", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        servicos.add(new ServicoAdicional(desc, preco));
        JOptionPane.showMessageDialog(null, "Serviço cadastrado com sucesso!");
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

    private static void listarPedidos(List<Pedido> pedidos) {
        if (pedidos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum pedido foi realizado ainda.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Pedido p : pedidos) {
            sb.append(p.getResumo()).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Pedidos", JOptionPane.INFORMATION_MESSAGE);
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

    private static void excluirItem(List<?> lista, String tipo, List<Pedido> pedidos) {
        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum " + tipo + " para excluir.", null, JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nomes = lista.stream().map(Object::toString).toArray(String[]::new);
        int idx = JOptionPane.showOptionDialog(null, "Excluir qual " + tipo + "?", "Excluir",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, nomes, nomes[0]);

        if (idx >= 0) {
            if (tipo.equals("pacote") && pedidos != null) {
                PacoteViagem pacote = (PacoteViagem) lista.get(idx);
                boolean pacoteEmUso = false;
                
                for (Pedido pedido : pedidos) {
                    for (PacoteViagem p : pedido.getPacotes()) {
                        if (p.equals(pacote)) {
                            pacoteEmUso = true;
                            break;
                        }
                    }
                    if (pacoteEmUso) break;
                }
                
                if (pacoteEmUso) {
                    JOptionPane.showMessageDialog(null, 
                        "Não é possível excluir este pacote pois ele está associado a um ou mais pedidos.", 
                        "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            lista.remove(idx);
            JOptionPane.showMessageDialog(null, tipo + " removido.");
        }
    }

    private static void buscarCliente(List<Cliente> clientes) {
        if (clientes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum cliente cadastrado.");
            return;
        }

        String termo = JOptionPane.showInputDialog("Digite o nome, telefone, email ou documento do cliente:");
        if (termo == null || termo.isBlank()) return;

        List<Cliente> resultados = new ArrayList<>();
        for (Cliente c : clientes) {
            if (c.getNome().toLowerCase().contains(termo.toLowerCase())) {
                resultados.add(c);
                continue;
            }
            if (c.getTelefone().contains(termo)) {
                resultados.add(c);
                continue;
            }
            if (c.getEmail().toLowerCase().contains(termo.toLowerCase())) {
                resultados.add(c);
                continue;
            }
            if (c.getIdentificacao().contains(termo)) {
                resultados.add(c);
            }
        }

        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum cliente encontrado com o termo: " + termo);
        } else {
            listar(resultados.stream().map(Cliente::getResumo).toList(), "Resultados da Busca");
        }
    }

    private static void buscarPacote(List<PacoteViagem> pacotes) {
        if (pacotes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum pacote cadastrado.");
            return;
        }

        String termo = JOptionPane.showInputDialog("Digite o nome, destino ou tipo do pacote:");
        if (termo == null || termo.isBlank()) return;

        List<PacoteViagem> resultados = new ArrayList<>();
        for (PacoteViagem p : pacotes) {
            if (p.getNome().toLowerCase().contains(termo.toLowerCase())) {
                resultados.add(p);
                continue;
            }
            if (p.getDestino().toLowerCase().contains(termo.toLowerCase())) {
                resultados.add(p);
                continue;
            }
            if (p.getTipo().toLowerCase().contains(termo.toLowerCase())) {
                resultados.add(p);
            }
        }

        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum pacote encontrado com o termo: " + termo);
        } else {
            listar(resultados.stream().map(PacoteViagem::getResumo).toList(), "Resultados da Busca");
        }
    }

    private static void buscarServico(List<ServicoAdicional> servicos) {
        if (servicos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum serviço cadastrado.");
            return;
        }

        String termo = JOptionPane.showInputDialog("Digite a descrição do serviço:");
        if (termo == null || termo.isBlank()) return;

        List<ServicoAdicional> resultados = new ArrayList<>();
        for (ServicoAdicional s : servicos) {
            if (s.getDescricao().toLowerCase().contains(termo.toLowerCase())) {
                resultados.add(s);
            }
        }

        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum serviço encontrado com o termo: " + termo);
        } else {
            listar(resultados.stream().map(ServicoAdicional::getResumo).toList(), "Resultados da Busca");
        }
    }

    private static void mostrarPacotesPorCliente(List<Cliente> clientes, List<Pedido> pedidos) {
        if (clientes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum cliente cadastrado.");
            return;
        }

        if (pedidos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum pedido foi realizado ainda.");
            return;
        }

        String[] nomesClientes = clientes.stream().map(Cliente::getResumo).toArray(String[]::new);
        int idxCliente = JOptionPane.showOptionDialog(null, "Escolha o cliente:", "Pacotes por Cliente",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, nomesClientes, nomesClientes[0]);
        if (idxCliente == -1) return;

        Cliente clienteSelecionado = clientes.get(idxCliente);
        StringBuilder sb = new StringBuilder("Pacotes contratados por " + clienteSelecionado.getResumo() + ":\n\n");

        boolean encontrou = false;
        for (Pedido p : pedidos) {
            if (p.getCliente().equals(clienteSelecionado)) {
                encontrou = true;
                for (PacoteViagem pacote : p.getPacotes()) {
                    sb.append(" - ").append(pacote.getResumo()).append("\n");
                }
            }
        }

        if (!encontrou) {
            sb.append("Nenhum pacote contratado por este cliente.");
        }

        JOptionPane.showMessageDialog(null, sb.toString());
    }

    private static void mostrarClientesPorPacote(List<Pedido> pedidos) {
        if (pedidos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum pedido foi realizado ainda.");
            return;
        }

        // Coletar todos os pacotes únicos dos pedidos
        Set<PacoteViagem> todosPacotes = new HashSet<>();
        for (Pedido p : pedidos) {
            todosPacotes.addAll(p.getPacotes());
        }

        if (todosPacotes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum pacote foi contratado ainda.");
            return;
        }

        String[] nomesPacotes = todosPacotes.stream().map(PacoteViagem::getResumo).toArray(String[]::new);
        int idxPacote = JOptionPane.showOptionDialog(null, "Escolha o pacote:", "Clientes por Pacote",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, nomesPacotes, nomesPacotes[0]);
        if (idxPacote == -1) return;

        PacoteViagem pacoteSelecionado = todosPacotes.toArray(new PacoteViagem[0])[idxPacote];
        StringBuilder sb = new StringBuilder("Clientes que contrataram " + pacoteSelecionado.getResumo() + ":\n\n");

        Set<Cliente> clientesEncontrados = new HashSet<>();
        for (Pedido p : pedidos) {
            if (p.getPacotes().contains(pacoteSelecionado)) {
                clientesEncontrados.add(p.getCliente());
            }
        }

        if (clientesEncontrados.isEmpty()) {
            sb.append("Nenhum cliente contratou este pacote.");
        } else {
            for (Cliente c : clientesEncontrados) {
                sb.append(" - ").append(c.getResumo()).append("\n");
            }
        }

        JOptionPane.showMessageDialog(null, sb.toString());
    }
}
