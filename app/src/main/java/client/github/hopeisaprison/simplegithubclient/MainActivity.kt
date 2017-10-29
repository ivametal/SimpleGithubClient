package client.github.hopeisaprison.simplegithubclient

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import client.github.hopeisaprison.simplegithubclient.adapter.RepoAdapter
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.kohsuke.github.GitHub
import java.io.IOException
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private val USERNAME_TAG = "12easd"
    private val AVATAR_URL_TAG = "asdqwe12"
    private var mAvatarUrl: String? = null

    private lateinit var mRecyclerView : RecyclerView
    private lateinit var mImageViewAvatar : CircleImageView
    private lateinit var mTextViewName : TextView

    var mGithubConnection: GitHub by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById(R.id.recyclerview_repos)
        mTextViewName = findViewById(R.id.textview_commit_name)
        mImageViewAvatar = findViewById(R.id.imageview_commit_avatar)

        mRecyclerView.layoutManager = LinearLayoutManager(this)

        bindViews(savedInstanceState?.getString(USERNAME_TAG), savedInstanceState?.getString(AVATAR_URL_TAG))

        AuthentificationValidator().execute()
}

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putString(AVATAR_URL_TAG, mAvatarUrl)
        outState?.putString(USERNAME_TAG, mTextViewName.text.toString())
    }

    override fun onResume() {
        super.onResume()
    }

    private fun bindViews(name : String?, avatarUrl : String?) {

        Picasso.with(this)
                .load(avatarUrl)
                .into(mImageViewAvatar)
        mAvatarUrl = avatarUrl

        mTextViewName.text = name

    }

    private fun bindAdapters() {
        mRecyclerView.adapter = RepoAdapter(this, mGithubConnection)
        mRecyclerView.isNestedScrollingEnabled = false
    }



    private inner class AuthentificationValidator : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            val preferences = getSharedPreferences("github_prefs", Context.MODE_PRIVATE)
            val oAuthToken = preferences.getString("oauth_token", null)
            return if (oAuthToken !==null)
                return  try {
                    mGithubConnection = GitHub.connectUsingOAuth(oAuthToken)
                    mGithubConnection.isCredentialValid
                }
                catch (exc : IOException) {
                    exc.printStackTrace()
                    return false
                }
            else
                false
        }

        override fun onPostExecute(result: Boolean) {
            if (!result) {
                intent = Intent(applicationContext, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                bindViews(mGithubConnection.myself.name, mGithubConnection.myself.avatarUrl)
                bindAdapters()
            }
        }

    }
}
