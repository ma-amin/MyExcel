package ir.chamran.myexcel;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.armdroid.rxfilechooser.RxFileChooser;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.chamran.myexcel.adapter.PostsAdapter;
import ir.chamran.myexcel.adapter.SwipeToDeleteCallback;
import ir.chamran.myexcel.databinding.ActivityMainBinding;
import ir.chamran.myexcel.db.excelFile.SQLiteToExcel;
import ir.chamran.myexcel.db.room.PostsRepository;
import ir.chamran.myexcel.db.room.PostsViewModel;
import ir.chamran.myexcel.model.ShopDetails;
import ir.chamran.myexcel.utilities.ConvertDb;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private PostsViewModel postsViewModel;
    private PostsAdapter postsAdapter;
    private MaterialSearchView searchView;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        ini();
    }

    private void ini() {
        setSupportActionBar(mainBinding.appbarMain.toolbar);
        searchView = mainBinding.appbarMain.searchView;
        //searchView.setCursorDrawable(R.drawable.search_cursor);
        //searchView.setHint(getResources().getString(R.string.str_search));
        searchView.setEllipsize(true);
        searchView.setVoiceSearch(false);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                postsAdapter.getFilter().filter(newText);
                return true;
            }
        });

        mainBinding.contentMain.recMain.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.contentMain.recMain.setHasFixedSize(true);
        //mainBinding.contentMain.recMain.setItemAnimator(new DefaultItemAnimator());
        //LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, animationList[i]);
        //recyclerView.setLayoutAnimation(controller);
        postsAdapter = new PostsAdapter(this, this::onSelectItem);
        mainBinding.contentMain.recMain.setAdapter(postsAdapter);
        enableSwipeToDeleteAndUndo();

        postsViewModel = ViewModelProviders.of(this).get(PostsViewModel.class);
        getPosts();

        mainBinding.fabMain.setOnClickListener(view -> onSelectItem(null));
    }

    private void getPosts() {
        postsViewModel.getPosts().observe(MainActivity.this, shopDetails -> {
            if (shopDetails != null)
                postsAdapter.updateList(shopDetails);
            //mainBinding.executePendingBindings();
        });
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final ShopDetails item = postsAdapter.getData().get(position);

                postsAdapter.removeItem(position);

                Snackbar snackbar = Snackbar.make(mainBinding.cndMain, R.string.toast_post_del, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.str_undo, view -> {
                    postsAdapter.restoreItem(item, position);
                    mainBinding.contentMain.recMain.scrollToPosition(position);
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (event == 2)
                            postsViewModel.delete(item.getDbID());
                        super.onDismissed(transientBottomBar, event);
                    }
                });
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mainBinding.contentMain.recMain);
    }
/*
    private void enableRecyclerSwipe() {
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT,
                (viewHolder, direction, position) -> {
            if (viewHolder instanceof PostsAdapter.PostsViewHolder) {

                String name = postsAdapter.getData().get(viewHolder.getAdapterPosition()).getTitle();

                final int deletedIndex = viewHolder.getAdapterPosition();
                final ShopDetails deletedItem = postsAdapter.getData().get(position);

                postsAdapter.removeItem(viewHolder.getAdapterPosition());

                Snackbar snackbar = Snackbar
                        .make(mainBinding.cndMain, name + " removed from cart!", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", view -> {
                    postsAdapter.restoreItem(deletedItem, deletedIndex);
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        });

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mainBinding.contentMain.recMain);
    }
*/

    private void newCompositeDisposable() {
        if (compositeDisposable == null)
            compositeDisposable = new CompositeDisposable();
    }

    private void onPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };

        newCompositeDisposable();
        RxPermissions rxPermissions = new RxPermissions(this);
        compositeDisposable.add(rxPermissions.request(permissions)
                .subscribe(permission -> {
                    if (permission) {
                        onExportExcel();
                    }
                }));
    }

    private void onFileChooser() {
        newCompositeDisposable();
        compositeDisposable.add(RxFileChooser.from(this)
                .pickFile().withMimeTypes("application/vnd.ms-excel")
                .useExternalStorage().single()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(content -> onImportExcel(content.getPath()),
                        throwable -> Toast.makeText(MainActivity.this, "err>> " + throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }

    private void onExportExcel() {
        ConvertDb convertDb = new ConvertDb(this);
        convertDb.onExportExcel(new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {
                //Log.i(TAG, "onStart");
            }

            @Override
            public void onCompleted(String filePath) {
                String path = getResources().getString(R.string.toast_excelInfo_export) + ">> " + filePath;
                Toast.makeText(MainActivity.this, path, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onImportExcel(String path) {
        ConvertDb convertDb = new ConvertDb(this);
        convertDb.onImportExcel(path, new ConvertDb.ImportListener() {
            @Override
            public void onStart() {
                //Log.i(TAG, "onStart");
            }

            @Override
            public void onCompleted(List<ShopDetails> shopDetails) {
                if (shopDetails != null) {
                    //Log.i(TAG, "onCompleted>> " + shopDetails.size());
                    new PostsRepository(MainActivity.this).insertPosts(shopDetails);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
/*
    private void openExcel(String filePath) {
        if (filePath != null) {
            try {
                File fileExcel = new File(filePath);

                POIFSFileSystem myFileSystem = new POIFSFileSystem(fileExcel);
                HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                HSSFSheet mySheet = myWorkBook.getSheetAt(0);

                Iterator<Row> rowIter = mySheet.rowIterator();
                int rowno =0;
                //textView.append("\n");

                while (rowIter.hasNext()) {

                    Log.e(TAG, " row no "+ rowno );

                    HSSFRow myRow = (HSSFRow) rowIter.next();
                    if(rowno !=0) {
                        Iterator<Cell> cellIter = myRow.cellIterator();
                        int colno =0;

                        String sno="", date="", det="";

                        while (cellIter.hasNext()) {
                            HSSFCell myCell = (HSSFCell) cellIter.next();

                            if (colno==0){
                                sno = myCell.toString();
                            }else if (colno==1){
                                date = myCell.toString();
                            }else if (colno==2){
                                det = myCell.toString();
                            }
                            colno++;

                            Log.e(TAG, " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                        }
                        //textView.append( sno + " -- "+ date+ "  -- "+ det+"\n");
                    }

                    rowno++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
*/

    private void aboutMe() {
        Snackbar snackbar = Snackbar.make(mainBinding.cndMain, R.string.toast_about_me, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.str_ok, view -> snackbar.dismiss()).setActionTextColor(Color.WHITE).show();
    }

    private void onSelectItem(ShopDetails shopDetails) {
        Intent intent = new Intent(this, PostDetailsActivity.class);
        if (shopDetails != null)
            intent.putExtra(PostDetailsActivity.KEY_POST_ID, shopDetails.getDbID());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_search) {
            if (searchView != null)
                searchView.showSearch(true);
            return true;

        } else if (id == R.id.menu_import_excel) {
            onFileChooser();
            return true;

        } else if (id == R.id.menu_export_excel) {
            onPermissions();
            return true;
        } else if (id == R.id.menu_about_me) {
            aboutMe();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (searchView != null && searchView.isSearchOpen()) {
            searchView.closeSearch();

        } else
            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null)
            compositeDisposable.dispose();
    }
}
