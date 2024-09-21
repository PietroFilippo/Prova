package com.example.gerenciamentoloja

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gerenciamentoloja.ui.theme.GerenciamentoLojaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}

data class Produto(
    val nome: String,
    val categoria: String,
    val preco: Double,
    val quantidade: Int
)


object ProdutosGerenciamento {
    val listaProdutos = mutableStateListOf<Produto>()
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "cadastro") {
        composable("cadastro") { TelaCadastro(navController) }
        composable("lista") { TelaListaProdutos(navController) }
        composable("detalhes/{produtoIndex}") { backStackEntry ->
            val produtoIndex = backStackEntry.arguments?.getString("produtoIndex")?.toInt()
            if (produtoIndex != null) {
                TelaDetalhesProduto(produtoIndex)
            }
        }
    }
}

@Composable
fun TelaListaProdutos(navController: NavController) {
    val listaProdutos = remember { ProdutosGerenciamento.listaProdutos }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Lista de Produtos", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        if (listaProdutos.isEmpty()) {
            Text("Nenhum produto cadastrado.")
        } else {
            LazyColumn {
                items(listaProdutos) { produto ->
                    ProdutoItem(produto, navController)
                }
            }
        }
    }
}

@Composable
fun ProdutoItem(produto: Produto, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${produto.nome} (${produto.quantidade} unidades)")
            Button(onClick = {
                val index = ProdutosGerenciamento.listaProdutos.indexOf(produto)
                navController.navigate("detalhes/$index")
            }) {
                Text("Detalhes")
            }
        }
    }
}

@Composable
fun TelaCadastro(navController: NavController) {

    var nome by remember { mutableStateOf("")}
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        Text("Cadastro de Produto", fontSize = 25.sp, modifier = Modifier.padding(bottom = 16.dp))

        // Campo de Nome
        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome do Produto") },
            modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(10.dp))

        // Campo de Categoria
        TextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoria") },
            modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = preco,
            onValueChange = { preco = it },
            label = { Text("Preço") },
            modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = quantidade,
            onValueChange = { quantidade = it },
            label = { Text("Quantidade em Estoque") },
            modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = {
                if (nome.isBlank() || categoria.isBlank() || preco.isBlank() || quantidade.isBlank()) {
                    Toast.makeText(context, "Todos os campos são obrigatórios", Toast.LENGTH_LONG).show()
                } else {
                    val precoDouble = preco.toDoubleOrNull()
                    val quantidadeInt = quantidade.toIntOrNull()

                    if (precoDouble != null && quantidadeInt != null) {
                        val novoProduto = Produto(nome, categoria, precoDouble, quantidadeInt)
                        ProdutosGerenciamento.listaProdutos.add(novoProduto)

                        nome = ""
                        categoria = ""
                        preco = ""
                        quantidade = ""

                        Toast.makeText(context, "Produto cadastrado com sucesso!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Preço e Quantidade devem ser numéricos", Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar")
        }
    }
}

@Composable
fun TelaDetalhesProduto(produtoIndex: Int) {
    val produto = ProdutosGerenciamento.listaProdutos.getOrNull(produtoIndex)
    if (produto != null) {
        Text("Detalhes do Produto: ${produto.nome}")
    } else {
        Text("Produto não encontrado.")
    }
}