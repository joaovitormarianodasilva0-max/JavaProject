import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaCaixaEletronico extends JFrame {

    private final ICaixaEletronico caixa;

    public TelaCaixaEletronico(ICaixaEletronico caixa) {
        super("Caixa eletronico");
        this.caixa = caixa;
        montarTela();
    }

    private void montarTela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(rotulo("Modulo do Cliente:"));
        add(botao("Efetuar Saque", new SaqueAction()));

        add(rotulo("Modulo do Administrador:"));
        add(botao("Relatorio de Cedulas", new RelatorioAction()));
        add(botao("Valor total disponivel", new TotalAction()));
        add(botao("Reposicao de Cedulas", new ReposicaoAction()));
        add(botao("Cota Minima", new CotaMinimaAction()));

        add(rotulo("Modulo de Ambos:"));
        add(botao("Sair", e -> sair()));

        pack();
        setSize(280, 360);
        setLocationRelativeTo(null);
    }

    private JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setBorder(BorderFactory.createEmptyBorder(6, 8, 2, 8));
        return l;
    }

    private JButton botao(String texto, ActionListener acao) {
        JButton b = new JButton(texto);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        b.addActionListener(acao);
        return b;
    }

    private void msg(String s) {
        JTextArea area = new JTextArea(s);
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Caixa Eletronico", JOptionPane.INFORMATION_MESSAGE);
    }

    private Integer pedeInteiro(String pergunta) {
        String s = JOptionPane.showInputDialog(this, pergunta);
        if (s == null) return null;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException ex) {
            msg("Valor invalido. Digite um numero inteiro.");
            return null;
        }
    }

    // ----- Acoes -----
    private class SaqueAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Integer v = pedeInteiro("Informe o valor do saque:");
            if (v != null) msg(caixa.sacar(v));
        }
    }

    private class RelatorioAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            msg(caixa.pegaRelatorioCedulas());
        }
    }

    private class TotalAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            msg(caixa.pegaValorTotalDisponivel());
        }
    }

    private class ReposicaoAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Integer c = pedeInteiro("Cedula (2, 5, 10, 20, 50 ou 100):");
            if (c == null) return;
            Integer q = pedeInteiro("Quantidade:");
            if (q == null) return;
            msg(caixa.reposicaoCedulas(c, q));
        }
    }

    private class CotaMinimaAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Integer m = pedeInteiro("Informe a cota minima do caixa:");
            if (m != null) msg(caixa.armazenaCotaMinima(m));
        }
    }

    /** Mostra o extrato antes de encerrar a aplicacao. */
    private void sair() {
        String extrato;
        if (caixa instanceof CaixaEletronico) {
            extrato = ((CaixaEletronico) caixa).getExtrato();
        } else {
            extrato = "Encerrando o sistema...";
        }
        JTextArea area = new JTextArea(extrato, 20, 45);
        area.setEditable(false);
        int op = JOptionPane.showConfirmDialog(this, new JScrollPane(area),
                "Extrato - Saindo", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
        if (op == JOptionPane.OK_OPTION) System.exit(0);
    }
}
