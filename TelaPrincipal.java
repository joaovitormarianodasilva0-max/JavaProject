package sistemaacademico.gui;

import sistemaacademico.dao.AlunoDAO;
import sistemaacademico.modelo.Aluno;
import sistemaacademico.modelo.NotaFalta;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;


public class TelaPrincipal extends JFrame {

    // ---- Abas ----

    private final JTabbedPane abas = new JTabbedPane();

    // ---- Dados Pessoais ----


    private final JTextField txtRgm        = new JTextField(10);
    private final JTextField txtNome       = new JTextField(20);
    private final JFormattedTextField txtDataNasc = criarMascara("##/##/####");
    private final JFormattedTextField txtCpf      = criarMascara("###.###.###-##");
    private final JTextField txtEmail      = new JTextField(25);
    private final JTextField txtEndereco   = new JTextField(30);
    private final JTextField txtMunicipio  = new JTextField(15);
    private final JComboBox<String> cmbUf  = new JComboBox<>(new String[]{
            "SP","RJ","MG","PR","RS","SC","BA","DF","ES","GO","PE","CE"});
    private final JFormattedTextField txtCelular = criarMascara("(##) #####-####");

    // ---- Curso ----


    private final JComboBox<String> cmbCurso  = new JComboBox<>(new String[]{
            "Análise e Desenvolvimento de Sistemas",
            "Ciência da Computação",
            "Engenharia de Software",
            "Sistemas de Informação"});
    private final JComboBox<String> cmbCampus = new JComboBox<>(new String[]{
            "Tatuapé","Pinheiros","Vergueiro","Anália Franco"});
    private final JRadioButton rbMatutino   = new JRadioButton("Matutino");
    private final JRadioButton rbVespertino = new JRadioButton("Vespertino");
    private final JRadioButton rbNoturno    = new JRadioButton("Noturno");

    // ---- Notas e Faltas ----


    private final JLabel lblNomeAluno = new JLabel("(consulte um RGM)");
    private final JLabel lblCursoAluno = new JLabel("");
    private final JComboBox<String> cmbDisciplina = new JComboBox<>(new String[]{
            "Programação Orientada a Objetos",
            "Estrutura de Dados",
            "Banco de Dados",
            "Engenharia de Software",
            "Redes de Computadores"});
    private final JComboBox<String> cmbSemestre = new JComboBox<>(new String[]{
            "2024-1","2024-2","2025-1","2025-2","2026-1","2026-2"});
    private final JTextField txtNota   = new JTextField(5);
    private final JTextField txtFaltas = new JTextField(5);

    // ---- Boletim ----


    private final DefaultTableModel modeloBoletim =
            new DefaultTableModel(new String[]{"Disciplina","Semestre","Nota","Faltas","Situação"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
    private final JTable tabelaBoletim = new JTable(modeloBoletim);
    private final JLabel lblBolRgm   = new JLabel("—");
    private final JLabel lblBolNome  = new JLabel("—");
    private final JLabel lblBolCurso = new JLabel("—");
    private final JLabel lblBolCampus= new JLabel("—");

    public TelaPrincipal() {
        super("Sistema Acadêmico - UNICID");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 520);
        setLocationRelativeTo(null);

        setJMenuBar(criarMenu());

        abas.addTab("Dados Pessoais", criarAbaDadosPessoais());
        abas.addTab("Curso",          criarAbaCurso());
        abas.addTab("Notas e Faltas", criarAbaNotasFaltas());
        abas.addTab("Boletim",        criarAbaBoletim());

        setLayout(new BorderLayout());
        add(abas, BorderLayout.CENTER);
        add(criarBarraBotoes(), BorderLayout.SOUTH);

        ButtonGroup g = new ButtonGroup();
        g.add(rbMatutino); g.add(rbVespertino); g.add(rbNoturno);
        rbMatutino.setSelected(true);
    }

    // ===================== MENU =====================


    private JMenuBar criarMenu() {
        JMenuBar mb = new JMenuBar();

        JMenu mAluno = new JMenu("Aluno");
        JMenuItem miSalvar    = new JMenuItem("Salvar");
        JMenuItem miAlterar   = new JMenuItem("Alterar");
        JMenuItem miConsultar = new JMenuItem("Consultar");
        JMenuItem miExcluir   = new JMenuItem("Excluir");
        JMenuItem miSair      = new JMenuItem("Sair");
        miSalvar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        miSair.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.SHIFT_DOWN_MASK));
        miSalvar.addActionListener(e -> salvar());
        miAlterar.addActionListener(e -> alterar());
        miConsultar.addActionListener(e -> consultar());
        miExcluir.addActionListener(e -> excluir());
        miSair.addActionListener(e -> sair());
        mAluno.add(miSalvar); mAluno.add(miAlterar); mAluno.add(miConsultar);
        mAluno.add(miExcluir); mAluno.addSeparator(); mAluno.add(miSair);

        JMenu mNotas = new JMenu("Notas e Faltas");
        JMenuItem miLancar   = new JMenuItem("Lançar");
        JMenuItem miBoletim  = new JMenuItem("Visualizar Boletim");
        miLancar.addActionListener(e -> abas.setSelectedIndex(2));
        miBoletim.addActionListener(e -> { atualizarBoletim(); abas.setSelectedIndex(3); });
        mNotas.add(miLancar); mNotas.add(miBoletim);

        JMenu mAjuda = new JMenu("Ajuda");
        JMenuItem miSobre = new JMenuItem("Sobre");
        miSobre.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Sistema Acadêmico v1.0\nUNICID — Programação Orientada a Objetos\n© 2026",
                "Sobre", JOptionPane.INFORMATION_MESSAGE));
        mAjuda.add(miSobre);

        mb.add(mAluno); mb.add(mNotas); mb.add(mAjuda);
        return mb;
    }

    // ===================== ABA 1: DADOS PESSOAIS =====================


    private JPanel criarAbaDadosPessoais() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder(
                new EtchedBorder(), "Identificação do Aluno",
                TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5);
        g.anchor = GridBagConstraints.WEST;

        int y = 0;
        addCampo(p, g, 0, y, "RGM",  txtRgm);
        addCampo(p, g, 2, y, "Nome", txtNome); y++;
        addCampo(p, g, 0, y, "Data de Nascimento", txtDataNasc);
        addCampo(p, g, 2, y, "CPF",  txtCpf); y++;
        addCampo(p, g, 0, y, "Email", txtEmail); y++;
        addCampo(p, g, 0, y, "Endereço", txtEndereco); y++;
        addCampo(p, g, 0, y, "Município", txtMunicipio);
        addCampo(p, g, 2, y, "UF", cmbUf); y++;
        addCampo(p, g, 0, y, "Celular", txtCelular);
        return p;
    }

    // ===================== ABA 2: CURSO =====================


    private JPanel criarAbaCurso() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder(
                new EtchedBorder(), "Dados Acadêmicos",
                TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8,8,8,8);
        g.anchor = GridBagConstraints.WEST;

        addCampo(p, g, 0, 0, "Curso",  cmbCurso);
        addCampo(p, g, 0, 1, "Campus", cmbCampus);

        g.gridx = 0; g.gridy = 2;
        p.add(new JLabel("Período:"), g);
        JPanel pr = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pr.add(rbMatutino); pr.add(rbVespertino); pr.add(rbNoturno);
        g.gridx = 1; g.gridwidth = 3;
        p.add(pr, g);
        return p;
    }

    // ===================== ABA 3: NOTAS E FALTAS =====================


    private JPanel criarAbaNotasFaltas() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder(
                new EtchedBorder(), "Lançamento de Notas e Faltas",
                TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5);
        g.anchor = GridBagConstraints.WEST;

        g.gridx=0; g.gridy=0; p.add(new JLabel("RGM:"), g);
        g.gridx=1; p.add(lblBolRgm, g); // reusa label
        g.gridx=2; p.add(new JLabel("Nome:"), g);
        g.gridx=3; p.add(lblNomeAluno, g);

        g.gridx=0; g.gridy=1; p.add(new JLabel("Curso:"), g);
        g.gridx=1; g.gridwidth=3; p.add(lblCursoAluno, g); g.gridwidth=1;

        addCampo(p, g, 0, 2, "Disciplina", cmbDisciplina);
        addCampo(p, g, 0, 3, "Semestre",   cmbSemestre);
        addCampo(p, g, 0, 4, "Nota (0-10)", txtNota);
        addCampo(p, g, 2, 4, "Faltas",      txtFaltas);

        JButton btnLancar = new JButton("Lançar");
        btnLancar.addActionListener(e -> lancarNota());
        g.gridx=3; g.gridy=5; p.add(btnLancar, g);
        return p;
    }

    // ===================== ABA 4: BOLETIM =====================


    private JPanel criarAbaBoletim() {
        JPanel p = new JPanel(new BorderLayout(5,5));
        p.setBorder(BorderFactory.createTitledBorder(
                new EtchedBorder(), "Boletim Escolar",
                TitledBorder.LEFT, TitledBorder.TOP));

        JPanel cab = new JPanel(new GridLayout(2,4,8,4));
        cab.add(new JLabel("RGM:"));    cab.add(lblBolRgm);
        cab.add(new JLabel("Curso:"));  cab.add(lblBolCurso);
        cab.add(new JLabel("Nome:"));   cab.add(lblBolNome);
        cab.add(new JLabel("Campus:")); cab.add(lblBolCampus);
        p.add(cab, BorderLayout.NORTH);
        p.add(new JScrollPane(tabelaBoletim), BorderLayout.CENTER);

        JButton btnAtualizar = new JButton("Atualizar Boletim");
        btnAtualizar.addActionListener(e -> atualizarBoletim());
        JPanel sul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sul.add(btnAtualizar);
        p.add(sul, BorderLayout.SOUTH);
        return p;
    }

    // ===================== BARRA DE BOTÕES =====================


    private JPanel criarBarraBotoes() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton bSalvar    = new JButton("Salvar");
        JButton bAlterar   = new JButton("Alterar");
        JButton bConsultar = new JButton("Consultar");
        JButton bExcluir   = new JButton("Excluir");
        JButton bSair      = new JButton("Sair");
        bSalvar.addActionListener(e -> salvar());
        bAlterar.addActionListener(e -> alterar());
        bConsultar.addActionListener(e -> consultar());
        bExcluir.addActionListener(e -> excluir());
        bSair.addActionListener(e -> sair());
        p.add(bSalvar); p.add(bAlterar); p.add(bConsultar);
        p.add(bExcluir); p.add(bSair);
        return p;
    }

    // ===================== AÇÕES =====================


    private void salvar() {
        if (!validarObrigatorios()) return;
        Aluno a = lerFormulario();
        if (AlunoDAO.inserir(a)) {
            JOptionPane.showMessageDialog(this, "Aluno cadastrado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Já existe aluno com o RGM " + a.getRgm(),
                    "RGM Duplicado", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void alterar() {
        if (txtRgm.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o RGM para alterar.",
                    "Atenção", JOptionPane.WARNING_MESSAGE); return;
        }
        Aluno a = lerFormulario();
        if (AlunoDAO.alterar(a)) {
            JOptionPane.showMessageDialog(this, "Dados alterados com sucesso!");
        } else {
            JOptionPane.showMessageDialog(this, "Aluno não encontrado.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void consultar() {
        String rgm = txtRgm.getText().trim();
        if (rgm.isEmpty()) {
            // mostra lista
            List<Aluno> todos = AlunoDAO.listar();
            if (todos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum aluno cadastrado.");
                return;
            }
            Aluno escolha = (Aluno) JOptionPane.showInputDialog(this,
                    "Selecione o aluno:", "Consultar",
                    JOptionPane.QUESTION_MESSAGE, null,
                    todos.toArray(), todos.get(0));
            if (escolha != null) preencherFormulario(escolha);
            return;
        }
        Aluno a = AlunoDAO.buscar(rgm);
        if (a == null) {
            JOptionPane.showMessageDialog(this, "Aluno não encontrado.",
                    "Consulta", JOptionPane.WARNING_MESSAGE);
        } else {
            preencherFormulario(a);
        }
    }

    private void excluir() {
        String rgm = txtRgm.getText().trim();
        if (rgm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o RGM para excluir.");
            return;
        }
        int op = JOptionPane.showConfirmDialog(this,
                "Excluir o aluno RGM " + rgm + "?\nTodas as notas e faltas serão removidas (cascata).",
                "Confirmação", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            if (AlunoDAO.excluir(rgm)) {
                limparFormulario();
                JOptionPane.showMessageDialog(this, "Excluído com sucesso.");
            } else {
                JOptionPane.showMessageDialog(this, "Aluno não encontrado.");
            }
        }
    }

    private void sair() {
        int op = JOptionPane.showConfirmDialog(this, "Deseja realmente sair?",
                "Sair", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) System.exit(0);
    }

    private void lancarNota() {
        String rgm = txtRgm.getText().trim();
        Aluno a = AlunoDAO.buscar(rgm);
        if (a == null) {
            JOptionPane.showMessageDialog(this,
                    "Consulte um RGM válido na aba Dados Pessoais.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            double nota = Double.parseDouble(txtNota.getText().replace(",", "."));
            int faltas  = Integer.parseInt(txtFaltas.getText().trim());
            if (nota < 0 || nota > 10) throw new NumberFormatException();
            if (faltas < 0) throw new NumberFormatException();
            AlunoDAO.inserirNota(new NotaFalta(rgm,
                    (String) cmbDisciplina.getSelectedItem(),
                    (String) cmbSemestre.getSelectedItem(),
                    nota, faltas));
            txtNota.setText(""); txtFaltas.setText("");
            JOptionPane.showMessageDialog(this, "Nota/Falta lançada com sucesso!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Nota (0-10) e Faltas (>=0) devem ser numéricos válidos.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarBoletim() {
        modeloBoletim.setRowCount(0);
        String rgm = txtRgm.getText().trim();
        Aluno a = AlunoDAO.buscar(rgm);
        if (a == null) {
            lblBolRgm.setText("—"); lblBolNome.setText("—");
            lblBolCurso.setText("—"); lblBolCampus.setText("—");
            return;
        }
        lblBolRgm.setText(a.getRgm());
        lblBolNome.setText(a.getNome());
        lblBolCurso.setText(a.getCurso());
        lblBolCampus.setText(a.getCampus());
        for (NotaFalta n : AlunoDAO.listarNotas(rgm)) {
            modeloBoletim.addRow(new Object[]{
                    n.getDisciplina(), n.getSemestre(),
                    String.format("%.1f", n.getNota()),
                    n.getFaltas(), n.getSituacao()});
        }
    }

    // ===================== HELPERS =====================


    private boolean validarObrigatorios() {
        String[][] req = {
                {txtRgm.getText(), "RGM"},
                {txtNome.getText(), "Nome"},
                {txtDataNasc.getText().replace("/","").trim(), "Data de Nascimento"},
                {txtCpf.getText().replaceAll("[^0-9]",""), "CPF"},
                {txtEmail.getText(), "Email"},
                {txtEndereco.getText(), "Endereço"},
                {txtMunicipio.getText(), "Município"},
                {txtCelular.getText().replaceAll("[^0-9]",""), "Celular"}
        };
        for (String[] c : req) {
            if (c[0] == null || c[0].trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Campo obrigatório: " + c[1],
                        "Validação", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private Aluno lerFormulario() {
        Aluno a = new Aluno();
        a.setRgm(txtRgm.getText().trim());
        a.setNome(txtNome.getText().trim());
        a.setDataNascimento(txtDataNasc.getText());
        a.setCpf(txtCpf.getText());
        a.setEmail(txtEmail.getText().trim());
        a.setEndereco(txtEndereco.getText().trim());
        a.setMunicipio(txtMunicipio.getText().trim());
        a.setUf((String) cmbUf.getSelectedItem());
        a.setCelular(txtCelular.getText());
        a.setCurso((String) cmbCurso.getSelectedItem());
        a.setCampus((String) cmbCampus.getSelectedItem());
        a.setPeriodo(rbMatutino.isSelected() ? "Matutino"
                : rbVespertino.isSelected() ? "Vespertino" : "Noturno");
        return a;
    }

    private void preencherFormulario(Aluno a) {
        txtRgm.setText(a.getRgm());
        txtNome.setText(a.getNome());
        txtDataNasc.setText(a.getDataNascimento());
        txtCpf.setText(a.getCpf());
        txtEmail.setText(a.getEmail());
        txtEndereco.setText(a.getEndereco());
        txtMunicipio.setText(a.getMunicipio());
        if (a.getUf() != null) cmbUf.setSelectedItem(a.getUf());
        txtCelular.setText(a.getCelular());
        if (a.getCurso() != null)  cmbCurso.setSelectedItem(a.getCurso());
        if (a.getCampus() != null) cmbCampus.setSelectedItem(a.getCampus());
        if ("Vespertino".equals(a.getPeriodo())) rbVespertino.setSelected(true);
        else if ("Noturno".equals(a.getPeriodo())) rbNoturno.setSelected(true);
        else rbMatutino.setSelected(true);

        lblNomeAluno.setText(a.getNome());
        lblCursoAluno.setText(a.getCurso());
    }

    private void limparFormulario() {
        for (JTextField t : new JTextField[]{
                txtRgm, txtNome, txtDataNasc, txtCpf, txtEmail,
                txtEndereco, txtMunicipio, txtCelular, txtNota, txtFaltas}) {
            t.setText("");
        }
        cmbUf.setSelectedIndex(0);
        cmbCurso.setSelectedIndex(0);
        cmbCampus.setSelectedIndex(0);
        rbMatutino.setSelected(true);
        lblNomeAluno.setText("(consulte um RGM)");
        lblCursoAluno.setText("");
    }

    private void addCampo(JPanel p, GridBagConstraints g, int x, int y,
                          String label, JComponent campo) {
        g.gridx = x;     g.gridy = y; p.add(new JLabel(label + ":"), g);
        g.gridx = x + 1; g.gridy = y; p.add(campo, g);
    }

    private JFormattedTextField criarMascara(String m) {
        try {
            javax.swing.text.MaskFormatter f = new javax.swing.text.MaskFormatter(m);
            f.setPlaceholderCharacter(' ');
            JFormattedTextField t = new JFormattedTextField(f);
            t.setColumns(m.length());
            return t;
        } catch (java.text.ParseException e) {
            return new JFormattedTextField();
        }
    }
}
