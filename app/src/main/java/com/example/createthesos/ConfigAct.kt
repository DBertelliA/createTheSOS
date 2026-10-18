package com.example.createthesos

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.createthesos.databinding.ActivityConfiguradoBinding
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil


//Debo de llamar a Binding para que me lo genere automaticamente y me llame al xml automaticamente


class ConfigAct : AppCompatActivity(){

    private lateinit var confB : ActivityConfiguradoBinding //Posee los componentes del xml de nombre invertido
    //la clase es una generada por el propio id
    private lateinit var shareF : SharedPreferences //SharedPreferences, es para meterse a los archivos

    private lateinit var telephoneShared : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        confB = ActivityConfiguradoBinding.inflate(layoutInflater)
        //esto infla la variable con las referencias en el layout

        setContentView(confB.root)
        //**Esto es un setContent, equivalente a la contruccion del entorno + la añadicion de la referencia con los botones**\\

        initSearchForSavedSharedPreferences()
        start()
    }

    //Nuestra intencion en principio es crear una apluicacion que almacene el numero de telefono que
    //nosotros le vayemos a pasar
    //Y teniendo eso en cuenta, debemos tambien tener en cuenta que
    //no siempre necesitamos pasarselo, porque lo puede ya tener almacenado

    //funcion que sirve para la busqueda de un archivo y la comprbacion de algo dentro
    private fun initSearchForSavedSharedPreferences(){
        val nameSharedFichero = getString(R.string.name_preference_share_fich)
        this.telephoneShared = getString(R.string.configura_el_numero_de_telefono)

        this.shareF = getSharedPreferences(nameSharedFichero, Context.MODE_PRIVATE) //El private mode provoca que el fichero que se ve a a mirar va a estar solo disponible apra la aplicacion
    }

    private fun start(){
        var sharedPhone : String? = shareF.getString(telephoneShared,null)

        sharedPhone?.let{
            //Un metodo para iniciar la actividad que actua como return
            startMainAct(it)
        }.run{
            var nameShareFich = getString(R.string.name_preference_share_fich)
            var namePhoneSH = getString(R.string.phone_string)

            var mod = getSharedPreferences(nameShareFich, Context.MODE_PRIVATE)
            mod.edit().remove(namePhoneSH).apply()
        }

        confB.button.setOnClickListener {
            val phoneN = confB.editPhoneText.text.toString()

            if (phoneN.isEmpty()){
                Toast.makeText(this, "Esta vacío, que haces", Toast.LENGTH_LONG).show()

            }else{
                if (!(isValidPhoneNumber2(phoneN, "ES"))) {
                    Toast.makeText(this,"Oye, que no es valido", Toast.LENGTH_LONG).show()
                }else{
                    val edit = shareF.edit().putString(telephoneShared, phoneN)
                    edit.apply()

                    //mismo metodo en la zona let
                    startMainAct(phoneN)
                }
            }

        }

    }

    //onResume, es un metodo que nosotros como programadores no llamamamos, sino que andoird lo hace por nosotros cada
    //que la aplicacion se detiene
    //Su funcion es que cada que se ejecuta, dependiendo de si es true o false se elimina el contenido o no (numero de telefono (vacio))

    override fun onResume() {
        super.onResume()
        var retenedor = intent.getBooleanExtra("back", false)
        //Genera una variable que no iniciada sea false, y que la palabra a la que se haga referencia para el valor boolean sea back

        if (retenedor){
            confB.editPhoneText.setText("")
            Toast.makeText(this, R.string.mensaje_tostador_phone, Toast.LENGTH_LONG).show() //mensajes que creas tu en el string para ser llamados por el tostador dependiendo de lo que necesites

            intent.removeExtra("back") //Elimina el true del retenedor
        }
    }

    private fun startMainAct(phone : String){
        val intent = Intent(this, MainActivity::class.java)
        intent.apply {
            putExtra(getString(R.string.phone_string), phone) //Hey, pon en esta variable, el siguiente telefono con ese id

            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP) //esto limpia las capas duplicadas
        }
        startActivity(intent) //Empieza la actividad que se creará en esa variable a partir del this
    }



    //metodo para valida telefonos: solo CopyPaste
    fun isValidPhoneNumber2(phoneNumber: String, countryCode: String): Boolean {
        val phoneUtil = PhoneNumberUtil.getInstance()
        return try {
            // Parseamos el número de teléfono en base al código de país proporcionado
            val number = phoneUtil.parse(phoneNumber, countryCode)
            // Verificamos si es un número válido para el país especificado
            phoneUtil.isValidNumber(number)
        } catch (e: NumberParseException) {
            e.printStackTrace()
            false
        }
    }

    override fun onNewIntent(intent: Intent) {
        //Metodo que como el resume, es llamado automaticamente por android, y seta automaticamente
        //los nuevos intents, como los antiguos
        super.onNewIntent(intent)
        setIntent(intent)
    }


}