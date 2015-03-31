package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.hisp.dhis.mobile.datacapture.R;

public class EditDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String EDIT_DIALOG_FRAGMENT = EditDialogFragment.class.getName();

    private static final String EXTRA_STRING = "extraString";
    private static final String EXTRA_ID = "extraId";

    private static final String EMPTY_STRING = "";
    private static final int EMPTY_ID = -1;

    private EditText mEditText;
    private Button mOk;
    private Button mCancel;
    private EditDialogListener mListener;

    public static EditDialogFragment newInstance(int id, String string) {
        Bundle args = new Bundle();
        EditDialogFragment fragment = new EditDialogFragment();

        args.putString(EXTRA_STRING, string);
        args.putInt(EXTRA_ID, id);
        fragment.setArguments(args);

        return fragment;
    }

    public static EditDialogFragment newInstance() {
        return newInstance(EMPTY_ID, EMPTY_STRING);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.save_changes && mListener != null) {
            int id = EMPTY_ID;
            if (getArguments() != null) {
                id = getArguments().getInt(EXTRA_ID);
            }
            mListener.onFinishEditDialog(id, mEditText.getText().toString());
        }

        dismiss();
    }

    public void setListener(EditDialogListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE,
                R.style.Theme_AppCompat_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_edittext_layout, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditText = (EditText) view.findViewById(R.id.dashboard_name);
        mOk = (Button) view.findViewById(R.id.save_changes);
        mCancel = (Button) view.findViewById(R.id.discard_changes);

        mOk.setOnClickListener(this);
        mCancel.setOnClickListener(this);

        if (getArguments() != null) {
            String text = getArguments().getString(EXTRA_STRING);
            mEditText.setText(text);
        }
    }

    public interface EditDialogListener {
        void onFinishEditDialog(int id, String inputText);
    }
}
