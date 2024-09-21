package com.example.gerenciamentoloja
import com.example.gerenciamentoloja.ui.theme.ProdutosGerenciamento

class Estoque {
    companion object {
        private val listaProdutos = mutableListOf<Produto>()

        fun adicionarProduto(produto: Produto): Boolean {
            if (produto.quantidade > 0 && produto.preco > 0) {
                listaProdutos.add(produto)
                ProdutosGerenciamento.listaProdutos.add(produto) // Adiciona à lista global
                return true
            }
            return false
        }

        fun calcularValorTotalEstoque(): Double {
            return listaProdutos.sumOf { it.preco * it.quantidade }
        }

        fun quantidadeTotalProdutos(): Int {
            return listaProdutos.sumOf { it.quantidade }
        }
    }
}
