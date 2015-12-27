package com.shareyourproxy.app.fragment


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import com.shareyourproxy.IntentLauncher.launchInviteFriendIntent
import com.shareyourproxy.IntentLauncher.launchUserProfileActivity
import com.shareyourproxy.R
import com.shareyourproxy.R.dimen.common_margin_medium
import com.shareyourproxy.R.dimen.common_svg_null_screen_small
import com.shareyourproxy.R.id.*
import com.shareyourproxy.R.string.fragment_contact_main_empty_message
import com.shareyourproxy.R.string.fragment_contact_main_empty_title
import com.shareyourproxy.R.style.Proxy_TextAppearance_Body
import com.shareyourproxy.R.style.Proxy_TextAppearance_Body2
import com.shareyourproxy.api.domain.model.User
import com.shareyourproxy.api.rx.JustObserver
import com.shareyourproxy.api.rx.RxBusDriver
import com.shareyourproxy.api.rx.RxBusDriver.post
import com.shareyourproxy.api.rx.RxGoogleAnalytics
import com.shareyourproxy.api.rx.RxQuery
import com.shareyourproxy.api.rx.command.SyncContactsCommand
import com.shareyourproxy.api.rx.command.eventcallback.LoggedInUserUpdatedEventCallback
import com.shareyourproxy.api.rx.event.NotificationCardActionEvent
import com.shareyourproxy.api.rx.event.SyncAllContactsErrorEvent
import com.shareyourproxy.api.rx.event.SyncAllContactsSuccessEvent
import com.shareyourproxy.api.rx.event.UserSelectedEvent
import com.shareyourproxy.app.adapter.BaseRecyclerView
import com.shareyourproxy.app.adapter.BaseViewHolder.ItemClickListener
import com.shareyourproxy.app.adapter.UserContactsAdapter
import com.shareyourproxy.app.adapter.UserContactsAdapter.UserViewHolder
import com.shareyourproxy.util.ViewUtils.svgToBitmapDrawable
import com.shareyourproxy.util.bindView
import com.shareyourproxy.widget.DismissibleNotificationCard.NotificationCard.INVITE_FRIENDS
import org.jetbrains.anko.onClick
import rx.subscriptions.CompositeSubscription

/**
 * A recyclerView of Favorite [User]s.
 */
class MainContactsFragment : BaseFragment(), ItemClickListener {
    private val recyclerView: BaseRecyclerView by bindView(fragment_contact_main_recyclerview)
    private val swipeRefreshLayout: SwipeRefreshLayout by bindView(fragment_contact_main_swipe_refresh)
    private val emptyTextView: TextView by bindView(fragment_contact_main_empty_textview)
    private val emptyView: ScrollView by bindView(fragment_contact_main_empty_view)
    private val emptyButton: Button by bindView(fragment_contact_main_empty_button)
    internal var catPadding: Int = resources.getDimensionPixelSize(common_margin_medium)
    internal var marginNullScreen: Int = resources.getDimensionPixelSize(common_svg_null_screen_small)
    internal var nullTitle: String = getString(fragment_contact_main_empty_title)
    internal var nullMessage: String = getString(fragment_contact_main_empty_message)
    internal var refreshListener: OnRefreshListener = OnRefreshListener {
        post(SyncContactsCommand(loggedInUser))
    }
    private val showHeader = !sharedPreferences.getBoolean(INVITE_FRIENDS.key, false)
    private var adapter: UserContactsAdapter = UserContactsAdapter.newInstance(recyclerView, sharedPreferences, showHeader, this)
    private var subscriptions: CompositeSubscription = CompositeSubscription()
    private val onClickInvite: View.OnClickListener = View.OnClickListener {
        launchInviteFriendIntent(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_contacts_main, container, false)
        emptyButton.onClick { onClickInvite }
        initializeRecyclerView()
        initializeSwipeRefresh(swipeRefreshLayout, refreshListener)
        return rootView
    }

    /**
     * Initialize a recyclerView with User data.
     */
    private fun initializeRecyclerView() {
        initializeEmptyView()
        recyclerView.setEmptyView(emptyView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter
    }

    private fun initializeEmptyView() {
        val context = context
        val draw = catDrawable
        draw.setBounds(-catPadding, 0, draw.intrinsicWidth, draw.intrinsicHeight)
        emptyTextView.setPadding(catPadding, 0, catPadding, 0)
        emptyTextView.setCompoundDrawables(null, draw, null, null)

        val sb = SpannableStringBuilder(nullTitle).append("\n").append(nullMessage)
        sb.setSpan(TextAppearanceSpan(context, Proxy_TextAppearance_Body2), 0, nullTitle.length, SPAN_INCLUSIVE_INCLUSIVE)
        sb.setSpan(TextAppearanceSpan(context, Proxy_TextAppearance_Body), nullTitle.length + 1, sb.length, SPAN_INCLUSIVE_INCLUSIVE)
        emptyTextView.text = sb
    }

    /**
     * Parse a svg and return a null screen sized [ContentDescriptionDrawable] .

     * @return Drawable with a contentDescription
     */
    private val catDrawable: Drawable
        get() = svgToBitmapDrawable(activity, R.raw.ic_gato, marginNullScreen)

    override fun onResume() {
        super.onResume()
        subscriptions.add(RxBusDriver.rxBusObservable().subscribe(busObserver))
        checkRefresh(loggedInUser)
    }

    val busObserver: JustObserver<Any>
        get() = object : JustObserver<Any>() {
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
            override fun next(event: Any?) {
                if (event is UserSelectedEvent) {
                    onUserSelected(event)
                } else if (event is LoggedInUserUpdatedEventCallback) {
                    userUpdated(event)
                } else if (event is SyncContactsCommand) {
                    swipeRefreshLayout.isRefreshing = true
                } else if (event is SyncAllContactsSuccessEvent) {
                    swipeRefreshLayout.isRefreshing = false
                } else if (event is SyncAllContactsErrorEvent) {
                    swipeRefreshLayout.isRefreshing = false
                } else if (event is NotificationCardActionEvent) {
                    launchInviteFriendIntent(activity)
                }
            }
        }

    /**
     * Refresh user data.
     * @param user contacts to refresh
     */
    fun checkRefresh(user: User) {
        adapter.refreshUserList(RxQuery.queryUserContacts(activity, user.contacts))
    }

    override fun onPause() {
        super.onPause()
        subscriptions.unsubscribe()
        //if we're refreshing data, get rid of the UI
        swipeRefreshLayout.isRefreshing = false
    }

    private fun userUpdated(event: LoggedInUserUpdatedEventCallback) {
        checkRefresh(event.user)
    }

    override fun onItemClick(view: View, position: Int) {
        val holder = recyclerView.getChildViewHolder(view) as UserViewHolder
        val user = adapter.getItemData(position)
        RxGoogleAnalytics(activity).contactProfileViewed(user)
        post(UserSelectedEvent(holder.userImage, holder.userName, user))
    }

    /**
     * User selected, launch that contacts profile.
     * @param event data
     */
    fun onUserSelected(event: UserSelectedEvent) {
        launchUserProfileActivity(activity, event.user, loggedInUser.id, event.imageView, event.textView)
    }

    companion object {

        /**
         * Create a new layouts.fragment with favorite contacts.
         * @return user layouts.fragment
         */
        fun newInstance(): MainContactsFragment {
            return MainContactsFragment()
        }
    }
}
