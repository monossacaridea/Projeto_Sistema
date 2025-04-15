//codigo do mysql 

  package com.ceub.bancodedados;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;



	public class Conexao {

	    private static final String URL = "jdbc:mysql://localhost:3306/aula_jdbc";
	    private static final String USUARIO = "root";
	    private static final String SENHA = "ceub123456";

	    
	    public static Connection conectar() throws SQLException {

	        return DriverManager.getConnection(URL, USUARIO, SENHA);

	    }

	    public static void desconectar(Connection conn) {

	        try {

	            if (conn != null && !conn.isClosed()) {
	                conn.close();
	            }

	        } catch (SQLException e) {

	            System.err.println("Erro ao fechar conexão: " + e.getMessage());
	        }
	    }
	    
	    public static void main(String[] args) {
	    	try (Connection conn = Conexao.conectar();

	                Statement stmt = conn.createStatement();
	                ResultSet rs = stmt.executeQuery("SELECT * FROM pessoa")) {

	                while (rs.next()) {

	                      System.out.println("ID: " + rs.getInt("id"));

	                      System.out.println("Nome: " + rs.getString("nome"));

	                      System.out.println("Idade: " + rs.getInt("idade"));

	                      System.out.println("---");

	                }

	            } catch (Exception e) {
	                 System.err.println("Erro ao consultar: " + e.getMessage());
	            }

	  

	        String sql = "INSERT INTO pessoa (nome, idade) VALUES (?, ?)";

	        try (Connection conn = Conexao.conectar();

	              PreparedStatement stmt = conn.prepareStatement(sql)) {

	              stmt.setString(1, "Maria");
	              stmt.setInt(2, 28);

	              int linhasAfetadas = stmt.executeUpdate();
	              System.out.println("Linhas inseridas: " + linhasAfetadas);

	        } catch (Exception e) {

	              System.err.println("Erro ao inserir: " + e.getMessage());
	        }
	    }
	}


