package sanchez.carlos.practica12_sanchezcarlos

import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    val REQUEST_IMAGE_GET = 1
    val CLOUD_NAME = "dvznvnzam"
    val UPLOAD_PRESET = "pokemon-preset"
    var imageUri: Uri? = null

    private val pokemonsRef = FirebaseDatabase.getInstance().getReference("pokemons")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        initCloudinary()

        val name : EditText = findViewById(R.id.etName)
        val num : EditText = findViewById(R.id.etNum)
        val upload : Button = findViewById(R.id.btnUpload)
        val save : Button = findViewById(R.id.btnGuardar)
        //val thumbnail : ImageView = findViewById(R.id.ivPreview)


        upload.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }

        save.setOnClickListener {
            savePokemon()
        }


    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            val fullPhotoUri: Uri? = data?.data

            if(fullPhotoUri != null) {
                imageUri = fullPhotoUri
                changeImage(fullPhotoUri)
            }
        }
    }

    fun changeImage(uri: Uri){
        var thumbnail: ImageView = findViewById(R.id.ivPreview)
        try{
            thumbnail.setImageURI(uri)
        }catch(e:Exception){
            e.printStackTrace()
        }
    }


    fun initCloudinary() {
        val config: MutableMap<String, String> = HashMap<String, String>()
        config["cloud_name"]= CLOUD_NAME
        MediaManager.init(this, config)
    }

    fun savePokemon() {
        if (imageUri != null) {
            MediaManager.get().upload(imageUri).unsigned(UPLOAD_PRESET).callback(object :
                UploadCallback {
                override fun onStart(requestId: String) {}

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as String? ?: ""
                    Log.d("Cloudinary", "Upload success: $url")

                    val name: String = findViewById<EditText>(R.id.etName).text.toString()
                    val num: String = findViewById<EditText>(R.id.etNum).text.toString()

                    if (name.isNotEmpty() && num.isNotEmpty()) {
                        val pokemon = mapOf(
                            "num" to num,
                            "name" to name,
                            "imageUrl" to url
                        )
                        pokemonsRef.child(num).setValue(pokemon)
                            .addOnSuccessListener {
                                Log.d("Firebase", "Pokemon saved successfully")
                                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firebase", "Error saving pokemon", e)
                            }
                    } else {
                        Log.e("Validation", "Name or num is empty")
                    }
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    Log.d("Cloudinary", "Upload failed: ${error.description}")
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {}

            }).dispatch()
        } else {
            Log.e("Image", "No image selected")
        }
    }


}