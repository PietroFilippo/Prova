package com.example.gerenciamentoloja

class Estoque {
    companion object {
        private val listaProdutos = mutableListOf<Produto>()

        fun adicionarProduto(produto: Produto): Boolean {
            if (produto.quantidade > 0 && produto.preco > 0) {
                listaProdutos.add(produto)
                return true
            }
            return false
        }

        fun calcularValorTotalEstoque(): Double {
            return listaProdutos.sumOf { it.preco * it.quantidade }
        }
    }
}