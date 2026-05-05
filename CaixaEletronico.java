public class CaixaEletronico implements ICaixaEletronico {

    // matriz com as cedulas
    private int[][] cedulas = {
        {100, 100},
        { 50, 200},
        { 20, 300},
        { 10, 350},
        {  5, 450},
        {  2, 500}
    };

    // cota minima do caixa
    private int cotaMinima = 0;

    // extrato dos saques
    private String extrato = "";
    private int totalSaques = 0;


     //Calcula o valor total atualmente disponivel no caixa.

    private int calculaTotal() {
        int total = 0;
        for (int i = 0; i < cedulas.length; i++) {
            total = total + cedulas[i][0] * cedulas[i][1];
        }
        return total;
    }

    private boolean montarSaque(int valor, int i, int[] usadas) {
        if (valor == 0) {
            return true; // conseguiu montar
        }
        if (i >= cedulas.length) {
            return false; // acabaram as cedulas e ainda sobra valor
        }

        int valorCedula = cedulas[i][0];
        int dispon = cedulas[i][1];

        // quantidade maxima possivel desta cedula
        int max = valor / valorCedula;
        if (max > dispon) {
            max = dispon;
        }

        for (int q = max; q >= 0; q--) {
            usadas[i] = q;
            if (montarSaque(valor - q * valorCedula, i + 1, usadas)) {
                return true;
            }
        }
        usadas[i] = 0;
        return false;
    }

    @Override
    public String pegaValorTotalDisponivel() {
        int total = calculaTotal();
        return "Valor total disponivel no caixa: R$ " + total + ",00";
    }

    @Override
    public String pegaRelatorioCedulas() {
        String resposta = "===== RELATORIO DE CEDULAS =====\n";
        resposta = resposta + "Valor   |  Quantidade\n";
        resposta = resposta + "--------------------\n";
        for (int i = 0; i < cedulas.length; i++) {
            resposta = resposta + "R$ " + cedulas[i][0] + "\t|\t" + cedulas[i][1] + "\n";
        }
        resposta = resposta + "--------------------\n";
        resposta = resposta + "Total: R$ " + calculaTotal() + ",00\n";
        return resposta;
    }

    @Override
    public String reposicaoCedulas(Integer cedula, Integer quantidade) {
        if (cedula == null || quantidade == null) {
            return "Dados invalidos para reposicao.";
        }
        if (quantidade <= 0) {
            return "Quantidade deve ser maior que zero.";
        }
        // procura a cedula na matriz
        for (int i = 0; i < cedulas.length; i++) {
            if (cedulas[i][0] == cedula.intValue()) {
                cedulas[i][1] = cedulas[i][1] + quantidade;
                return "Reposicao realizada: +" + quantidade + " cedula(s) de R$ " + cedula
                        + ".\nNova quantidade: " + cedulas[i][1];
            }
        }
        return "Cedula de R$ " + cedula + " nao existe no caixa.\n"
             + "Use apenas: 2, 5, 10, 20, 50 ou 100.";
    }

    @Override
    public String armazenaCotaMinima(Integer minimo) {
        if (minimo == null || minimo < 0) {
            return "Cota minima invalida.";
        }
        this.cotaMinima = minimo;
        return "Cota minima armazenada: R$ " + minimo + ",00";
    }

    @Override
    public String sacar(Integer valor) {
        if (valor == null || valor <= 0) {
            return "Valor de saque invalido.";
        }

        int totalAtual = calculaTotal();
        if (totalAtual < cotaMinima) {
            return "Caixa Vazio: Chame o Operador";
        }

        int[] usadas = new int[cedulas.length];
        boolean conseguiu = montarSaque(valor, 0, usadas);

        if (!conseguiu) {
            return "Saque nao realizado por falta de cedulas";
        }

        // Conta total de cedulas usadas
        int totalCedulasUsadas = 0;
        for (int k = 0; k < usadas.length; k++) {
            totalCedulasUsadas = totalCedulasUsadas + usadas[k];
        }

        // Verifica limite de 30 cedulas

        if (totalCedulasUsadas > 30) {
            return "Saque nao permitido: numero de cedulas excede 30.";
        }

        // Verifica se apos o saque o caixa nao ficaria abaixo da cota minima

        if ((totalAtual - valor) < cotaMinima) {
            return "Caixa Vazio: Chame o Operador";
        }

        // Atualiza a Matriz depois do Saque

        for (int k = 0; k < cedulas.length; k++) {
            cedulas[k][1] = cedulas[k][1] - usadas[k];
        }

        // Monta a resposta
        String resposta = "===== SAQUE REALIZADO =====\n";
        resposta = resposta + "Valor sacado: R$ " + valor + ",00\n";
        resposta = resposta + "Cedulas entregues:\n";
        for (int k = 0; k < cedulas.length; k++) {
            if (usadas[k] > 0) {
                resposta = resposta + "  " + usadas[k] + " x R$ " + cedulas[k][0] + "\n";
            }
        }
        resposta = resposta + "Total de cedulas: " + totalCedulasUsadas + "\n";
        resposta = resposta + "Saldo restante no caixa: R$ " + calculaTotal() + ",00\n";

        // Acrescenta no extrato
        totalSaques++;
        extrato = extrato + "Saque #" + totalSaques + " - R$ " + valor
                + ",00 (cedulas: " + totalCedulasUsadas + ") | Saldo: R$ "
                + calculaTotal() + ",00\n";

        return resposta;
    }

    public String getExtrato() {
        String resposta = "===== EXTRATO DE OPERACOES =====\n";
        if (totalSaques == 0) {
            resposta = resposta + "Nenhum saque realizado.\n";
        } else {
            resposta = resposta + extrato;
        }
        resposta = resposta + "--------------------------------\n";
        resposta = resposta + "Total de saques: " + totalSaques + "\n";
        resposta = resposta + "Saldo final do caixa: R$ " + calculaTotal() + ",00\n";
        return resposta;
    }

    public static void main(String[] arg) {
        CaixaEletronico caixa = new CaixaEletronico();
        TelaCaixaEletronico janela = new TelaCaixaEletronico(caixa);
        janela.setVisible(true);
    }
}
