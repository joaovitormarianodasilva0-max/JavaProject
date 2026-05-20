package sistemaacademico.dao;

import sistemaacademico.modelo.Aluno;
import sistemaacademico.modelo.NotaFalta;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class AlunoDAO {

    private static final String ARQ_ALUNOS = "alunos.dat";
    private static final String ARQ_NOTAS  = "notas.dat";

    private static List<Aluno> alunos = carregarAlunos();
    private static List<NotaFalta> notas = carregarNotas();

    // ---------- ALUNOS ----------


    public static List<Aluno> listar() {
        return new ArrayList<>(alunos);
    }

    public static Aluno buscar(String rgm) {
        for (Aluno a : alunos) if (a.getRgm().equals(rgm)) return a;
        return null;
    }

    public static boolean inserir(Aluno a) {
        if (buscar(a.getRgm()) != null) return false; // PK duplicada
        alunos.add(a);
        salvarAlunos();
        return true;
    }

    public static boolean alterar(Aluno a) {
        for (int i = 0; i < alunos.size(); i++) {
            if (alunos.get(i).getRgm().equals(a.getRgm())) {
                alunos.set(i, a);
                salvarAlunos();
                return true;
            }
        }
        return false;
    }

    public static boolean excluir(String rgm) {
        boolean removeu = alunos.removeIf(a -> a.getRgm().equals(rgm));
        if (removeu) {
            notas.removeIf(n -> n.getRgm().equals(rgm));
            salvarAlunos();
            salvarNotas();
        }
        return removeu;
    }

    // ---------- NOTAS ----------


    public static List<NotaFalta> listarNotas(String rgm) {
        List<NotaFalta> r = new ArrayList<>();
        for (NotaFalta n : notas) if (n.getRgm().equals(rgm)) r.add(n);
        return r;
    }

    public static void inserirNota(NotaFalta n) {
        notas.add(n);
        salvarNotas();
    }

    public static void removerNota(String rgm, String disciplina, String semestre) {
        Iterator<NotaFalta> it = notas.iterator();
        while (it.hasNext()) {
            NotaFalta n = it.next();
            if (n.getRgm().equals(rgm) && n.getDisciplina().equals(disciplina)
                    && n.getSemestre().equals(semestre)) {
                it.remove();
            }
        }
        salvarNotas();
    }

    // ---------- Persistência ----------

    
    @SuppressWarnings("unchecked")
    private static List<Aluno> carregarAlunos() {
        File f = new File(ARQ_ALUNOS);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return (List<Aluno>) in.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    private static List<NotaFalta> carregarNotas() {
        File f = new File(ARQ_NOTAS);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return (List<NotaFalta>) in.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private static void salvarAlunos() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARQ_ALUNOS))) {
            out.writeObject(alunos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void salvarNotas() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARQ_NOTAS))) {
            out.writeObject(notas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
