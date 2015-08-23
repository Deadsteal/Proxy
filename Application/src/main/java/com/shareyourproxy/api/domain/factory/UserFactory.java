package com.shareyourproxy.api.domain.factory;

import com.shareyourproxy.api.domain.model.Channel;
import com.shareyourproxy.api.domain.model.Contact;
import com.shareyourproxy.api.domain.model.Group;
import com.shareyourproxy.api.domain.model.Id;
import com.shareyourproxy.api.domain.model.User;
import com.shareyourproxy.api.domain.realm.RealmUser;

import java.util.HashMap;

import io.realm.RealmResults;

import static com.shareyourproxy.BuildConfig.VERSION_CODE;
import static com.shareyourproxy.api.domain.factory.ChannelFactory.getModelChannels;
import static com.shareyourproxy.api.domain.factory.ContactFactory.getContactIds;
import static com.shareyourproxy.api.domain.factory.GroupFactory.getModelGroups;


/**
 * Factory for creating domain model {@link User}s.
 */
public class UserFactory {

    /**
     * Private Constructor.
     */
    private UserFactory() {
    }

    /**
     * Could be in and IdFractory but is only one method.
     *
     * @param userId users Id.
     * @return user Id
     */
    private static Id getUserId(String userId) {
        return Id.builder().value(userId).build();
    }

    /**
     * Take in a realm user and create a model user.
     *
     * @param realmUser to copy
     * @return model user
     */
    public static User createModelUser(RealmUser realmUser) {
        if (realmUser == null) {
            return null;
        }
        return User.create(getUserId(realmUser.getId()), realmUser.getFirst(),
            realmUser.getLast(), realmUser.getEmail(), realmUser.getProfileURL(),
            realmUser.getCoverURL(), getModelChannels(realmUser.getChannels()),
            getModelGroups(realmUser.getGroups()),
            getContactIds(realmUser.getContacts()), VERSION_CODE);
    }

    /**
     * Create a model User from a contactId to use in user profiles.
     *
     * @param contact
     * @return
     */
    public static User createModelUser(Contact contact) {
        return User.create(contact.id(), contact.first(), contact.last(), null,
            contact.profileURL(), contact.coverURL(), null, null, null, VERSION_CODE);
    }

    /**
     * Create a HashMap of Users from all
     *
     * @param realmUsers
     * @return
     */
    public static HashMap<String, User> createModelUsers(RealmResults<RealmUser> realmUsers) {
        HashMap<String, User> users = new HashMap<>();
        for (RealmUser realmUser : realmUsers) {
            users.put(realmUser.getId(), createModelUser(realmUser));
        }
        return users;
    }

    public static User addUserContact(User user, String contactId) {
        HashMap<String, Id> contactList = user.contacts();
        if (contactList == null) {
            contactList = new HashMap<>();
        }
        contactList.put(contactId, Id.create(contactId));
        return User.create(user.id(), user.first(), user.last(), user.email(),
            user.profileURL(), user.coverURL(), user.channels(), user.groups(), contactList,
            VERSION_CODE);
    }

    /**
     * Create the same {@link User} with the updated email value.
     *
     * @param user to copy
     * @return updated user
     */
    public static User addUserChannel(User user, Channel channel) {
        HashMap<String, Channel> channelHashMap = user.channels();
        if (channelHashMap == null) {
            channelHashMap = new HashMap<>();
        }
        channelHashMap.put(channel.id().value(), channel);
        return User.create(user.id(), user.first(), user.last(), user.email(),
            user.profileURL(), user.coverURL(), channelHashMap, user.groups(), user.contacts(),
            VERSION_CODE);
    }

    /**
     * Create the same {@link User} with the updated List<{@link Group}> values.
     *
     * @param user to copy
     * @return updated user
     */
    public static User addUserGroup(User user, Group newGroup) {
        HashMap<String, Group> groups = user.groups();
        groups.put(newGroup.id().value(), newGroup);
        return addUserGroups(user, groups);
    }

    public static User deleteUserGroup(User user, Group group) {
        HashMap<String, Group> groups = user.groups();
        groups.remove(group.id().value());
        return addUserGroups(user, groups);
    }

    /**
     * Create the same {@link User} with the updated List<{@link Group}> values.
     *
     * @param user   to copy
     * @param groups to update
     * @return updated user
     */
    public static User addUserGroups(User user, HashMap<String, Group> groups) {
        return User.create(user.id(), user.first(), user.last(), user.email(),
            user.profileURL(), user.coverURL(), user.channels(), groups, user.contacts(),
            VERSION_CODE);
    }

    public static User deleteUserContact(User user, String contactId) {
        HashMap<String, Id> contactList = user.contacts();
        if (contactList != null) {
            contactList.remove(contactId);
        }
        return User.create(user.id(), user.first(), user.last(), user.email(),
            user.profileURL(), user.coverURL(), user.channels(), user.groups(), contactList,
            VERSION_CODE);
    }

    public static User deleteUserChannel(User user, Channel channel) {
        HashMap<String, Channel> channels = user.channels();
        if (channels != null) {
            channels.remove(channel.id().value());
        }
        return User.create(user.id(), user.first(), user.last(), user.email(),
            user.profileURL(), user.coverURL(), channels, user.groups(), user.contacts(),
            VERSION_CODE);
    }
}
