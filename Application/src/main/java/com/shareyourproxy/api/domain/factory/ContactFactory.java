package com.shareyourproxy.api.domain.factory;

import com.shareyourproxy.api.domain.model.Contact;
import com.shareyourproxy.api.domain.model.Id;
import com.shareyourproxy.api.domain.model.User;
import com.shareyourproxy.api.domain.realm.RealmContact;

import java.util.ArrayList;

import io.realm.RealmList;

import static com.shareyourproxy.api.domain.factory.ChannelFactory.getModelChannels;

/**
 * Factory for creating domain model {@link Contact}s.
 */
public class ContactFactory {

    /**
     * Return a RealmList of Contacts from a user.
     *
     * @param realmContactsArray to get contacts from
     * @return RealmList of Contacts
     */
    public static ArrayList<Contact> getModelContacts(RealmList<RealmContact> realmContactsArray) {
        if (realmContactsArray != null) {
            ArrayList<Contact> contactArrayList = new ArrayList<>(realmContactsArray.size());
            for (RealmContact realmContact : realmContactsArray) {
                contactArrayList.add(
                    Contact.create(Id.builder().value(realmContact.getId()).build(),
                        realmContact.getFirst(), realmContact.getLast(), realmContact.getImageURL(),
                        getModelChannels(realmContact.getChannels())));
            }
            return contactArrayList;
        }
        return null;
    }

    public static Contact createModelContact(User user) {
        return Contact.create(user.id(), user.first(), user.last(),
            user.imageURL(), user.channels());
    }
}
