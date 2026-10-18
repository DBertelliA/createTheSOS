package com.example.createthesos

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.createthesos.databinding.ActivityPrincipalBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainContainer : ActivityPrincipalBinding

    private var phoneSOS : String? = null

    private lateinit var requestPermissionLauncher : ActivityResultLauncher<String> //Esto es para lanzar los permisos

    private var alreadyPermitted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainContainer = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(mainContainer.root)

        enableEdgeToEdge()
        init()
        initEventCall()
    }

    override fun onResume() {
        super.onResume()

        alreadyPermitted = isPermissionCall()
        val stringPhone = getString(R.string.phone_string) //Este es para que obtenga el id
        phoneSOS = intent.getStringExtra(stringPhone)
        mainContainer.numberHolder.setText(phoneSOS)
    }

    private fun init(){
        registerLauncher()
        if (!isPermissionCall()){
            requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }

        mainContainer.changePhone.setOnClickListener {
//            var nameShareFich = getString(R.string.name_preference_share_fich)
//            var namePhoneSH = getString(R.string.phone_string)
//
//            var mod = getSharedPreferences(nameShareFich, Context.MODE_PRIVATE)
//            mod.edit().remove(namePhoneSH).apply()
            //esto de arriba no deberia estar aqui
            val intent = Intent(this, ConfigAct::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                putExtra("back", true) //Volvemos al activity anterior con el valor este a true
            }
            startActivity(intent)
        }

    }


    //lo de los launcher
    //Y los permisos es una locura
    private fun registerLauncher(){
        //el launch lanza lo que has construido aqui!!!
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted ->
                if(isGranted){
                    alreadyPermitted = true
                }
                else{
                    Toast.makeText(this, "Necesitas habilitar los permisos", Toast.LENGTH_LONG).show()
                    goToConfApp()
                }
        }

    }

    private fun isPermissionToUser() = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED

    private fun isPermissionCall():Boolean{
        //Debido a un error muy gordo de los desarrolladores de android, si la API es inferior al 23, los permisos se aceptan automaticamente
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true
        }else{
            return isPermissionToUser()
        }
    }

    //Contructor para accionar la llamada o oreguntar por permisos
    private fun initEventCall(){
        mainContainer.buttonCall.setOnClickListener {
            alreadyPermitted = isPermissionCall()
            if (alreadyPermitted){
                call()
            }else{
                requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        }
    }

    //Los intents se usan para abrir otras cosas... tenerlo muy en cuenta

    private fun call(){
        //Dependiendo de que intent le tengamos que poner le tendremos que poner un Uri diferente
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:"+phoneSOS!!)
        }
        startActivity(intent)
    }

    private fun goToConfApp(){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Actualiza el Intent con los nuevos extras
    }

}