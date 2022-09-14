package br.com.bb.compra.model.entity;

import br.com.bb.compra.model.entity.ClienteEntity;
import br.com.bb.compra.model.entity.ItemPedidoEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
public class PedidoEntity extends PanacheEntity {

    @ManyToOne
    private ClienteEntity cliente;

    // mappedBy refere-se ao nome da variavel pedido na classe ItemPedidoEntity
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Set<ItemPedidoEntity> itens;

    @Enumerated(EnumType.STRING)
    private StatusPedidoTipo status;

    private String mensagemStatus;
}
