package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeradorDePagamentoTest {

    private GeradorDePagamento geradorDePagamento;
    @Mock
    private PagamentoDao pagamentoDao;

    @Captor
    private ArgumentCaptor<Pagamento> captor;

    @Mock
    private Clock clock;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
        this.geradorDePagamento = new GeradorDePagamento(pagamentoDao, clock);
    }

    @Test
    void deveriaCriarPagamentoParaVencedorDoLeilao() {

        Leilao leilao = leilao();


        Lance lanceVencedor = leilao.getLanceVencedor();

        LocalDate data = LocalDate.of(2023, 8, 24);
        Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();

        Mockito.when(clock.instant()).thenReturn(instant);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        geradorDePagamento.gerarPagamento(lanceVencedor);

        Mockito.verify(pagamentoDao).salvar(captor.capture());

        Pagamento pagamento = captor.getValue();

        Assert.assertEquals(data.plusDays(1), pagamento.getVencimento());
        Assert.assertEquals(lanceVencedor.getValor(), pagamento.getValor());
        Assert.assertFalse(pagamento.getPago());
        Assert.assertEquals(lanceVencedor.getUsuario(), pagamento.getUsuario());
        Assert.assertEquals(leilao, pagamento.getLeilao());

    }

    @Test
    void quantoDataDeVencimentoForCairNoSabadoDeveriaSerPassadoParaPrimeiroDiaUtil() {

        Leilao leilao = leilao();
        Lance lanceVencedor = leilao.getLanceVencedor();


        LocalDate data = LocalDate.of(2023, 8, 24);
        data = data.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));

        Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();

        Mockito.when(clock.instant()).thenReturn(instant);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        geradorDePagamento.gerarPagamento(lanceVencedor);

        Mockito.verify(pagamentoDao).salvar(captor.capture());
        Pagamento pagamento = captor.getValue();

        Assert.assertEquals(DayOfWeek.MONDAY, pagamento.getVencimento().getDayOfWeek());

    }

    @Test
    void quantoDataDeVencimentoForCairNoDomingoDeveriaSerPassadoParaPrimeiroDiaUtil() {

        Leilao leilao = leilao();
        Lance lanceVencedor = leilao.getLanceVencedor();


        LocalDate data = LocalDate.of(2023, 8, 24);
        data = data.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));

        Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();

        Mockito.when(clock.instant()).thenReturn(instant);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        geradorDePagamento.gerarPagamento(lanceVencedor);

        Mockito.verify(pagamentoDao).salvar(captor.capture());
        Pagamento pagamento = captor.getValue();

        Assert.assertEquals(DayOfWeek.MONDAY, pagamento.getVencimento().getDayOfWeek());

    }

    private Leilao leilao() {
        Leilao leilao = new Leilao("Celular", new BigDecimal("500"), new Usuario("Fulano"));

        Lance lance = new Lance(new Usuario("Ciclano"), new BigDecimal("900"));

        leilao.propoe(lance);
        leilao.setLanceVencedor(lance);

        return leilao;
    }

}