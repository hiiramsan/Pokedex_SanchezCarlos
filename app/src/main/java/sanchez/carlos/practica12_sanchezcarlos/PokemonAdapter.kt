package sanchez.carlos.practica12_sanchezcarlos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class PokemonAdapter(private val context: Context, private val pokemonList: List<Pokemon>) : BaseAdapter() {

    override fun getCount(): Int = pokemonList.size

    override fun getItem(position: Int): Any = pokemonList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_pokemon, parent, false)

        val imageView: ImageView = view.findViewById(R.id.ivPokemon)
        val textViewNum: TextView = view.findViewById(R.id.tvPokemonNum)
        val textViewName: TextView = view.findViewById(R.id.tvPokemonName)

        val pokemon = pokemonList[position]

        Glide.with(context).load(pokemon.imageUrl).into(imageView)

        textViewNum.text = "#${pokemon.num}"
        textViewName.text = "${pokemon.name}"

        return view
    }
}