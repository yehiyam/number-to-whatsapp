package xyz.livnehiot.numtowhatsapp

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import com.google.i18n.phonenumbers.PhoneNumberUtil
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendButton.setOnClickListener{
            this.sendToWhatsapp(phone_number.text.toString())
        }
        this.setTitle("Number To WhatsApp")
        val receivedAction = intent.action
        val receivedType = intent.type
        if (receivedAction==Intent.ACTION_SEND){
            try {
                if (receivedType == null || !receivedType.startsWith("text/")) return
                //handle sent text
                var text = intent.getStringExtra(Intent.EXTRA_TEXT)
//            val isValidNumber = android.util.Patterns.PHONE.matcher(text).matches()
//            if (!isValidNumber) return
                this.sendToWhatsapp((text))
            }
            catch (e: Exception){
                Toast.makeText(this,"Error: "+e.message, Toast.LENGTH_LONG)
            }

        }
        else {

        }
    }

    fun getPhoneCountry(): String {
        val tm = ContextCompat.getSystemService(this, TelephonyManager::class.java)
        return if (tm != null && !TextUtils.isEmpty(tm.networkCountryIso)) {
            tm.networkCountryIso.toUpperCase(Locale.US)
        } else {
            Locale.getDefault().getCountry()
        }
    }

    fun sendToWhatsapp(inputText: String) {
        var text = inputText.replace("-","")
        val phoneUtil = PhoneNumberUtil.getInstance()
        var formattedNumber=text
        try {
            var phoneNumber = phoneUtil.parse(inputText,this.getPhoneCountry())
            formattedNumber=phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164)
            phone_number.setText(formattedNumber)

        } catch (e: Exception) {
            phone_number.setText(text)
            Toast.makeText(this,e.message,Toast.LENGTH_LONG)
            return
        }
        formattedNumber = formattedNumber.removePrefix("+")
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            component = ComponentName("com.whatsapp","com.whatsapp.Conversation")
            type="text/plain"
            putExtra(Intent.EXTRA_TEXT, "")
            putExtra("jid", formattedNumber+"@s.whatsapp.net")
            `package`="com.whatsapp"
        }
        startActivity(sendIntent)
        this.finish()
    }
}
