package com.shareyourproxy.api.domain.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Groups only have names for now.
 */
public class RealmGroup extends RealmObject {

    @PrimaryKey
    private String id;
    private String label;
    private RealmList<RealmString> channels;
    private RealmList<RealmString> contacts;


    /**
     * Getter.
     *
     * @return unique group identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Setter.
     *
     * @param id unique identifier
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter.
     *
     * @return contactGroups name
     */
    public String getLabel() {
        return label;
    }

    /**
     * Setter.
     *
     * @param label name
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Getter.
     *
     * @return newChannel permissions shared with this {@link RealmGroup}
     */
    public RealmList<RealmString> getChannels() {
        return channels;
    }

    /**
     * Setter.
     *
     * @param channels group channels
     */
    public void setChannels(RealmList<RealmString> channels) {
        this.channels = channels;
    }

    /**
     * Getter.
     *
     * @return contacts in this {@link RealmGroup}
     */
    public RealmList<RealmString> getContacts() {
        return contacts;
    }

    /**
     * Setter.
     *
     * @param contacts in this {@link RealmGroup}
     */
    public void setContacts(RealmList<RealmString> contacts) {
        this.contacts = contacts;
    }

}
