package com.shareyourproxy.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shareyourproxy.Constants;
import com.shareyourproxy.IntentLauncher;
import com.shareyourproxy.R;
import com.shareyourproxy.api.domain.model.User;
import com.shareyourproxy.api.gson.UserTypeAdapter;
import com.shareyourproxy.api.rx.JustObserver;
import com.shareyourproxy.api.rx.command.SyncAllUsersCommand;
import com.shareyourproxy.api.rx.event.SyncAllUsersErrorEvent;
import com.shareyourproxy.api.rx.event.SyncAllUsersSuccessEvent;
import com.shareyourproxy.app.MainActivity;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.shareyourproxy.IntentLauncher.launchLoginActivity;
import static com.shareyourproxy.util.ViewUtils.dpToPx;
import static com.shareyourproxy.util.ViewUtils.svgToBitmapDrawable;

/**
 * Dispatch Fragment to handle dispatching a {@link com.shareyourproxy.app.LoginActivity} or a
 * {@link MainActivity} base off the current user.
 */
public class DispatchFragment extends BaseFragment {
    public static final int HOLD_ON_HALF_A_SECOND = 500;
    @Bind(R.id.fragment_dispatch_image)
    ImageView imageView;

    Runnable loginRunnable = new Runnable() {
        @Override
        public void run() {
            User user = null;
            String jsonUser = getSharedPrefrences().getString(Constants.KEY_LOGGED_IN_USER, null);
            if (jsonUser != null) {
                try {
                    user = UserTypeAdapter.newInstance()
                        .fromJson(jsonUser);
                } catch (IOException e) {
                    Timber.e(Log.getStackTraceString(e));
                }
            }

            if (user == null) {
                launchLoginActivity(getActivity());
                getActivity().finish();
            } else {
                setLoggedInUser(user);
                getRxBus().post(new SyncAllUsersCommand(user.id().value()));
            }
        }
    };
    private CompositeSubscription _subscriptions;

    /**
     * Constructor.
     */
    public DispatchFragment() {
    }

    /**
     * Return new Fragment instance.
     *
     * @return layouts.fragment
     */
    public static DispatchFragment newInstance() {
        return new DispatchFragment();
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dispatch, container, false);
        ButterKnife.bind(this, rootView);
        drawLogo();
        rootView.postDelayed(loginRunnable, HOLD_ON_HALF_A_SECOND);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        _subscriptions = new CompositeSubscription();
        _subscriptions.add(getRxBus().toObserverable()
            .subscribe(getRxBusObserver()));
    }

    public JustObserver<Object> getRxBusObserver() {
        return new JustObserver<Object>() {
            @Override
            public void onError() {
            }

            @Override
            public void onNext(Object event) {
                if (event instanceof SyncAllUsersSuccessEvent) {
                    login();
                } else if (event instanceof SyncAllUsersErrorEvent) {
                    login();
                }
            }
        };
    }

    private void login() {
        IntentLauncher.launchMainActivity(getActivity(),
            MainFragment.ARG_SELECT_CONTACTS_TAB, false, null);
        getActivity().finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        _subscriptions.unsubscribe();
        _subscriptions = null;
    }

    /**
     * Set the Logo image.drawable on this activities {@link ImageView}.
     */
    private void drawLogo() {
        ViewCompat.setLayerType(imageView, ViewCompat.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setElevation(imageView, getElevation());
        imageView.setImageDrawable(svgToBitmapDrawable(getActivity(),
            R.raw.ic_proxy_logo, (int) getResourceDimension(getActivity())));
    }

    /**
     * Get a big icon dimension size.
     *
     * @param activity context
     * @return resource dimension
     */
    private float getResourceDimension(Activity activity) {
        return dpToPx(activity.getResources(), R.dimen.common_svg_ultra);
    }

    /**
     * Get the elevation resource for FAB.
     *
     * @return diemnsion of elevation
     */
    private float getElevation() {
        return dpToPx(getActivity().getResources(), R.dimen.common_fab_elevation);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
