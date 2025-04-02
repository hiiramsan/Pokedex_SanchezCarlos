package sanchez.carlos.practica12_sanchezcarlos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {


    private lateinit var listView: ListView
    private lateinit var pokemonsRef: DatabaseReference
    private val pokemonList = mutableListOf<Pokemon>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnAgregar : Button = findViewById(R.id.btnAdd)

        btnAgregar.setOnClickListener{
            val intent : Intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        listView = findViewById(R.id.listView)

        pokemonsRef = FirebaseDatabase.getInstance().getReference("pokemons")
        cargarPokemons()

    }

    private fun cargarPokemons() {
        pokemonsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pokemonList.clear()

                for (pokemonSnapshot in snapshot.children) {
                    val pokemon = pokemonSnapshot.getValue(Pokemon::class.java)
                    if(pokemon!=null) {
                        pokemonList.add(pokemon)
                    }

                }

                val adapter = PokemonAdapter(this@MainActivity, pokemonList)
                listView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error cargando pokemos", error.toException())
            }
        })
    }
}