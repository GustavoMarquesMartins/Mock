package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class FinalizarLeilaoServiceTest {

    private FinalizarLeilaoService service;
    @Mock
    private LeilaoDao leilaoDao;
    @Mock
    private EnviadorDeEmails enviadorDeEmails;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
        this.service = new FinalizarLeilaoService(leilaoDao, enviadorDeEmails);
    }

    @Test
    void deveriaFinaliarUmLeilao() {
        //defindo var que serao usadas para o teste
        List<Leilao> leiloes = leiloes();

        //modificando o metodo
        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);

        //chamando metodo que vai ser usado para o teste
        service.finalizarLeiloesExpirados();

        //verificando se apos o leilao ter um vencedor se ele foi fechado
        Leilao leilao = leiloes.get(0);
        Assert.assertTrue(leilao.isFechado());

        //verificando se o campeao realmente foi o que pagou o maior valor
        Assert.assertEquals(new BigDecimal("900"), leilao.getLanceVencedor().getValor());

        //verificando se o metodo salvar da classe dao foi chamado
        Mockito.verify(leilaoDao).salvar(leilao);
    }

    @Test
    void deveriaEnviarEmailParaVencedorDoLeilao() {
        //defindo var que serao usadas para o teste
        List<Leilao> leiloes = leiloes();

        //modificando o metodo
        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);

        //chamando metodo que vai ser usado para o teste
        service.finalizarLeiloesExpirados();

        //verificando se apos o leilao ter um vencedor se ele foi fechado
        Leilao leilao = leiloes.get(0);
        Lance lanceVendedor = leilao.getLanceVencedor();


        //verificando se o metodo enviar email foi enviado para o vencedor do lance
        Mockito.verify(enviadorDeEmails).enviarEmailVencedorLeilao(lanceVendedor);

    }

    @Test
    void naoDeveriaEnviarEmailParaVencedorDoLeilaoEmCasoErroAoEncerrarOLeilao() {
        //defindo var que serao usadas para o teste
        List<Leilao> leiloes = leiloes();

        //modificando o metodo
        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);

        //modificando o metodo para que ele sempre dispara uma exception
        Mockito.when(leilaoDao.salvar(Mockito.any())).thenThrow(RuntimeException.class);

        try {
            service.finalizarLeiloesExpirados();
            Mockito.verifyNoInteractions(enviadorDeEmails);
        } catch (Exception e) {

        }
    }
    private List<Leilao> leiloes() {
        List<Leilao> lista = new ArrayList<>();

        Leilao leilao = new Leilao("Celular", new BigDecimal("500"), new Usuario("Fulano"));

        Lance primeiro = new Lance(new Usuario("Beltrano"), new BigDecimal("600"));
        Lance segundo = new Lance(new Usuario("Ciclano"), new BigDecimal("900"));

        leilao.propoe(primeiro);
        leilao.propoe(segundo);

        lista.add(leilao);

        return lista;
    }
}