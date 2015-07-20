package com.shareyourproxy.api.rx;

import android.content.Context;
import android.support.v7.util.SortedList;

import com.shareyourproxy.api.RestClient;
import com.shareyourproxy.api.domain.factory.GroupFactory;
import com.shareyourproxy.api.domain.factory.UserFactory;
import com.shareyourproxy.api.domain.model.Channel;
import com.shareyourproxy.api.domain.model.Group;
import com.shareyourproxy.api.domain.model.GroupEditChannel;
import com.shareyourproxy.api.domain.model.SharedLink;
import com.shareyourproxy.api.domain.model.User;
import com.shareyourproxy.api.rx.command.eventcallback.EventCallback;
import com.shareyourproxy.api.rx.command.eventcallback.GroupChannelsUpdatedEventCallback;
import com.shareyourproxy.api.rx.command.eventcallback.UserGroupAddedEventCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;

import static com.shareyourproxy.api.RestClient.getUserGroupService;
import static com.shareyourproxy.api.rx.RxHelper.updateRealmUser;

/**
 * Update a groups channel map, or update a channel in all groups.
 */
public class RxGroupChannelSync {
    private RxGroupChannelSync() {
    }

    public static List<EventCallback> addUserGroupsChannel(
        Context context, User user, Channel channel) {
        String channelId = channel.id().value();
        for (Map.Entry<String, Group> entryGroup : user.groups().entrySet()) {
            entryGroup.getValue().channels().put(channelId, channel);
        }
        return Observable.zip(
            saveRealmUserGroupChannels(context, user),
            saveFirebaseUserGroups(user),
            zipAddGroupsChannel())
            .toList().toBlocking().single();
    }

    public static Func1<GroupChannelsUpdatedEventCallback, EventCallback> saveSharedLink() {
        return new Func1<GroupChannelsUpdatedEventCallback, EventCallback>() {
            @Override
            public GroupChannelsUpdatedEventCallback call(GroupChannelsUpdatedEventCallback event) {
                SharedLink link = SharedLink.create(event.user, event.group);
                RestClient.getSharedLinkService()
                    .addSharedLink(link.id().value(), link).subscribe();
                return event;
            }
        };
    }

    private static Func2<User, Group, EventCallback> zipAddGroupsChannel() {
        return new Func2<User, Group, EventCallback>() {
            @Override
            public UserGroupAddedEventCallback call(User user, Group group) {
                return new UserGroupAddedEventCallback(user, group);
            }
        };
    }

    private static Observable<User> saveRealmUserGroupChannels(
        final Context context, final User user) {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                updateRealmUser(context, user);
            }
        }).map(returnUser(user));
    }

    private static Func1<Object, User> returnUser(final User user) {
        return new Func1<Object, User>() {
            @Override
            public User call(Object o) {
                return user;
            }
        };
    }

    private static Observable<Group> saveFirebaseUserGroups(User user) {
        String userId = user.id().value();
        return RestClient.getUserGroupService().updateUserGroups(userId, user.groups());
    }

    public static HashMap<String, Channel> getSelectedChannels(
        SortedList<GroupEditChannel> channels) {
        return Observable.just(channels).map(getSelectedChannels()).toBlocking().single();
    }


    public static List<EventCallback> updateGroupChannels(
        Context context, User user, String newTitle, Group oldGroup,
        HashMap<String, Channel> channels) {
        return Observable.zip(
            saveRealmGroupChannels(context, user, newTitle, oldGroup, channels),
            saveFirebaseGroupChannels(user.id().value(), newTitle, oldGroup, channels),
            zipAddGroupChannels(user, channels))
            .map(saveSharedLink())
            .toList().toBlocking().single();
    }

    private static Func1<SortedList<GroupEditChannel>, HashMap<String, Channel>>
    getSelectedChannels() {
        return new Func1<SortedList<GroupEditChannel>, HashMap<String, Channel>>() {
            @Override
            public HashMap<String, Channel> call(SortedList<GroupEditChannel> groupEditChannels) {
                HashMap<String, Channel> selectedChannels = new HashMap<>();
                for (int i = 0; i < groupEditChannels.size(); i++) {
                    GroupEditChannel editChannel = groupEditChannels.get(i);
                    if (editChannel.inGroup()) {
                        Channel channel = editChannel.getChannel();
                        selectedChannels.put(channel.id().value(), channel);
                    }
                }
                return selectedChannels;
            }
        };
    }

    private static rx.Observable<Group> saveRealmGroupChannels(
        Context context, User user, String newTitle, Group oldGroup, HashMap<String, Channel>
        channels) {
        return Observable.just(channels)
            .map(addRealmGroupChannels(context, user, newTitle, oldGroup));
    }

    private static Func1<HashMap<String, Channel>, Group> addRealmGroupChannels(
        final Context context, final User user, final String newTitle, final Group oldGroup) {
        return new Func1<HashMap<String, Channel>, Group>() {
            @Override
            public Group call(HashMap<String, Channel> channels) {
                Group newGroup = GroupFactory.addGroupChannels(newTitle, oldGroup, channels);
                User newUser = UserFactory.addUserGroup(user, newGroup);
                updateRealmUser(context, newUser);
                return newGroup;
            }
        };
    }

    private static Observable<Group> saveFirebaseGroupChannels(
        String userId, String newTitle, Group group, HashMap<String, Channel> channels) {
        String groupId = group.id().value();
        return getUserGroupService()
            .addUserGroup(userId, groupId, Group.copy(group, newTitle, channels));
    }

    private static Func2<Group, Group, GroupChannelsUpdatedEventCallback> zipAddGroupChannels(
        final User user, final HashMap<String, Channel> channels) {
        return new Func2<Group, Group, GroupChannelsUpdatedEventCallback>() {
            @Override
            public GroupChannelsUpdatedEventCallback call(Group group, Group group2) {
                return new GroupChannelsUpdatedEventCallback(user, group, channels);
            }
        };
    }
}
