package br.com.bb.compra.service.impl.pedido;

import br.com.bb.compra.model.entity.PedidoEntity;
import br.com.bb.compra.model.entity.StatusPedidoTipo;
import io.quarkus.arc.Priority;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
@Priority(1)
public class BaixaEstoque implements ProcessamentoPedido{

    @Override
    @Transactional
    public void processar(PedidoEntity pedidoEntity) {

        if (!StatusPedidoTipo.NOVO.equals(pedidoEntity.getStatus())){
            return;
        }

        pedidoEntity.getItens()
                            .forEach(item -> {
                                item.getProduto().setEstoque(item.getProduto().getEstoque() - item.getQuantidade());
                            });
    }
}
