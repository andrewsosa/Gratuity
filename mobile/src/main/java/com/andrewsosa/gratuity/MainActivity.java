package com.andrewsosa.gratuity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private float amount;
    private float gratuity;
    private float gratuityValue;
    private boolean customGratuity = true;
    private int partySize;
    private boolean display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DrawerLayout drawer = ((DrawerLayout)findViewById(R.id.drawer));
        drawer.setStatusBarBackgroundColor(getResources().getColor(R.color.primaryDark));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu_white_24dp, null));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //drawer.openDrawer(Gravity.LEFT);
            }
        });



        toolbar.setTitle("");
        //toolbar.inflateMenu(R.menu.menu_main);
        setSupportActionBar(toolbar);

        if(savedInstanceState != null) {
            setAmount(savedInstanceState.getFloat("amount"));
            gratuity = savedInstanceState.getFloat("gratuity");
            partySize = savedInstanceState.getInt("party");
            display = savedInstanceState.getBoolean("display");
        } else {
            setAmount((float) 0.0);
            gratuity = (float) 0.0;
            partySize = 0;
            display = false;
        }


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.set_amount:
                inputDialog();
                break;
            case R.id.add_tip:
                selectorDialog();
                break;
            case R.id.fab:
                seekbarDialog();
                break;


        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putFloat("amount", amount);
        outState.putFloat("gratuity", gratuity);
        outState.putInt("party", partySize);
        outState.putBoolean("display", display);

        super.onSaveInstanceState(outState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            new MaterialDialog.Builder(this)
                    .title(R.string.action_about)
                    .content(R.string.about_content)
                    .positiveText(R.string.done)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }


    private void updateFragment(boolean createDisplay) {

        if(createDisplay || this.display) {
            this.display = true;

            //Float total = amount + (amount * gratuity);
            Float total = amount + gratuityValue;

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.animator.slide_in_bottom, R.animator.slide_out_bottom);
            ReceiptFragment newFragment = ReceiptFragment.newInstance(amount, gratuityValue, total, partySize + 1);
            ft.replace(R.id.container, newFragment, "detailFragment");

            // Start the animated transition.
            ft.commit();
        }
    }

    private void inputDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.numeric_dialog_title)
                .positiveColorRes(R.color.primary)
                .negativeText(R.string.cancel)
                .negativeColorRes(R.color.primary)
                .widgetColorRes(R.color.primary)
                .inputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {

                        try {

                            String value = input.toString();
                            Float f = Float.parseFloat(value);
                            DecimalFormat df = new DecimalFormat("0.00");
                            df.setMaximumFractionDigits(2);
                            df.setMinimumFractionDigits(2);
                            value = df.format(f);
                            setAmount(Float.parseFloat(value));

                            if(!customGratuity) {
                                gratuityValue = gratuity * amount;
                            }

                            updateFragment(true);

                        } catch (Exception e) {
                            Log.e("inputDialog", e.getMessage());
                        }
                    }
                }).show();
    }

    private void selectorDialog() {

        final double[] d = {0.1, 0.15, 0.18, 0.2, 0.25};

        String[] strings = {
                "10% tip:\t $",
                "15% tip:\t $",
                "18% tip:\t $",
                "20% tip:\t $",
                "25% tip:\t $",
        };

        for(int i = 0; i < 5; ++i) {
            float f = (float) (amount * d[i]);
            DecimalFormat form = new DecimalFormat("0.00");
            strings[i] = strings[i] + form.format(f);
        }

        new MaterialDialog.Builder(this)
                //.title(R.string.selector_content)
                .items(strings)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        //gratuity = (float) (amount * d[which]);
                        customGratuity = false;
                        gratuity = (float) d[which];
                        gratuityValue = gratuity * amount;

                        updateFragment(false);
                    }
                })
                .negativeText(R.string.cancel)
                .negativeColorRes(R.color.primary)
                .neutralText("Custom Tip")
                .negativeColorRes(R.color.primary)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        customTipDialog();
                    }
                })
                .show();
    }

    private void customTipDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.custom_tip_title)
                .positiveColorRes(R.color.primary)
                .negativeText(R.string.cancel)
                .negativeColorRes(R.color.primary)
                .widgetColorRes(R.color.primary)
                .inputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {

                        try {
                            customGratuity = true;
                            gratuityValue = Float.parseFloat(input.toString());
                            updateFragment(false);

                        } catch (Exception e) {
                            Log.e("inputDialog", e.getMessage());
                        }
                    }
                }).show();
    }


    TextView personDisplay;
    SeekBar seekbar;
    private void seekbarDialog() {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .customView(R.layout.seekbar_dialog, false)
                .negativeText(R.string.cancel)
                .positiveColorRes(R.color.primary)
                .negativeColorRes(R.color.primary)
                .positiveText("Done")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        partySize = seekbar.getProgress();
                        updateFragment(false);
                    }
                })
                .build();

        seekbar = (SeekBar) dialog.getCustomView().findViewById(R.id.seekBar);
        personDisplay = (TextView) dialog.getCustomView().findViewById(R.id.person_display);


        seekbar.setProgress(partySize);
        seekbar.setMax(9);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String ways = (progress > 0) ? " ways." : " way.";
                String out = "Split " + (progress + 1) + ways;
                personDisplay.setText(out);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        dialog.show();

    }

    public void setAmount(Float f) {
        amount = f;
        ((TextView)findViewById(R.id.amount_text)).setText("$" + f.toString());
    }
}
