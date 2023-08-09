package my.edu.utar.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;
import android.content.Intent;


public class MainActivity extends AppCompatActivity {

    EditText Bill,InputPpl;
    RadioGroup rgBreakdownOptions;
    LinearLayout layoutCustomBreakdown;
    Button btnCalculateSplit,share;
    LinearLayout layoutSplitAmounts;
    LinearLayout layoutEqualBreakdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bill = findViewById(R.id.In_bill);
        rgBreakdownOptions = findViewById(R.id.Breakdown_Options);
        layoutCustomBreakdown = findViewById(R.id.layout_CustomBreakdown);
        btnCalculateSplit = findViewById(R.id.btn_split);
        layoutSplitAmounts = findViewById(R.id.layout_Split);
        layoutEqualBreakdown = findViewById(R.id.layout_equal_breakdown);
        InputPpl = findViewById(R.id.In_ppl);
        share=findViewById(R.id.btn_share);

        rgBreakdownOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.equal:
                        enableCustomBreakdownViews(false);
                        enableEqualBreakdownViews(true);
                        break;
                    case R.id.custom:
                        enableCustomBreakdownViews(true);
                        enableEqualBreakdownViews(false);
                        break;
                }
            }
        });

        btnCalculateSplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateSplit();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareResult();
            }
        });
    }

    private void enableCustomBreakdownViews(boolean enabled) {
        for (int i = 0; i < layoutCustomBreakdown.getChildCount(); i++) {
            View childView = layoutCustomBreakdown.getChildAt(i);
            if (childView instanceof EditText) {
                childView.setEnabled(enabled);
            }
        }
    }

    private void enableEqualBreakdownViews(boolean enabled) {
        for (int i = 0; i < layoutEqualBreakdown.getChildCount(); i++) {
            View childView = layoutEqualBreakdown.getChildAt(i);
            if (childView instanceof EditText) {
                childView.setEnabled(enabled);
            }
        }
    }

    private void calculateSplit() {
        String billAmountString = Bill.getText().toString().trim();
        if (billAmountString.isEmpty()) {
            // Handle empty bill amount error
            return;
        }

        double billAmount = Double.parseDouble(billAmountString);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        layoutSplitAmounts.removeAllViews();
        if (rgBreakdownOptions.getCheckedRadioButtonId() == R.id.equal) {
            String numPeopleString = InputPpl.getText().toString().trim();
            if (numPeopleString.isEmpty()) {
                // Handle empty number of people error
                Toast.makeText(this, "Please enter the number of people", Toast.LENGTH_SHORT).show();
                return; // Exit the function
            }

            int numPeople = Integer.parseInt(numPeopleString);
            if (numPeople <= 0) {
                // Handle invalid number of people error
                Toast.makeText(this, "Number of people must be greater than zero", Toast.LENGTH_SHORT).show();
                return; // Exit the function
            }

            // Calculate split amount for each person
            double splitAmount = billAmount / numPeople;

            // Display split amounts in TextViews
            for (int i = 0; i < numPeople; i++) {
                TextView tvSplitAmount = new TextView(this);
                tvSplitAmount.setText("Split Amount " + (i + 1) + ": RM " + decimalFormat.format(splitAmount));
                layoutSplitAmounts.addView(tvSplitAmount);
            }
            // ... Equal breakdown calculation ...

        } else if (rgBreakdownOptions.getCheckedRadioButtonId() == R.id.custom) {
            int numPercentages = layoutCustomBreakdown.getChildCount() / 2;
            double totalPercentage = 0.0; // For summing up entered percentages

            for (int i = 0; i < numPercentages; i++) {
                View percentageView = layoutCustomBreakdown.getChildAt(i * 2 + 1);
                if (percentageView instanceof EditText) {
                    EditText etPercentage = (EditText) percentageView;
                    String percentageString = etPercentage.getText().toString().trim();

                    if (!percentageString.isEmpty()) {
                        double percentage = Double.parseDouble(percentageString);
                        totalPercentage += percentage;

                        double splitAmount = billAmount * (percentage / 100.0);

                        TextView tvSplitAmount = new TextView(this);
                        tvSplitAmount.setText("Split Amount " + (i + 1) + ": RM " + decimalFormat.format(splitAmount));
                        layoutSplitAmounts.addView(tvSplitAmount);
                    }
                }
            }

            if (totalPercentage != 100.0) {
                Toast.makeText(this, "Total percentage must be 100%", Toast.LENGTH_SHORT).show();
                layoutSplitAmounts.removeAllViews(); // Clear split amounts if verification fails
            }
        }
    }


    private void shareResult() {
        StringBuilder resultBuilder = new StringBuilder();
        int numSplitAmounts = layoutSplitAmounts.getChildCount();

        for (int i = 0; i < numSplitAmounts; i++) {
            View splitAmountView = layoutSplitAmounts.getChildAt(i);
            if (splitAmountView instanceof TextView) {
                TextView tvSplitAmount = (TextView) splitAmountView;
                resultBuilder.append(tvSplitAmount.getText().toString()).append("\n");
            }
        }

        String result = resultBuilder.toString();
        if (!result.isEmpty()) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, result);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        } else {
            Toast.makeText(this, "No split amounts to share", Toast.LENGTH_SHORT).show();
        }
    }
}