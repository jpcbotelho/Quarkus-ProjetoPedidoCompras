package br.com.bb.compra.service.impl.pedido;

import br.com.bb.compra.model.entity.PedidoEntity;
import br.com.bb.compra.model.entity.ProdutoEntity;
import br.com.bb.compra.model.entity.StatusPedidoTipo;
import io.quarkus.arc.Priority;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import static java.lang.String.format;

@ApplicationScoped
@Priority(2)
public class ValidaEstoque implements ProcessamentoPedido{

    private static final String MENSAGEM_STATUS_CANCELADO = "Quantidade do produto %s não disponível";
    @Override
    @Transactional
    public void processar(PedidoEntity pedidoEntity) {
        pedidoEntity.getItens().forEach(item -> {
           final ProdutoEntity produto = item.getProduto();
           if(produto.getEstoque() < item.getQuantidade()){
               pedidoEntity.setStatus(StatusPedidoTipo.CANCELADO);
               pedidoEntity.setMensagemStatus(format(MENSAGEM_STATUS_CANCELADO, produto.getNome()));
           }
        });
    }
}
