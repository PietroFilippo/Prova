package com.example.gerenciamentoloja

import android.os.Bundle
import com.google.gson.Gson
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gerenciamentoloja.ui.theme.GerenciamentoLojaTheme
import com.example.gerenciamentoloja.ui.theme.ProdutosGerenciamento

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "cadastro") {
        composable("cadastro") { TelaCadastro(navController) }
        composable("lista") { TelaListaProdutos(navController) }
        composable("detalhes/{produtoJson}") { backStackEntry ->
            val produtoJson = backStackEntry.arguments?.getString("produtoJson")
            if (produtoJson != null) {
                TelaDetalhesProduto(produtoJson, navController)
            } else {
                Text("Produto não encontrado.")
            }
        }
    }
}

@Composable
fun ProdutoItem(produto: Produto, navController: NavController) {
    val gson = Gson()
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
                val produtoJson = gson.toJson(produto)
                navController.navigate("detalhes/$produtoJson")
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

                if (precoDouble != null && quantidadeInt != null && precoDouble >= 0 && quantidadeInt >= 0) {
                    val novoProduto = Produto(nome, categoria, precoDouble, quantidadeInt)
                    ProdutosGerenciamento.listaProdutos.add(novoProduto)

                    // Limpar campos após o cadastro
                    nome = ""
                    categoria = ""
                    preco = ""
                    quantidade = ""

                    Toast.makeText(context, "Produto cadastrado com sucesso!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Preço e Quantidade devem ser numéricos e maiores ou iguais a zero", Toast.LENGTH_LONG).show()
                }
            }
        },
            modifier = Modifier.fillMaxWidth()) {
            Text("Cadastrar")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { navController.navigate("lista") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Lista de Produtos")
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

        Spacer(modifier = Modifier.height(16.dp))

        // Botão para voltar à tela de cadastro
        Button(onClick = { navController.navigate("cadastro") }) {
            Text("Voltar para Cadastro")
        }
    }
}

@Composable
fun TelaDetalhesProduto(produtoJson: String, navController: NavController) {
    val gson = Gson()
    val produto = gson.fromJson(produtoJson, Produto::class.java)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Nome: ${produto.nome}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Categoria: ${produto.categoria}", fontSize = 20.sp)
        Text("Preço: R$ ${produto.preco}", fontSize = 20.sp)
        Text("Quantidade em Estoque: ${produto.quantidade} unidades", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("lista") }) {
            Text("Voltar para Lista")
        }
    }
}