package client.github.hopeisaprison.simplegithubclient

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import client.github.hopeisaprison.simplegithubclient.adapter.CommitsAdapter
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.kohsuke.github.GHBranch
import org.kohsuke.github.GitHub
import org.reactivestreams.Subscriber
import java.io.IOException
import kotlin.properties.Delegates


class RepoActivity : Activity() {

    private val mBranchesList = ArrayList<GHBranch>()

    var mGithubConnection: GitHub by Delegates.notNull()

    companion object {
        val REPO_TAG = "reposa1"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repo)
        Observable.create<Unit> {
            loadSpinner()
            it.onComplete()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {
                    Toast.makeText(this@RepoActivity, "Connection or login failed",
                            Toast.LENGTH_SHORT).show()
                },
                        {
                            bindSpinner()
                        })
    }

    private fun bindRecycler(branch: GHBranch) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_commits)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CommitsAdapter(this)
        recyclerView.addOnScrollListener(CommitsAdapter.OnScrollDataLoader(recyclerView, branch))

    }

    private fun bindSpinner() {
        val spinner = findViewById<Spinner>(R.id.spinner_branches)
        val array = arrayOfNulls<String>(mBranchesList.size)
        for (i in 0 until mBranchesList.size)
            array[i] = mBranchesList[i].name
        spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                bindRecycler(mBranchesList[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }


    private fun loadSpinner() {
        val preferences = getSharedPreferences("github_prefs", Context.MODE_PRIVATE)
        val oAuthToken = preferences.getString("oauth_token", null)
        if (oAuthToken !== null) {
            mGithubConnection = GitHub.connectUsingOAuth(oAuthToken)
            if (mGithubConnection.isCredentialValid) {
                val repo = mGithubConnection.getRepository(intent.getStringExtra(REPO_TAG))
                val branches = repo.branches
                branches.forEach {
                    mBranchesList.add(it.value)
                }
            }
        }
    }


}