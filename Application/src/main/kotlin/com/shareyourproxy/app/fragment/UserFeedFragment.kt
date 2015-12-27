package com.shareyourproxy.app.fragment

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.shareyourproxy.Constants.ARG_USER_SELECTED_PROFILE
import com.shareyourproxy.R
import com.shareyourproxy.R.dimen.common_svg_null_screen_small
import com.shareyourproxy.R.id.fragment_user_feed_recyclerview
import com.shareyourproxy.R.layout.fragment_user_feed
import com.shareyourproxy.R.string.*
import com.shareyourproxy.R.style.Proxy_TextAppearance_Body
import com.shareyourproxy.R.style.Proxy_TextAppearance_Body2
import com.shareyourproxy.api.domain.model.User
import com.shareyourproxy.api.rx.JustObserver
import com.shareyourproxy.api.rx.command.eventcallback.ActivityFeedDownloadedEvent
import com.shareyourproxy.app.adapter.ActivityFeedAdapter
import com.shareyourproxy.app.adapter.BaseRecyclerView
import com.shareyourproxy.app.adapter.BaseViewHolder.ItemClickListener
import com.shareyourproxy.util.ViewUtils.svgToBitmapDrawable
import com.shareyourproxy.util.bindView
import rx.subscriptions.CompositeSubscription

/**
 * User feeds.
 */
class UserFeedFragment : BaseFragment(), ItemClickListener {
    private val recyclerView: BaseRecyclerView by bindView(fragment_user_feed_recyclerview)
    private val emptyTextView: TextView by bindView(R.id.fragment_user_feed_empty_textview)
    private val loggedInNullTitle: String = getString(fragment_userfeed_empty_title)
    internal var stringNullMessage: String = getString(fragment_userfeed_empty_message)
    internal var contactNullTitle: String = getString(fragment_userprofile_contact_empty_title)
    internal var marginNullScreen: Int = resources.getDimensionPixelSize(common_svg_null_screen_small)
    private var isLoggedInUser: Boolean = false
    private var userContact: User = arguments.getParcelable<User>(ARG_USER_SELECTED_PROFILE)
    private var subscriptions: CompositeSubscription = CompositeSubscription()
    private var adapter: ActivityFeedAdapter = ActivityFeedAdapter.newInstance(recyclerView, userContact, this)
    private var lastClickedAuthItem: Int = 0

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        isLoggedInUser = isLoggedInUser(userContact)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(fragment_user_feed, container, false)
        initialize()
        return rootView
    }

    fun ActivityFeedObserver(): JustObserver<ActivityFeedDownloadedEvent> {
        return object : JustObserver<ActivityFeedDownloadedEvent>() {
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
            override fun next(event: ActivityFeedDownloadedEvent?) {
                activityFeedDownloaded(event)
            }
        }
    }

    private fun activityFeedDownloaded(event: ActivityFeedDownloadedEvent?) {
        adapter.refreshFeedData(event?.feedItems)
    }

    override fun onResume() {
        super.onResume()
        subscriptions = CompositeSubscription()
    }

    override fun onPause() {
        super.onPause()
        subscriptions.unsubscribe()
    }

    /**
     * Initialize this fragments views.
     */
    private fun initialize() {
        initializeRecyclerView()
    }

    /**
     * Initialize a recyclerView with User data.
     */
    private fun initializeRecyclerView() {
        initializeEmptyView()
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    private fun initializeEmptyView() {
        val context = context
        if (isLoggedInUser) {
            val sb = SpannableStringBuilder(loggedInNullTitle).append("\n").append(stringNullMessage)
            sb.setSpan(TextAppearanceSpan(context, Proxy_TextAppearance_Body2), 0, loggedInNullTitle.length, SPAN_INCLUSIVE_INCLUSIVE)
            sb.setSpan(TextAppearanceSpan(context, Proxy_TextAppearance_Body), loggedInNullTitle.length + 1, sb.length, SPAN_INCLUSIVE_INCLUSIVE)

            emptyTextView.text = sb
            emptyTextView.setCompoundDrawablesWithIntrinsicBounds(null, getNullDrawable(R.raw.ic_ghost_doge), null, null)
        } else {
            val contactNullMessage = getString(fragment_userprofile_contact_empty_message, userContact.first)
            val sb = SpannableStringBuilder(contactNullTitle).append("\n").append(contactNullMessage)

            sb.setSpan(TextAppearanceSpan(context, Proxy_TextAppearance_Body2), 0, loggedInNullTitle.length, SPAN_INCLUSIVE_INCLUSIVE)
            sb.setSpan(TextAppearanceSpan(context, Proxy_TextAppearance_Body), loggedInNullTitle.length + 1, sb.length, SPAN_INCLUSIVE_INCLUSIVE)

            emptyTextView.text = sb
            emptyTextView.setCompoundDrawablesWithIntrinsicBounds(null, getNullDrawable(R.raw.ic_ghost_sloth), null, null)
        }
        recyclerView.setEmptyView(emptyTextView)
    }

    /**
     * Parse a svg and return a null screen sized [ContentDescriptionDrawable] .
     * @return Drawable with a contentDescription
     */
    private fun getNullDrawable(resId: Int): Drawable {
        return svgToBitmapDrawable(activity, resId, marginNullScreen)
    }

    override fun onItemClick(view: View, position: Int) {
        when (adapter.getItemViewType(position)) {
        }
    }
    companion object {

        /**
         * Create a new user activity feed fragment.
         * @return user activity feed fragment.
         */
        fun newInstance(): UserFeedFragment {
            return UserFeedFragment()
        }
    }
}
