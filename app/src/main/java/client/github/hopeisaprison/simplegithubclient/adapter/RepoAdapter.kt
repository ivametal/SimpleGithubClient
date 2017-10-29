package client.github.hopeisaprison.simplegithubclient.adapter

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import client.github.hopeisaprison.simplegithubclient.R
import client.github.hopeisaprison.simplegithubclient.RepoActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.kohsuke.github.GitHub

/**
 * Created by hopeisaprison on 10/24/17.
 */
class RepoAdapter(private val mActivity: Activity, private val mGitHub: GitHub) : RecyclerView.Adapter<RepoAdapter.RepoHolder>() {
    val reposList = ArrayList<GitRepo>()

    init {
        RepoLoader().execute()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            RepoHolder(LayoutInflater.from(parent?.context).inflate(R.layout.new_cardview_repo, parent, false))


    override fun getItemCount(): Int = reposList.size

    override fun onBindViewHolder(holder: RepoHolder?, position: Int) {
        holder?.textViewName?.text = reposList[position].name
        holder?.textViewBrachesCount?.text = reposList[position].branchesCount.toString()
        holder?.textViewForksCount?.text = reposList[position].forksCount.toString()
        holder?.textViewDescription?.text = reposList[position].description
        Picasso.with(mActivity)
                .load(reposList[position].authorAvatarUrl)
                .into(holder?.imageViewAuthorAvatar)
        holder?.textViewWatchersCount?.text = reposList[position].watchersCount.toString()
        holder?.itemView?.setOnClickListener {
            val intent = Intent(mActivity, RepoActivity::class.java)
            intent.putExtra(RepoActivity.REPO_TAG, reposList[position].fullName)
            mActivity.startActivity(intent)
        }
//        holder?.textViewAuthorName?.text = reposList[position].authorName
    }

    class RepoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName = itemView.findViewById<TextView>(R.id.textview_repo_name)
        val textViewDescription = itemView.findViewById<TextView>(R.id.textview_repo_description)
        val textViewBrachesCount = itemView.findViewById<TextView>(R.id.textview_branches_count)
        val textViewForksCount = itemView.findViewById<TextView>(R.id.textview_forks_count)
        val imageViewAuthorAvatar = itemView.findViewById<CircleImageView>(R.id.imageview_repo_author_avatar)
        //        val textViewAuthorName = itemView.findViewById<TextView>(R.id.textview_author_name)
        val textViewWatchersCount = itemView.findViewById<TextView>(R.id.textview_watchers_count)
    }


    inner class RepoLoader : AsyncTask<Void, Void, Void?>() {
        override fun doInBackground(vararg params: Void?): Void? {
            mActivity.runOnUiThread {
                Toast.makeText(mActivity, "Loading repos", Toast.LENGTH_LONG).show()
            }
            mGitHub.myself.allRepositories.forEach {
                reposList.add(GitRepo(it.value.name,
                        it.value.description,
                        it.value.ownerName,
                        it.value.owner.avatarUrl,
                        it.value.forks,
                        it.value.branches.size,
                        it.value.watchers,
                        it.value.fullName))
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            notifyDataSetChanged()
        }

    }


    data class GitRepo(val name: String, val description: String?, val authorName: String,
                       val authorAvatarUrl: String, val forksCount: Int, val branchesCount: Int,
                       val watchersCount: Int, val fullName: String)
}