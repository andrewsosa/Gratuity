package com.andrewsosa.gratuity;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiptFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiptFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_AMOUNT = "amount";
    private static final String ARG_GRATUITY = "gratuity";
    private static final String ARG_TOTAL = "total";
    private static final String ARG_PARTY = "party";

    private static final String[] tips = {
            "10% Gratuity",
            "15% Gratuity",
            "18% Gratuity",
            "20% Gratuity",
            "25% Gratuity",
            "Gratuity"
    };

    private float amount;
    private float gratuity;
    private float total;
    private int party;

    private TextView amountText;
    private TextView gratuityText;
    private TextView gratuityLabel;
    private TextView totalText;
    private TextView partyText;
    private RelativeLayout partyLayout;


    public static ReceiptFragment newInstance(float amount, float gratuity, float total, int party) {
        ReceiptFragment fragment = new ReceiptFragment();
        Bundle args = new Bundle();
        args.putFloat(ARG_AMOUNT, amount);
        args.putFloat(ARG_GRATUITY, gratuity);
        args.putFloat(ARG_TOTAL, total);
        args.putInt(ARG_PARTY, party);
        fragment.setArguments(args);
        return fragment;
    }

    public ReceiptFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            amount = getArguments().getFloat(ARG_AMOUNT);
            gratuity = getArguments().getFloat(ARG_GRATUITY);
            total = getArguments().getFloat(ARG_TOTAL);
            party = getArguments().getInt(ARG_PARTY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_receipt, container, false);

        amountText = (TextView) v.findViewById(R.id.amount_value);
        gratuityText = (TextView) v.findViewById(R.id.tip_value);
        gratuityLabel = (TextView) v.findViewById(R.id.gratuity_text);
        totalText = (TextView) v.findViewById(R.id.total_value);
        partyText = (TextView) v.findViewById(R.id.individual_cost);
        partyLayout = (RelativeLayout) v.findViewById(R.id.party_layout);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            amount = savedInstanceState.getFloat("amount");
            gratuity = savedInstanceState.getFloat("gratuity");
            total = savedInstanceState.getFloat("total");
            party = savedInstanceState.getInt("party");
        }

        DecimalFormat form = new DecimalFormat("0.00");
        amountText.setText(form.format(amount));
        gratuityText.setText(form.format(gratuity * amount));
        totalText.setText(form.format(total));

        if(party > 1) {
            partyLayout.setVisibility(View.VISIBLE);
            partyText.setText(form.format(total / (float) party));
        } else {
            partyLayout.setVisibility(View.GONE);
        }

        int i;
        switch ("" + gratuity) {
            case "0.1":  i = 0; break;
            case "0.15":  i = 1; break;
            case "0.18":  i = 2; break;
            case "0.20":  i = 3; break;
            case "0.25":  i = 4; break;
            default: i = 5; break;
        }

        gratuityLabel.setText(tips[i]);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat("amount", amount);
        outState.putFloat("gratuity", gratuity);
        outState.putFloat("total", total);
        outState.putInt("party", party);

    }
}
