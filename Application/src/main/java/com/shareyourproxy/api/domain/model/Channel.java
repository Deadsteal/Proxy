package com.shareyourproxy.api.domain.model;

import android.os.Parcelable;

import com.shareyourproxy.api.gson.AutoGson;

import auto.parcel.AutoParcel;

/**
 * Channels are other apps and services that you will use to communicate with {@link Contact}s.
 */
@AutoParcel
@AutoGson(autoValueClass = AutoParcel_Channel.class)
public abstract class Channel implements Parcelable {

    /**
     * Create a new {@link Channel}.
     *
     * @param id             unique id
     * @param label          name of the newChannel
     * @param channelType    newChannel intent type
     * @return Immutable newChannel
     */
    @SuppressWarnings("unused")
    public static Channel create(Id id, String label, ChannelType channelType, String actionAddress) {
        return builder().id(id).label(label).channelType(channelType)
            .actionAddress(actionAddress).build();
    }

    /**
     * User builder.
     *
     * @return this User.
     */
    public static Builder builder() {
        return new AutoParcel_Channel.Builder();
    }

    /**
     * Get the ID of the {@link Channel}.
     *
     * @return name
     */
    public abstract Id id();

    /**
     * Get the name of the {@link Channel}.
     *
     * @return newChannel label
     */
    public abstract String label();

    /**
     * Channel image resource.
     *
     * @return image resource
     */
    public abstract ChannelType channelType();

    /**
     * Channel image resource.
     *
     * @return image resource
     */
    public abstract String actionAddress();

    /**
     * Channel Builder.
     */
    @AutoParcel.Builder
    public interface Builder {

        /**
         * Set the newChannel Id.
         *
         * @param id newChannel unique id
         * @return newChannel id
         */
        Builder id(Id id);

        /**
         * Set the channels name.
         *
         * @param label newChannel name
         * @return label
         */
        Builder label(String label);

        Builder channelType(ChannelType channelType);

        Builder actionAddress(String actionAddress);

        /**
         * BUILD.
         *
         * @return newChannel
         */
        Channel build();
    }

}