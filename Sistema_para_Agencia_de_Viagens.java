//Sistema para Agência de Viagens

package projetoSistemaAgencia;

import javax.swing.JOptionPane;

public class Agencia {

    public static void main(String[] args) {
        Cliente c = new Cliente();  // Agora a classe Cliente é concreta
        c.incluirCliente();
    }
}

// Agora é uma classe concreta, não abstrata
class Cliente {
    String nome;
    String CPF;
    String passaporte;
    String email;

    public void incluirCliente() {
        nome = JOptionPane.showInputDialog(null, "Insira seu nome:", "Cadastro", JOptionPane.QUESTION_MESSAGE);
        CPF = JOptionPane.showInputDialog(null, "Insira seu CPF:", "Cadastro", JOptionPane.QUESTION_MESSAGE);
        passaporte = JOptionPane.showInputDialog(null, "Insira seu passaporte:", "Cadastro", JOptionPane.QUESTION_MESSAGE);
        email = JOptionPane.showInputDialog(null, "Insira seu email:", "Cadastro", JOptionPane.QUESTION_MESSAGE);
    }
}

// Mantive como abstratas, já que você ainda não implementou nada nelas
abstract class PacoteViagem {
    // conteúdo futuro
}

abstract class ServicoAdicional {
    // conteúdo futuro
}
