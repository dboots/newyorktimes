package com.donboots.newyorktimes.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.donboots.newyorktimes.R;
import com.donboots.newyorktimes.models.FilterSettings;

import java.util.Calendar;
import java.util.HashMap;

public class FilterFragment extends DialogFragment {
    EditText etBeginDate;
    Button btnSave;
    CheckBox cbArts;
    CheckBox cbFashion;
    CheckBox cbSports;
    Spinner spinnerSortOrder;
    private int mYear, mMonth, mDay;

    public interface FilterDialogListener {
        void onFinishEditDialog(FilterSettings settings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_settings, container);

        String begindate = getArguments().getString("begindate");
        String sort = getArguments().getString("sort");

        etBeginDate = (EditText) view.findViewById(R.id.etBeginDate);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        cbArts = (CheckBox) view.findViewById(R.id.cbArts);
        cbFashion = (CheckBox) view.findViewById(R.id.cbFashion);
        cbSports = (CheckBox) view.findViewById(R.id.cbSports);
        spinnerSortOrder = (Spinner) view.findViewById(R.id.spinnerSortOrder);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.sort_arrays, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortOrder.setAdapter(adapter);
        if (!sort.equals(null)) {
            int spinnerPosition = adapter.getPosition(sort);
            spinnerSortOrder.setSelection(spinnerPosition);
        }
        etBeginDate.setText(begindate);

        etBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                String current = etBeginDate.getText().toString();
                mYear = Integer.parseInt(current.substring(0, 4));
                mMonth = Integer.parseInt(current.substring(4, 6)) - 1;
                mDay = Integer.parseInt(current.substring(6, 8));

                DatePickerDialog dpd = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String m = Integer.toString(monthOfYear + 1);
                                String d = Integer.toString(dayOfMonth);

                                if (m.length() == 1)
                                    m = "0" + m;

                                if (d.length() == 1)
                                    d = "0" + d;

                                etBeginDate.setText(year + "" + m + "" + d);
                            }
                        }, mYear, mMonth, mDay);

                dpd.show();
            } //-- end onClick
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Boolean> map = new HashMap<String, Boolean>();
                map.put("Arts", cbArts.isChecked());
                map.put("Fashion & Style", cbFashion.isChecked());
                map.put("Sports", cbSports.isChecked());

                FilterSettings fs = new FilterSettings();
                fs.setBeginDate(etBeginDate.getText().toString());
                fs.setSortOrder(spinnerSortOrder.getSelectedItem().toString());
                fs.setNewsDesks(map);

                FilterDialogListener listener = (FilterDialogListener) getActivity();
                listener.onFinishEditDialog(fs);
                dismiss();
            }
        });

        return view;
    }


}
