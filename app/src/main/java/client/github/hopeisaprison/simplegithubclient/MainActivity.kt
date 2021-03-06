package client.github.hopeisaprison.simplegithubclient

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import android.widget.Toast
import client.github.hopeisaprison.simplegithubclient.adapter.RepoAdapter
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.kohsuke.github.GitHub
import java.io.IOException
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private val USERNAME_TAG = "12easd"
    private val AVATAR_URL_TAG = "asdqwe12"
    private var mAvatarUrl: String? = null

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mImageViewAvatar: CircleImageView
    private lateinit var mTextViewName: TextView

    var mGithubConnection: GitHub by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById(R.id.recyclerview_repos)
        mTextViewName = findViewById(R.id.textview_commit_name)
        mImageViewAvatar = findViewById(R.id.imageview_commit_avatar)

        mRecyclerView.layoutManager = LinearLayoutManager(this)

        bindViews(savedInstanceState?.getString(USERNAME_TAG), savedInstanceState?.getString(AVATAR_URL_TAG))

        Observable.create<Unit> {
            validateAuth()
            it.onComplete()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {
                    intent = Intent(applicationContext, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                        {
                            bindViews(mGithubConnection.myself.name, mGithubConnection.myself.avatarUrl)
                            bindAdapters()
                        })
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putString(AVATAR_URL_TAG, mAvatarUrl)
        outState?.putString(USERNAME_TAG, mTextViewName.text.toString())
    }


    private fun bindViews(name: String?, avatarUrl: String?) {

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

    fun validateAuth()  {
        val preferences = getSharedPreferences("github_prefs", Context.MODE_PRIVATE)
        val oAuthToken = preferences.getString("oauth_token", null)
        if (oAuthToken !== null) {
            mGithubConnection = GitHub.connectUsingOAuth(oAuthToken)
            mGithubConnection.isCredentialValid
        }
    }

}
