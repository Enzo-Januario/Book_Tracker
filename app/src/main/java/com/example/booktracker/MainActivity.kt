package com.example.booktracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.example.booktracker.databinding.ActivityMainBinding
import com.example.booktracker.ui.add.AddBookActivity
import com.example.booktracker.ui.main.HomeFragment
import com.example.booktracker.ui.search.SearchFragment
import com.example.booktracker.ui.stats.StatsFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // ⭐ ADICIONE ESTA LINHA ANTES DO super.onCreate
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            setSupportActionBar(binding.toolbar)

            // Carregar fragment inicial
            if (savedInstanceState == null) {
                loadFragment(HomeFragment())
            }

            setupFab()
        } catch (e: Exception) {
            Log.e("MainActivity", "Erro ao criar MainActivity", e)
            Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupFab() {
        binding.fabAddBook.setOnClickListener {
            try {
                showAddBookOptions()
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro ao abrir opções", e)
                Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showAddBookOptions() {
        val options = arrayOf("Cadastro Manual", "Buscar Online")

        MaterialAlertDialogBuilder(this)
            .setTitle("Adicionar Livro")
            .setItems(options) { _, which ->
                try {
                    when (which) {
                        0 -> {
                            // Cadastro manual
                            val intent = Intent(this, AddBookActivity::class.java)
                            startActivity(intent)
                        }
                        1 -> {
                            // Buscar online
                            loadFragment(SearchFragment())
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Erro ao navegar", e)
                    Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
            .show()
    }

    private fun loadFragment(fragment: Fragment) {
        try {
            currentFragment = fragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        } catch (e: Exception) {
            Log.e("MainActivity", "Erro ao carregar fragment", e)
            Toast.makeText(this, "Erro ao carregar tela: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                loadFragment(HomeFragment())
                true
            }
            R.id.action_search -> {
                loadFragment(SearchFragment())
                true
            }
            R.id.action_stats -> {
                loadFragment(StatsFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        // Recarregar fragment atual para atualizar dados
        currentFragment?.let {
            if (it is HomeFragment) {
                loadFragment(HomeFragment())
            }
        }
    }
}