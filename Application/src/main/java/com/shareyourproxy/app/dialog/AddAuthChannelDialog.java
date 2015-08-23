package com.shareyourproxy.app.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.shareyourproxy.IntentLauncher;
import com.shareyourproxy.R;
import com.shareyourproxy.api.domain.model.Channel;
import com.shareyourproxy.api.rx.command.AddUserChannelCommand;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

import static com.shareyourproxy.api.domain.factory.ChannelFactory.createModelInstance;
import static com.shareyourproxy.util.DebugUtils.getSimpleName;
import static com.shareyourproxy.util.ViewUtils.hideSoftwareKeyboard;

/**
 * Update a facebook intent with user input data.
 */
public class AddAuthChannelDialog extends BaseDialogFragment {
    private static final String ARG_CHANNEL = "AddAuthChannelDialog.Channel";
    private static final String TAG = getSimpleName(AddAuthChannelDialog.class);
    private final DialogInterface.OnClickListener _helpClicked =
        new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                IntentLauncher.launchFacebookHelpIntent(getActivity());
            }
        };
    @Bind(R.id.dialog_channel_auth_action_address_edittext)
    protected EditText editTextActionAddress;
    private final DialogInterface.OnClickListener _negativeClicked =
        new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hideSoftwareKeyboard(editTextActionAddress);
                dialogInterface.dismiss();
            }
        };
    @Bind(R.id.dialog_channel_auth_action_address_floatlabel)
    protected TextInputLayout floatLabelAddress;
    @BindColor(R.color.common_text)
    protected int _textColor;
    @BindColor(R.color.common_divider)
    protected int _gray;
    @BindColor(R.color.common_blue)
    protected int _blue;
    private Channel _channel;
    /**
     * EditorActionListener that detects when the software keyboard's done or enter button is
     * pressed.
     */
    private final TextView.OnEditorActionListener _onEditorActionListener =
        new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == KeyEvent.KEYCODE_ENTER
                    || actionId == KeyEvent.KEYCODE_ENDCALL) {
                    addUserChannel();
                    getDialog().dismiss();
                    return true;
                }
                return false;
            }
        };
    private final DialogInterface.OnClickListener _positiveClicked =
        new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addUserChannel();
                dialogInterface.dismiss();
            }
        };

    /**
     * Create a new instance of a {@link AddAuthChannelDialog}.
     *
     * @return A {@link AddAuthChannelDialog
     */
    public static AddAuthChannelDialog newInstance(Channel channel) {
        //Bundle arguments
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_CHANNEL, channel);
        //create dialog instance
        AddAuthChannelDialog dialog = new AddAuthChannelDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    /**
     * Dispatch a Channel Added Event
     */
    private void addUserChannel() {
        String actionContent = editTextActionAddress.getText().toString();
        if (!TextUtils.isEmpty(actionContent.trim())) {
            Channel channel =
                createModelInstance(_channel, actionContent);
            getRxBus().post(new AddUserChannelCommand(getLoggedInUser(), channel));
        }
    }

    /**
     * If text is entered into the dialog {@link EditText}, change the background underline of the
     * widget.
     *
     * @param editable the string entered in the {@link EditText}
     */
    @OnTextChanged(value = R.id.dialog_channel_auth_action_address_edittext,
        callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterActionAddressChanged(Editable editable) {
        editTextActionAddress.getBackground().setColorFilter(
            TextUtils.isEmpty(editable) ? _gray : _blue, PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        _channel = getArguments().getParcelable(ARG_CHANNEL);
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = getActivity().getLayoutInflater()
            .inflate(R.layout.dialog_auth_channel, null, false);
        ButterKnife.bind(this, view);
        editTextActionAddress.setOnEditorActionListener(_onEditorActionListener);
        AlertDialog dialog = new AlertDialog.Builder(getActivity(),
            R.style.Base_Theme_AppCompat_Light_Dialog)
            .setTitle(getString(
                R.string.dialog_addchannel_title_add_blank, _channel.channelType().getLabel()))
            .setView(view)
            .setPositiveButton(getString(R.string.save), _positiveClicked)
            .setNegativeButton(android.R.string.cancel, _negativeClicked)
            .setNeutralButton(R.string.common_help, _helpClicked)
            .create();

        dialog.setCanceledOnTouchOutside(false);
        // Show the SW Keyboard on dialog start. Always.
        dialog.getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
        initializeEditTextColors();
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        // Setup Button Colors
        setButtonTint(dialog.getButton(Dialog.BUTTON_POSITIVE), _blue);
        setButtonTint(dialog.getButton(Dialog.BUTTON_NEGATIVE), _textColor);
        setButtonTint(dialog.getButton(Dialog.BUTTON_NEUTRAL), _textColor);
    }

    /**
     * Initialize values for EditText to switch color on.
     */
    private void initializeEditTextColors() {
        editTextActionAddress.getBackground().setColorFilter(_gray, PorterDuff.Mode.SRC_IN);
        floatLabelAddress.setHint(getString(
            R.string.dialog_addchannel_hint_address_blank_handle,
            _channel.channelType().getLabel()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * Use the private string TAG from this class as an identifier.
     *
     * @param fragmentManager manager of fragments
     * @return this dialog
     */
    public AddAuthChannelDialog show(FragmentManager fragmentManager) {
        show(fragmentManager, TAG);
        return this;
    }
}
