package client.github.hopeisaprison.simplegithubclient

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import client.github.hopeisaprison.simplegithubclient.oauth.GithubOauth

class AuthActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val buttonLogin = findViewById<Button>(R.id.button_login)
        buttonLogin.setOnClickListener {
            Log.d("Package", packageName)
            GithubOauth.Builder()
                    .withClientId("d21dba5a929a3d2a81f4")
                    .withClientSecret("bd7223307db5816cc2188e6df98332a49848944a")
                    .withContext(this@AuthActivity)
                    .packageName(packageName)
                    .nextActivity(packageName + ".MainActivity")
                    .debug(true)
                    .execute()

        }
    }
}