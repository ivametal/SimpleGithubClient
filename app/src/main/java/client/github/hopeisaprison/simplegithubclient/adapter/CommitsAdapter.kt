package client.github.hopeisaprison.simplegithubclient.adapter

import android.content.Context
import android.os.AsyncTask
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import client.github.hopeisaprison.simplegithubclient.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.kohsuke.github.GHBranch
import org.kohsuke.github.GHCommit
import org.kohsuke.github.GitHub
import java.text.DateFormat
import java.util.*

/**
 * Created by hopeisaprison on 10/27/17.
 */
class CommitsAdapter(private val mContext : Context) :
        RecyclerView.Adapter<CommitsAdapter.CommitHolder>() {
    private val mCommitsList = ArrayList<Commit>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            CommitHolder(LayoutInflater.from(parent?.context).inflate(R.layout.row_commit, parent, false))

    override fun getItemCount() = mCommitsList.size


    override fun onBindViewHolder(holder: CommitHolder?, position: Int) {
        holder?.textViewDate?.text = DateFormat.getInstance().format(mCommitsList[position].date)
        holder?.textViewHash?.text = mCommitsList[position].sha
        holder?.textViewName?.text = mCommitsList[position].authorName
        Picasso.with(mContext)
                .load(mCommitsList[position].authorAvatarUrl)
                .into(holder?.imageViewAvatar)
    }

    fun addCommits(commitsList : List<Commit>) = mCommitsList.addAll(commitsList)

    class CommitHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewAvatar = itemView.findViewById<CircleImageView>(R.id.imageview_commit_avatar)
        val textViewName = itemView.findViewById<TextView>(R.id.textview_commit_name)
        val textViewDate = itemView.findViewById<TextView>(R.id.textview_commit_date)
        val textViewHash = itemView.findViewById<TextView>(R.id.textview_commit_hash)
    }

    class OnScrollDataLoader(private val gitHub: GitHub, private val recyclerView: RecyclerView?,
                             private val branch : GHBranch) : RecyclerView.OnScrollListener() {
        private var isLoading = false
        init {
            MoreItemLoader(recyclerView).execute()
        }

        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView?.layoutManager as LinearLayoutManager
            val visibleItemCount = layoutManager.childCount
            val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
            val itemCount = layoutManager.itemCount
            if (visibleItemCount+firstVisibleItem > itemCount && !isLoading) {
                isLoading = true
                MoreItemLoader(recyclerView).execute()
            }
        }

        inner class MoreItemLoader(private val recyclerView: RecyclerView?) : AsyncTask<Void, Void, Void?>() {
            override fun doInBackground(vararg params: Void?): Void? {
                val commits = branch.owner.queryCommits().from(branch.shA1).list().asList()
                val myCommits = ArrayList<Commit>()
                commits.forEach {
                    myCommits.add(Commit(it.author?.avatarUrl,
                            it.author?.name,
                            it.authoredDate,
                            it.shA1))
                }
                (recyclerView?.adapter as? CommitsAdapter)?.addCommits(myCommits)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                isLoading = false
                recyclerView?.adapter?.notifyDataSetChanged()

            }
    }

    }
    data class Commit(val authorAvatarUrl : String?, val authorName : String?, val date : Date, val sha : String)
}