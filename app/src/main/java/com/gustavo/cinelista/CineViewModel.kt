package com.gustavo.cinelista

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

data class Movie(val id: Int, val title: String, val year: Int, val genre: String, val synopsis: String)

class CineViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = application.getSharedPreferences("cinelista", 0)
    var loggedIn by mutableStateOf(preferences.getBoolean("logged_in", false))
        private set
    var userName by mutableStateOf(preferences.getString("user_name", "") ?: "")
        private set
    var movies by mutableStateOf(loadMovies())
        private set

    fun login(name: String) {
        userName = name.trim()
        loggedIn = true
        preferences.edit().putBoolean("logged_in", true).putString("user_name", userName).apply()
    }
    fun logout() {
        loggedIn = false
        preferences.edit().putBoolean("logged_in", false).apply()
    }
    fun removeMovie(id: Int) { movies = movies.filterNot { it.id == id }; persistMovies() }
    fun clearMovies() { movies = emptyList(); persistMovies() }
    fun restoreMovies() { movies = sampleMovies; persistMovies() }
    fun movie(id: Int) = movies.find { it.id == id }
    private fun persistMovies() {
        preferences.edit().putStringSet("movie_ids", movies.map { it.id.toString() }.toSet()).apply()
    }
    private fun loadMovies(): List<Movie> {
        val ids = preferences.getStringSet("movie_ids", null) ?: return sampleMovies
        return sampleMovies.filter { it.id.toString() in ids }
    }
    companion object {
        val sampleMovies = listOf(
            Movie(1, "Interestelar", 2014, "Ficção científica", "Uma equipe atravessa o espaço em busca de um novo lar para a humanidade, enquanto um pai tenta preservar sua conexão com a filha."),
            Movie(2, "A Viagem de Chihiro", 2001, "Animação", "Uma menina entra em um mundo de espíritos e precisa encontrar coragem para resgatar sua família."),
            Movie(3, "O Show de Truman", 1998, "Drama", "Truman começa a perceber que sua vida cotidiana esconde um segredo e decide investigar o mundo ao seu redor."),
            Movie(4, "Wall-E", 2008, "Animação", "Um pequeno robô encarregado de limpar a Terra descobre o amor e embarca em uma jornada pelo espaço."),
            Movie(5, "De Volta para o Futuro", 1985, "Aventura", "Marty viaja ao passado em uma máquina do tempo e precisa corrigir a história de sua família para voltar para casa."),
            Movie(6, "A Chegada", 2016, "Ficção científica", "Uma linguista tenta compreender a comunicação de visitantes extraterrestres e encontra uma nova maneira de perceber o tempo."),
            Movie(7, "Divertida Mente", 2015, "Animação", "As emoções de Riley aprendem a trabalhar juntas quando a menina enfrenta grandes mudanças em sua vida."),
            Movie(8, "O Grande Hotel Budapeste", 2014, "Comédia", "Um concierge e seu jovem aprendiz vivem uma aventura envolvendo uma herança, amizade e um hotel inesquecível."),
            Movie(9, "Soul", 2020, "Animação", "Um músico repensa seus sonhos e descobre valor nos pequenos momentos da vida."),
            Movie(10, "Perdido em Marte", 2015, "Ficção científica", "Um astronauta usa ciência e criatividade para sobreviver em Marte enquanto espera por um resgate."),
            Movie(11, "Ratatouille", 2007, "Animação", "Um rato apaixonado por gastronomia tenta realizar o sonho de cozinhar em um restaurante de Paris."),
            Movie(12, "O Castelo Animado", 2004, "Animação", "Sophie, transformada por um feitiço, encontra abrigo em um castelo ambulante e descobre uma nova confiança em si mesma.")
        )
    }
}
