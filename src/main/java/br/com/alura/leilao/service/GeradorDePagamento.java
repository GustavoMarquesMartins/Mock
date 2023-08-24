package br.com.alura.leilao.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Pagamento;

@Service
public class GeradorDePagamento {

    private PagamentoDao pagamentos;
    private Clock clock;

    @Autowired
    public GeradorDePagamento(PagamentoDao pagamentos, Clock clock) {
        this.pagamentos = pagamentos;
        this.clock = clock;
    }

    public void gerarPagamento(Lance LanceVencedor) {
        LocalDate vencimento = LocalDate.now(clock).plusDays(1);
        Pagamento pagamento = new Pagamento(LanceVencedor, proximoDiaUtil(vencimento));
        this.pagamentos.salvar(pagamento);
    }

    private LocalDate proximoDiaUtil(LocalDate dataBase) {
        DayOfWeek diaDaSemana = dataBase.getDayOfWeek();
        if (diaDaSemana == DayOfWeek.SATURDAY) {
            return dataBase.plusDays(2);
        } else if (diaDaSemana == DayOfWeek.SUNDAY) {
            return dataBase.plusDays(1);
        }

        return dataBase;
    }
}
