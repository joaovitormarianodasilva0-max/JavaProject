package sistemaacademico.modelo;

import java.io.Serializable;


public class NotaFalta implements Serializable {
    private static final long serialVersionUID = 1L;

    private String rgm;
    private String disciplina;
    private String semestre;
    private double nota;
    private int faltas;

    public NotaFalta() {}

    public NotaFalta(String rgm, String disciplina, String semestre, double nota, int faltas) {
        this.rgm = rgm;
        this.disciplina = disciplina;
        this.semestre = semestre;
        this.nota = nota;
        this.faltas = faltas;
    }

    public String getRgm() { return rgm; }
    public void setRgm(String rgm) { this.rgm = rgm; }

    public String getDisciplina() { return disciplina; }
    public void setDisciplina(String disciplina) { this.disciplina = disciplina; }

    public String getSemestre() { return semestre; }
    public void setSemestre(String semestre) { this.semestre = semestre; }

    public double getNota() { return nota; }
    public void setNota(double nota) { this.nota = nota; }

    public int getFaltas() { return faltas; }
    public void setFaltas(int faltas) { this.faltas = faltas; }

    public String getSituacao() {
        return (nota >= 6.0 && faltas <= 15) ? "APROVADO" : "REPROVADO";
    }
}
