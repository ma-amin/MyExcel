package ir.chamran.myexcel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.chamran.myexcel.databinding.ActivityDetailsBinding;
import ir.chamran.myexcel.db.room.PostRepository;
import ir.chamran.myexcel.db.room.PostViewModel;
import ir.chamran.myexcel.model.ShopDetails;
import ir.chamran.myexcel.utilities.Utils;

public class PostDetailsActivity extends AppCompatActivity {

    //<editor-fold desc="Variable">
    public static final String KEY_POST_ID = "postID";

    private ActivityDetailsBinding detailsBinding;
    private ShopDetails mShopDetails;
    private Snackbar mSnackbar;

    private boolean bolState = false;
    //</editor-fold>

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        ini();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Integer postID = bundle.getInt(KEY_POST_ID);
            getPost(postID);

        } else {
            setPost();
        }
        //Log.d("Mammad", postID.toString());
    }

    private void ini() {
        iniAppBar();
    }

    private void onAnimatorListener() {
        detailsBinding.contentDetails.animStateDetails.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                detailsBinding.contentDetails.animStateDetails.setEnabled(true);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                detailsBinding.contentDetails.animStateDetails.setEnabled(false);
            }
        });

        detailsBinding.contentDetails.animStateDetails.setOnClickListener(v -> {
            if (detailsBinding.contentDetails.animStateDetails.isEnabled()) {
                if (bolState)
                    onAnimState(false);
                else
                    onAnimState(true);
                bolState = !bolState;
            }
        });
    }

    private void onAnimState(boolean state) {
        if (state)
            detailsBinding.contentDetails.animStateDetails.setAnimation("anim_true.json");
        else
            detailsBinding.contentDetails.animStateDetails.setAnimation("anim_false.json");

        detailsBinding.contentDetails.animStateDetails.playAnimation();
    }

    private void iniAppBar() {
        detailsBinding.appbarMain.toolbar.setTitle("");
        setSupportActionBar(detailsBinding.appbarMain.toolbar);
        /*detailsBinding.appbarMain.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (verticalOffset == 0) {
                //toolbar.setVisibility(View.GONE);
                bolAppBar = true;

            } else if (bolAppBar) {
                //toolbar.setVisibility(View.VISIBLE);
                bolAppBar = false;
            }
        });*/

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setViewsEnable(boolean enable) {
        detailsBinding.contentDetails.etiRegion.setEnabled(enable);
        detailsBinding.contentDetails.etiDistrict.setEnabled(enable);
        detailsBinding.contentDetails.etiAddress.setEnabled(enable);
        detailsBinding.contentDetails.etiType.setEnabled(enable);
        detailsBinding.contentDetails.etiFullName.setEnabled(enable);
        detailsBinding.contentDetails.etiTel1.setEnabled(enable);
        detailsBinding.contentDetails.etiTel2.setEnabled(enable);
        //detailsBinding.contentDetails.etiSubject.setFocusable(enable);
    }

    private void showDatePicker() {
        PersianCalendar now = new PersianCalendar();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
                    String date = year + "/" + monthOfYear + "/" + dayOfMonth;
                    detailsBinding.contentDetails.etiExpireTime.setText(date);
                },
                now.getPersianYear(),
                now.getPersianMonth(),
                now.getPersianDay());

        datePickerDialog.setTypeface("IRANSansMobile");
        datePickerDialog.show(getFragmentManager(), "tpd");
    }

    private void setPost() {
        forEdit();
        onAnimState(false);
    }

    private void setFabOnClickForInsert() {
        detailsBinding.fabDetails.setOnClickListener(view -> {
            mSnackbar = Snackbar.make(view, R.string.hint_save_post, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.str_ok, v -> onSave()).setActionTextColor(Color.GREEN);
            mSnackbar.show();
        });
    }

    private void setFabOnClickForEdit() {
        detailsBinding.fabDetails.setOnClickListener(view -> {
            mSnackbar = Snackbar.make(view, R.string.hint_edit_post, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.str_ok, v -> onEdit(true));
            mSnackbar.show();
        });
    }

    private void getPost(Integer postID) {
        //Log.e("Mammad", "getPost);

        PostViewModel.Factory factory = new PostViewModel.Factory(getApplication(), postID);
        PostViewModel postViewModel = ViewModelProviders.of(this, factory).get(PostViewModel.class);

        postViewModel.getPost().observe(this, shopDetails -> {
            if (shopDetails != null) {
                mShopDetails = shopDetails;

                //setEditText
                detailsBinding.contentDetails.setShopDetails(shopDetails);

                if (shopDetails.isState()) {
                    bolState = true;
                    onAnimState(true);

                } else {
                    bolState = false;
                    onAnimState(false);
                }

                onEdit(false);

            } else {
                onEdit(true);
            }
        });
    }

    private void onEdit(boolean state) {
        if (state) {
            detailsBinding.fabDetails.setImageResource(R.drawable.ic_check_black_24dp);
            forEdit();
            setViewsEnable(true);

        } else {
            detailsBinding.fabDetails.setImageResource(R.drawable.ic_edit_black_24dp);
            setFabOnClickForEdit();
            setViewsEnable(false);
        }
    }

    private void forEdit() {
        onAnimatorListener();
        setFabOnClickForInsert();
        detailsBinding.contentDetails.inputExpireTime.setOnClickListener(v -> showDatePicker());
        detailsBinding.contentDetails.etiExpireTime.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePicker();
            }
        });
    }

    private void onSave() {
        if (validateEditText(detailsBinding.contentDetails.etiType)
                && validateEditText(detailsBinding.contentDetails.etiFullName)
                && validateEditText(detailsBinding.contentDetails.etiExpireTime)
        ) {
            onCheckShopDetails();
        }
    }

    private boolean validateEditText(TextView editText) {
        String text = String.valueOf(editText.getText()).trim();

        if (text.isEmpty()) {
            requestFocus(editText);
            Toast.makeText(this, R.string.toast_login_empty, Toast.LENGTH_LONG).show();
            return false;

        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private String getEditText(TextView editText) {
        return String.valueOf(editText.getText()).trim();
    }

    private void onCheckShopDetails() {
        if (mShopDetails != null) {
            ShopDetails details = getShopDetails();
            details.setDbID(mShopDetails.getDbID());

            if (mShopDetails.equals(details)) {
                Utils.hideKeyboard(this);
                finish();

            } else {
                onSaveShopDetails(details);
            }

        } else {
            onSaveShopDetails(getShopDetails());
        }
    }

    private ShopDetails getShopDetails() {
        ShopDetails shopDetails = new ShopDetails();
        shopDetails.setRegion(getEditText(detailsBinding.contentDetails.etiRegion));
        shopDetails.setDistrict(getEditText(detailsBinding.contentDetails.etiDistrict));
        shopDetails.setAddress(getEditText(detailsBinding.contentDetails.etiAddress));
        shopDetails.setType(getEditText(detailsBinding.contentDetails.etiType));
        shopDetails.setFullName(getEditText(detailsBinding.contentDetails.etiFullName));
        shopDetails.setTel_1(getEditText(detailsBinding.contentDetails.etiTel1));
        shopDetails.setTel_2(getEditText(detailsBinding.contentDetails.etiTel2));
        shopDetails.setDate(detailsBinding.contentDetails.etiExpireTime.getText().toString());
        shopDetails.setState(bolState);

        return shopDetails;
    }

    private void onSaveShopDetails(ShopDetails shopDetails) {
        Utils.hideKeyboard(this);

        new PostRepository(this).insertPost(shopDetails, this::finish);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mSnackbar != null && mSnackbar.isShown())
            mSnackbar.dismiss();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
