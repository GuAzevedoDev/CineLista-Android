package com.gustavo.cinelista

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.*
import androidx.navigation.toRoute
import com.gustavo.cinelista.ui.theme.CineListaTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { CineListaTheme { CineApp() } }
    }
}

@Composable
fun CineApp() {
    // A única criação do ViewModel: todas as telas recebem esta mesma instância.
    val vm: CineViewModel = viewModel()
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val destination = entry?.destination
    val mainScreen = destination?.let {
        it.hasRoute<Route.Home>() || it.hasRoute<Route.Movies>() ||
            it.hasRoute<Route.Profile>() || it.hasRoute<Route.Detail>()
    } == true
    Scaffold(bottomBar = {
        if (mainScreen) NavigationBar {
            val tabs = listOf(
                Triple<Route, String, androidx.compose.ui.graphics.vector.ImageVector>(Route.Home, "Início", Icons.Default.Home),
                Triple<Route, String, androidx.compose.ui.graphics.vector.ImageVector>(Route.Movies, "Filmes", Icons.Default.Movie),
                Triple<Route, String, androidx.compose.ui.graphics.vector.ImageVector>(Route.Profile, "Perfil", Icons.Default.Person)
            )
            tabs.forEach { (route, label, icon) ->
                val selected = when (route) {
                    Route.Home -> destination?.hasRoute<Route.Home>() == true
                    Route.Movies -> destination?.hasRoute<Route.Movies>() == true || destination?.hasRoute<Route.Detail>() == true
                    Route.Profile -> destination?.hasRoute<Route.Profile>() == true
                    else -> false
                }
                NavigationBarItem(selected = selected, onClick = {
                    nav.navigate(route) {
                        popUpTo<Route.Home> { saveState = true }
                        restoreState = true
                        launchSingleTop = true
                    }
                }, icon = { Icon(icon, contentDescription = null) }, label = { Text(label) })
            }
        }
    }) { padding ->
        NavHost(navController = nav, startDestination = Route.Splash, modifier = Modifier.padding(padding)) {
            composable<Route.Splash> {
                SplashScreen(vm) {
                    // O hospedeiro decide o destino; Splash apenas emite a conclusão.
                    val target: Route = if (vm.loggedIn) Route.Home else Route.Login
                    nav.navigate(target) { popUpTo<Route.Splash> { inclusive = true } }
                }
            }
            composable<Route.Login> {
                LoginScreen(vm) { name ->
                    vm.login(name)
                    nav.navigate(Route.Home) { popUpTo<Route.Login> { inclusive = true } }
                }
            }
            composable<Route.Home> { HomeScreen(vm) { nav.navigate(Route.Movies) { popUpTo<Route.Home> { saveState = true }; restoreState = true; launchSingleTop = true } } }
            composable<Route.Movies> { MoviesScreen(vm) { id -> nav.navigate(Route.Detail(id)) } }
            composable<Route.Profile> {
                ProfileScreen(vm) {
                    vm.logout()
                    nav.navigate(Route.Login) { popUpTo<Route.Home> { inclusive = true } }
                }
            }
            composable<Route.Detail> { detailEntry ->
                val route = detailEntry.toRoute<Route.Detail>()
                DetailScreen(vm, route.movieId) { nav.popBackStack() }
            }
        }
    }
}

@Composable
fun SplashScreen(vm: CineViewModel, onFinished: () -> Unit) {
    val latestFinished by rememberUpdatedState(onFinished)
    LaunchedEffect(Unit) { delay(1200); latestFinished() }
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
        Text("CineLista", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
        Text("Seu próximo favorito começa aqui.", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp))
        CircularProgressIndicator()
    }
}

@Composable
fun LoginScreen(vm: CineViewModel, onLogin: (String) -> Unit) {
    var name by rememberSaveable { mutableStateOf(vm.userName) }
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Uma lista com a sua cara", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text("Entre com seu nome para organizar seus filmes favoritos. Esta é uma sessão local de demonstração, sem senha ou servidor.")
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Seu nome") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onLogin(name) }, enabled = name.isNotBlank(), modifier = Modifier.fillMaxWidth()) { Text("Entrar no CineLista") }
    }
}

@Composable
fun HomeScreen(vm: CineViewModel, onMovies: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text("Olá, ${vm.userName}!", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text("Toda boa história merece ser lembrada.", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("${vm.movies.size} filmes na sua coleção", style = MaterialTheme.typography.headlineSmall)
                Text("Explore o catálogo e abra um filme para conhecer sua história.")
                Button(onClick = onMovies) { Text("Ver meus filmes") }
            }
        }
        Text("Dica de cinema", style = MaterialTheme.typography.titleLarge)
        Text("Revisite um favorito ou experimente um gênero diferente. A próxima grande descoberta pode estar na sua lista.")
    }
}

@Composable
fun MoviesScreen(vm: CineViewModel, onDetail: (Int) -> Unit) {
    // rememberLazyListState usa um Saver; o NavController salva a posição ao trocar abas.
    val listState = rememberLazyListState()
    Column(Modifier.fillMaxSize()) {
        Text("Meus filmes", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(24.dp))
        if (vm.movies.isEmpty()) {
            Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(Icons.Default.Movie, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
                Text("Sua lista está vazia", style = MaterialTheme.typography.headlineSmall)
                Text("Restaure o catálogo para começar a explorar.")
                Spacer(Modifier.height(16.dp))
                Button(onClick = vm::restoreMovies) { Text("Restaurar filmes") }
            }
        } else {
            LazyColumn(state = listState, contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(vm.movies, key = { it.id }) { movie ->
                    Card(onClick = { onDetail(movie.id) }, modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Movie, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(movie.title, style = MaterialTheme.typography.titleMedium)
                                Text("${movie.year} • ${movie.genre}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { vm.removeMovie(movie.id) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Remover ${movie.title} da lista")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(vm: CineViewModel, movieId: Int, onBack: () -> Unit) {
    val movie = vm.movie(movieId)
    Column(Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Detalhes do filme") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar para a lista de filmes") }
        })
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            if (movie == null) {
                Text("Filme não encontrado", style = MaterialTheme.typography.headlineMedium)
                Text("Este filme foi removido da sua coleção.")
                Button(onClick = onBack) { Text("Voltar à lista") }
            } else {
                Icon(Icons.Default.LocalMovies, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(80.dp))
                Text(movie.title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                SuggestionChip(onClick = {}, label = { Text("${movie.year} • ${movie.genre}") })
                Text("Sinopse", style = MaterialTheme.typography.titleLarge)
                Text(movie.synopsis, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun ProfileScreen(vm: CineViewModel, onLogout: () -> Unit) {
    var confirmClear by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text("Meu perfil", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text(vm.userName, style = MaterialTheme.typography.titleLarge)
        Text("${vm.movies.size} favoritos salvos neste dispositivo.")
        OutlinedButton(onClick = { confirmClear = true }, enabled = vm.movies.isNotEmpty()) { Text("Esvaziar lista") }
        OutlinedButton(onClick = vm::restoreMovies) { Text("Restaurar catálogo de exemplo") }
        Spacer(Modifier.weight(1f))
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Sair") }
    }
    if (confirmClear) AlertDialog(onDismissRequest = { confirmClear = false }, title = { Text("Esvaziar a lista?") },
        text = { Text("Você poderá restaurar o catálogo de exemplo a qualquer momento.") },
        confirmButton = { TextButton(onClick = { vm.clearMovies(); confirmClear = false }) { Text("Esvaziar") } },
        dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("Cancelar") } })
}
