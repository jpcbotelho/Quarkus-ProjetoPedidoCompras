package br.com.bb.compra.service.impl;

import br.com.bb.compra.model.Produto;
import br.com.bb.compra.model.entity.*;
import br.com.bb.compra.model.PedidoRequestDto;
import br.com.bb.compra.model.PedidoResponseDto;
import br.com.bb.compra.repository.ClienteRepository;
import br.com.bb.compra.repository.ProdutoRepository;
import br.com.bb.compra.service.PedidoService;
import br.com.bb.compra.service.impl.pedido.ProcessamentoPedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PedidoServiceImpl implements PedidoService {

    private final JsonWebToken accessToken;
    private final ClienteRepository clienteRepository;

    private final List<ProcessamentoPedido> processamentoPedidos;

    @Override
    @Transactional
    public PedidoResponseDto realizarPedido(PedidoRequestDto pedidoDto) {
        final String email = accessToken.getSubject();
        final ClienteEntity entity = clienteRepository.findByEmail(email);

        final PedidoEntity pedidoEntity = criarPedido(entity, pedidoDto);

        pedidoEntity.persist();

        processamentoPedidos.forEach(p -> p.processar(pedidoEntity));

        log.info("O usuario {} iniciou pedido {}", email, pedidoDto);
        return new PedidoResponseDto(pedidoEntity.id);
    }

    private PedidoEntity criarPedido(ClienteEntity cliente, PedidoRequestDto pedidoDto) {
        PedidoEntity pedido = new PedidoEntity();
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedidoTipo.NOVO);
        final Set<ItemPedidoEntity> pedidoEntities = pedidoDto.getItens().stream()
                .map(dto -> {
                    final ProdutoEntity produto = ProdutoEntity.findById(dto.getProdutoId());
                    ItemPedidoEntity itemPedido = new ItemPedidoEntity();
                    itemPedido.setProduto(produto);
                    itemPedido.setDesconto(produto.getDesconto());
                    itemPedido.setPreco(produto.getPreco());
                    itemPedido.setQuantidade(dto.getQuantidade());
                    itemPedido.setPedido(pedido);
                    return itemPedido;
                }).collect(Collectors.toCollection(HashSet::new));
        pedido.setItens(pedidoEntities);
        return pedido;
    }

}
