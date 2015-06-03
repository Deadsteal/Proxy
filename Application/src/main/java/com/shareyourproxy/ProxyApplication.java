/*Copyright 2013 Jake Wharton

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.shareyourproxy;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.firebase.client.Firebase;
import com.shareyourproxy.api.domain.model.User;
import com.shareyourproxy.api.rx.RxBusDriver;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import timber.log.Timber;

/**
 * Plant a logging tree.
 */
public class ProxyApplication extends Application {

    private User _currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.USE_CRASHLYTICS) {
            Fabric.with(this, new Crashlytics());
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            LeakCanary.install(this);
        }
        Firebase.setAndroidContext(this);
        Realm.deleteRealmFile(this);
    }

    /**
     * Getter.
     *
     * @return currerntly logged in user
     */
    public User getCurrentUser() {
        return _currentUser;
    }

    /**
     * Setter.
     *
     * @param currentUser currently logged in user
     */
    public void setCurrentUser(User currentUser) {
        _currentUser = currentUser;
    }

    public RxBusDriver getRxBus() {
        return RxBusDriver.getInstance();
    }
}